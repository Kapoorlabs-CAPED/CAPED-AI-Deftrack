package kalmanTracker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;

import javax.swing.JProgressBar;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import costMatrix.CostFunction;
import costMatrix.JaqamanLinkingCostMatrixCreator;
import ellipsoidDetector.Intersectionobject;
import kalmanForSegments.Segmentobject;
import linkers.CVMKalmanFilter;
import linkers.JaqamanLinker;
import linkers.Logger;
import net.imglib2.RealPoint;

public class KFsearch implements IntersectionTracker {

	private static final double ALTERNATIVE_COST_FACTOR = 1.05d;

	private static final double PERCENTILE = 1d;

	private static final String BASE_ERROR_MSG = "[KalmanTracker] ";

	private final ArrayList<ArrayList<Intersectionobject>> Allblobs;
	public final JProgressBar jpb;
	private final double maxsearchRadius;
	private final double initialsearchRadius;
	private final CostFunction<Intersectionobject, Intersectionobject> UserchosenCostFunction;
	private final int maxframeGap;
	private HashMap<String, Integer> Accountedframes;
	private SimpleWeightedGraph<Intersectionobject, DefaultWeightedEdge> graph;

	protected Logger logger = Logger.DEFAULT_LOGGER;
	protected String errorMessage;
	ArrayList<ArrayList<Intersectionobject>> Allblobscopy;

	public KFsearch(final ArrayList<ArrayList<Intersectionobject>> Allblobs,
			final CostFunction<Intersectionobject, Intersectionobject> UserchosenCostFunction,
			final double maxsearchRadius, final double initialsearchRadius, final int maxframeGap,
			HashMap<String, Integer> Accountedframes, final JProgressBar jpb) {

		this.Allblobs = Allblobs;
		this.jpb = jpb;
		this.UserchosenCostFunction = UserchosenCostFunction;
		this.initialsearchRadius = initialsearchRadius;
		this.maxsearchRadius = maxsearchRadius;
		this.maxframeGap = maxframeGap;
		this.Accountedframes = Accountedframes;

	}

	@Override
	public SimpleWeightedGraph<Intersectionobject, DefaultWeightedEdge> getResult() {
		return graph;
	}

	@Override
	public boolean checkInput() {
		final StringBuilder errrorHolder = new StringBuilder();
		;
		final boolean ok = checkInput();
		if (!ok) {
			errorMessage = errrorHolder.toString();
		}
		return ok;
	}

	@Override
	public boolean process() {
		/*
		 * Outputs
		 */

		graph = new SimpleWeightedGraph<Intersectionobject, DefaultWeightedEdge>(DefaultWeightedEdge.class);

	    Collection<Intersectionobject> Firstorphan = Allblobs.get(0);
		

		Collection<Intersectionobject> Secondorphan = Allblobs.get(1);
		String uniqueIDnext  = Integer.toString(Allblobs.get(1).get(0).z) + Integer.toString(Allblobs.get(1).get(0).t) + Integer.toString(Allblobs.get(1).get(0).celllabel ); 
		// Max KF search cost.
		final double maxCost = maxsearchRadius * maxsearchRadius;

		// Max cost to nucleate KFs.
		final double maxInitialCost = initialsearchRadius * initialsearchRadius;

		/*
		 * Estimate Kalman filter variances.
		 *
		 * The search radius is used to derive an estimate of the noise that affects
		 * position and velocity. The two are linked: if we need a large search radius,
		 * then the fluoctuations over predicted states are large.
		 */
		final double positionProcessStd = maxsearchRadius / 2d;
		final double velocityProcessStd = maxsearchRadius / 2d;

		double meanSpotRadius = 1d;

		final double positionMeasurementStd = meanSpotRadius / 1d;

		final Map<CVMKalmanFilter, Intersectionobject> kalmanFiltersMap = new HashMap<CVMKalmanFilter, Intersectionobject>(
				Secondorphan.size());
		// Loop from the second frame to the last frame and build
		// KalmanFilterMap
		for (int i = 1; i < Allblobs.size();++i) {

		
			
			if(Allblobs.get(i).size() > 0) {
			uniqueIDnext =  Integer.toString(Allblobs.get(i).get(0).z) + Integer.toString(Allblobs.get(i).get(0).t) + Integer.toString(Allblobs.get(i).get(0).celllabel );
			
			
			
			List<Intersectionobject> measurements = Allblobs.get(i);
			// Make the preditiction map
			final Map<ComparableRealPoint, CVMKalmanFilter> predictionMap = new HashMap<ComparableRealPoint, CVMKalmanFilter>(
					kalmanFiltersMap.size());

			for (final CVMKalmanFilter kf : kalmanFiltersMap.keySet()) {
				final double[] X = kf.predict();
				final ComparableRealPoint point = new ComparableRealPoint(X);
				predictionMap.put(point, kf);

			}
			final List<ComparableRealPoint> predictions = new ArrayList<ComparableRealPoint>(predictionMap.keySet());
			// Orphans are dealt with later
			final Collection<CVMKalmanFilter> childlessKFs = new HashSet<CVMKalmanFilter>(kalmanFiltersMap.keySet());

			/*
			 * Here we simply link based on minimizing the squared distances to get an
			 * initial starting point, more advanced Kalman filter costs will be built in
			 * the next step
			 */

			if (!predictions.isEmpty() && !measurements.isEmpty()) {
				// Only link measurements to predictions if we have predictions.

				final JaqamanLinkingCostMatrixCreator<ComparableRealPoint, Intersectionobject> crm = new JaqamanLinkingCostMatrixCreator<ComparableRealPoint, Intersectionobject>(
						predictions, measurements, DistanceBasedcost, maxCost, ALTERNATIVE_COST_FACTOR, PERCENTILE);

				final JaqamanLinker<ComparableRealPoint, Intersectionobject> linker = new JaqamanLinker<ComparableRealPoint, Intersectionobject>(
						crm);
				if (!linker.checkInput() || !linker.process()) {
					errorMessage = BASE_ERROR_MSG + "Error linking candidates in frame " + uniqueIDnext + ": "
							+ linker.getErrorMessage();
					return false;
				}
				final Map<ComparableRealPoint, Intersectionobject> agnts = linker.getResult();
				final Map<ComparableRealPoint, Double> costs = linker.getAssignmentCosts();

				// Deal with found links.
				Secondorphan = new HashSet<Intersectionobject>(measurements);
				for (final ComparableRealPoint cm : agnts.keySet()) {
					final CVMKalmanFilter kf = predictionMap.get(cm);

					// Create links for found match.
					final Intersectionobject source = kalmanFiltersMap.get(kf);
					final Intersectionobject target = agnts.get(cm);
					
					graph.addVertex(source);
					graph.addVertex(target);
					final DefaultWeightedEdge edge = graph.addEdge(source, target);
					final double cost = costs.get(cm);
					graph.setEdgeWeight(edge, cost);

					// Update Kalman filter
					kf.update(MeasureBlob(target));

					// Update Kalman track PreRoiobject
					kalmanFiltersMap.put(kf, target);

					// Remove from orphan set
					Secondorphan.remove(target);

					// Remove from childless KF set
					childlessKFs.remove(kf);
				}
			}

			// Deal with orphans from the previous frame.
			// Here is the real linking with the actual cost function

			if (!Firstorphan.isEmpty() && !Secondorphan.isEmpty()) {

				// Trying to link orphans with unlinked candidates.

				final JaqamanLinkingCostMatrixCreator<Intersectionobject, Intersectionobject> ic = new JaqamanLinkingCostMatrixCreator<Intersectionobject, Intersectionobject>(
						Firstorphan, Secondorphan, UserchosenCostFunction, maxInitialCost, ALTERNATIVE_COST_FACTOR,
						PERCENTILE);
				final JaqamanLinker<Intersectionobject, Intersectionobject> newLinker = new JaqamanLinker<Intersectionobject, Intersectionobject>(
						ic);

				if (!newLinker.checkInput() || !newLinker.process()) {
					errorMessage = BASE_ERROR_MSG + "Error linking Blobs from frame " + (uniqueIDnext)
							+ " to next frame " + ": " + newLinker.getErrorMessage();
					return false;
				}

				final Map<Intersectionobject, Intersectionobject> newAssignments = newLinker.getResult();
				final Map<Intersectionobject, Double> assignmentCosts = newLinker.getAssignmentCosts();

				// Build links and new KFs from these links.
				for (final Intersectionobject source : newAssignments.keySet()) {
					final Intersectionobject target = newAssignments.get(source);

					// Remove from orphan collection.

					// Derive initial state and create Kalman filter.
					final double[] XP = estimateInitialState(source, target);
					final CVMKalmanFilter kt = new CVMKalmanFilter(XP, Double.MIN_NORMAL, positionProcessStd,
							velocityProcessStd, positionMeasurementStd);
					// We trust the initial state a lot.

					// Store filter and source
					kalmanFiltersMap.put(kt, target);

					synchronized (graph) {
						// Add edge to the graph.
						graph.addVertex(source);
						graph.addVertex(target);
						
						if(source.hashCode()!=target.hashCode()) {
						final DefaultWeightedEdge edge = graph.addEdge(source, target);
						final double cost = assignmentCosts.get(source);
						graph.setEdgeWeight(edge, cost);
						}
					}

				}
			}

			Firstorphan = Secondorphan;
			// Deal with childless KFs.
			for (final CVMKalmanFilter kf : childlessKFs) {
				// Echo we missed a measurement
				kf.update(null);

				// We can bridge a limited number of gaps. If too much, we die.
				// If not, we will use predicted state next time.
				if (kf.getNOcclusion() > maxframeGap) {
					kalmanFiltersMap.remove(kf);
				}
			}

		}
		}
		;
		return true;
	}

	@Override
	public String getErrorMessage() {

		return errorMessage;
	}

	private static final List<Intersectionobject> generateSpotList(final IntersectionobjectCollection spots,
			final String frame) {
		final List<Intersectionobject> list = new ArrayList<>(spots.getNThreeDRoiobjects(frame, true));
		for (final Iterator<Intersectionobject> iterator = spots.iterator(frame, true); iterator.hasNext();) {
			list.add(iterator.next());
		}
		return list;
	}

	private static final double[] MeasureBlob(final Intersectionobject target) {
		final double[] location = new double[] { target.Intersectionpoint[0], target.Intersectionpoint[1] };
		return location;
	}

	private static final double[] estimateInitialState(final Intersectionobject first,
			final Intersectionobject second) {
		final double[] xp = new double[] { second.Intersectionpoint[0], second.Intersectionpoint[1],
				second.diffTo(first, 0), second.diffTo(first, 1) };
		return xp;
	}

	/**
	 * 
	 * Implementations of various cost functions, starting with the simplest one,
	 * based on minimizing the distances between the links, followed by minimizing
	 * cost function based on intensity differences between the links.
	 *
	 * Cost function that returns the square distance between a KF state and a Blob.
	 */
	private static final CostFunction<ComparableRealPoint, Intersectionobject> DistanceBasedcost = new CostFunction<ComparableRealPoint, Intersectionobject>() {

		@Override
		public double linkingCost(final ComparableRealPoint state, final Intersectionobject Blob) {
			final double dx = state.getDoublePosition(0) - Blob.Intersectionpoint[0];
			final double dy = state.getDoublePosition(1) - Blob.Intersectionpoint[1];
			return dx * dx + dy * dy + Double.MIN_NORMAL;
			// So that it's never 0

		}
	};

	private static final class ComparableRealPoint extends RealPoint implements Comparable<ComparableRealPoint> {
		public ComparableRealPoint(final double[] A) {
			// Wrap array.
			super(A, false);
		}

		/**
		 * Sort based on X, Y
		 */
		@Override
		public int compareTo(final ComparableRealPoint o) {
			int i = 0;
			while (i < n) {
				if (getDoublePosition(i) != o.getDoublePosition(i)) {
					return (int) Math.signum(getDoublePosition(i) - o.getDoublePosition(i));
				}
				i++;
			}
			return hashCode() - o.hashCode();
		}
	}

}

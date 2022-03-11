package curvatureFinder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.JProgressBar;

import bdv.util.Bdv;
import bdvOverlay.BdvOverlayDisplay;
import curvatureUtils.ClockDisplayer;
import ellipsoidDetector.Distance;
import ellipsoidDetector.Intersectionobject;
import mpicbg.models.Point;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.RealPoint;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;
import pluginTools.InteractiveSimpleEllipseFit;
import pluginTools.RegressionCurveSegment;
import ransacPoly.RegressionFunction;
import utility.Listordereing;

public class CurvatureFinderDistance<T extends RealType<T> & NativeType<T>> extends MasterCurvature<T>
		implements CurvatureFinders<T> {

	public final InteractiveSimpleEllipseFit parent;
	public final JProgressBar jpb;
	public final int thirdDimension;
	public final int fourthDimension;
	public final int percent;
	public final int celllabel;
	public final ArrayList<Intersectionobject> AllCurveintersection;
	public final HashMap<Integer,Intersectionobject> AlldenseCurveintersection;
	ConcurrentHashMap<Integer, RegressionCurveSegment> Bestdelta = new ConcurrentHashMap<Integer, RegressionCurveSegment>();
	public final RandomAccessibleInterval<FloatType> ActualRoiimg;
	private final String BASE_ERROR_MSG = "[DistanceMeasure-]";
	protected String errorMessage;

	public CurvatureFinderDistance(final InteractiveSimpleEllipseFit parent,
			ArrayList<Intersectionobject> AllCurveintersection,  HashMap<Integer,Intersectionobject> AlldenseCurveintersection,
			final RandomAccessibleInterval<FloatType> ActualRoiimg, final JProgressBar jpb, final int percent,
			final int celllabel, final int thirdDimension, final int fourthDimension) {

		this.parent = parent;
		this.AllCurveintersection = AllCurveintersection;
		this.AlldenseCurveintersection = AlldenseCurveintersection;
		this.jpb = jpb;
		this.ActualRoiimg = ActualRoiimg;
		this.celllabel = celllabel;
		this.thirdDimension = thirdDimension;
		this.fourthDimension = fourthDimension;
		this.percent = percent;
	}


	public class ParallelCalls implements Callable< RegressionCurveSegment>{

		
		public final InteractiveSimpleEllipseFit parent;
		public final List<RealLocalizable> allorderedtruths;
		public final RealLocalizable centerpoint;
		public final int ndims;
		public final int celllabel;
		public final int z;
		public final int t;
		public final int index;

		
		public ParallelCalls( InteractiveSimpleEllipseFit parent,
		 List<RealLocalizable> allorderedtruths,
		 RealLocalizable centerpoint,
		 int ndims,
		 int celllabel,
		 int z,
		 int t,
		 int index) {
			
			
			this.parent = parent;
			this.allorderedtruths = allorderedtruths;
			this.centerpoint = centerpoint;
			this.ndims = ndims;
			this.celllabel = celllabel;
			this.z = z;
			this.t = t;
			this.index = index;
			
			
			
		}
		
		@Override
		public RegressionCurveSegment call() throws Exception {
			
			RegressionCurveSegment  result = getCurvature(parent, allorderedtruths, centerpoint, ndims, celllabel, z, t, index);
			
			
			return result;
			
			
			
			
			
		}
	}
	public HashMap<Integer, Intersectionobject> getMap() {

		return AlldenseCurveintersection;
	}
	
	@Override
	public ConcurrentHashMap<Integer, RegressionCurveSegment> getResult() {

		return Bestdelta;
	}

	@Override
	public boolean checkInput() {
		if (parent.CurrentViewOrig.numDimensions() > 4) {
			errorMessage = BASE_ERROR_MSG + " Can only operate on 4D, make slices of your stack . Got "
					+ parent.CurrentViewOrig.numDimensions() + "D.";
			return false;
		}
		return true;
	}

	@Override
	public boolean process() {

		
		int ndims = ActualRoiimg.numDimensions();

		String uniqueID = Integer.toString(thirdDimension) + Integer.toString(fourthDimension);

		List<RealLocalizable> truths = GetCandidatePoints.ListofPoints(parent, ActualRoiimg, jpb, percent,
				fourthDimension, thirdDimension);
		// Get mean co-ordinate from the candidate points
		RealLocalizable centerpoint = Listordereing.getMeanCord(truths);

		// Get the sparse list of points
		Pair<RealLocalizable, List<RealLocalizable>> Ordered = Listordereing.getOrderedList(truths, parent.resolution);

		DisplayListOverlay.ArrowDisplay(parent, Ordered, uniqueID);

		OverSliderLoop(parent, Ordered.getB(), centerpoint, truths, AllCurveintersection, AlldenseCurveintersection,
				ndims, celllabel, fourthDimension, thirdDimension);

		return true;
	}

	@Override
	public String getErrorMessage() {

		return errorMessage;
	}

	@Override
	public RegressionLineProfile getLocalcurvature(ArrayList<double[]> Cordlist,
			RealLocalizable centerpoint, int strideindex) {
		double[] x = new double[Cordlist.size()];
		double[] y = new double[Cordlist.size()];

		ArrayList<RealLocalizable> list = new ArrayList<RealLocalizable>();
		for (int index = 0; index < Cordlist.size() - 1; ++index) {
			x[index] = Cordlist.get(index)[0];
			y[index] = Cordlist.get(index)[1];

			RealPoint point = new RealPoint(new double[] { x[index], y[index] });
			list.add(point);

		}

		// Here you choose which method is used to detect curvature
		ArrayList<Pair<double[], double[]>> linescanpoints = new ArrayList<Pair<double[], double[]>>();
		
		RegressionLineProfile Curvaturedistancelist = DistanceCurvatureBlock(list, centerpoint, 0, linescanpoints);

		
		
		
		return Curvaturedistancelist;
	}
	@Override
	public Pair<RegressionLineProfile, ClockDisplayer> getCircleLocalcurvature(ArrayList<double[]> Cordlist,
			RealLocalizable centerpoint, int strideindex, final String name) {
		double[] x = new double[Cordlist.size()];
		double[] y = new double[Cordlist.size()];

		ArrayList<Point> pointlist = new ArrayList<Point>();
		ArrayList<RealLocalizable> list = new ArrayList<RealLocalizable>();
		
		for (int index = 0; index < Cordlist.size() - 1; ++index) {
			
			x[index] = Cordlist.get(index)[0];
			y[index] = Cordlist.get(index)[1];

			RealPoint point = new RealPoint(new double[] { x[index], y[index] });
			list.add(point);
			pointlist.add(new Point(new double[] { x[index], y[index] }));

		}

		// Here you choose which method is used to detect curvature
		Pair<RegressionLineProfile, ClockDisplayer> finalfunctionandList = RansacEllipseBlock(parent, list, centerpoint, centerpoint.numDimensions(), strideindex, true, name);

       
		
		return finalfunctionandList;
	}
	
	

	
	
	public RegressionLineProfile DistanceCurvatureBlock(
			final ArrayList<RealLocalizable> pointlist, RealLocalizable centerpoint, int strideindex,ArrayList<Pair<double[], double[]>> linescanpoints) {

		double Kappa = 0;
		double perimeter = 0;
		int ndims = centerpoint.numDimensions();
		double meanIntensity = 0;
		double meanSecIntensity = 0;
		ArrayList<double[]> Curvaturepoints = new ArrayList<double[]>();
		ArrayList<double[]> AllCurvaturepoints = new ArrayList<double[]>();
		double[] newpos = new double[ndims];
		long[] longnewpos = new long[ndims];
		
		
			
		perimeter = pointlist.size();
			
		
		int size = pointlist.size();
		final double[] pointB = new double[ndims];

		int splitindex;
		if (size % 2 == 0)
			splitindex = size / 2;
		else
			splitindex = (size - 1) / 2;

		for (int i = 0; i < ndims; ++i) {
			pointB[i] = pointlist.get(splitindex).getDoublePosition(i);

		}
		for (RealLocalizable point : pointlist) {

			point.localize(newpos);

			Kappa = getDistance(point, centerpoint);
			for (int d = 0; d < newpos.length; ++d)
				longnewpos[d] = (long) newpos[d];
			net.imglib2.Point intpoint = new net.imglib2.Point(longnewpos);
			long[] centerloc = new long[] { (long) centerpoint.getDoublePosition(0),
					(long) centerpoint.getDoublePosition(1) };
			net.imglib2.Point centpos = new net.imglib2.Point(centerloc);
			Pair<Double, Double> Intensity = getIntensity(parent, intpoint, centpos);
			// Average the intensity.
			meanIntensity += Intensity.getA();
			meanSecIntensity += Intensity.getB();

			AllCurvaturepoints.add(new double[] { newpos[0], newpos[1], Math.max(0, Kappa), perimeter, Intensity.getA(),
					Intensity.getB(), Math.max(0, Kappa) });

		}

		meanIntensity /= size;
		meanSecIntensity /= size;
		Curvaturepoints.add(
				new double[] { pointB[0], pointB[1], Math.max(0, Kappa), perimeter, meanIntensity, meanSecIntensity, Math.max(0, Kappa) });

		RegressionFunction finalfunctionransac = new RegressionFunction(Curvaturepoints);
		
		RegressionLineProfile currentprofile = new RegressionLineProfile(finalfunctionransac, AllCurvaturepoints, "");
		return currentprofile;

	}

	@Override
	public void OverSliderLoop(InteractiveSimpleEllipseFit parent, List<RealLocalizable> Ordered,
			RealLocalizable centerpoint, List<RealLocalizable> truths,
			ArrayList<Intersectionobject> AllCurveintersection,  HashMap<Integer,Intersectionobject> AlldenseCurveintersection,
			int ndims, int celllabel, int t, int z) {

		if (parent.minNumInliers > truths.size())
			parent.minNumInliers = truths.size();
		int i = parent.increment;
		
		// Get the sparse list of points, skips parent.resolution pixel points
		// set up executor service
		int nThreads = Runtime.getRuntime().availableProcessors();
		final ExecutorService taskExecutor = Executors.newFixedThreadPool(nThreads);
		
		List<Future<RegressionCurveSegment>> list = new ArrayList<Future<RegressionCurveSegment>>();
		RegressionCurveSegment oldresultpair = CommonLoop(parent, Ordered, centerpoint, ndims, celllabel, t, z);
        
		ConcurrentHashMap<Integer, ArrayList<LineProfileCircle>> zeroline = oldresultpair.LineScanIntensity;
		// Get the sparse list of points
		List<RealLocalizable> allorderedtruths = Listordereing.getList(Ordered, i);

		ParallelCalls call = new ParallelCalls(parent, allorderedtruths, centerpoint, ndims, celllabel, z, 1, 0);
		Future<RegressionCurveSegment> Futureresultpair = taskExecutor.submit(call);
		list.add(Futureresultpair);
		taskExecutor.shutdown();

		for(Future<RegressionCurveSegment> fut : list){
			
			
			
			
			try {
				
				
				oldresultpair = fut.get();
				RegressionCurveSegment newresultpair = new RegressionCurveSegment(oldresultpair.functionlist, oldresultpair.Curvelist, zeroline);
				
				Bestdelta.put(0, newresultpair);
			

				parent.localCurvature = newresultpair.Curvelist;
				parent.functions = newresultpair.functionlist;
				
				
			} catch (InterruptedException | ExecutionException e) {
				// TODO Auto-generated catch block
				continue;
			}
		}
		
		// Get the sparse list of points, skips parent.resolution pixel points

		
		Pair<Intersectionobject, Intersectionobject> sparseanddensepair = GetSingle(parent, centerpoint, Bestdelta);
		AllCurveintersection.add(sparseanddensepair.getA());
		AlldenseCurveintersection.put(celllabel, sparseanddensepair.getB());
		parent.AlllocalCurvature.add(parent.localCurvature);

	}

}

package kalmanTracker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import ellipsoidDetector.Intersectionobject;
import net.imglib2.KDTree;
import net.imglib2.RealPoint;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;
import utility.FlagNode;

public class NearestNeighbourSearch implements IntersectionTracker {

	private final HashMap<String, ArrayList<Intersectionobject>> ALLIntersections;
	private SimpleWeightedGraph<Intersectionobject, DefaultWeightedEdge> simplegraph;
	private HashMap<String, Integer> Accountedframes;
	protected String errorMessage;
	private final int z;
	private final int fourthDimSize;
	private final double maxdistance;

	public NearestNeighbourSearch(final HashMap<String, ArrayList<Intersectionobject>> ALLIntersections,
			final int z, final int fourthDimSize, final double maxdistance,
			final HashMap<String, Integer> Accountedframes) {

		this.ALLIntersections = ALLIntersections;
		this.z = z;
		this.fourthDimSize = fourthDimSize;
		this.maxdistance = maxdistance;
		this.Accountedframes = Accountedframes;

	}

	@Override
	public SimpleWeightedGraph<Intersectionobject, DefaultWeightedEdge> getResult() {

		return simplegraph;
	}

	@Override
	public boolean checkInput() {

		return true;
	}

	@Override
	public boolean process() {


			reset();
			Iterator<Map.Entry<String, Integer>> it = Accountedframes.entrySet().iterator();
			while (it.hasNext()) {

				int t = it.next().getValue();

				while (it.hasNext()) {
					int nextt = it.next().getValue();

					String uniqueID = Integer.toString(z) + Integer.toString(t);
					String uniqueIDnext = Integer.toString(z) + Integer.toString(nextt);
					String Zid = Integer.toString(z);

					ArrayList<Intersectionobject> baseobject = ALLIntersections.get(uniqueID);
					ArrayList<Intersectionobject> targetobject = ALLIntersections.get(uniqueIDnext);

					if (targetobject != null && targetobject.size() > 0) {

						Iterator<Intersectionobject> baseobjectiterator = baseobject.iterator();

						final int Targetintersections = targetobject.size();

						final List<RealPoint> targetCoords = new ArrayList<RealPoint>(Targetintersections);

						final List<FlagNode<Intersectionobject>> targetNodes = new ArrayList<FlagNode<Intersectionobject>>(
								Targetintersections);

						for (int index = 0; index < targetobject.size(); ++index) {

							targetCoords.add(new RealPoint(targetobject.get(index).Intersectionpoint));
							targetNodes.add(new FlagNode<Intersectionobject>(targetobject.get(index)));

						}

						if (targetNodes.size() > 0 && targetCoords.size() > 0) {

							final KDTree<FlagNode<Intersectionobject>> Tree = new KDTree<FlagNode<Intersectionobject>>(
									targetNodes, targetCoords);

							final NNFlagsearchKDtree<Intersectionobject> Search = new NNFlagsearchKDtree<Intersectionobject>(
									Tree);

							while (baseobjectiterator.hasNext()) {

								final Intersectionobject source = baseobjectiterator.next();

								final RealPoint sourceCoords = new RealPoint(source.Intersectionpoint);
								Search.search(sourceCoords);
								final double squareDist = Search.getSquareDistance();
								final FlagNode<Intersectionobject> targetNode = Search.getSampler().get();

								targetNode.setVisited(true);

								synchronized (simplegraph) {

									simplegraph.addVertex(source);
									simplegraph.addVertex(targetNode.getValue());
									final DefaultWeightedEdge edge = simplegraph.addEdge(source, targetNode.getValue());
									simplegraph.setEdgeWeight(edge, squareDist);

								}

							}

						}

					
				}
                    t = nextt;
			}
		}
		return true;
	}

	@Override
	public String getErrorMessage() {

		return errorMessage;
	}

	public void reset() {
		simplegraph = new SimpleWeightedGraph<Intersectionobject, DefaultWeightedEdge>(DefaultWeightedEdge.class);

			if (Accountedframes.entrySet().iterator().hasNext()) {

				String TID = Integer.toString(Accountedframes.entrySet().iterator().next().getValue());
                 String uniqueID = z + TID;
					if (ALLIntersections.get(uniqueID) != null) {
						final Iterator<Intersectionobject> it = ALLIntersections.get(uniqueID).iterator();
						
						while (it.hasNext()) {
							simplegraph.addVertex(it.next());
						}
					}
				}
			}


}

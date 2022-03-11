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
import utility.FlagNode;

public class NearestNeighbourSearch2D implements IntersectionTracker {

	private final HashMap<String, ArrayList<Intersectionobject>> ALLIntersections;
	private SimpleWeightedGraph<Intersectionobject, DefaultWeightedEdge> graph;
	protected String errorMessage;
	private final int fourthDimSize;
	private final double maxdistance;
	private HashMap<String, Integer> Accountedframes;
	public final double mindistance;
	public NearestNeighbourSearch2D(final HashMap<String, ArrayList<Intersectionobject>> ALLIntersections ,
			final int fourthDimSize, final double maxdistance,  HashMap<String, Integer> Accountedframes, double mindistance) {

		this.ALLIntersections = ALLIntersections;
		this.fourthDimSize = fourthDimSize;
		this.maxdistance = maxdistance;
        this.Accountedframes = Accountedframes;
        this.mindistance = mindistance;
	}

	@Override
	public SimpleWeightedGraph<Intersectionobject, DefaultWeightedEdge> getResult() {

		return graph;
	}

	@Override
	public boolean checkInput() {

		return true;
	}

	@Override
	public boolean process() {
		
		graph = new SimpleWeightedGraph<Intersectionobject, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		Iterator<Map.Entry<String, Integer>> it = Accountedframes.entrySet().iterator();
		while (it.hasNext()) {

			int t = it.next().getValue();
			while (it.hasNext()) {
				int nextt = it.next().getValue();
			String uniqueID =  Integer.toString(t) + Integer.toString(1);
			String uniqueIDnext =  Integer.toString(nextt) + Integer.toString(1);

			ArrayList<Intersectionobject> baseobject = ALLIntersections.get(uniqueID);
			ArrayList<Intersectionobject> targetobject = ALLIntersections.get(uniqueIDnext);

			
			if(targetobject!=null && targetobject.size() > 0) {

			Iterator<Intersectionobject> baseobjectiterator = baseobject.iterator();

			final int Targetintersections = targetobject.size();

			final List<RealPoint> targetCoords = new ArrayList<RealPoint>(Targetintersections);

			final List<FlagNode<Intersectionobject>> targetNodes = new ArrayList<FlagNode<Intersectionobject>>(
					Targetintersections);

			for (int index = 0; index < targetobject.size(); ++index) {

					targetCoords.add(new RealPoint( targetobject.get(index).Intersectionpoint));
					targetNodes.add(new FlagNode<Intersectionobject>(targetobject.get(index)));


			}
			if (targetNodes.size() > 0 && targetCoords.size() > 0) {

				final KDTree<FlagNode<Intersectionobject>> Tree = new KDTree<FlagNode<Intersectionobject>>(targetNodes,
						targetCoords);

				final NNFlagsearchKDtree<Intersectionobject> Search = new NNFlagsearchKDtree<Intersectionobject>(Tree);

				while (baseobjectiterator.hasNext()) {

					final Intersectionobject source = baseobjectiterator.next();

					
						final RealPoint sourceCoords = new RealPoint(source.Intersectionpoint);
						Search.search(sourceCoords);
						final double squareDist = Search.getSquareDistance();
					
						final FlagNode<Intersectionobject> targetNode = Search.getSampler().get();
						

						targetNode.setVisited(true);

						synchronized (graph) {

							graph.addVertex(source);
							graph.addVertex(targetNode.getValue());
							final DefaultWeightedEdge edge = graph.addEdge(source, targetNode.getValue());
							graph.setEdgeWeight(edge, squareDist);

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

}
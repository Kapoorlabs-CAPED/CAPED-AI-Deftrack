package kalmanForSegments;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;

import ellipsoidDetector.Intersectionobject;


public class TimeDirectedSegmentDepthFirstIterator extends SortedSegmentDepthFirstIterator<Segmentobject, DefaultWeightedEdge> {

	public TimeDirectedSegmentDepthFirstIterator(Graph<Segmentobject, DefaultWeightedEdge> g, Segmentobject startVertex) {
		super(g, startVertex, null);
	}
	
	
	
    protected void addUnseenChildrenOf(Segmentobject vertex) {
    	
    	int ts = vertex.getFeature(Segmentobject.Z).intValue();
        for (DefaultWeightedEdge edge : specifics.edgesOf(vertex)) {
            if (nListeners != 0) {
                fireEdgeTraversed(createEdgeTraversalEvent(edge));
            }

            Segmentobject oppositeV = Graphs.getOppositeVertex(graph, edge, vertex);
            int tt = oppositeV.getFeature(Segmentobject.Z).intValue();
            if (tt <= ts) {
            	continue;
            }

            if ( seen.containsKey(oppositeV)) {
                encounterVertexAgain(oppositeV, edge);
            } else {
                encounterVertex(oppositeV, edge);
            }
        }
    }

	
	
}

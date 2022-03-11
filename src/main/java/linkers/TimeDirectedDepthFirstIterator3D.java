package linkers;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;

import utility.ThreeDRoiobject;

public class TimeDirectedDepthFirstIterator3D extends SortedDepthFirstIterator<ThreeDRoiobject, DefaultWeightedEdge> {

	public TimeDirectedDepthFirstIterator3D(Graph<ThreeDRoiobject, DefaultWeightedEdge> g, ThreeDRoiobject startVertex) {
		super(g, startVertex, null);
	}
	
	
	
    protected void addUnseenChildrenOf(ThreeDRoiobject vertex) {
    	
    	int ts = vertex.getFeature(ThreeDRoiobject.Time).intValue();
        for (DefaultWeightedEdge edge : specifics.edgesOf(vertex)) {
            if (nListeners != 0) {
                fireEdgeTraversed(createEdgeTraversalEvent(edge));
            }

            ThreeDRoiobject oppositeV = Graphs.getOppositeVertex(graph, edge, vertex);
            int tt = oppositeV.getFeature(ThreeDRoiobject.Time).intValue();
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

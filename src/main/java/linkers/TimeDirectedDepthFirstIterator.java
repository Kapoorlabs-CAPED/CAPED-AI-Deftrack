package linkers;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;

import utility.PreRoiobject;

public class TimeDirectedDepthFirstIterator extends SortedDepthFirstIterator<PreRoiobject, DefaultWeightedEdge> {

	public TimeDirectedDepthFirstIterator(Graph<PreRoiobject, DefaultWeightedEdge> g, PreRoiobject startVertex) {
		super(g, startVertex, null);
	}
	
	
	
    protected void addUnseenChildrenOf(PreRoiobject vertex) {
    	
    	int ts = vertex.getFeature(PreRoiobject.Time).intValue();
        for (DefaultWeightedEdge edge : specifics.edgesOf(vertex)) {
            if (nListeners != 0) {
                fireEdgeTraversed(createEdgeTraversalEvent(edge));
            }

            PreRoiobject oppositeV = Graphs.getOppositeVertex(graph, edge, vertex);
            int tt = oppositeV.getFeature(PreRoiobject.Time).intValue();
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

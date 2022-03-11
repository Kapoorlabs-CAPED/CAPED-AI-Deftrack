package kalmanTracker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;

import ellipsoidDetector.Intersectionobject;




public class TimeDirectedSortedDepthFirstIterator extends SortedDepthFirstIterator<Intersectionobject, DefaultWeightedEdge> {

	public TimeDirectedSortedDepthFirstIterator(final Graph<Intersectionobject, DefaultWeightedEdge> g, final Intersectionobject startVertex, final Comparator<Intersectionobject> comparator) {
		super(g, startVertex, comparator);
	}



    @Override
	protected void addUnseenChildrenOf(final Intersectionobject vertex) {

		// Retrieve target vertices, and sort them in a list
		final List< Intersectionobject > sortedChildren = new ArrayList< Intersectionobject >();
    	// Keep a map of matching edges so that we can retrieve them in the same order
    	final Map<Intersectionobject, DefaultWeightedEdge> localEdges = new HashMap<Intersectionobject, DefaultWeightedEdge>();

    	final int ts = vertex.getFeature(Intersectionobject.Time).intValue();
        for (final DefaultWeightedEdge edge : specifics.edgesOf(vertex)) {

        	final Intersectionobject oppositeV = Graphs.getOppositeVertex(graph, edge, vertex);
        	final int tt = oppositeV.getFeature(Intersectionobject.Time).intValue();
        	if (tt <= ts) {
        		continue;
        	}

        	if (!seen.containsKey(oppositeV)) {
        		sortedChildren.add(oppositeV);
        	}
        	localEdges.put(oppositeV, edge);
        }

		Collections.sort( sortedChildren, Collections.reverseOrder( comparator ) );
		final Iterator< Intersectionobject > it = sortedChildren.iterator();
        while (it.hasNext()) {
			final Intersectionobject child = it.next();

            if (nListeners != 0) {
                fireEdgeTraversed(createEdgeTraversalEvent(localEdges.get(child)));
            }

            if (seen.containsKey(child)) {
                encounterVertexAgain(child, localEdges.get(child));
            } else {
                encounterVertex(child, localEdges.get(child));
            }
        }
    }



}

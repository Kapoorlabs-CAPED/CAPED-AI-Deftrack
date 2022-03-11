package linkers;

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

import utility.PreRoiobject;


public class TimeDirectedSortedDepthFirstIterator extends SortedDepthFirstIterator<PreRoiobject, DefaultWeightedEdge> {

	public TimeDirectedSortedDepthFirstIterator(final Graph<PreRoiobject, DefaultWeightedEdge> g, final PreRoiobject startVertex, final Comparator<PreRoiobject> comparator) {
		super(g, startVertex, comparator);
	}



    @Override
	protected void addUnseenChildrenOf(final PreRoiobject vertex) {

		// Retrieve target vertices, and sort them in a list
		final List< PreRoiobject > sortedChildren = new ArrayList< PreRoiobject >();
    	// Keep a map of matching edges so that we can retrieve them in the same order
    	final Map<PreRoiobject, DefaultWeightedEdge> localEdges = new HashMap<PreRoiobject, DefaultWeightedEdge>();

    	final int ts = vertex.getFeature(PreRoiobject.Time).intValue();
        for (final DefaultWeightedEdge edge : specifics.edgesOf(vertex)) {

        	final PreRoiobject oppositeV = Graphs.getOppositeVertex(graph, edge, vertex);
        	final int tt = oppositeV.getFeature(PreRoiobject.Time).intValue();
        	if (tt <= ts) {
        		continue;
        	}

        	if (!seen.containsKey(oppositeV)) {
        		sortedChildren.add(oppositeV);
        	}
        	localEdges.put(oppositeV, edge);
        }

		Collections.sort( sortedChildren, Collections.reverseOrder( comparator ) );
		final Iterator< PreRoiobject > it = sortedChildren.iterator();
        while (it.hasNext()) {
			final PreRoiobject child = it.next();

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

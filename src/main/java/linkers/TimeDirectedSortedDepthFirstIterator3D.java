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

import utility.ThreeDRoiobject;


public class TimeDirectedSortedDepthFirstIterator3D extends SortedDepthFirstIterator<ThreeDRoiobject, DefaultWeightedEdge> {

	public TimeDirectedSortedDepthFirstIterator3D(final Graph<ThreeDRoiobject, DefaultWeightedEdge> g, final ThreeDRoiobject startVertex, final Comparator<ThreeDRoiobject> comparator) {
		super(g, startVertex, comparator);
	}



    @Override
	protected void addUnseenChildrenOf(final ThreeDRoiobject vertex) {

		// Retrieve target vertices, and sort them in a list
		final List< ThreeDRoiobject > sortedChildren = new ArrayList< ThreeDRoiobject >();
    	// Keep a map of matching edges so that we can retrieve them in the same order
    	final Map<ThreeDRoiobject, DefaultWeightedEdge> localEdges = new HashMap<ThreeDRoiobject, DefaultWeightedEdge>();

    	final int ts = vertex.getFeature(ThreeDRoiobject.Time).intValue();
        for (final DefaultWeightedEdge edge : specifics.edgesOf(vertex)) {

        	final ThreeDRoiobject oppositeV = Graphs.getOppositeVertex(graph, edge, vertex);
        	final int tt = oppositeV.getFeature(ThreeDRoiobject.Time).intValue();
        	if (tt <= ts) {
        		continue;
        	}

        	if (!seen.containsKey(oppositeV)) {
        		sortedChildren.add(oppositeV);
        	}
        	localEdges.put(oppositeV, edge);
        }

		Collections.sort( sortedChildren, Collections.reverseOrder( comparator ) );
		final Iterator< ThreeDRoiobject > it = sortedChildren.iterator();
        while (it.hasNext()) {
			final ThreeDRoiobject child = it.next();

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

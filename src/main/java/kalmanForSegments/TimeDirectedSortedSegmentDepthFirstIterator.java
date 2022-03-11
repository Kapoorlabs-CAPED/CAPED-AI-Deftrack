package kalmanForSegments;

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





public class TimeDirectedSortedSegmentDepthFirstIterator extends SortedSegmentDepthFirstIterator<Segmentobject, DefaultWeightedEdge> {

	public TimeDirectedSortedSegmentDepthFirstIterator(final Graph<Segmentobject, DefaultWeightedEdge> g, final Segmentobject startVertex, final Comparator<Segmentobject> comparator) {
		super(g, startVertex, comparator);
	}



    @Override
	protected void addUnseenChildrenOf(final Segmentobject vertex) {

		// Retrieve target vertices, and sort them in a list
		final List< Segmentobject > sortedChildren = new ArrayList< Segmentobject >();
    	// Keep a map of matching edges so that we can retrieve them in the same order
    	final Map<Segmentobject, DefaultWeightedEdge> localEdges = new HashMap<Segmentobject, DefaultWeightedEdge>();

    	final int ts = vertex.getFeature(Segmentobject.Time).intValue();
        for (final DefaultWeightedEdge edge : specifics.edgesOf(vertex)) {

        	final Segmentobject oppositeV = Graphs.getOppositeVertex(graph, edge, vertex);
        	final int tt = oppositeV.getFeature(Segmentobject.Time).intValue();
        	if (tt <= ts) {
        		continue;
        	}

        	if (!seen.containsKey(oppositeV)) {
        		sortedChildren.add(oppositeV);
        	}
        	localEdges.put(oppositeV, edge);
        }

		Collections.sort( sortedChildren, Collections.reverseOrder( comparator ) );
		final Iterator< Segmentobject > it = sortedChildren.iterator();
        while (it.hasNext()) {
			final Segmentobject child = it.next();

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

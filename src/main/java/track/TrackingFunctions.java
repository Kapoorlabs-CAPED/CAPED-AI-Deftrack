package track;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import ellipsoidDetector.Intersectionobject;
import ij.IJ;
import kalmanForSegments.ETrackSegmentCostFunction;
import kalmanForSegments.KFSegmentsearch;
import kalmanForSegments.Segmentobject;
import kalmanForSegments.SegmentobjectCollection;
import kalmanTracker.ETrackCostFunction;
import kalmanTracker.IntersectionobjectCollection;
import kalmanTracker.KFsearch;
import pluginTools.InteractiveSimpleEllipseFit;

public class TrackingFunctions {

	final InteractiveSimpleEllipseFit parent;

	public TrackingFunctions(final InteractiveSimpleEllipseFit parent) {

		this.parent = parent;

	}

	public SimpleWeightedGraph<Intersectionobject, DefaultWeightedEdge> Trackfunction() {

		parent.UserchosenCostFunction = new ETrackCostFunction(1, 0);

		ArrayList<ArrayList<Intersectionobject>> colllist = new ArrayList<ArrayList<Intersectionobject>>();
		parent.ALLIntersections = hashMapSorter.SortTimeorZ.sortByIntegerInter(parent.ALLIntersections);
		for (Map.Entry<String, ArrayList<Intersectionobject>> entry : parent.ALLIntersections.entrySet()) {

			ArrayList<Intersectionobject> bloblist = entry.getValue();
			if(bloblist.size() > 0)
			colllist.add(bloblist);
		

		}

		KFsearch Tsearch = new KFsearch(colllist, parent.UserchosenCostFunction,  100,
				100, 10, parent.AccountedZ, parent.jpb);
		Tsearch.process();
		SimpleWeightedGraph<Intersectionobject, DefaultWeightedEdge> simplegraph = Tsearch.getResult();

		return simplegraph;

	}
	
	public SimpleWeightedGraph<Intersectionobject, DefaultWeightedEdge> Trackdensefunction() {

		parent.UserchosenCostFunction = new ETrackCostFunction(1, 0);

		ArrayList<ArrayList<Intersectionobject>> colllist = new ArrayList<ArrayList<Intersectionobject>>();
		parent.ALLdenseIntersections = hashMapSorter.SortTimeorZ.sortByIntegerInter(parent.ALLdenseIntersections);
		
		for (Map.Entry<String, ArrayList<Intersectionobject>> entry : parent.ALLdenseIntersections.entrySet()) {
			
			ArrayList<Intersectionobject> bloblist = entry.getValue();
	
			if(bloblist.size() > 0) {
			colllist.add(bloblist);

			}
			
		}
		KFsearch Tsearch = new KFsearch(colllist, parent.UserchosenCostFunction,  100,
				100, 10, parent.AccountedZ, parent.jpb);
		Tsearch.process();
		
		SimpleWeightedGraph<Intersectionobject, DefaultWeightedEdge> simplegraph = Tsearch.getResult();

		return simplegraph;

	}

	

}

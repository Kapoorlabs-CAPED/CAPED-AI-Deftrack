package utility;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import linkers.TrackModel;

public class AnalzeZTrack {

	
	public static HashMap<Integer, ArrayList<PreRoiobject>> get3Dobjects(SimpleWeightedGraph< PreRoiobject, DefaultWeightedEdge > Zgraph){
		
		TrackModel model = new TrackModel(Zgraph);
		HashMap<Integer, ArrayList<PreRoiobject>> threeDmap = new HashMap<Integer, ArrayList<PreRoiobject>>();
		
		for (final Integer id : model.trackIDs(true)) {
			
			model.setName(id, "Track" + id);
			
			final HashSet<PreRoiobject> Snakeset = model.trackPreRoiobjects(id);
			ArrayList<PreRoiobject> list = new ArrayList<PreRoiobject>();

			Comparator<PreRoiobject> ThirdDimcomparison = new Comparator<PreRoiobject>() {

				@Override
				public int compare(final PreRoiobject A, final PreRoiobject B) {

					return A.thirdDimension - B.thirdDimension;

				}

			};
			
			Iterator<PreRoiobject> Snakeiter = Snakeset.iterator();

			while (Snakeiter.hasNext()) {

				PreRoiobject currentsnake = Snakeiter.next();

				list.add(currentsnake);

			}
			Collections.sort(list, ThirdDimcomparison);
			
			threeDmap.put(id, list);
			
		}
	
		
		return threeDmap;
		
	}
	
	public static int getMaxID(SimpleWeightedGraph< PreRoiobject, DefaultWeightedEdge > Zgraph) {
		
		int maxid = Integer.MIN_VALUE;
		
		TrackModel model = new TrackModel(Zgraph);
		for (final Integer id : model.trackIDs(true)) {
		
			if (id > maxid)
				maxid = id;
			
		}
		
		
		
		
		return maxid;
		
	}
	
	
}

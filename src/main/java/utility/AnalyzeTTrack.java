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
import linkers.TrackModel3D;


public class AnalyzeTTrack {

	
	public static HashMap<Integer, ArrayList<ThreeDRoiobject>> get3DTracks(SimpleWeightedGraph< ThreeDRoiobject, DefaultWeightedEdge > Tgraph){
		
		TrackModel3D model = new TrackModel3D(Tgraph);
		HashMap<Integer, ArrayList<ThreeDRoiobject>> fourDmap = new HashMap<Integer, ArrayList<ThreeDRoiobject>>();
		
		for (final Integer id : model.trackIDs(true)) {
			
           model.setName(id, "Track" + id);
			
			final HashSet<ThreeDRoiobject> Snakeset = model.trackThreeDRoiobjects(id);
			ArrayList<ThreeDRoiobject> list = new ArrayList<ThreeDRoiobject>();

			Comparator<ThreeDRoiobject> FourthDimcomparison = new Comparator<ThreeDRoiobject>() {

				@Override
				public int compare(final ThreeDRoiobject A, final ThreeDRoiobject B) {

					return A.fourthDimension - B.fourthDimension;

				}

			};
			
			Iterator<ThreeDRoiobject> Snakeiter = Snakeset.iterator();

			while (Snakeiter.hasNext()) {

				ThreeDRoiobject currentsnake = Snakeiter.next();

				list.add(currentsnake);

			}
			Collections.sort(list, FourthDimcomparison);
			
			fourDmap.put(id, list);
			
			
		}
		
		return fourDmap;
		
	}
	
	public static int getMaxID(SimpleWeightedGraph< ThreeDRoiobject, DefaultWeightedEdge > Tgraph) {
		
		int maxid = Integer.MIN_VALUE;
		
		TrackModel3D model = new TrackModel3D(Tgraph);
		for (final Integer id : model.trackIDs(true)) {
		
			if (id > maxid)
				maxid = id;
			
		}
		
		
		
		
		return maxid;
		
	}
	
	
	
}

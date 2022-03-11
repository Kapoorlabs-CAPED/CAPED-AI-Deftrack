package pluginTools;

import java.util.ArrayList;
import java.util.HashMap;

import ellipsoidDetector.Intersectionobject;
import net.imglib2.util.Pair;

public class Binobject {

	
	public final HashMap<String, Integer> maxid;
	public final HashMap<String, ArrayList<Intersectionobject>> sortedmap;
	public final HashMap<String, Double> bincurve;
	
	public Binobject(HashMap<String, Integer> maxid,HashMap<String, ArrayList<Intersectionobject>> sortedmap,HashMap<String, Double> bincurve ) {
		
		this.maxid = maxid;
		this.sortedmap = sortedmap;
		this.bincurve = bincurve;
		
	}
	
	
}

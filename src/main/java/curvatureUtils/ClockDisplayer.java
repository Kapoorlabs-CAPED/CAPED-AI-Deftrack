package curvatureUtils;

import java.util.ArrayList;

import net.imglib2.util.Pair;

public class ClockDisplayer {

	
	
	public String name;
	public Pair<double[], double[]> startendline;
	
	
	public ClockDisplayer(String name,Pair<double[], double[]> startendline ) {
		
		this.name = name;
		this.startendline = startendline;
		
	}
	
}

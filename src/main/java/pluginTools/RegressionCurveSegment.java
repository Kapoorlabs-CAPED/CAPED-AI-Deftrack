package pluginTools;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import curvatureFinder.LineProfileCircle;
import kalmanForSegments.Segmentobject;
import ransacPoly.RegressionFunction;
import utility.Curvatureobject;

public class RegressionCurveSegment {
	
	
	
	public final ArrayList<RegressionFunction> functionlist;
	public final ArrayList<Curvatureobject> Curvelist;
	public final ConcurrentHashMap<Integer, ArrayList<LineProfileCircle>> LineScanIntensity;
	
	public RegressionCurveSegment(final ArrayList<RegressionFunction> functionlist, final ArrayList<Curvatureobject> Curvelist, final ConcurrentHashMap<Integer, ArrayList<LineProfileCircle>> LineScanIntensity) {
		
		
		this.functionlist = functionlist;
		
		this.Curvelist = Curvelist;
		
		this.LineScanIntensity = LineScanIntensity;
		
	}
	
	public RegressionCurveSegment(final ArrayList<RegressionFunction> functionlist, final ArrayList<Curvatureobject> Curvelist) {
		
		
		this.functionlist = functionlist;
		
		this.Curvelist = Curvelist;
		
		this.LineScanIntensity = null;
		
	}

}

package curvatureFinder;

import java.util.ArrayList;

import ransacPoly.RegressionFunction;

public class RegressionLineProfile {

	public final RegressionFunction regfunc;
	
	public final ArrayList<LineProfileCircle> LineScanIntensity;
	
	public final ArrayList<double[]> AllCurvaturepoints;
	
	public final String name;
	
	
	public RegressionLineProfile(final RegressionFunction regfunc, final ArrayList<LineProfileCircle> LineScanIntensity, final ArrayList<double[]> AllCurvaturepoints, final String name ) {
		
		
		this.regfunc = regfunc;
		
		this.LineScanIntensity = LineScanIntensity;
		
		this.AllCurvaturepoints = AllCurvaturepoints;
		
		this.name = name;
		
	}
	
	
	public RegressionLineProfile(final RegressionFunction regfunc, final ArrayList<double[]> AllCurvaturepoints, final String name ) {
		
		
		this.regfunc = regfunc;
		
		this.LineScanIntensity = null;
		
		this.AllCurvaturepoints = AllCurvaturepoints;
		
		this.name  = name;
		
		
	}
	
	
	
}

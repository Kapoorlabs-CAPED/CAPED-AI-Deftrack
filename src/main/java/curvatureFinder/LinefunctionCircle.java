package curvatureFinder;

import net.imglib2.RealLocalizable;
import varun_algorithm_ransac_Ransac.Circle;

public class LinefunctionCircle {

	
	public final Circle circle;
	
	public final RealLocalizable currentpoint;
	
	public final int ndims;
	
	double fcteps = 1.0E-10;
	
	public LinefunctionCircle(final Circle circle, final RealLocalizable currentpoint) {
		
		this.circle = circle;
		
		this.currentpoint = currentpoint;
		
		this.ndims = currentpoint.numDimensions(); 
	}
	
	
	
	public double[] NormalatPoint() {
		
		
		
		
		double[] center = circle.getCenter();
		
		double[] midpoint = circle.getMid();
		
		
		double slope = (midpoint[1] - center[1]) / (midpoint[0] - center[0] + fcteps);
		
		double intercept = midpoint[1] - slope * midpoint[0];
		
		double[] slopeandintercept = {slope , intercept};
		
		
		return slopeandintercept;
	}
	
	
	
	
	
}

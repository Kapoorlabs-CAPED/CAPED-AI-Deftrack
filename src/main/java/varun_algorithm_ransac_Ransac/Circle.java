package varun_algorithm_ransac_Ransac;


public class Circle {

	final double[] center;
	final double radii;
	final double sign;
	final double[] start;
	final double[] mid;
	final double[] end;

	public Circle(final double[] center, final double[] start, final double[] mid, final double[] end, final double radii, double sign) {

		this.center = center;
		this.radii = radii;
		this.sign = sign;
		this.start = start;
		this.mid = mid;
		this.end = end;

	}

	public static Circle FitCircleMA(final double[] pa, final double[] pb, final double[] pc) {
		
	//	System.out.println("Fitting Circle on: " + " (" + pa[0] + "," + pa[1] + ")" + " (" + pb[0] + "," + pb[1] + ")" + " (" + pc[0] + "," + pc[1] + ")");
		double[] centrad = new double[3];
		final double fcteps = 1.0E-10;
		// Points can not be collinear
				if ((pa[0] == pb[0] && pb[0] == pc[0]) || (pa[1] == pb[1] && pb[1] == pc[1])) {
					centrad[0] = 0; // x
					centrad[1] = 0; // y
					centrad[2] = -1; // radius
					Circle threepoint = new Circle(new double[] { centrad[0], centrad[1] },pa, pb, pc, centrad[2], 0);
					return threepoint;
				}
				
				final double offset = pb[0] * pb[0] + pb[1] * pb[1];
				final double bc = (pa[0] * pa[0] + pa[1] * pa[1] - offset) /2.0;
				final double cd = (offset - pc[0] * pc[0] - pc[1] * pc[1]) / 2.0;
				final double det = (pa[0] - pb[0]) * (pb[1] - pc[1]) - (pb[0] - pc[0]) * (pa[1] - pb[1]);
		
		
				if(Math.abs(det) < fcteps) {
					
					centrad[0] = 0; // x
					centrad[1] = 0; // y
					centrad[2] = -1; // radius
					
				}
				
				
				else {
					
					final double idet = 1.0/det;
					centrad[0] = (bc * (pb[1] - pc[1]) - cd * (pa[1] - pb[1])) * idet;
					centrad[1] =  (cd * (pa[0] - pb[0]) - bc * (pb[0] - pc[0])) * idet;
					centrad[2]  = Math.sqrt(Math.pow((pa[0] - centrad[0]), 2) + Math.pow((pa[1] - centrad[1]), 2));
					
				}
				double sign = 1;
				
				Circle threepoint = new Circle(new double[] { centrad[0], centrad[1] },pa, pb,pc, centrad[2], sign);
				return threepoint;
				
	}
	
	public static Circle FitCircleMb(final double[] pa, final double[] pb, final double[] pc) {
	//	System.out.println("Fitting Circle on: " + " (" + pa[0] + "," + pa[1] + ")" + " (" + pb[0] + "," + pb[1] + ")" + " (" + pc[0] + "," + pc[1] + ")");
		double[] centrad = new double[3];
		// Points can not be collinear
		if ((pa[0] == pb[0] && pb[0] == pc[0]) || (pa[1] == pb[1] && pb[1] == pc[1])) {
			centrad[0] = 0; // x
			centrad[1] = 0; // y
			centrad[2] = -1; // radius
			Circle threepoint = new Circle(new double[] { centrad[0], centrad[1] },pa, pb, pc, centrad[2], 0);
			return threepoint;
		}

		double a = pb[0] - pa[0];
		double b = pb[1] - pa[1];
		double c = pc[0] - pa[0];
		double d = pc[1] - pa[1];

		double e = a * (pa[0] + pb[0]) + b * (pa[1] + pb[1]);
		double f = c * (pa[0] + pc[0]) + d * (pa[1] + pc[1]);

		double g = 2.0 * (a * (pc[1] - pb[1]) - b * (pc[0] - pb[0]));

		if (g == 0.0) {
			centrad[0] = 0; // x
			centrad[1] = 0; // y
			centrad[2] = -1; // radius
		} 
		
		else { 
			
			// return centre and radius of the circle
			centrad[0] = (d * e - b * f) / g;
			centrad[1] = (a * f - c * e) / g;
			centrad[2] = Math.sqrt(Math.pow((pa[0] - centrad[0]), 2) + Math.pow((pa[1] - centrad[1]), 2));
		}
		double sign = 1;
			
		Circle threepoint = new Circle(new double[] { centrad[0], centrad[1] },pa, pb, pc, centrad[2], sign);
		return threepoint;

	}

	public double[] getCenter() {

		return center;

	}

	public double[] getStart() {
		
		return start;
	}
	
	public double[] getMid() {
		
		return mid;
	}
	
    public double[] getEnd() {
		
		return end;
	}
	
	public double getRadii() {

		return radii;
	}
	
	public double getSign() {
		
		return sign;
	}

}

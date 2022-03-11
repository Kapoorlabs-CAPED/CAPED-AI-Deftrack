package utility;

import java.util.Comparator;
import ellipsoidDetector.Distance;
import net.imglib2.RealLocalizable;

public class AngleComparator implements Comparator<RealLocalizable> {

	
	public final RealLocalizable midpoint;
	public final RealLocalizable refpoint;
	
	public AngleComparator(final RealLocalizable refpoint, final RealLocalizable midpoint) {
		
		this.midpoint = midpoint;
		this.refpoint = refpoint;
	}
	
	@Override
	public int compare(RealLocalizable o1, RealLocalizable o2) {
		
		
		double angledegA = Distance.SlopeVectors( o1, midpoint);
		double angledegB = Distance.SlopeVectors( o2, midpoint);
		
		return (int)Math.round(angledegA - angledegB);
	}

	

}

package varun_algorithm_ransac_Ransac;

import java.util.ArrayList;
import ij.IJ;
import net.imglib2.Cursor;
import net.imglib2.Interval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.RealPoint;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Intervals;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;
import net.imglib2.view.Views;

public class ConnectedComponentCoordinates {
	static int span = 1;
	public static <T extends Comparable<T>> ArrayList<Pair<RealLocalizable, T>> GetCoordinates(
			RandomAccessibleInterval<T> source, final T threshold) {

		ArrayList<Pair<RealLocalizable, T>> coordinatelist = new ArrayList<Pair<RealLocalizable, T>>();

		Interval interval = Intervals.expand(source, -1);
		int ndims = source.numDimensions();
		if (ndims > 3)
			IJ.error("Only three dimensional Ellipsoids are supported");

		source = Views.interval(source, interval);

		final Cursor<T> center = Views.iterable(source).localizingCursor();

		while(center.hasNext()) {
			
			final T centerValue = center.next();
			double[] posf = new double[ndims];
			center.localize(posf);
			final RealPoint rpos = new RealPoint(posf);
			if (centerValue.compareTo(threshold) > 0) 
				coordinatelist.add(new ValuePair<RealLocalizable, T>(rpos, centerValue));
			
		}
		

		return coordinatelist;
	}

	public static ArrayList<RealLocalizable> GetCoordinatesBit(
			RandomAccessibleInterval<FloatType> actualRoiimg) {

		ArrayList<RealLocalizable> coordinatelist = new ArrayList<RealLocalizable>();
		int ndims = actualRoiimg.numDimensions();
		if (ndims > 3)
			IJ.error("Only three dimensional Ellipsoids are supported");


		final Cursor<FloatType> center = Views.iterable(actualRoiimg).localizingCursor();

		

		while(center.hasNext()) {
			
			center.fwd();
			
			double[] posf = new double[ndims];
			center.localize(posf);
			final RealPoint rpos = new RealPoint(posf);
			if(center.get().get() > 0) {
				coordinatelist.add(rpos);
			}
			
		}
		
	
	
		
		return coordinatelist;
	}
	
	
	/**
	 * Sor a list of reallocalizable
	 * 
	 * 
	 */
	
	
}

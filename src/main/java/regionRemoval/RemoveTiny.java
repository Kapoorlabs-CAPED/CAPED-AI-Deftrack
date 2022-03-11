package regionRemoval;

import net.imglib2.Cursor;
import net.imglib2.Interval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.neighborhood.HyperSphereShape;
import net.imglib2.algorithm.neighborhood.Neighborhood;
import net.imglib2.algorithm.neighborhood.RectangleShape;
import net.imglib2.algorithm.region.hypersphere.HyperSphere;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.NativeType;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Intervals;
import net.imglib2.view.Views;

public class  RemoveTiny  {

	
	public static RandomAccessibleInterval<BitType>  removeNbyNregion(RandomAccessibleInterval<BitType> current, int N) {
		
		
		RandomAccessibleInterval<BitType> cleancurrent = new ArrayImgFactory<BitType>().create(current, new BitType());
		RandomAccess<BitType> ranac = cleancurrent.randomAccess();
		 Interval interval = Intervals.expand( current, -N );
		 
	        // create a view on the source with this interval
	        current = Views.interval( current, interval );
	        
	        final Cursor< BitType > center = Views.iterable( current ).cursor();
	        
	        final HyperSphereShape shape = new HyperSphereShape( N );

			int span = 2 * N + 1;
			int maxblackpixel = span * span / 2;
			
			
	        for ( final Neighborhood< BitType > localNeighborhood : shape.neighborhoods( current ) ) {
	        	
	        	int blackcount  = 0;
	        	
	        	final BitType centerValue = center.next();
	        	
	        	ranac.setPosition(center);
	        	for(final BitType value : localNeighborhood)
	        	{
	        		
	        		if (centerValue.compareTo(value) > 0)
	        		   blackcount++;
	        			
	        	}
	        	
	        	if (blackcount >= maxblackpixel)
	        	ranac.get().setZero();
	        	else
	        	ranac.get().set(centerValue);
	        	
	        	
	        }
	        
	        	
	        	
	        	
	
	return cleancurrent;
		
	}
	
}

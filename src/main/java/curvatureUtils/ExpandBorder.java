package curvatureUtils;

import net.imglib2.Cursor;
import net.imglib2.Interval;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;

import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.NativeType;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.RealType;
import net.imglib2.util.Intervals;
import net.imglib2.view.Views;
import pluginTools.InteractiveSimpleEllipseFit;
import varun_algorithm.*;
import varun_algorithm_neighborhood.Neighborhood;
import varun_algorithm_neighborhood.RectangleShape;
import varun_algorithm_region.hypersphere.HyperSphere;
public class ExpandBorder {

	/**
	 * 
	 * This method expands the border pixel of an integer image by certain pixels
	 * 
	 * @param parent
	 * @param source
	 */
	public static  < T extends RealType< T > & NativeType< T >> RandomAccessibleInterval<T> extendBorder(final InteractiveSimpleEllipseFit parent, RandomAccessibleInterval<T> source, int min) {
		
		T type = Views.iterable(source).cursor().next().createVariable();
		RandomAccessibleInterval<T> output =  new ArrayImgFactory<T>().create(source, type);
		
		copy(source, Views.iterable(output));
	
		
		final Cursor< T > center =  Views.iterable( source ).cursor();
		
		  final RectangleShape shape = new RectangleShape( (int)parent.borderpixel, true );
		
		  
	       // so that the search in the 8-neighborhood (3x3x3...x3) never goes outside
	        // of the defined interval
	        Interval interval = Intervals.expand( source, - (int)parent.borderpixel );
	 
	        // create a view on the source with this interval
	        source = Views.interval( source, interval );
	        
		  for ( final Neighborhood< T > localNeighborhood : shape.neighborhoods( source ) ) 
		  {
			  
			  final T centerValue = center.next();
			  
			  if((int)centerValue.getRealDouble() != min) {
			  boolean isBorderPixel = false;
			  
			  
			  for (final T value : localNeighborhood)
			  {
				  
				  if(centerValue.compareTo(value)!=0) {
					  
					  
					  isBorderPixel = true;
					  
					  break;
					  
					  
				  }
				  
				  
			  }
			  
			  if(isBorderPixel) {
				  
				  HyperSphere< T > hypershpere = new HyperSphere< T >(output, center, (int)parent.borderpixel );
				  
				  
				  for (T value : hypershpere)
					  value.set(centerValue);
				  
				  
			  }
			  
			
			  }
			  
		  }
		return output;
		
	}
	
	
	  /**
     * Copy from a source that is just RandomAccessible to an IterableInterval. Latter one defines
     * size and location of the copy operation. It will query the same pixel locations of the
     * IterableInterval in the RandomAccessible. It is up to the developer to ensure that these
     * coordinates match.
     *
     * Note that both, input and output could be Views, Img or anything that implements
     * those interfaces.
     *
     * @param source - a RandomAccess as source that can be infinite
     * @param target - an IterableInterval as target
     */
    public static < T extends Type< T > > void copy( final RandomAccessible< T > source,
        final IterableInterval< T > target )
    {
        // create a cursor that automatically localizes itself on every move
        Cursor< T > targetCursor = target.localizingCursor();
        RandomAccess< T > sourceRandomAccess = source.randomAccess();
 
        // iterate over the input cursor
        while ( targetCursor.hasNext())
        {
            // move input cursor forward
            targetCursor.fwd();
 
            // set the output cursor to the position of the input cursor
            sourceRandomAccess.setPosition( targetCursor );
 
            // set the value of this pixel of the output image, every Type supports T.set( T type )
            targetCursor.get().set( sourceRandomAccess.get() );
        }
    }

	
	
}

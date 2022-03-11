package preProcessing;

import java.util.Iterator;

import net.imglib2.IterableInterval;
import net.imglib2.img.Img;
import net.imglib2.type.Type;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;

public class MinMaxIntensity {

	
	  /**
     * Compute the min and max for any {@link Iterable}, like an {@link Img}.
     *
     * The only functionality we need for that is to iterate. Therefore we need no {@link Cursor}
     * that can localize itself, neither do we need a {@link RandomAccess}. So we simply use the
     * most simple interface in the hierarchy.
     *
     * @param input - the input that has to just be {@link Iterable}
     * @param min - the type that will have min
     * @param max - the type that will have max
     */
    public static < T extends Comparable< T > & Type< T > > Pair<T,T> computeMinMax(
        final IterableInterval< T > input)
    {
        // create a cursor for the image (the order does not matter)
        final Iterator< T > iterator = input.iterator();
        // create two empty variables
        T min = input.firstElement().createVariable();
        T max = input.firstElement().createVariable();
        // initialize min and max with the first image value
        T type = iterator.next();
 
        min.set( type );
        max.set( type );
 
        // loop over the rest of the data and determine min and max value
        while ( iterator.hasNext() )
        {
            // we need this type more than once
            type = iterator.next();
 
            if ( type.compareTo( min ) < 0 )
                min.set( type );
 
            if ( type.compareTo( max ) > 0 )
                max.set( type );
        }
        
        return new ValuePair<T,T>(min, max);
    }
	
  
	
}

package preProcessing;

import net.imglib2.Cursor;
import net.imglib2.FinalInterval;
import net.imglib2.Interval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.neighborhood.Neighborhood;
import net.imglib2.algorithm.neighborhood.RectangleShape;
import net.imglib2.algorithm.region.hypersphere.HyperSphere;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.NativeType;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Intervals;
import net.imglib2.view.Views;

public class GenericFilters {

	
	/**
	 * 
	 * Gets the boundary between different labels and make a binary image of the current label
	 * 
	 * @param inputimg
	 * @param label
	 * @return
	 */
	public static RandomAccessibleInterval<BitType> BinaryBoundaryImage(RandomAccessibleInterval<BitType> inputimg){
		
		RandomAccessibleInterval<BitType> gradientimg = new ArrayImgFactory<BitType>().create(inputimg,
				new BitType());
		RandomAccess<BitType> gradran = gradientimg.randomAccess();
		Interval interval = Intervals.expand( inputimg, -1 );
		 final Cursor< BitType > center = Views.iterable( inputimg ).cursor();
		 final RectangleShape shape = new RectangleShape( 1, true );
		 inputimg = Views.interval( inputimg, interval );
		 for ( final Neighborhood< BitType > localNeighborhood : shape.neighborhoods( inputimg ) )
	        {
	            final BitType centerValue = center.next();
	 
	            boolean isSimilar = true;
	 
	            for ( final BitType value : localNeighborhood )
	            {
	                if ( centerValue.compareTo( value ) < 0 )
	                {
	                    isSimilar = false;
	                    break;
	                }
	            }
	 
	            if ( isSimilar )
	            {
	            	
	            	gradran.setPosition(center);
	            	gradran.get().set(centerValue);
	            }
	        }
		
		
		 
		 
		return gradientimg;
		
	}
	

	public static RandomAccessibleInterval<BitType> CurrentLabelImage(RandomAccessibleInterval<IntType> Intimg, int currentLabel) {
		int n = Intimg.numDimensions();
		long[] position = new long[n];

		Cursor<IntType> intCursor = Views.iterable(Intimg).cursor();

		RandomAccessibleInterval<BitType> outimg = new ArrayImgFactory<BitType>().create(Intimg, new BitType());

		RandomAccess<BitType> imageRA = outimg.randomAccess();

		// Go through the whole image and add every pixel, that belongs to
		// the currently processed label
		long[] minVal = { Intimg.max(0), Intimg.max(1) };
		long[] maxVal = { Intimg.min(0), Intimg.min(1) };

		while (intCursor.hasNext()) {
			intCursor.fwd();
			imageRA.setPosition(intCursor);
			int i = intCursor.get().get();
			if (i == currentLabel) {
				intCursor.localize(position);
				for (int d = 0; d < n; ++d) {
					if (position[d] < minVal[d]) {
						minVal[d] = position[d];
					}
					if (position[d] > maxVal[d]) {
						maxVal[d] = position[d];
					}

				}

				imageRA.get().setOne();
			} else
				imageRA.get().setZero();

		}
	

		return outimg;

	}
	
	public static <T extends RealType<T> & NativeType<T>> RandomAccessibleInterval<FloatType> GradientmagnitudeImage(
			RandomAccessibleInterval<T> inputimg) {
		
		
		final T type = inputimg.randomAccess().get().createVariable();
		RandomAccessibleInterval<FloatType> gradientimg = new ArrayImgFactory<FloatType>().create(inputimg,
				new FloatType());
		Cursor<FloatType> cursor = Views.iterable(gradientimg).localizingCursor();
		RandomAccessible<T> view = Views.extendBorder(inputimg);
		RandomAccess<T> randomAccess = view.randomAccess();

		// iterate over all pixels
		while (cursor.hasNext()) {
			// move the cursor to the next pixel
			cursor.fwd();

			// compute gradient and its direction in each dimension
			double gradient = 0;

			for (int d = 0; d < inputimg.numDimensions(); ++d) {
				// set the randomaccess to the location of the cursor
				randomAccess.setPosition(cursor);

				// move one pixel back in dimension d
				randomAccess.bck(d);

				// get the value
				double Back = randomAccess.get().getRealDouble();

				// move twice forward in dimension d, i.e.
				// one pixel above the location of the cursor
				randomAccess.fwd(d);
				randomAccess.fwd(d);

				// get the value
				double Fwd = randomAccess.get().getRealDouble();

				gradient += ((Fwd - Back) * (Fwd - Back)) / 4;

			}

			cursor.get().setReal(Math.sqrt(gradient));

		}

		return gradientimg;
	}
	
	public static <T extends RealType<T> & NativeType<T>> RandomAccessibleInterval<T> AutoThreshold(RandomAccessibleInterval<T> inputimg){
		
		final T type = inputimg.randomAccess().get().createVariable();
		RandomAccessibleInterval<T> Threshimg = new ArrayImgFactory<T>().create(inputimg,
				type);
		//Supress values below the low threshold
		int n = inputimg.numDimensions();
		double[] position = new double[n];
				final double Lowthreshold = GlobalThresholding.AutomaticThresholdingSec(inputimg);
				 double threshold = Lowthreshold;
				Cursor<T> inputcursor = Views.iterable(inputimg).localizingCursor();
				RandomAccess<T> outputran = Threshimg.randomAccess();
				while(inputcursor.hasNext()){
					inputcursor.fwd();
					inputcursor.localize(position);
					outputran.setPosition(inputcursor);
					if (inputcursor.get().getRealDouble()<= threshold)
						outputran.get().setZero();
					else
						outputran.get().set(inputcursor.get());
				}
			return Threshimg;	
				
				
	}
	
	
}

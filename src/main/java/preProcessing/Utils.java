package preProcessing;



import java.awt.Rectangle;
import java.util.ArrayList;

import javax.swing.JProgressBar;

import fiji.util.DistanceComparator;
import ij.gui.EllipseRoi;
import net.imglib2.Cursor;
import net.imglib2.FinalInterval;
import net.imglib2.IterableInterval;
import net.imglib2.Point;
import net.imglib2.PointSampleList;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealPoint;
import net.imglib2.algorithm.stats.Normalize;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.NativeType;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;
import net.imglib2.view.Views;
import preProcessing.GetLocalmaxminMT;

public class Utils {

	
	
	
	public static <T extends RealType<T> & NativeType<T>>RandomAccessibleInterval<T> FlatFieldMedian(RandomAccessibleInterval<T> originalimg,
			final double[] flatfieldradius) {


		final FlatFieldCorrection<T> flatfilter = new FlatFieldCorrection<T>(originalimg, 1, flatfieldradius);
		flatfilter.process();
		RandomAccessibleInterval<T> ProgramPreprocessedimg = flatfilter.getResult();
		return ProgramPreprocessedimg;
				
			}
	public static <T extends RealType<T> & NativeType<T>>  RandomAccessibleInterval<T> FlatFieldOnly(RandomAccessibleInterval<T> originalimg, final int flatfieldradius) {


		final FlatFieldOnly<T> flatfilter = new FlatFieldOnly<T>(originalimg, flatfieldradius);
		flatfilter.process();
		RandomAccessibleInterval<T> ProgramPreprocessedimg = flatfilter.getResult();
		return ProgramPreprocessedimg;
				
			}
	
	public static RandomAccessibleInterval<FloatType> MedianOnly(RandomAccessibleInterval<FloatType> originalimg, final int radius) {


		final MedianFilterOnly flatfilter = new MedianFilterOnly(originalimg, radius);
		flatfilter.process();
		RandomAccessibleInterval<FloatType> ProgramPreprocessedimg = flatfilter.getResult();
		return ProgramPreprocessedimg;
				
			}
	
	
	public static<T extends RealType<T> & NativeType<T>> RandomAccessibleInterval<T> Preprocess(RandomAccessibleInterval<T> originalimg, final double[] psf) {


		final FlatFieldCorrection<T> flatfilter = new FlatFieldCorrection<T>(originalimg, 1, psf);
		flatfilter.process();
		RandomAccessibleInterval<T> ProgramPreprocessedimg = flatfilter.getResult();
		return ProgramPreprocessedimg;
				
			}
	public static<T extends RealType<T> & NativeType<T>> Img< T > copyImage(final RandomAccessibleInterval< T > input) {
		// create a new Image with the same dimensions but the other imgFactory
		// note that the input provides the size for the new image by
		// implementing the Interval interface
		Img<T> output = new ArrayImgFactory<T>().create(input, Views.iterable(input).firstElement());

		// create a cursor that automatically localizes itself on every move
		Cursor<T> cursorInput = Views.iterable(input).localizingCursor();
		RandomAccess<T> randomAccess = output.randomAccess();

		// iterate over the input cursor
		while (cursorInput.hasNext()) {
			// move input cursor forward
			cursorInput.fwd();

			// set the output cursor to the position of the input cursor
			randomAccess.setPosition(cursorInput);

			// set the value of this pixel of the output image, every Type
			// supports T.set( T type )
			randomAccess.get().set(cursorInput.get());
		}

		// return the copy
		return output;
	}
	
	
	
	/**
	 * Generic, type-agnostic method to create an identical copy of an Img
	 *
	 * @param currentPreprocessedimg2
	 *            - the Img to copy
	 * @return - the copy of the Img
	 */
	public static Img<UnsignedByteType> copytoByteImage(final RandomAccessibleInterval<FloatType> input, final Rectangle standardRectangle) {
		// create a new Image with the same properties
		// note that the input provides the size for the new image as it
		// implements
		// the Interval interface
		final RandomAccessibleInterval<FloatType> inputcopy = copyImage(input);
		Normalize.normalize(Views.iterable(inputcopy), new FloatType(0), new FloatType(255));
		final UnsignedByteType type = new UnsignedByteType();
		final ImgFactory<UnsignedByteType> factory = net.imglib2.util.Util.getArrayOrCellImgFactory(input, type);
		final Img<UnsignedByteType> output = factory.create(input, type);
		// create a cursor for both images
		RandomAccess<FloatType> ranac = inputcopy.randomAccess();
		Cursor<UnsignedByteType> cursorOutput = output.cursor();

		// iterate over the input
		while (cursorOutput.hasNext()) {
			// move both cursors forward by one pixel
			cursorOutput.fwd();

			int x = cursorOutput.getIntPosition(0);
			int y = cursorOutput.getIntPosition(1);

			if (standardRectangle.contains(x, y)) {

				ranac.setPosition(cursorOutput);

				// set the value of this pixel of the output image to the same
				// as
				// the input,
				// every Type supports T.set( T type )
				cursorOutput.get().set((int) Math.round(ranac.get().getRealFloat()));
			}
		}

		// return the copy
		return output;
	}
	
	
	
	public static Img<UnsignedByteType> copytoByteImage(final RandomAccessibleInterval<FloatType> input, final FinalInterval standardInterval) {
		// create a new Image with the same properties
		// note that the input provides the size for the new image as it
		// implements
		// the Interval interface
		RandomAccessibleInterval<FloatType> inputcopy = copyImage(input);
		Normalize.normalize(Views.iterable(inputcopy), new FloatType(0), new FloatType(255));
		
		inputcopy = Views.interval(input, standardInterval);
		final UnsignedByteType type = new UnsignedByteType();
		final ImgFactory<UnsignedByteType> factory = net.imglib2.util.Util.getArrayOrCellImgFactory(input, type);
		final Img<UnsignedByteType> output = factory.create(input, type);
		// create a cursor for both images
		RandomAccess<FloatType> ranac = inputcopy.randomAccess();
		
		
		Cursor<UnsignedByteType> cursorOutput = output.cursor();

		// iterate over the input
		while (cursorOutput.hasNext()) {
			// move both cursors forward by one pixel
			cursorOutput.fwd();

		


				ranac.setPosition(cursorOutput);

				// set the value of this pixel of the output image to the same
				// as
				// the input,
				// every Type supports T.set( T type )
				cursorOutput.get().set((int) ranac.get().get());
		}

		// return the copy
		return output;
	}
	
	public static Img<UnsignedByteType> copytoByteImage(final RandomAccessibleInterval<FloatType> input, final RandomAccessibleInterval<IntType> intimg,
			final Rectangle standardRectangle, int label) {
		// create a new Image with the same properties
		// note that the input provides the size for the new image as it
		// implements
		// the Interval interface
		RandomAccess<IntType> intran = intimg.randomAccess();
		final RandomAccessibleInterval<FloatType> inputcopy = copyImage(input);
		Normalize.normalize(Views.iterable(inputcopy), new FloatType(0), new FloatType(255));
		final UnsignedByteType type = new UnsignedByteType();
		final ImgFactory<UnsignedByteType> factory = net.imglib2.util.Util.getArrayOrCellImgFactory(input, type);
		final Img<UnsignedByteType> output = factory.create(input, type);
		// create a cursor for both images
		RandomAccess<FloatType> ranac = inputcopy.randomAccess();
		Cursor<UnsignedByteType> cursorOutput = output.cursor();

		// iterate over the input
		while (cursorOutput.hasNext()) {
			// move both cursors forward by one pixel
			cursorOutput.fwd();

			int x = cursorOutput.getIntPosition(0);
			int y = cursorOutput.getIntPosition(1);

			if (standardRectangle.contains(x, y)) {

				intran.setPosition(cursorOutput);
				if(intran.get().get() == label){
				ranac.setPosition(cursorOutput);

				// set the value of this pixel of the output image to the same
				// asintfra
				// the input,
				// every Type supports T.set( T type )
				cursorOutput.get().set((int) ranac.get().get());
				}
			}
		}

		// return the copy
		return output;
	}
	
	
	
	public static  double[] Transformback(double[] location, double[] size, double[] min,
			double[] max) {

		int n = location.length;

		double[] delta = new double[n];

		final double[] realpos = new double[n];

		for (int d = 0; d < n; ++d){
			
			delta[d] = (max[d] - min[d]) / size[d];
		    
			realpos[d] = (location[d] - min[d]) / delta[d];
		}
		return realpos;

	}
	
	
	public static <T extends RealType<T> & NativeType<T>>  RandomAccessibleInterval<T> extractImage(final RandomAccessibleInterval<T> intervalView, final FinalInterval interval) {

		return intervalView;
	}
	
	
	public static<T extends RealType<T> & NativeType<T>>  RandomAccessibleInterval<T> oldextractImage(final RandomAccessibleInterval<T> intervalView, final FinalInterval interval) {

		final T type = intervalView.randomAccess().get().createVariable();
		final ImgFactory<T> factory = net.imglib2.util.Util.getArrayOrCellImgFactory(intervalView, type);
		RandomAccessibleInterval<T> totalimg = factory.create(intervalView, type);
		final RandomAccessibleInterval<T> img = Views.interval(intervalView, interval);

		double[] newmin = Transformback(new double[] { img.min(0), img.min(1) },
				new double[] { totalimg.dimension(0), totalimg.dimension(1) }, new double[] { img.min(0), img.min(1) },
				new double[] { img.max(0), img.max(1) });

		double[] newmax = Transformback(new double[] { img.max(0), img.max(1) },
				new double[] { totalimg.dimension(0), totalimg.dimension(1) },
				new double[] { totalimg.min(0), totalimg.min(1) }, new double[] { totalimg.max(0), totalimg.max(1) });
		long[] newminlong = new long[] { Math.round(newmin[0]), Math.round(newmin[1]) };
		long[] newmaxlong = new long[] { Math.round(newmax[0]), Math.round(newmax[1]) };

		RandomAccessibleInterval<T> outimg = factory.create(new FinalInterval(newminlong, newmaxlong), type);
		RandomAccess<T> ranac = outimg.randomAccess();
		final Cursor<T> cursor = Views.iterable(img).localizingCursor();

		while (cursor.hasNext()) {

			cursor.fwd();

			double[] newlocation = Transformback(
					new double[] { cursor.getDoublePosition(0), cursor.getDoublePosition(1) },
					new double[] { totalimg.dimension(0), totalimg.dimension(1) },
					new double[] { totalimg.min(0), totalimg.min(1) },
					new double[] { totalimg.max(0), totalimg.max(1) });
			long[] newlocationlong = new long[] { Math.round(newlocation[0]), Math.round(newlocation[1]) };
			ranac.setPosition(newlocationlong);
			ranac.get().set(cursor.get());

		}

		return intervalView;
	}
	
	
	

	
	public static <T extends RealType<T> & NativeType<T>>  RandomAccessibleInterval<T> getCurrentView(RandomAccessibleInterval<T> originalimg, int thirdDimension, int thirdDimensionSize) {

		final T type = originalimg.randomAccess().get().createVariable();
		long[] dim = { originalimg.dimension(0), originalimg.dimension(1) };
		final ImgFactory<T> factory = net.imglib2.util.Util.getArrayOrCellImgFactory(originalimg, type);
		RandomAccessibleInterval<T> totalimg = factory.create(dim, type);

		if (thirdDimensionSize == 0) {

			totalimg = originalimg;
		}

		if (thirdDimensionSize > 0) {

			totalimg = Views.hyperSlice(originalimg, 2, thirdDimension - 1);

		}
		
		return totalimg;

	}
	public static RandomAccessibleInterval<UnsignedByteType> copytoByteImageBit(RandomAccessibleInterval<BitType> input,
			RandomAccessibleInterval<IntType> intimg, Rectangle standardRectangle, int label) {
		// create a new Image with the same properties
				// note that the input provides the size for the new image as it
				// implements
				// the Interval interface
				RandomAccessibleInterval<BitType> inputcopy = copyImage(input);
				
				final UnsignedByteType type = new UnsignedByteType();
				final ImgFactory<UnsignedByteType> factory = net.imglib2.util.Util.getArrayOrCellImgFactory(input, type);
				final Img<UnsignedByteType> output = factory.create(input, type);
				// create a cursor for both images
				RandomAccess<BitType> ranac = inputcopy.randomAccess();
				
				
				Cursor<UnsignedByteType> cursorOutput = output.cursor();

				// iterate over the input
				while (cursorOutput.hasNext()) {
					// move both cursors forward by one pixel
					cursorOutput.fwd();


						ranac.setPosition(cursorOutput);

						// set the value of this pixel of the output image to the same
						// as
						// the input,
						// every Type supports T.set( T type )
						
						if (ranac.get().get())
							cursorOutput.get().set(255);
						else
							cursorOutput.get().set(0);
						
				}

				// return the copy
				return output;
	}

	final public static< T extends RealType< T > & NativeType< T >>  void addGaussian( final RandomAccessibleInterval< T > image, final double[] location, final double[] sigma)
	{
	final int numDimensions = image.numDimensions();
	final int[] size = new int[ numDimensions ];

	final long[] min = new long[ numDimensions ];
	final long[] max = new long[ numDimensions ];

	final double[] two_sq_sigma = new double[ numDimensions ];

	for ( int d = 0; d < numDimensions; ++d )
	{
	size[ d ] = 2 * getSuggestedKernelDiameter( sigma[ d ] );
	min[ d ] = (int)Math.round( location[ d ] ) - size[ d ] / 2;
	max[ d ] = min[ d ] + size[ d ] - 1;
	two_sq_sigma[ d ] =  sigma[ d ] * sigma[ d ];
	}

	final RandomAccessible< T > infinite = Views.extendZero( image );
	final RandomAccessibleInterval< T > interval = Views.interval( infinite, min, max );
	final IterableInterval< T > iterable = Views.iterable( interval );
	final Cursor< T > cursor = iterable.localizingCursor();
	
	
	
	while ( cursor.hasNext() )
	{
	cursor.fwd();

	double value = 1;

	for ( int d = 0; d < numDimensions; ++d )
	{
	final double x = location[ d ] - cursor.getDoublePosition( d );
	value *= Math.exp( -(x * x) / two_sq_sigma[ d ] );
	
	
	}
	
	
	cursor.get().setReal( cursor.get().getPowerFloat() + (float)value );
	
	
	}
	
	
	
	
	}
	
	
	
	
	
	
	final public static void addGaussian( final IterableInterval< FloatType > image, final double Amplitude,
			final double[] location, final double[] sigma)
	{
	final int numDimensions = image.numDimensions();
	final int[] size = new int[ numDimensions ];

	final long[] min = new long[ numDimensions ];
	final long[] max = new long[ numDimensions ];


	for ( int d = 0; d < numDimensions; ++d )
	{
	size[ d ] = getSuggestedKernelDiameter( sigma[ d ] ) * 2;
	min[ d ] = (int)Math.round( location[ d ] ) - size[ d ]/2;
	max[ d ] = min[ d ] + size[ d ] - 1;
	
	}

	
	final Cursor< FloatType > cursor = image.localizingCursor();
	while ( cursor.hasNext() )
	{
	cursor.fwd();

	double value = Amplitude;

	for ( int d = 0; d < numDimensions; ++d )
	{
	final double x = location[ d ] - cursor.getIntPosition( d );
	value *= Math.exp( -(x * x) / (sigma[ d ] * sigma[ d ] ) );
	}
	
	
	cursor.get().set( cursor.get().get() + (float)value );
	
	
	}
	
	}

	public static int getSuggestedKernelDiameter( final double sigma )
	{
	int size = 0;
    int cutoff = 5; // This number means cutoff is chosen to be cutoff times sigma. 
    if ( sigma > 0 )
	size = Math.max( cutoff, ( 2 * ( int ) ( cutoff * sigma + 0.5 ) + 1 ) );

	return size;
	}
	
	
	
}

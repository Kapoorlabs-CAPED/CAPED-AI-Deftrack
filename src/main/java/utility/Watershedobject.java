package utility;

import net.imglib2.Cursor;
import net.imglib2.FinalInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.RealSum;
import net.imglib2.util.Util;
import net.imglib2.view.Views;
import preProcessing.GenericFilters;
import preProcessing.Kernels;
import varun_algorithm_stats.Normalize;

public class Watershedobject {

	public final RandomAccessibleInterval<FloatType> source;
	public final double meanIntensity;
	public final double Size;

	public Watershedobject(final RandomAccessibleInterval<FloatType> source, final double meanIntensity,
			final double Size) {

		this.source = source;
		this.meanIntensity = meanIntensity;
		this.Size = Size;

	}

	public static Watershedobject CurrentLabelImage(RandomAccessibleInterval<IntType> Intimg, int currentLabel) {
		int n = Intimg.numDimensions();
		long[] position = new long[n];

		Cursor<IntType> intCursor = Views.iterable(Intimg).cursor();

		RandomAccessibleInterval<FloatType> outimg = new ArrayImgFactory<FloatType>().create(Intimg, new FloatType());

		RandomAccess<FloatType> imageRA = outimg.randomAccess();

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
		FinalInterval intervalsmall = new FinalInterval(minVal, maxVal);

		RandomAccessibleInterval<FloatType> outimgsmall = extractImage(outimg, intervalsmall);
		double meanIntensity = computeAverage(Views.iterable(outimgsmall));
		double size = (intervalsmall.max(0) - intervalsmall.min(0)) * (intervalsmall.max(1) - intervalsmall.min(1));
		Watershedobject currentobject = new Watershedobject(outimgsmall, meanIntensity, size);

		return currentobject;

	}

	public static RandomAccessibleInterval<IntType> OnlyCurrentLabel(RandomAccessibleInterval<IntType> Intimg, int currentLabel){
		
		RandomAccessibleInterval<IntType> returnimg = new ArrayImgFactory<IntType>().create(Intimg, new IntType());
		
		RandomAccess<IntType> intranac = returnimg.randomAccess();
		
		Cursor<IntType> intcursor = Views.iterable(Intimg).localizingCursor();
		
		while(intcursor.hasNext()) {
			
			intcursor.fwd();
			intranac.setPosition(intcursor);
			int i = intcursor.get().get();
			if (i == currentLabel) {
				
				intranac.get().set(i);
				
				
			}
			
			
		}
		
		return returnimg;
	}
	
	
	public static Watershedobject CurrentLabelBinaryImage(RandomAccessibleInterval<IntType> Intimg, int currentLabel) {
		int n = Intimg.numDimensions();
		long[] position = new long[n];
		Cursor<IntType> intCursor = Views.iterable(Intimg).cursor();
        
		RandomAccessibleInterval<FloatType> outimg = new ArrayImgFactory<FloatType>().create(Intimg, new FloatType());
		RandomAccess<FloatType> imageRA = outimg.randomAccess();
		RandomAccessibleInterval<FloatType> currentimg = GenericFilters.GradientmagnitudeImage(Intimg);
	
		RandomAccess<FloatType> inputRA = currentimg.randomAccess();

		// Go through the whole image and add every pixel, that belongs to
		// the currently processed label
		long[] minVal = { Intimg.max(0), Intimg.max(1) };
		long[] maxVal = { Intimg.min(0), Intimg.min(1) };

		while (intCursor.hasNext()) {
			intCursor.fwd();
			inputRA.setPosition(intCursor);
			imageRA.setPosition(inputRA);
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

				if(inputRA.get().get() > 0)
				imageRA.get().set(1);
				else
					imageRA.get().set(0);	
			} else
				imageRA.get().setZero();

		}
		FinalInterval intervalsmall = new FinalInterval(minVal, maxVal);

		double meanIntensity = computeAverage(Views.iterable(outimg));
		double size = (intervalsmall.max(0) - intervalsmall.min(0)) * (intervalsmall.max(1) - intervalsmall.min(1));
		Watershedobject currentobject = new Watershedobject(outimg, meanIntensity, size);

		return currentobject;

	}

	/**
	 * Compute the average intensity for an {@link Iterable}.
	 *
	 * @param input
	 *            - the input data
	 * @return - the average as double
	 */
	public static <T extends RealType<T>> double computeAverage(final Iterable<T> input) {
		// Count all values using the RealSum class.
		// It prevents numerical instabilities when adding up millions of pixels
		final RealSum realSum = new RealSum();
		long count = 0;
        double meanIntensity = 0;
		for (final T type : input) {
			realSum.add(type.getRealDouble());
			++count;
		}
		if(count > 0)
			meanIntensity = realSum.getSum() / count;

		return meanIntensity;
	}

	public static RandomAccessibleInterval<FloatType> extractImage(final RandomAccessibleInterval<FloatType> outimg,
			final FinalInterval interval) {

		return outimg;
	}

}

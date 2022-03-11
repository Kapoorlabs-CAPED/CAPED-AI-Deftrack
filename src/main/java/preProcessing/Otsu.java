package preProcessing;

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.Point;
import net.imglib2.PointSampleList;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.stats.Normalize;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Pair;
import net.imglib2.util.RealSum;
import net.imglib2.util.ValuePair;
import net.imglib2.view.Views;

public class Otsu {

	
	
	
	
	public static RandomAccessibleInterval<FloatType> convertBittoFloat(final Img<BitType> bitinputimg){
		
		
		final FloatType type = new FloatType();
		final ImgFactory<FloatType> factory = net.imglib2.util.Util.getArrayOrCellImgFactory(bitinputimg, type);
		final RandomAccessibleInterval<FloatType> output = factory.create(bitinputimg, type);
		
		Cursor<BitType> cursor = bitinputimg.localizingCursor();
		
		RandomAccess<FloatType> ranac = output.randomAccess();
		
		while(cursor.hasNext()) {
			
			cursor.fwd();
			
			ranac.setPosition(cursor);
			
			ranac.get().setReal(cursor.get().getRealDouble());
			
			
		}
		
		return output;
		
	}
	
	
	
	public static RandomAccessibleInterval<FloatType> Getlabelledimage(final RandomAccessibleInterval<FloatType> inputimg, final int label ){
		
		final FloatType type = new FloatType();
		final ImgFactory<FloatType> factory = net.imglib2.util.Util.getArrayOrCellImgFactory(inputimg, type);
		final Img<FloatType> output = factory.create(inputimg, type);

		Cursor<FloatType> cursor = Views.iterable(inputimg).localizingCursor();
		
		RandomAccess<FloatType> ranac = output.randomAccess();
		
		while(cursor.hasNext()) {
			
			cursor.fwd();
			
			ranac.setPosition(cursor);
			
			if (cursor.get().get() == label)
				ranac.get().setOne();
			else
				ranac.get().setZero();
				
			
			
			
		}
		
		
		
		
		return output;
		
		
	}
	
	public static Img<BitType> Getbinaryimage(final RandomAccessibleInterval<FloatType> inputimg, final float threshold){
		
		
		final BitType type = new BitType();
		final ImgFactory<BitType> factory = net.imglib2.util.Util.getArrayOrCellImgFactory(inputimg, type);
		final Img<BitType> output = factory.create(inputimg, type);
		
		Cursor<FloatType> cursor = Views.iterable(inputimg).localizingCursor();
		
		RandomAccess<BitType> ranac = output.randomAccess();
		
		while(cursor.hasNext()) {
			
			cursor.fwd();
			
			ranac.setPosition(cursor);
			
			if (cursor.get().get() > threshold)
				ranac.get().setOne();
			else
				ranac.get().setZero();
				
			
			
			
		}
		
		
		
		
		return output;
	}
	
	
	
	// Automatic thresholding done on the Normalized input image
		// Algorithm: Get max and min intensity for an image and choose an initial
		// threshold value, T = (max-min)/2. This threshold value
		// segments image into two regions, get the mean pixel value for both the
		// regions (x1, x2)
		// then set the new threshold T_N = (x1 +x2)/2, segment initial image by
		// this value and repeat the process
		// till (T_N - T_{N+1}<epsilon) where epsilon is a small number say 1.0E-3
		public static Float AutomaticThresholding(RandomAccessibleInterval<FloatType> inputimg) {
			
			FloatType min = new FloatType();
			FloatType max = new FloatType();

			Float ThresholdNew, Thresholdupdate;

			Pair<FloatType, FloatType> pair = new ValuePair<FloatType, FloatType>(min, max);
			pair = computeMinMaxIntensity(inputimg);

			ThresholdNew = (pair.getB().get() - pair.getA().get()) / 2;

			// Get the new threshold value after segmenting the inputimage with thresholdnew
			Thresholdupdate = SegmentbyThresholding(Views.iterable(inputimg), ThresholdNew);

			while (true) {

				ThresholdNew = SegmentbyThresholding(Views.iterable(inputimg), Thresholdupdate);
				// Check if the new threshold value is close to the previous value
				if (Math.abs(Thresholdupdate - ThresholdNew) < 1.0E-2)
					break;
				Thresholdupdate = ThresholdNew;
			}
			

			return ThresholdNew;

		}

		
	public static Float AutomaticThresholdingSec(RandomAccessibleInterval<FloatType> inputimg) {
			
			FloatType min = new FloatType();
			FloatType max = new FloatType();

			Float ThresholdNew, Thresholdupdate;

			Pair<FloatType, FloatType> pair = new ValuePair<FloatType, FloatType>(min, max);
			pair = computesecondMinMaxIntensity(inputimg);

			ThresholdNew = (pair.getB().get() - pair.getA().get()) / 2;

			// Get the new threshold value after segmenting the inputimage with thresholdnew
			Thresholdupdate = SegmentbyThresholding(Views.iterable(inputimg), ThresholdNew);

			while (true) {

				ThresholdNew = SegmentbyThresholding(Views.iterable(inputimg), Thresholdupdate);

				// Check if the new threshold value is close to the previous value
				if (Math.abs(Thresholdupdate - ThresholdNew) < 1.0E-2)
					break;
				Thresholdupdate = ThresholdNew;
			}
			

			return ThresholdNew;

		}

		// Segment image by thresholding, used to determine automatic thresholding
		// level
		public static Float SegmentbyThresholding(IterableInterval<FloatType> inputimg, Float Threshold) {

			int n = inputimg.numDimensions();
			Float ThresholdNew;
			PointSampleList<FloatType> listA = new PointSampleList<FloatType>(n);
			PointSampleList<FloatType> listB = new PointSampleList<FloatType>(n);
			Cursor<FloatType> cursor = inputimg.localizingCursor();
			while (cursor.hasNext()) {
				cursor.fwd();

				if (cursor.get().get() >= 0 && cursor.get().get() < Threshold) {
					Point newpointA = new Point(n);
					newpointA.setPosition(cursor);
					listA.add(newpointA, cursor.get().copy());
				} else if (cursor.get().get() >= 0 && cursor.get().get() >= Threshold )  {
					Point newpointB = new Point(n);
					newpointB.setPosition(cursor);
					listB.add(newpointB, cursor.get().copy());
				}
			}
			final RealSum realSumA = new RealSum();
			long countA = 0;

			for (final FloatType type : listA) {
				realSumA.add(type.getRealDouble());
				++countA;
			}

			final double sumA = realSumA.getSum() / countA;

			final RealSum realSumB = new RealSum();
			long countB = 0;

			for (final FloatType type : listB) {
				realSumB.add(type.getRealDouble());
				++countB;
			}

			final double sumB = realSumB.getSum() / countB;

			ThresholdNew = (float) (sumA + sumB) / 2;

			return ThresholdNew;

		}
		public static void InvertInensityMap(RandomAccessibleInterval<FloatType> inputimg, FloatType minval, FloatType maxval){
	        // Normalize the input image
	 		Normalize.normalize(Views.iterable(inputimg), minval, maxval);
	 		// Now invert the normalization scale to get intensity inversion
			Normalize.normalize(Views.iterable(inputimg), maxval, minval);
		}
		public static Pair<FloatType, FloatType> computeMinMaxIntensity(final RandomAccessibleInterval<FloatType> inputimg) {
			// create a cursor for the image (the order does not matter)
			final Cursor<FloatType> cursor = Views.iterable(inputimg).cursor();

			// initialize min and max with the first image value
			FloatType type = cursor.next();
			FloatType min = type.copy();
			FloatType max = type.copy();

			// loop over the rest of the data and determine min and max value
			while (cursor.hasNext()) {
				// we need this type more than once
				type = cursor.next();

				if (type.compareTo(min) < 0) {
					min.set(type);

				}

				if (type.compareTo(max) > 0) {
					max.set(type);

				}
			}
			Pair<FloatType, FloatType> pair = new ValuePair<FloatType, FloatType>(min, max);
			return pair;
		}
		public static Pair<FloatType, FloatType> computesecondMinMaxIntensity(final RandomAccessibleInterval<FloatType> inputimg) {
			// create a cursor for the image (the order does not matter)
			final Cursor<FloatType> cursor = Views.iterable(inputimg).cursor();

			// initialize min and max with the first image value
			FloatType type = cursor.next();
			FloatType min = type.copy();
			FloatType secondmin = type.copy();
			FloatType max = type.copy();

			// loop over the rest of the data and determine min and max value
			while (cursor.hasNext()) {
				// we need this type more than once
				type = cursor.next();

				if (type.compareTo(min) < 0) {
					min.set(type);

				}
				if (type.compareTo(secondmin) < 0 && secondmin.compareTo(min)>0) {
					secondmin.set(type);

				}
				
				if (type.compareTo(max) > 0) {
					max.set(type);

				}
			}
			Pair<FloatType, FloatType> pair = new ValuePair<FloatType, FloatType>(secondmin, max);
			return pair;
		}
	
	
}

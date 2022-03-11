/*-
 * #%L
 * Microtubule tracker.
 * %%
 * Copyright (C) 2017 MTrack developers.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */
package preProcessing;



import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.Point;
import net.imglib2.PointSampleList;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.stats.Normalize;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Pair;
import net.imglib2.util.RealSum;
import net.imglib2.util.ValuePair;
import net.imglib2.view.Views;

public class GlobalThresholding {

	// Automatic thresholding done on the Normalized input image
	// Algorithm: Get max and min intensity for an image and choose an initial
	// threshold value, T = (max-min)/2. This threshold value
	// segments image into two regions, get the mean pixel value for both the
	// regions (x1, x2)
	// then set the new threshold T_N = (x1 +x2)/2, segment initial image by
	// this value and repeat the process
	// till (T_N - T_{N+1}<epsilon) where epsilon is a small number say 1.0E-3
	public static< T extends RealType< T > & NativeType< T >>  Float AutomaticThresholding(RandomAccessibleInterval<T> inputimg) {
		
		
		final T type = inputimg.randomAccess().get().createVariable();
		T min = type;
		T max = type;

		Float ThresholdNew, Thresholdupdate;

		Pair<T, T> pair = new ValuePair<T, T>(min, max);
		pair = GetLocalmaxminMT.computeMinMaxIntensity(inputimg);

		ThresholdNew = (float) ((pair.getB().getRealDouble() - pair.getA().getRealDouble()) / 2);

		// Get the new threshold value after segmenting the inputimage with thresholdnew
		Thresholdupdate = (float) SegmentbyThresholding(Views.iterable(inputimg), ThresholdNew);

		while (true) {

			ThresholdNew = (float) SegmentbyThresholding(Views.iterable(inputimg), Thresholdupdate);

			// Check if the new threshold value is close to the previous value
			if (Math.abs(Thresholdupdate - ThresholdNew) < 1.0E-2)
				break;
			Thresholdupdate = ThresholdNew;
		}
		

		return ThresholdNew;

	}

	
public static< T extends RealType< T > & NativeType< T >> double AutomaticThresholdingSec(RandomAccessibleInterval<T> inputimg) {
		
	final T type = inputimg.randomAccess().get().createVariable();
	T min = type;
	T max = type;

		Float ThresholdNew, Thresholdupdate;

		Pair<T, T> pair = new ValuePair<T, T>(min, max);
		pair = GetLocalmaxminMT.computesecondMinMaxIntensity(inputimg);

		ThresholdNew = (float) ((pair.getB().getRealDouble() - pair.getA().getRealDouble()) / 2);

		// Get the new threshold value after segmenting the inputimage with thresholdnew
		Thresholdupdate = (float) SegmentbyThresholding(Views.iterable(inputimg), ThresholdNew);
		while (true) {

			ThresholdNew = (float) SegmentbyThresholding(Views.iterable(inputimg), Thresholdupdate);
			
			// Check if the new threshold value is close to the previous value
			if (Math.abs(Thresholdupdate - ThresholdNew) < 1.0E-2)
				break;
			Thresholdupdate = ThresholdNew;
		}
		

		return ThresholdNew;

	}

	// Segment image by thresholding, used to determine automatic thresholding
	// level
	public static< T extends RealType< T > & NativeType< T >> double SegmentbyThresholding(IterableInterval<T> inputimg, Float Threshold) {

		int n = inputimg.numDimensions();
		double ThresholdNew;
		PointSampleList<T> listA = new PointSampleList<T>(n);
		PointSampleList<T> listB = new PointSampleList<T>(n);
		Cursor<T> cursor = inputimg.localizingCursor();
		while (cursor.hasNext()) {
			cursor.fwd();

			if (cursor.get().getRealDouble() > 0 && cursor.get().getRealDouble() < Threshold) {
				Point newpointA = new Point(n);
				newpointA.setPosition(cursor);
				listA.add(newpointA, cursor.get().copy());
			} else if (cursor.get().getRealDouble() > 0 && cursor.get().getRealDouble() >= Threshold )  {
				Point newpointB = new Point(n);
				newpointB.setPosition(cursor);
				listB.add(newpointB, cursor.get().copy());
			}
		}
		final RealSum realSumA = new RealSum();
		long countA = 1;

		for (final T type : listA) {
			realSumA.add(type.getRealDouble());
			++countA;
		}

		final double sumA = realSumA.getSum() / countA;

		final RealSum realSumB = new RealSum();
		long countB = 1;

		for (final T type : listB) {
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
}

package utility;

import java.util.Iterator;

import ij.ImageJ;

import net.imglib2.Cursor;
import net.imglib2.KDTree;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealPoint;
import net.imglib2.RealPointSampleList;
import net.imglib2.algorithm.labeling.AllConnectedComponents;
import net.imglib2.algorithm.labeling.Watershed;
import net.imglib2.algorithm.stats.Normalize;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.labeling.DefaultROIStrategyFactory;
import net.imglib2.labeling.Labeling;
import net.imglib2.labeling.LabelingROIStrategy;
import net.imglib2.labeling.NativeImgLabeling;
import net.imglib2.neighborsearch.NearestNeighborSearchOnKDTree;
import net.imglib2.roi.labeling.ImgLabeling;
import net.imglib2.type.NativeType;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Util;
import net.imglib2.view.IntervalView;
import net.imglib2.view.Views;

public class IlastikConnectedComponents {

	static RandomAccessibleInterval<IntType> connectedcomponentImage;
	static RandomAccessibleInterval<BitType> bitimg;
	static RandomAccessibleInterval<FloatType> distimg;

	public static <T extends NativeType<T>> RandomAccessibleInterval<IntType> GetLabelledImage(
			RandomAccessibleInterval<FloatType> source) {
		final ImgFactory<FloatType> factory = Util.getArrayOrCellImgFactory(source, new FloatType());
		distimg = factory.create(source, new FloatType());
		DistanceTransformImage(source, distimg);
		// Prepare seed image
		NativeImgLabeling<Integer, IntType> oldseedLabeling = new NativeImgLabeling<Integer, IntType>(
				new ArrayImgFactory<IntType>().create(source, new IntType()));
		oldseedLabeling = PrepareSeedImage(source);

		NativeImgLabeling<Integer, IntType> outputLabeling = new NativeImgLabeling<Integer, IntType>(
				new ArrayImgFactory<IntType>().create(source, new IntType()));

		outputLabeling = GetlabeledImage(source, oldseedLabeling);
		connectedcomponentImage = oldseedLabeling.getStorageImg();
		return connectedcomponentImage;
	}

	public static NativeImgLabeling<Integer, IntType> GetlabeledImage(
			RandomAccessibleInterval<FloatType> inputimg, NativeImgLabeling<Integer, IntType> seedLabeling) {

		int n = inputimg.numDimensions();
		long[] dimensions = new long[n];

		for (int d = 0; d < n; ++d)
			dimensions[d] = inputimg.dimension(d);
		final NativeImgLabeling<Integer, IntType> outputLabeling = new NativeImgLabeling<Integer, IntType>(
				new ArrayImgFactory<IntType>().create(inputimg, new IntType()));

		final Watershed<FloatType, Integer> watershed = new Watershed<FloatType, Integer>();

		watershed.setSeeds(seedLabeling);
		watershed.setIntensityImage(inputimg);
		watershed.setStructuringElement(AllConnectedComponents.getStructuringElement(2));
		watershed.setOutputLabeling(outputLabeling);
		watershed.process();
		DefaultROIStrategyFactory<Integer> deffactory = new DefaultROIStrategyFactory<Integer>();
		LabelingROIStrategy<Integer, Labeling<Integer>> factory = deffactory
				.createLabelingROIStrategy(watershed.getResult());
		outputLabeling.setLabelingCursorStrategy(factory);

		return outputLabeling;

	}

	public static <T extends NativeType<T>> void Dodistance(RandomAccessibleInterval<T> inputimg,
			RandomAccessibleInterval<BitType> bitimg, RandomAccessibleInterval<FloatType> outimg) {
		int n = inputimg.numDimensions();

		// make an empty list
		final RealPointSampleList<BitType> list = new RealPointSampleList<BitType>(n);

		// cursor on the binary image
		final Cursor<BitType> cursor = Views.iterable(bitimg).localizingCursor();
		// for every pixel that is 1, make a new RealPoint at that location
		while (cursor.hasNext())
			if (cursor.next().getInteger() == 1)
				list.add(new RealPoint(cursor), cursor.get());

		// build the KD-Tree from the list of points that == 1
		final KDTree<BitType> tree = new KDTree<BitType>(list);

		// Instantiate a nearest neighbor search on the tree (does not modifiy
		// the tree, just uses it)
		final NearestNeighborSearchOnKDTree<BitType> search = new NearestNeighborSearchOnKDTree<BitType>(tree);

		// randomaccess on the output
		final RandomAccess<FloatType> ranac = outimg.randomAccess();

		// reset cursor for the input (or make a new one)
		cursor.reset();

		// for every pixel of the binary image
		while (cursor.hasNext()) {
			cursor.fwd();

			// set the randomaccess to the same location
			ranac.setPosition(cursor);

			// if value == 0, look for the nearest 1-valued pixel
			if (cursor.get().getInteger() == 0) {
				// search the nearest 1 to the location of the cursor (the
				// current 0)
				search.search(cursor);

				// get the distance (the previous call could return that, this
				// for generality that it is two calls)

				ranac.get().setReal(search.getDistance());

			} else {
				// if value == 1, no need to search
				ranac.get().setZero();
			}
		}

	}

	/***
	 * 
	 * Do the distance transform of the input image using the bit image provided.
	 * 
	 * @param inputimg
	 *            The pre-processed input image as RandomAccessibleInterval <T>
	 * @param outimg
	 *            The distance transormed image having the same dimensions as the
	 *            input image.
	 * @param invtype
	 *            Straight: The intensity value is set to the distance, gives white
	 *            on black background. Inverse: The intensity is set to the negative
	 *            of the distance, gives black on white background.
	 */

	public static <T extends NativeType<T>> void DistanceTransformImage(RandomAccessibleInterval<T> inputimg,
			RandomAccessibleInterval<FloatType> outimg) {
		Dodistance(inputimg, bitimg, outimg);

	}

	public static <T extends NativeType<T>> void DistanceTransformImage(RandomAccessibleInterval<T> inputimg,
			RandomAccessibleInterval<BitType> bitimg, RandomAccessibleInterval<FloatType> outimg) {

		Dodistance(inputimg, bitimg, outimg);
	}

	public static void ProcessSlice(final RandomAccessibleInterval<FloatType> source,
			final RandomAccessibleInterval<BitType> bitimg, final RandomAccessibleInterval<IntType> intimg) {
		final ImgFactory<FloatType> factory = Util.getArrayOrCellImgFactory(source, new FloatType());
		distimg = factory.create(source, new FloatType());
		DistanceTransformImage(source, bitimg, distimg);
		// Prepare seed image
		NativeImgLabeling<Integer, IntType> oldseedLabeling = new NativeImgLabeling<Integer, IntType>(
				new ArrayImgFactory<IntType>().create(source, new IntType()));
		oldseedLabeling = PrepareSeedImage(source, bitimg);

		NativeImgLabeling<Integer, IntType> outputLabeling = new NativeImgLabeling<Integer, IntType>(
				new ArrayImgFactory<IntType>().create(source, new IntType()));

		outputLabeling = GetlabeledImage(distimg, oldseedLabeling);
		connectedcomponentImage = outputLabeling.getStorageImg();

		final Cursor<IntType> cursor = Views.iterable(intimg).localizingCursor();
		final RandomAccess<IntType> ranac = connectedcomponentImage.randomAccess();

		while (cursor.hasNext()) {

			cursor.fwd();

			ranac.setPosition(cursor);

			cursor.get().set(ranac.get());

		}

	}

	public static <T extends NativeType<T>> NativeImgLabeling<Integer, IntType> PrepareSeedImage(
			RandomAccessibleInterval<T> inputimg) {

		// New Labeling type
		final ImgLabeling<Integer, IntType> seedLabeling = new ImgLabeling<Integer, IntType>(
				new ArrayImgFactory<IntType>().create(inputimg, new IntType()));

		// Old Labeling type
		final NativeImgLabeling<Integer, IntType> oldseedLabeling = new NativeImgLabeling<Integer, IntType>(
				new ArrayImgFactory<IntType>().create(inputimg, new IntType()));

		// The label generator for both new and old type
		final Iterator<Integer> labelGenerator = AllConnectedComponents.getIntegerNames(0);

		// Getting unique labelled image (old version)
		AllConnectedComponents.labelAllConnectedComponents(oldseedLabeling, bitimg, labelGenerator,
				AllConnectedComponents.getStructuringElement(inputimg.numDimensions()));
		return oldseedLabeling;
	}

	public static <T extends NativeType<T>> NativeImgLabeling<Integer, IntType> PrepareSeedImage(
			RandomAccessibleInterval<T> inputimg, RandomAccessibleInterval<BitType> bitimg) {

		// New Labeling type
		final ImgLabeling<Integer, IntType> seedLabeling = new ImgLabeling<Integer, IntType>(
				new ArrayImgFactory<IntType>().create(inputimg, new IntType()));

		// Old Labeling type
		final NativeImgLabeling<Integer, IntType> oldseedLabeling = new NativeImgLabeling<Integer, IntType>(
				new ArrayImgFactory<IntType>().create(inputimg, new IntType()));

		// The label generator for both new and old type
		final Iterator<Integer> labelGenerator = AllConnectedComponents.getIntegerNames(0);

		// Getting unique labelled image (old version)
		AllConnectedComponents.labelAllConnectedComponents(oldseedLabeling, bitimg, labelGenerator,
				AllConnectedComponents.getStructuringElement(inputimg.numDimensions()));
		return oldseedLabeling;
	}

	public static RandomAccessibleInterval<BitType> CreateBinaryImage(RandomAccessibleInterval<FloatType> inputimage,
			final FloatType threshold) {

		final ImgFactory<BitType> factory = net.imglib2.util.Util.getArrayOrCellImgFactory(inputimage, new BitType());
		RandomAccessibleInterval<BitType> binaryimage = factory.create(inputimage, new BitType());

		Cursor<FloatType> cursor = Views.iterable(inputimage).localizingCursor();
		RandomAccess<BitType> ranac = binaryimage.randomAccess();

		while (cursor.hasNext()) {

			cursor.fwd();

			ranac.setPosition(cursor);

			if (cursor.get().compareTo(threshold) >= 1)
				ranac.get().setOne();
			else
				ranac.get().setZero();

		}

		return binaryimage;
	}

	

}

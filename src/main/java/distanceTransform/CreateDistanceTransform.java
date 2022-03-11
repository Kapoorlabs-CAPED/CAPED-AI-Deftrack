package distanceTransform;

import java.util.Iterator;

import net.imglib2.Cursor;
import net.imglib2.KDTree;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealPoint;
import net.imglib2.RealPointSampleList;
import net.imglib2.algorithm.BenchmarkAlgorithm;
import net.imglib2.algorithm.OutputAlgorithm;
import net.imglib2.algorithm.labeling.AllConnectedComponents;
import net.imglib2.algorithm.labeling.Watershed;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
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
import net.imglib2.view.Views;


	
	
	public class CreateDistanceTransform <T extends NativeType<T>> extends BenchmarkAlgorithm
	implements OutputAlgorithm <RandomAccessibleInterval<FloatType>> {
		
		private static final String BASE_ERROR_MSG = "[WatershedDistimg] ";
		private final RandomAccessibleInterval<T> source;
		
		private final RandomAccessibleInterval<BitType> bitimg;
		RandomAccessibleInterval<FloatType> distimg;
		/**
		 * Do watershedding after doing distance transformation on the biimg
		 * provided by the user using a user set threshold value.
		 * 
		 * @param source
		 *              The image to be watershedded.
		 * @param bitimg
		 *              The image used to compute distance transform and seeds for watershedding.
		 */
		public CreateDistanceTransform(final RandomAccessibleInterval<T> source, final RandomAccessibleInterval<BitType> bitimg){
			
			this.source = source;
			this.bitimg = bitimg;
		}
		

		
		@Override
		public boolean checkInput() {
			if (source.numDimensions() > 2) {
				errorMessage = BASE_ERROR_MSG + " Can only operate on 1D, 2D, make slices of your stack . Got "
						+ source.numDimensions() + "D.";
				return false;
			}
			return true;
		}

		@Override
		public boolean process() {

			// Perform the distance transform
			final ImgFactory<FloatType> factory = Util.getArrayOrCellImgFactory(source, new FloatType());
			distimg = factory.create(source, new FloatType());

			DistanceTransformImage(source, distimg);
			
			
			return true;
		}

		@Override
		public RandomAccessibleInterval<FloatType> getResult() {
			
			return distimg;
		}
	
		

		/***
		 * 
		 * Do the distance transform of the input image using the bit image
		 * provided.
		 * 
		 * @param inputimg
		 *            The pre-processed input image as RandomAccessibleInterval <T>
		 * @param outimg
		 *            The distance transormed image having the same dimensions as
		 *            the input image.
		 * @param invtype
		 *            Straight: The intensity value is set to the distance, gives
		 *            white on black background. Inverse: The intensity is set to
		 *            the negative of the distance, gives black on white background.
		 */

		private void DistanceTransformImage(RandomAccessibleInterval<T> inputimg,
				RandomAccessibleInterval<FloatType> outimg) {
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

		
	
	
}

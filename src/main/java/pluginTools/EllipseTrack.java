package pluginTools;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import distanceTransform.DistWatershedBinary;
import distanceTransform.WatershedBinary;
import ellipsoidDetector.Intersectionobject;
import ij.IJ;

import javax.swing.JProgressBar;

import curvatureUtils.ExpandBorder;
import net.imglib2.Cursor;
import net.imglib2.KDTree;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealPoint;
import net.imglib2.RealPointSampleList;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.neighborsearch.NearestNeighborSearchOnKDTree;
import net.imglib2.type.Type;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Pair;
import net.imglib2.util.Util;
import net.imglib2.util.ValuePair;
import net.imglib2.view.Views;
import pluginTools.InteractiveSimpleEllipseFit.ValueChange;
import preProcessing.Kernels;
import strategies.ThinningStrategy;
import strategies.ThinningStrategyFactory;
import strategies.ThinningStrategyFactory.Strategy;
import thinning.ThinningOp;

public class EllipseTrack {

	final InteractiveSimpleEllipseFit parent;
	final JProgressBar jpb;
	Pair<Boolean, String> isVisited;

	public EllipseTrack(final InteractiveSimpleEllipseFit parent, final JProgressBar jpb) {

		this.parent = parent;

		this.jpb = jpb;
	}







	public void BlockRepeatCurve(double percent, int z, int t) throws Exception {

		
		
		parent.updatePreview(ValueChange.THIRDDIMmouse);
		percent++;
		if(jpb!=null )
		utility.ProgressBar.SetProgressBar(jpb, 100 * percent / (parent.fourthDimensionSize + 1),
				"Computing Curvature = " + t + "/" + parent.fourthDimensionSize + " Z = " + z + "/"
						+ parent.thirdDimensionSize);

		RandomAccessibleInterval<BitType> CurrentView = utility.Slicer.getCurrentViewBit(parent.empty, z,
				parent.thirdDimensionSize, t, parent.fourthDimensionSize);
		RandomAccessibleInterval<IntType> CurrentViewInt = utility.Slicer.getCurrentViewInt(parent.originalimgsuper, z,
				parent.thirdDimensionSize, t, parent.fourthDimensionSize);
		IntType min = new IntType();
		IntType max = new IntType();
		computeMinMax(Views.iterable(CurrentViewInt), min, max);
		// Neglect the background class label
        int currentLabel = min.get();
	
           RandomAccessibleInterval<IntType> expanededtotalimg = ExpandBorder.extendBorder(parent, CurrentViewInt, currentLabel);

		//RandomAccessibleInterval<BitType> CurrentViewthin = getThin(CurrentView);
		GetPixelList(expanededtotalimg);
		
		Computeinwater compute = new Computeinwater(parent, CurrentView, expanededtotalimg, t, z, (int) percent);
		compute.ParallelRansacCurve();
		
	}
	
	public void TestAuto(int z, int t) {
		
		parent.updatePreview(ValueChange.THIRDDIMmouse);


		RandomAccessibleInterval<BitType> CurrentViewSmooth = utility.Slicer.getCurrentViewBit(parent.emptysmooth, z,
				parent.thirdDimensionSize, t, parent.fourthDimensionSize);

		// Use smoothed image for segmentation and non smooth image for getting the
		// candidate points for fitting ellipses
		RandomAccessibleInterval<IntType> CurrentInt = getSeg(CurrentViewSmooth);
		if (parent.showWater) {

			Watershow(CurrentInt);

		}
		
	}

	
	
	public void BlockRepeatAutoCurve(double percent, int z, int t) throws Exception {

		parent.updatePreview(ValueChange.THIRDDIMmouse);

		percent++;
		if(jpb!=null)
		utility.ProgressBar.SetProgressBar(jpb, 100 * percent / (parent.fourthDimensionSize + 1),
				"Computing Curvature = " + t + "/" + parent.fourthDimensionSize + " Z = " + z + "/"
						+ parent.thirdDimensionSize);

		RandomAccessibleInterval<BitType> CurrentView = utility.Slicer.getCurrentViewBit(parent.empty, z,
				parent.thirdDimensionSize, t, parent.fourthDimensionSize);
		RandomAccessibleInterval<BitType> CurrentViewSmooth = utility.Slicer.getCurrentViewBit(parent.emptysmooth, z,
				parent.thirdDimensionSize, t, parent.fourthDimensionSize);

		// Use smoothed image for segmentation and non smooth image for getting the
		// candidate points for fitting ellipses
		RandomAccessibleInterval<IntType> CurrentInt = getSeg(CurrentViewSmooth);
	
		
		if (parent.showWater) {

			Watershow(CurrentInt);

		}

		IntType min = new IntType();
		IntType max = new IntType();
		computeMinMax(Views.iterable(CurrentInt), min, max);
		// Neglect the background class label
		  int currentLabel = min.get();
			
			parent.background = currentLabel;
			parent.backField.setText(String.valueOf(new DecimalFormat("#.###").format(parent.background)));

           RandomAccessibleInterval<IntType> expanededtotalimg = ExpandBorder.extendBorder(parent, CurrentInt, currentLabel);

		//RandomAccessibleInterval<BitType> CurrentViewthin = getThin(CurrentView);
		GetPixelList(expanededtotalimg);
		Computeinwater compute = new Computeinwater(parent, CurrentView, expanededtotalimg, t, z, (int) percent);
		compute.ParallelRansacCurve();

	}

	public void Watershow(RandomAccessibleInterval<IntType> CurrentInt) {

		if (parent.localwaterimp == null || !parent.localwaterimp.isVisible()) {
			parent.localwaterimp = ImageJFunctions.show(CurrentInt);

		}

		else {

			final short[] pixels = (short[]) parent.localwaterimp.getProcessor().getPixels();
			final Cursor<IntType> c = Views.iterable(CurrentInt).cursor();

			for (int i = 0; i < pixels.length; ++i)
				pixels[i] = (short) c.next().get();

			parent.localwaterimp.updateAndDraw();
			parent.localwaterimp.setTitle("Watershed Image" + " " + "time point : " + parent.fourthDimension + " "
					+ " Z: " + parent.thirdDimension);

		}

	}



	

	public void ComputeCurvature() throws Exception {

		// Main method for computing intersections and tangents and angles between
		// tangents
		int percent = 0;

	

	
			if (parent.originalimg.numDimensions() > 3) {

				for (int t = parent.AutostartTime; t <= parent.AutoendTime; ++t) {
					parent.TID = Integer.toString(t);
					parent.Accountedframes.put(parent.TID, t);
					for (int z = 1; z <= parent.thirdDimensionSize; ++z) {

						parent.thirdDimension = z;
						parent.fourthDimension = t;
						
						BlockRepeatCurve(percent, z, t);
						
						
						if(IJ.escapePressed()) {
							IJ.resetEscape();
							break;
								
						}
					}
				}

			}

			else if (parent.originalimg.numDimensions() > 2 && parent.originalimg.numDimensions() < 4) {

				
				for (int z = parent.AutostartTime; z <= parent.AutoendTime; ++z) {

					parent.thirdDimension = z;
					parent.ZID = Integer.toString(z);
					parent.AccountedZ.put(parent.ZID, z);
				
					BlockRepeatCurve(percent, z, 1);
					if(IJ.escapePressed()) {
						IJ.resetEscape();
						break;
							
					}
				}

			} else {
				int z = parent.thirdDimension;
				int t = parent.fourthDimension;
				
				BlockRepeatCurve(percent, z, t);
				
			}

		
		
	}

	
	
	public void DistanceTransformImage(RandomAccessibleInterval<BitType> inputimg,
			RandomAccessibleInterval<BitType> bitimg, RandomAccessibleInterval<BitType> outimg) {
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
		final RandomAccess<BitType> ranac = outimg.randomAccess();

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

	public RandomAccessibleInterval<BitType> getDist(RandomAccessibleInterval<BitType> CurrentView) {

		RandomAccessibleInterval<BitType> copyoriginal = new ArrayImgFactory<BitType>().create(CurrentView,
				new BitType());
		final ImgFactory<BitType> factory = Util.getArrayOrCellImgFactory(CurrentView, new BitType());
		RandomAccessibleInterval<BitType> distimg = factory.create(CurrentView, new BitType());
		DistanceTransformImage(CurrentView, CurrentView, distimg);
		final Cursor<BitType> distcursor = Views.iterable(distimg).localizingCursor();
		final RandomAccess<BitType> distranac = copyoriginal.randomAccess();
		while (distcursor.hasNext()) {

			distcursor.fwd();

			distranac.setPosition(distcursor);
			if (distcursor.get().getRealDouble() > parent.lowprob
					&& distcursor.get().getRealDouble() < parent.highprob) {

				distranac.get().setOne();
			} else {
				distranac.get().setZero();
			}

		}

		return distimg;

	}

	public RandomAccessibleInterval<BitType> getMedian(RandomAccessibleInterval<BitType> CurrentView, double sigma) {

		RandomAccessibleInterval<BitType> newthinCurrentView = new ArrayImgFactory<BitType>().create(CurrentView,
				new BitType());
		newthinCurrentView = Kernels.MedianFilter(CurrentView, sigma);
		return newthinCurrentView;

	}

	public RandomAccessibleInterval<BitType> getCannyEdge(RandomAccessibleInterval<BitType> CurrentView) {

		RandomAccessibleInterval<BitType> newthinCurrentView = new ArrayImgFactory<BitType>().create(CurrentView,
				new BitType());
		newthinCurrentView = Kernels.CannyEdgeandMeanBit(CurrentView, 0);

		return newthinCurrentView;
	}

	public RandomAccessibleInterval<IntType> getSeg(RandomAccessibleInterval<BitType> CurrentView) {
	
		ThinningStrategyFactory fact = new ThinningStrategyFactory(true);
		ThinningStrategy strat = fact.getStrategy(Strategy.HILDITCH);
		ThinningOp thinit = new ThinningOp(strat, true, new ArrayImgFactory<BitType>());

		RandomAccessibleInterval<BitType> newCurrentView = new ArrayImgFactory<BitType>().create(CurrentView,
				new BitType());
		RandomAccessibleInterval<BitType> newthinCurrentView = new ArrayImgFactory<BitType>().create(CurrentView,
				new BitType());
	
		newCurrentView = Kernels.CannyEdgeandMeanBit(CurrentView, 1);
		thinit.compute(newCurrentView, newthinCurrentView);
		
		
		DistWatershedBinary segmentimage = new DistWatershedBinary(newthinCurrentView);

		segmentimage.process();

		RandomAccessibleInterval<IntType> CurrentViewInt = segmentimage.getResult();


		return CurrentViewInt;

	}



	public RandomAccessibleInterval<BitType> getCand(RandomAccessibleInterval<BitType> CurrentView) {

		ThinningStrategyFactory fact = new ThinningStrategyFactory(true);
		ThinningStrategy strat = fact.getStrategy(Strategy.HILDITCH);
		ThinningOp thinit = new ThinningOp(strat, true, new ArrayImgFactory<BitType>());

		RandomAccessibleInterval<BitType> newCurrentView = new ArrayImgFactory<BitType>().create(CurrentView,
				new BitType());
		RandomAccessibleInterval<BitType> newthinCurrentView = new ArrayImgFactory<BitType>().create(CurrentView,
				new BitType());

		newCurrentView = Kernels.CannyEdgeandMeanBit(CurrentView, 1);
		thinit.compute(newCurrentView, newthinCurrentView);

		return newthinCurrentView;

	}

	public Pair<RandomAccessibleInterval<IntType>, RandomAccessibleInterval<BitType>> getAutoint(
			RandomAccessibleInterval<BitType> CurrentView) {

		ThinningStrategyFactory fact = new ThinningStrategyFactory(true);
		ThinningStrategy strat = fact.getStrategy(Strategy.HILDITCH);
		ThinningOp thinit = new ThinningOp(strat, true, new ArrayImgFactory<BitType>());

		RandomAccessibleInterval<BitType> newCurrentView = new ArrayImgFactory<BitType>().create(CurrentView,
				new BitType());
		RandomAccessibleInterval<BitType> newthinCurrentView = new ArrayImgFactory<BitType>().create(CurrentView,
				new BitType());

		newCurrentView = Kernels.CannyEdgeandMeanBit(CurrentView, 1);
		thinit.compute(newCurrentView, newthinCurrentView);


		DistWatershedBinary segmentimage = new DistWatershedBinary(newthinCurrentView);

		segmentimage.process();

		RandomAccessibleInterval<IntType> CurrentViewInt = segmentimage.getResult();


		return new ValuePair<RandomAccessibleInterval<IntType>, RandomAccessibleInterval<BitType>>(CurrentViewInt,
				newthinCurrentView);

	}

	public RandomAccessibleInterval<BitType> CreateBinary(RandomAccessibleInterval<FloatType> source, double lowprob,
			double highprob) {

		RandomAccessibleInterval<BitType> copyoriginal = new ArrayImgFactory<BitType>().create(source, new BitType());

		final RandomAccess<BitType> ranac = copyoriginal.randomAccess();
		final Cursor<FloatType> cursor = Views.iterable(source).localizingCursor();

		while (cursor.hasNext()) {

			cursor.fwd();

			ranac.setPosition(cursor);
			if (cursor.get().getRealDouble() > lowprob && cursor.get().getRealDouble() < highprob) {

				ranac.get().setOne();
			} else {
				ranac.get().setZero();
			}

		}

		return copyoriginal;

	}

	public  void GetPixelList(RandomAccessibleInterval<IntType> intimg) {

		IntType min = new IntType();
		IntType max = new IntType();
		computeMinMax(Views.iterable(intimg), min, max);
		Cursor<IntType> intCursor = Views.iterable(intimg).cursor();
		// Neglect the background class label
		int currentLabel = max.get();
		parent.pixellist.clear();
		
		
		while (intCursor.hasNext()) {
			intCursor.fwd();
			int i = intCursor.get().get();
			if (i != currentLabel ) {

				parent.pixellist.add(i);

				currentLabel = i;

			}

		}

	}

	public int GetMaxlabelsseeded(RandomAccessibleInterval<IntType> intimg) {

		IntType min = new IntType();
		IntType max = new IntType();
		computeMinMax(Views.iterable(intimg), min, max);

		return max.get();

	}

	public <T extends Comparable<T> & Type<T>> void computeMinMax(final Iterable<T> input, final T min, final T max) {
		// create a cursor for the image (the order does not matter)
		final Iterator<T> iterator = input.iterator();

		// initialize min and max with the first image value
		T type = iterator.next();

		min.set(type);
		max.set(type);

		// loop over the rest of the data and determine min and max value
		while (iterator.hasNext()) {
			// we need this type more than once
			type = iterator.next();

			if (type.compareTo(min) < 0)
				min.set(type);

			if (type.compareTo(max) > 0)
				max.set(type);
		}
	}

	
	public void ComputeCurvatureCurrent() throws Exception {

		double percent = 0;

		int z = parent.thirdDimension;
		int t = parent.fourthDimension;
		
		if (parent.curvesupermode) {


			BlockRepeatCurve(percent, z, t);

		}

		if (parent.curveautomode) {

			

			BlockRepeatAutoCurve(percent, z, t);

		}

		


	}
	public RandomAccessibleInterval<IntType> getIntimg(RandomAccessibleInterval<BitType> CurrentView) {

		DistWatershedBinary segmentimage = new DistWatershedBinary(CurrentView);
		segmentimage.process();

		RandomAccessibleInterval<IntType> CurrentViewInt = segmentimage.getResult();

		return CurrentViewInt;
	}

}

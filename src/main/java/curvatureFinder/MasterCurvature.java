package curvatureFinder;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import bdv.util.Bdv;
import bdvOverlay.BdvOverlayDisplay;
import curvatureUtils.ClockDisplayer;
import curvatureUtils.PointExtractor;
import ellipsoidDetector.Distance;
import ellipsoidDetector.Intersectionobject;
import ij.IJ;
import ij.gui.Line;
import ij.gui.Overlay;
import ij.gui.TextRoi;
import kalmanForSegments.Segmentobject;
import mpicbg.models.Point;
import net.imglib2.Cursor;
import net.imglib2.Localizable;
import net.imglib2.RandomAccess;
import net.imglib2.RealLocalizable;
import net.imglib2.RealPoint;
import net.imglib2.algorithm.region.BresenhamLine;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;
import pluginTools.InteractiveSimpleEllipseFit;
import pluginTools.RegressionCurveSegment;
import ransacPoly.RegressionFunction;
import utility.Curvatureobject;
import utility.Listordereing;
import utility.Roiobject;
import varun_algorithm_ransac_Ransac.FitLocalEllipsoid;
import varun_algorithm_ransac_Ransac.RansacFunctionEllipsoid;
import varun_algorithm_region.hypersphere.HyperSphere;
import varun_algorithm_region.hypersphere.HyperSphereCursor;

public abstract class MasterCurvature<T extends RealType<T> & NativeType<T>> implements CurvatureFinders<T> {

	/**
	 * 
	 * Function to fit on a list of points which are not tree based
	 * 
	 * @param parent
	 * @param centerpoint
	 * @param sublist
	 * @param functions
	 * @param interpolatedCurvature
	 * @param smoothing
	 * @param maxError
	 * @param minNumInliers
	 * @param degree
	 * @param secdegree
	 * @return
	 */

	static int xindex = 0;
	static int yindex = 1;
	static int curveindex = 2;
	static int periindex = 3;
	static int intensityAindex = 4;
	static int intensityBindex = 5;
	static int distcurveindex = 6;

	public class ParallelCalls implements Callable<Pair<RegressionLineProfile, ClockDisplayer>> {

		public final InteractiveSimpleEllipseFit parent;

		public final RealLocalizable centerpoint;
		public final List<RealLocalizable> sublist;
		public final int strideindex;
		public final String name;

		public ParallelCalls(InteractiveSimpleEllipseFit parent, RealLocalizable centerpoint,
				List<RealLocalizable> sublist, int strideindex, String name) {

			this.parent = parent;

			this.centerpoint = centerpoint;

			this.sublist = sublist;

			this.strideindex = strideindex;

			this.name = name;

		}

		@Override
		public Pair<RegressionLineProfile, ClockDisplayer> call() throws Exception {

			Pair<RegressionLineProfile, ClockDisplayer> localfunction = FitCircleonList(parent, centerpoint, sublist, strideindex, name);

			return localfunction;
		}

	}

	public RegressionLineProfile FitonList(InteractiveSimpleEllipseFit parent, RealLocalizable centerpoint,
			List<RealLocalizable> sublist, int strideindex) {

		ArrayList<double[]> Cordlist = new ArrayList<double[]>();

		for (int i = 0; i < sublist.size(); ++i) {

			Cordlist.add(new double[] { sublist.get(i).getDoublePosition(xindex),
					sublist.get(i).getDoublePosition(yindex) });
		}

		// Interfaces have to implement the curvature getting methods
		
		RegressionLineProfile resultcurvature = getLocalcurvature(Cordlist, centerpoint, strideindex);


		return resultcurvature;

	}

	public Pair<RegressionLineProfile, ClockDisplayer> FitCircleonList(InteractiveSimpleEllipseFit parent, RealLocalizable centerpoint,
			List<RealLocalizable> sublist, int strideindex, String name) {

		ArrayList<double[]> Cordlist = new ArrayList<double[]>();

		for (int i = 0; i < sublist.size(); ++i) {

			Cordlist.add(new double[] { sublist.get(i).getDoublePosition(xindex),
					sublist.get(i).getDoublePosition(yindex) });
		}
        
		Pair<RegressionLineProfile, ClockDisplayer> resultcurvature = getCircleLocalcurvature(Cordlist, centerpoint, strideindex, name);

		// Draw the function

		return resultcurvature;
		
      

	}

	public RegressionCurveSegment CommonLoop(InteractiveSimpleEllipseFit parent, List<RealLocalizable> Ordered,
			RealLocalizable centerpoint, int ndims, int celllabel, int t, int z) {

		// Get the sparse list of points
		HashMap<Integer, RegressionCurveSegment> Bestdelta = new HashMap<Integer, RegressionCurveSegment>();

		int i = parent.increment;

		// Get the sparse list of points

		List<RealLocalizable> allorderedtruths = Listordereing.getList(Ordered, i);

		if (parent.fourthDimensionSize > 1)
			parent.timeslider.setValue(utility.Slicer.computeScrollbarPositionFromValue(parent.fourthDimension,
					parent.fourthDimensionsliderInit, parent.fourthDimensionSize, parent.scrollbarSize));
		parent.zslider.setValue(utility.Slicer.computeScrollbarPositionFromValue(parent.thirdDimension,
				parent.thirdDimensionsliderInit, parent.thirdDimensionSize, parent.scrollbarSize));

		RegressionCurveSegment resultpair = getCurvatureLineScan(parent, allorderedtruths, centerpoint, ndims,
				celllabel, z, t);

		// Here counter the segments where the number of inliers was too low
		Bestdelta.put(0, resultpair);

		parent.localCurvature = resultpair.Curvelist;

		parent.functions = resultpair.functionlist;
		return resultpair;

	}

	/**
	 * 
	 * Take in a list of ordered co-ordinates and compute a curvature object
	 * containing the curvature information at each co-ordinate Makes a tree
	 * structure of the list
	 * 
	 * @param orderedtruths
	 * @param ndims
	 * @param Label
	 * @param t
	 * @param z
	 * @param strideindex
	 * @return
	 */
	public RegressionCurveSegment getCurvature(InteractiveSimpleEllipseFit parent, List<RealLocalizable> truths,
			RealLocalizable centerpoint, int ndims, int Label, int z, int t, int strideindex) {

		ArrayList<Curvatureobject> curveobject = new ArrayList<Curvatureobject>();

		ArrayList<double[]> totalinterpolatedCurvature = new ArrayList<double[]>();

		ArrayList<RegressionFunction> totalfunctions = new ArrayList<RegressionFunction>();

		double perimeter = 0;

		MakeSegments(parent, truths, parent.minNumInliers, Label);
		// Now do the fitting
		for (Map.Entry<Integer, List<RealLocalizable>> entry : parent.Listmap.entrySet()) {

			List<RealLocalizable> sublist = entry.getValue();
			/***
			 * 
			 * Main method that fits on segments a function to get the curvature
			 * 
			 */
			RegressionLineProfile localfunction = FitonList(parent, centerpoint, sublist, strideindex);

			perimeter += localfunction.regfunc.Curvaturepoints.get(0)[periindex];
			totalfunctions.add(localfunction.regfunc);
			totalinterpolatedCurvature.addAll(localfunction.AllCurvaturepoints);
			double Curvature = localfunction.AllCurvaturepoints.get(0)[curveindex];
			double distCurvature = localfunction.AllCurvaturepoints.get(0)[distcurveindex];
			double IntensityA = localfunction.AllCurvaturepoints.get(0)[intensityAindex];
			double IntensityB = localfunction.AllCurvaturepoints.get(0)[intensityBindex];
			ArrayList<double[]> curvelist = new ArrayList<double[]>();

			curvelist.add(new double[] { centerpoint.getDoublePosition(xindex), centerpoint.getDoublePosition(yindex),
					Curvature, IntensityA, IntensityB, distCurvature });
			

		}

		for (int indexx = 0; indexx < totalinterpolatedCurvature.size(); ++indexx) {

			Curvatureobject currentobject = new Curvatureobject(totalinterpolatedCurvature.get(indexx)[curveindex], totalinterpolatedCurvature.get(indexx)[distcurveindex],
					perimeter, totalinterpolatedCurvature.get(indexx)[intensityAindex],
					totalinterpolatedCurvature.get(indexx)[intensityBindex], Label,
					new double[] { totalinterpolatedCurvature.get(indexx)[xindex],
							totalinterpolatedCurvature.get(indexx)[yindex] },
					z, t);

			curveobject.add(currentobject);

		}

		// All nodes are returned

		RegressionCurveSegment returnSeg = new RegressionCurveSegment(totalfunctions, curveobject);

		return returnSeg;

	}

	public RegressionCurveSegment getCurvatureLineScan(InteractiveSimpleEllipseFit parent, List<RealLocalizable> truths,
			RealLocalizable centerpoint, int ndims, int Label, int z, int t) {

	
		ArrayList<Curvatureobject> curveobject = new ArrayList<Curvatureobject>();

		ArrayList<double[]> totalinterpolatedCurvature = new ArrayList<double[]>();

		ArrayList<RegressionFunction> totalfunctions = new ArrayList<RegressionFunction>();


		ConcurrentHashMap<Integer, ArrayList<LineProfileCircle>> Hashtotalscan = new ConcurrentHashMap<Integer, ArrayList<LineProfileCircle>>();

		double perimeter = 0;

		int segmentlabel = 1;
		int fakesegmentlabel = 1;
		MakeSegments(parent, truths, parent.minNumInliers, Label);
		// Get the sparse list of points, skips parent.resolution pixel points
		// set up executor service
		int nThreads = Runtime.getRuntime().availableProcessors();
		final ExecutorService taskExecutor = Executors.newFixedThreadPool(nThreads);

		List<Future<Pair<RegressionLineProfile, ClockDisplayer>>> list = new ArrayList<Future<Pair<RegressionLineProfile, ClockDisplayer>>>();
		// Now do the fitting
		for (Map.Entry<Integer, List<RealLocalizable>> entry : parent.Listmap.entrySet()) {

			List<RealLocalizable> sublist = entry.getValue();
			/***
			 * 
			 * Main method that fits on segments a function to get the curvature
			 * 
			 */

			ParallelCalls call = new ParallelCalls(parent, centerpoint, sublist, 0, Integer.toString(fakesegmentlabel));
			Future<Pair<RegressionLineProfile, ClockDisplayer>> Futureresultpair = taskExecutor.submit(call);
			list.add(Futureresultpair);
			fakesegmentlabel++;
		}
		taskExecutor.shutdown();
		
		
		ArrayList<ClockDisplayer> Masterclock = new ArrayList<ClockDisplayer>();
		for (Future<Pair<RegressionLineProfile, ClockDisplayer>> fut : list) {

			try {
				
				Pair<RegressionLineProfile, ClockDisplayer> localfunction = fut.get();
				
				Masterclock.add(localfunction.getB());
						
				if (localfunction.getA().LineScanIntensity.size() > 0) {

					Hashtotalscan.put(segmentlabel, localfunction.getA().LineScanIntensity);
					segmentlabel++;

				}
				perimeter += localfunction.getA().regfunc.Curvaturepoints.get(0)[periindex];
				totalfunctions.add(localfunction.getA().regfunc);
				totalinterpolatedCurvature.addAll(localfunction.getA().AllCurvaturepoints);
				double Curvature = localfunction.getA().AllCurvaturepoints.get(0)[curveindex];
				double IntensityA = localfunction.getA().AllCurvaturepoints.get(0)[intensityAindex];
				double IntensityB = localfunction.getA().AllCurvaturepoints.get(0)[intensityBindex];
				ArrayList<double[]> curvelist = new ArrayList<double[]>();

				curvelist.add(new double[] { centerpoint.getDoublePosition(xindex),
						centerpoint.getDoublePosition(yindex), Curvature, IntensityA, IntensityB });
			} catch (InterruptedException | ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		
		
			
		// All nodes are returned
		for (int indexx = 0; indexx < totalinterpolatedCurvature.size(); ++indexx) {

			Curvatureobject currentobject = new Curvatureobject(totalinterpolatedCurvature.get(indexx)[curveindex], totalinterpolatedCurvature.get(indexx)[distcurveindex],
					perimeter, totalinterpolatedCurvature.get(indexx)[intensityAindex],
					totalinterpolatedCurvature.get(indexx)[intensityBindex], Label,
					new double[] { totalinterpolatedCurvature.get(indexx)[xindex],
							totalinterpolatedCurvature.get(indexx)[yindex] },
					z, t);

			curveobject.add(currentobject);

		}

		RegressionCurveSegment returnSeg = null;
		if (Hashtotalscan.size() == 0)
			returnSeg = new RegressionCurveSegment(totalfunctions, curveobject);
		else
			returnSeg = new RegressionCurveSegment(totalfunctions, curveobject, Hashtotalscan);
		return returnSeg;
		
	}

	public Pair<Intersectionobject, Intersectionobject> GetAverage(InteractiveSimpleEllipseFit parent,
			RealLocalizable centerpoint, ConcurrentHashMap<Integer, RegressionCurveSegment> bestdelta, int count) {

		RegressionCurveSegment resultpair = bestdelta.get(0);
		ArrayList<Curvatureobject> RefinedCurvature = new ArrayList<Curvatureobject>();
		ArrayList<Curvatureobject> localCurvature = resultpair.Curvelist;

		double[] X = new double[localCurvature.size()];
		double[] Y = new double[localCurvature.size()];
		double[] Z = new double[localCurvature.size()];
		double[] Zdist = new double[localCurvature.size()];
		double[] I = new double[localCurvature.size()];
		double[] ISec = new double[localCurvature.size()];

		for (int index = 0; index < localCurvature.size(); ++index) {

			ArrayList<Double> CurveXY = new ArrayList<Double>();
			ArrayList<Double> CurveXYdist = new ArrayList<Double>();
			ArrayList<Double> CurveI = new ArrayList<Double>();
			ArrayList<Double> CurveISec = new ArrayList<Double>();

			X[index] = localCurvature.get(index).cord[0];
			Y[index] = localCurvature.get(index).cord[1];
			Z[index] = localCurvature.get(index).radiusCurvature;
			Zdist[index] = localCurvature.get(index).distCurvature;
			I[index] = localCurvature.get(index).Intensity;
			ISec[index] = localCurvature.get(index).SecIntensity;

			CurveXY.add(Z[index]);
			CurveI.add(I[index]);
			CurveISec.add(ISec[index]);
			CurveXYdist.add(Zdist[index]);
			Iterator<Double> setiterdist = CurveXYdist.iterator();
			double frequdeltadist = Zdist[index];
			double intensitydelta = I[index];
			double intensitySecdelta = ISec[index];
			while (setiterdist.hasNext()) {

				Double s = setiterdist.next();

				frequdeltadist += s;

			}

			frequdeltadist /= CurveXYdist.size();
			Iterator<Double> Iiter = CurveI.iterator();
			
			
			while (Iiter.hasNext()) {

				Double s = Iiter.next();

				intensitydelta += s;

			}

			Iterator<Double> ISeciter = CurveISec.iterator();
			while (ISeciter.hasNext()) {

				Double s = ISeciter.next();

				intensitySecdelta += s;

			}
			
			for (int secindex = 1; secindex < count; ++secindex) {

				RegressionCurveSegment testpair = bestdelta.get(secindex);

				ArrayList<Curvatureobject> testlocalCurvature = testpair.Curvelist;

				double[] Xtest = new double[testlocalCurvature.size()];
				double[] Ytest = new double[testlocalCurvature.size()];
				double[] Ztest = new double[testlocalCurvature.size()];
				double[] Itest = new double[testlocalCurvature.size()];
				double[] ISectest = new double[testlocalCurvature.size()];

				for (int testindex = 0; testindex < testlocalCurvature.size(); ++testindex) {

					Xtest[testindex] = testlocalCurvature.get(testindex).cord[0];
					Ytest[testindex] = testlocalCurvature.get(testindex).cord[1];
					Ztest[testindex] = testlocalCurvature.get(testindex).radiusCurvature;
					Itest[testindex] = testlocalCurvature.get(testindex).Intensity;
					ISectest[testindex] = testlocalCurvature.get(testindex).SecIntensity;

					if (X[index] == Xtest[testindex] && Y[index] == Ytest[testindex]) {

						CurveXY.add(Ztest[testindex]);
						

					}

				}

			}

			double frequdeltaperi = localCurvature.get(0).perimeter;
			double frequdelta = Z[index];
			

			Iterator<Double> setiter = CurveXY.iterator();
			while (setiter.hasNext()) {

				Double s = setiter.next();

				frequdelta += s;

			}

			frequdelta /= CurveXY.size();

			

			Curvatureobject newobject = new Curvatureobject((float) frequdelta, (float) frequdeltadist, frequdeltaperi, intensitydelta,
					intensitySecdelta, localCurvature.get(index).Label, localCurvature.get(index).cord,
					localCurvature.get(index).t, localCurvature.get(index).z);

			RefinedCurvature.add(newobject);
		}

		Pair<ArrayList<RegressionFunction>, ArrayList<Curvatureobject>> Refinedresultpair = new ValuePair<ArrayList<RegressionFunction>, ArrayList<Curvatureobject>>(
				resultpair.functionlist, RefinedCurvature);
		parent.localCurvature = Refinedresultpair.getB();
		parent.functions.addAll(Refinedresultpair.getA());
		// Make intersection object here

		Pair<Intersectionobject, Intersectionobject> currentobjectpair = PointExtractor.CurvaturetoIntersection(parent,
				parent.localCurvature, parent.functions, resultpair.LineScanIntensity, centerpoint, parent.smoothing);
		Intersectionobject densecurrentobject = currentobjectpair.getA();
		Intersectionobject sparsecurrentobject = currentobjectpair.getB();

		return new ValuePair<Intersectionobject, Intersectionobject>(sparsecurrentobject, densecurrentobject);
	}

	public Pair<Intersectionobject, Intersectionobject> GetSingle(InteractiveSimpleEllipseFit parent,
			RealLocalizable centerpoint, ConcurrentHashMap<Integer, RegressionCurveSegment> bestdelta) {

		RegressionCurveSegment resultpair = bestdelta.get(0);
		ArrayList<Curvatureobject> RefinedCurvature = new ArrayList<Curvatureobject>();
		ArrayList<Curvatureobject> localCurvature = resultpair.Curvelist;

		double[] X = new double[localCurvature.size()];
		double[] Y = new double[localCurvature.size()];
		double[] Z = new double[localCurvature.size()];
		double[] I = new double[localCurvature.size()];
		double[] ISec = new double[localCurvature.size()];
		for (int index = 0; index < localCurvature.size(); ++index) {

			ArrayList<Double> CurveXY = new ArrayList<Double>();
			ArrayList<Double> CurveI = new ArrayList<Double>();
			ArrayList<Double> CurveISec = new ArrayList<Double>();

			X[index] = localCurvature.get(index).cord[0];
			Y[index] = localCurvature.get(index).cord[1];
			Z[index] = localCurvature.get(index).radiusCurvature;
			I[index] = localCurvature.get(index).Intensity;
			ISec[index] = localCurvature.get(index).SecIntensity;

			CurveXY.add(Z[index]);
			CurveI.add(I[index]);
			CurveISec.add(ISec[index]);

			double frequdeltaperi = localCurvature.get(0).perimeter;
			double frequdelta = Z[index];
			double intensitydelta = I[index];
			double intensitySecdelta = ISec[index];

			Iterator<Double> setiter = CurveXY.iterator();
			while (setiter.hasNext()) {

				Double s = setiter.next();

				frequdelta += s;

			}

			frequdelta /= CurveXY.size();

			Iterator<Double> Iiter = CurveI.iterator();
			while (Iiter.hasNext()) {

				Double s = Iiter.next();

				intensitydelta += s;

			}

			Iterator<Double> ISeciter = CurveISec.iterator();
			while (ISeciter.hasNext()) {

				Double s = ISeciter.next();

				intensitySecdelta += s;

			}

			Curvatureobject newobject = new Curvatureobject((float) frequdelta, (float) frequdelta, frequdeltaperi, intensitydelta,
					intensitySecdelta, localCurvature.get(index).Label, localCurvature.get(index).cord,
					localCurvature.get(index).t, localCurvature.get(index).z);

			RefinedCurvature.add(newobject);
		}

		Pair<ArrayList<RegressionFunction>, ArrayList<Curvatureobject>> Refinedresultpair = new ValuePair<ArrayList<RegressionFunction>, ArrayList<Curvatureobject>>(
				resultpair.functionlist, RefinedCurvature);
		parent.localCurvature = Refinedresultpair.getB();
		parent.functions.addAll(Refinedresultpair.getA());
		// intersection object here

		Pair<Intersectionobject, Intersectionobject> currentobjectpair = PointExtractor.CurvaturetoIntersection(parent,
				parent.localCurvature, parent.functions, resultpair.LineScanIntensity, centerpoint, parent.smoothing);
		Intersectionobject densecurrentobject = currentobjectpair.getA();
		Intersectionobject sparsecurrentobject = currentobjectpair.getB();

		return new ValuePair<Intersectionobject, Intersectionobject>(sparsecurrentobject, densecurrentobject);
	}
	

	
	public void MakeSegments(InteractiveSimpleEllipseFit parent, final List<RealLocalizable> truths, int boxSize,
			int celllabel) {

		List<RealLocalizable> copytruths = new ArrayList<RealLocalizable>(truths);
		if (truths.size() < 3)
			return;
		else {
			int size = truths.size();

			int maxpoints = (int) (boxSize/parent.calibration);
			if (maxpoints <= 2)
				maxpoints = 3;
			int biggestsize = maxpoints;
			int segmentLabel = 1;

			int index = truths.size() - 1;
			do {

				if (index >= truths.size())
					index = 0;

				copytruths.add(truths.get(index));

				index++;

			} while (copytruths.size() % maxpoints != 0);

			size = copytruths.size();
		

			List<RealLocalizable> sublist = new ArrayList<RealLocalizable>();

			int startindex = 0;
			int endindex = startindex + maxpoints;

			while (true) {

				sublist = copytruths.subList(startindex, Math.min(endindex, size));
				parent.Listmap.put(segmentLabel, sublist);

				if (biggestsize >= endindex - startindex)
					biggestsize = endindex - startindex;

				parent.CellLabelsizemap.put(celllabel, biggestsize);
				segmentLabel++;

				startindex = endindex;
				endindex = startindex + maxpoints;

				if (endindex >= size + 1)
					break;

			}

		}

	}

	/**
	 * Obtain intensity in the user defined
	 * 
	 * @param point
	 * @return
	 */

	public Pair<Double, Double> getIntensity(InteractiveSimpleEllipseFit parent, Localizable point,
			Localizable centerpoint) {

		RandomAccess<FloatType> ranac = parent.CurrentViewOrig.randomAccess();

		double Intensity = 0;
		double IntensitySec = 0;
		RandomAccess<FloatType> ranacsec;
		if (parent.CurrentViewSecOrig != null)
			ranacsec = parent.CurrentViewSecOrig.randomAccess();
		else
			ranacsec = ranac;

		ranac.setPosition(point);
		ranacsec.setPosition(ranac);
		double mindistance = getDistance(point, centerpoint);
		double[] currentPosition = new double[point.numDimensions()];

		HyperSphere<FloatType> hyperSphere = new HyperSphere<FloatType>(parent.CurrentViewOrig, ranac,
				(int) parent.regiondistance);
		HyperSphereCursor<FloatType> localcursor = hyperSphere.localizingCursor();
		int Area = 1;
		while (localcursor.hasNext()) {

			localcursor.fwd();

			ranacsec.setPosition(localcursor);

			ranacsec.localize(currentPosition);

			if (currentPosition[0] > parent.CurrentViewOrig.min(0) + parent.regiondistance
					&& currentPosition[1] > parent.CurrentViewOrig.min(1) + parent.regiondistance
					&& currentPosition[0] < parent.CurrentViewOrig.max(0) - parent.regiondistance
					&& currentPosition[1] < parent.CurrentViewOrig.max(1) - parent.regiondistance) {
				double currentdistance = getDistance(localcursor, centerpoint);
				if ((currentdistance - mindistance) <= parent.regiondistance) {
					Intensity += localcursor.get().getRealDouble();
					IntensitySec += ranacsec.get().getRealDouble();
					Area++;
				}
			}
		}

		return new ValuePair<Double, Double>(Intensity / Area, IntensitySec / Area);
	}

	/**
	 * 
	 * Fit an ellipse to a bunch of points
	 * 
	 * @param pointlist
	 * @param ndims
	 * @return
	 */

	public Pair<RegressionLineProfile, ClockDisplayer> RansacEllipseBlock(final InteractiveSimpleEllipseFit parent,
			final ArrayList<RealLocalizable> pointlist, RealLocalizable centerpoint, int ndims, int strideindex,
			boolean linescan, final String name) {

		final RansacFunctionEllipsoid ellipsesegment = FitLocalEllipsoid.findLocalEllipsoid(pointlist, ndims);

		double Kappa = 0;
		double Kappadistance = 0;
		double perimeter = 0;
		ArrayList<double[]> Curvaturepoints = new ArrayList<double[]>();

		ArrayList<double[]> AllCurvaturepoints = new ArrayList<double[]>();

		double radii = ellipsesegment.function.getRadii();
		double[] newpos = new double[ndims];
		long[] longnewpos = new long[ndims];

			perimeter = pointlist.size();
		
		int size = pointlist.size();
		final double[] pointA = new double[ndims];
		final double[] pointB = new double[ndims];
		final double[] pointC = new double[ndims];

		double meanIntensity = 0;
		double meanSecIntensity = 0;
		int splitindex;
		if (size % 2 == 0)
			splitindex = size / 2;
		else
			splitindex = (size - 1) / 2;

		for (int i = 0; i < ndims; ++i) {
			pointA[i] = pointlist.get(0).getDoublePosition(i);
			pointB[i] = pointlist.get(splitindex).getDoublePosition(i);
			pointC[i] = pointlist.get(size - 1).getDoublePosition(i);

		}

		long[] centerloc = new long[] { (long) centerpoint.getDoublePosition(0),
				(long) centerpoint.getDoublePosition(1) };
		net.imglib2.Point centpos = new net.imglib2.Point(centerloc);

		for (RealLocalizable point : pointlist) {

			point.localize(newpos);

			Kappa = 1.0 / (radii * parent.calibration);
			if(parent.combomethod)
				Kappadistance = getDistance(point, centerpoint);
			for (int d = 0; d < newpos.length; ++d)
				longnewpos[d] = (long) newpos[d];
			net.imglib2.Point intpoint = new net.imglib2.Point(longnewpos);

			Pair<Double, Double> Intensity = new ValuePair<Double, Double>(0.0, 0.0);

			Intensity = getIntensity(parent, intpoint, centpos);

			// Average the intensity.
			meanIntensity += Intensity.getA();
			meanSecIntensity += Intensity.getB();

			AllCurvaturepoints.add(new double[] { newpos[0], newpos[1], Math.max(0, Kappa), perimeter, Intensity.getA(),
					Intensity.getB(), Math.max(0, Kappadistance) });
		}
		meanIntensity /= size;
		meanSecIntensity /= size;
		Curvaturepoints.add(
				new double[] { pointB[0], pointB[1], Math.max(0, Kappa), perimeter, meanIntensity, meanSecIntensity, Math.max(0, Kappadistance) });

		RegressionFunction finalfunctionransac = new RegressionFunction(ellipsesegment.function, Curvaturepoints);

		
		ArrayList<LineProfileCircle> LineScanIntensity = new ArrayList<LineProfileCircle>();
		ClockDisplayer Clock = new ClockDisplayer("", null);
		if (strideindex == 0) {

			if (linescan) {

				
				Pair<ArrayList<LineProfileCircle>,ClockDisplayer> PairLineScanIntensity = getLineScanIntensity(parent, centerloc, ellipsesegment, pointB, ndims, name);
				LineScanIntensity = PairLineScanIntensity.getA();
				Clock = PairLineScanIntensity.getB();
			}

		}

		RegressionLineProfile currentprofile = new RegressionLineProfile(finalfunctionransac, LineScanIntensity,
				AllCurvaturepoints, name);

		return new ValuePair<RegressionLineProfile, ClockDisplayer>(currentprofile, Clock);

	}

	/**
	 * 
	 * Fit an ellipse to a bunch of points
	 * 
	 * @param pointlist
	 * @param ndims
	 * @return
	 */

	public RegressionLineProfile NoClockRansacEllipseBlock(final InteractiveSimpleEllipseFit parent,
			final ArrayList<RealLocalizable> pointlist, RealLocalizable centerpoint, int ndims, int strideindex,
			boolean linescan, final String name) {

		final RansacFunctionEllipsoid ellipsesegment = FitLocalEllipsoid.findLocalEllipsoid(pointlist, ndims);

		double Kappa = 0;
		double Kappadistance = 0;
		double perimeter = 0;
		ArrayList<double[]> Curvaturepoints = new ArrayList<double[]>();

		ArrayList<double[]> AllCurvaturepoints = new ArrayList<double[]>();

		double radii = ellipsesegment.function.getRadii();
		double[] newpos = new double[ndims];
		long[] longnewpos = new long[ndims];
	

			perimeter = pointlist.size();

		

		
		int size = pointlist.size();
		final double[] pointA = new double[ndims];
		final double[] pointB = new double[ndims];
		final double[] pointC = new double[ndims];

		double meanIntensity = 0;
		double meanSecIntensity = 0;
		int splitindex;
		if (size % 2 == 0)
			splitindex = size / 2;
		else
			splitindex = (size - 1) / 2;

		for (int i = 0; i < ndims; ++i) {
			pointA[i] = pointlist.get(0).getDoublePosition(i);
			pointB[i] = pointlist.get(splitindex).getDoublePosition(i);
			pointC[i] = pointlist.get(size - 1).getDoublePosition(i);

		}

		long[] centerloc = new long[] { (long) centerpoint.getDoublePosition(0),
				(long) centerpoint.getDoublePosition(1) };
		net.imglib2.Point centpos = new net.imglib2.Point(centerloc);

		for (RealLocalizable point : pointlist) {

			point.localize(newpos);

			Kappa = 1.0 / (radii * parent.calibration);
			
			if(parent.combomethod)
			Kappadistance = getDistance(point, centerpoint);
			
			for (int d = 0; d < newpos.length; ++d)
				longnewpos[d] = (long) newpos[d];
			net.imglib2.Point intpoint = new net.imglib2.Point(longnewpos);

			Pair<Double, Double> Intensity = new ValuePair<Double, Double>(0.0, 0.0);

			if (linescan)
				Intensity = getIntensity(parent, intpoint, centpos);

			// Average the intensity.
			meanIntensity += Intensity.getA();
			meanSecIntensity += Intensity.getB();

			AllCurvaturepoints.add(new double[] { newpos[0], newpos[1], Math.max(0, Kappa), perimeter, Intensity.getA(),
					Intensity.getB(), Math.max(0, Kappadistance) });
		}
		meanIntensity /= size;
		meanSecIntensity /= size;
		Curvaturepoints.add(
				new double[] { pointB[0], pointB[1], Math.max(0, Kappa), perimeter, meanIntensity, meanSecIntensity, Kappadistance });

		RegressionFunction finalfunctionransac = new RegressionFunction(ellipsesegment.function, Curvaturepoints);

		
		ArrayList<LineProfileCircle> LineScanIntensity = new ArrayList<LineProfileCircle>();
		if (strideindex == 0) {

			if (linescan) {

				
				Pair<ArrayList<LineProfileCircle>,ClockDisplayer> PairLineScanIntensity = getLineScanIntensity(parent, centerloc, ellipsesegment, pointB, ndims, name);
				LineScanIntensity = PairLineScanIntensity.getA();
			}

		}

		RegressionLineProfile currentprofile = new RegressionLineProfile(finalfunctionransac, LineScanIntensity,
				AllCurvaturepoints, name);

		return currentprofile;

	}

	
	public Pair<ArrayList<LineProfileCircle>,ClockDisplayer> getLineScanIntensity(final InteractiveSimpleEllipseFit parent,
			final long[] centerpos, RansacFunctionEllipsoid ellipsesegment, final double[] pointB, final int ndims,
			final String name) {

		int count = 0;

		int thickness = parent.linescanradius;

		ArrayList<LineProfileCircle> LineScanIntensity = new ArrayList<LineProfileCircle>(
				(int) (2 * parent.insidedistance));
		long[] longnewpos = new long[ndims];

		for (int d = 0; d < pointB.length; ++d)
			longnewpos[d] = (long) pointB[d];
		net.imglib2.Point intpoint = new net.imglib2.Point(longnewpos);

		RandomAccess<FloatType> ranac = parent.CurrentViewOrig.randomAccess();

		LinefunctionCircle NormalLine = new LinefunctionCircle(ellipsesegment.function, intpoint);

		double[] NormalSlopeIntercept = NormalLine.NormalatPoint();

		double startNormalX = intpoint.getDoublePosition(0)
				- parent.insidedistance / Math.sqrt(1 + NormalSlopeIntercept[0] * NormalSlopeIntercept[0]);
		double startNormalY = NormalSlopeIntercept[0] * startNormalX + NormalSlopeIntercept[1];

		double endNormalX = intpoint.getDoublePosition(0)
				+ parent.insidedistance / Math.sqrt(1 + NormalSlopeIntercept[0] * NormalSlopeIntercept[0]);
		double endNormalY = NormalSlopeIntercept[0] * endNormalX + NormalSlopeIntercept[1];

		double[] startNormal = { startNormalX, startNormalY };

		double[] endNormal = { endNormalX, endNormalY };

		Line line = new Line((int) startNormal[0], (int) startNormal[1], (int) endNormal[0], (int) endNormal[1],
				parent.imp);
		parent.overlay.add(line);
		TextRoi newellipse = new TextRoi(startNormal[0], startNormal[1], "" + name);

		Pair<double[], double[]> linescanpair = new ValuePair<double[], double[]>(startNormal, endNormal);
		ClockDisplayer masterclock = new ClockDisplayer(name, linescanpair);
		line.setStrokeWidth(1);
		line.setStrokeColor(Color.WHITE);
		parent.overlay.add(newellipse);

		parent.overlay.drawLabels(true);
		parent.overlay.drawNames(true);
		parent.imp.updateAndDraw();
		
		if (parent.thirdDimension == 1) {

		
			
			
			
			parent.clockoverlay.add(newellipse);
			parent.clockoverlay.add(line);

			parent.clockimp.setOverlay(parent.clockoverlay);
			parent.clockimp.updateAndDraw();

		}

		double[] outsidepoint = (Distance.DistanceSq(centerpos, startNormal) < Distance.DistanceSq(centerpos,
				endNormal)) ? endNormal : startNormal;
		double[] insidepoint = (Distance.DistanceSq(centerpos, startNormal) > Distance.DistanceSq(centerpos, endNormal))
				? endNormal
				: startNormal;

		net.imglib2.Point pointOut = new net.imglib2.Point(
				new long[] { Math.round(outsidepoint[0]), Math.round(outsidepoint[1]) });
		net.imglib2.Point pointIn = new net.imglib2.Point(
				new long[] { Math.round(insidepoint[0]), Math.round(insidepoint[1]) });

		double Intensity = 0;
		double IntensitySec = 0;
		RandomAccess<FloatType> ranacsec;
		if (parent.CurrentViewSecOrig != null)
			ranacsec = parent.CurrentViewSecOrig.randomAccess();
		else
			ranacsec = ranac;

		BresenhamLine<FloatType> newline = new BresenhamLine<FloatType>(ranac, pointOut, pointIn);

		Cursor<FloatType> linecursor = newline.copyCursor();

		while (linecursor.hasNext()) {

			linecursor.fwd();

			ranac.setPosition(linecursor);
			ranacsec.setPosition(ranac);

			HyperSphere<FloatType> hyperSphereOne = new HyperSphere<FloatType>(parent.CurrentViewOrig, ranac,
					(int) thickness);

			HyperSphereCursor<FloatType> localcursorOne = hyperSphereOne.localizingCursor();

			double[] currentPosition = new double[ndims];

			int avcount = 1;
			while (localcursorOne.hasNext()) {

				localcursorOne.fwd();

				ranacsec.setPosition(localcursorOne);

				ranacsec.localize(currentPosition);

				if (currentPosition[0] > parent.CurrentViewOrig.min(0) + thickness
						&& currentPosition[1] > parent.CurrentViewOrig.min(1) + thickness
						&& currentPosition[0] < parent.CurrentViewOrig.max(0) - thickness
						&& currentPosition[1] < parent.CurrentViewOrig.max(1) - thickness) {
					Intensity += localcursorOne.get().getRealDouble();
					IntensitySec += ranacsec.get().getRealDouble();
					avcount++;
				}
			}

			count++;
			Intensity /= avcount;
			IntensitySec /= avcount;
			LineProfileCircle linescan = new LineProfileCircle(count, Intensity, IntensitySec);
			LineScanIntensity.add(linescan);

		}

		do {

			LineProfileCircle linescan = new LineProfileCircle(count, Intensity, IntensitySec);
			LineScanIntensity.add(linescan);
			count++;

		} while (count < (int) parent.insidedistance * 2);

		return new ValuePair<ArrayList<LineProfileCircle>, ClockDisplayer>(LineScanIntensity, masterclock );

	}

	public double getDistance(Localizable point, Localizable centerpoint) {

		double distance = 0;

		int ndims = point.numDimensions();

		for (int i = 0; i < ndims; ++i) {

			distance += (point.getDoublePosition(i) - centerpoint.getDoublePosition(i))
					* (point.getDoublePosition(i) - centerpoint.getDoublePosition(i));

		}

		return Math.sqrt(distance);

	}

	public double getDistance(RealLocalizable point, RealLocalizable centerpoint) {

		double distance = 0;

		int ndims = point.numDimensions();

		for (int i = 0; i < ndims; ++i) {

			distance += (point.getDoublePosition(i) - centerpoint.getDoublePosition(i))
					* (point.getDoublePosition(i) - centerpoint.getDoublePosition(i));

		}

		return Math.sqrt(distance);

	}

}

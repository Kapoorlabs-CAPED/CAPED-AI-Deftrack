package utility;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import curvatureUtils.Node;
import ellipsoidDetector.Distance;
import kalmanForSegments.Segmentobject;
import mpicbg.models.Point;
import net.imglib2.Cursor;
import net.imglib2.Localizable;
import net.imglib2.RandomAccess;
import net.imglib2.RealLocalizable;
import net.imglib2.RealPoint;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;
import net.imglib2.view.Views;
import pluginTools.InteractiveSimpleEllipseFit;
import pluginTools.RegressionCurveSegment;
import ransac.PointFunctionMatch.PointFunctionMatch;
import ransac.loadFiles.Tracking;
import ransacPoly.HigherOrderPolynomialFunction;
import ransacPoly.MixedPolynomial;
import ransacPoly.MixedPolynomialFunction;
import ransacPoly.RansacFunction;
import ransacPoly.RegressionFunction;
import ransacPoly.Threepointfit;
import varun_algorithm_neighborhood.RectangleShape;
import varun_algorithm_ransac_Ransac.FitLocalEllipsoid;
import varun_algorithm_ransac_Ransac.RansacFunctionEllipsoid;
import varun_algorithm_region.hypersphere.HyperSphere;
import varun_algorithm_region.hypersphere.HyperSphereCursor;

public class CurvatureFunction {

	int evendepth;

	InteractiveSimpleEllipseFit parent;

	public CurvatureFunction(InteractiveSimpleEllipseFit parent) {

		this.parent = parent;
	}

	
	/**
	 * 
	 * Implementation of the curvature function to compute curvature at a point
	 * 
	 * @param previousCord
	 * @param currentCord
	 * @param nextCord
	 * @return
	 */

	public Pair<RegressionFunction, ArrayList<double[]>> getLocalcurvature(ArrayList<double[]> Cordlist,
			RealLocalizable centerpoint, double smoothing, double maxError, int minNumInliers, int degree,
			int secdegree, int label, int time) {

		double[] x = new double[Cordlist.size()];
		double[] y = new double[Cordlist.size()];

		ArrayList<Point> pointlist = new ArrayList<Point>();
		ArrayList<RealLocalizable> list = new ArrayList<RealLocalizable>();
		for (int index = 0; index < Cordlist.size() - 1; ++index) {
			x[index] = Cordlist.get(index)[0];
			y[index] = Cordlist.get(index)[1];

			RealPoint point = new RealPoint(new double[] { x[index], y[index] });
			list.add(point);
			pointlist.add(new Point(new double[] { x[index], y[index] }));

		}

		// Here you choose which method is used to detect curvature

		Pair<RegressionFunction, ArrayList<double[]>> finalfunctionandList = RansacBlock(pointlist, centerpoint, smoothing, maxError, minNumInliers, degree,
					secdegree);

		
		return finalfunctionandList;

	}

	/**
	 * 
	 * Fit a quadratic function via regression (not recommended, use Ransac instead)
	 * 
	 * @param points
	 * @return
	 */
	public RegressionFunction RegressionBlock(ArrayList<Point> points, RealLocalizable center, int degree) {

		// DO not use this
		double[] x = new double[points.size()];
		double[] y = new double[points.size()];
		ArrayList<double[]> Curvaturepoints = new ArrayList<double[]>();

		for (int index = 0; index < points.size(); ++index) {

			x[index] = points.get(index).getW()[0];

			y[index] = points.get(index).getW()[1];

		}

		Threepointfit regression = null;

		if (points.size() > degree + 1) {

			regression = new Threepointfit(x, y, degree);

		}

		double perimeter = 0.5;
		double Kappa = 0;
		for (int index = 0; index < points.size() - 1; ++index) {

			double dx = Math.abs(points.get(index).getW()[0] - points.get(index + 1).getW()[0]);
			double firstderiv = regression.predictderivative(points.get(index).getW()[0]);

			perimeter += Math.sqrt(1 + firstderiv * firstderiv) * dx;

		}
		for (int index = 0; index < points.size() - 1; ++index) {

			double secderiv = regression.predictsecderivative(points.get(index).getW()[0]);
			double firstderiv = regression.predictderivative(points.get(index).getW()[0]);

			Kappa = secderiv / Math.pow((1 + firstderiv * firstderiv), 3.0 / 2.0);

			long[] posf = new long[] { (long) points.get(index).getW()[0], (long) points.get(index).getW()[1] };
			net.imglib2.Point point = new net.imglib2.Point(posf);

			long[] centerloc = new long[] { (long) center.getDoublePosition(0), (long)center.getDoublePosition(1) };
			net.imglib2.Point centpos = new net.imglib2.Point(centerloc);
			
			Pair<Double, Double> Intensity = getIntensity(point, centpos);

			Curvaturepoints.add(new double[] { points.get(index).getW()[0], points.get(index).getW()[1],
					Math.abs(Kappa), perimeter, Kappa, Intensity.getA(), Intensity.getB() });

		}

		RegressionFunction finalfunction = new RegressionFunction(regression, Curvaturepoints);
		return finalfunction;

	}

	

	/**
	 * 
	 * Fitting a quadratic or a linear function using Ransac
	 * 
	 * @param pointlist
	 * @param maxError
	 * @param minNumInliers
	 * @param maxDist
	 * @return
	 */

	public Pair<RegressionFunction, ArrayList<double[]>> RansacBlock(final ArrayList<Point> pointlist,
			RealLocalizable center, double smoothing, double maxError, int minNumInliers, int degree, int secdegree) {

		// Ransac block
		MixedPolynomialFunction<HigherOrderPolynomialFunction, HigherOrderPolynomialFunction, MixedPolynomial<HigherOrderPolynomialFunction, HigherOrderPolynomialFunction>> mixedfunction = new MixedPolynomial<HigherOrderPolynomialFunction, HigherOrderPolynomialFunction>(
				new HigherOrderPolynomialFunction(degree), new HigherOrderPolynomialFunction(secdegree), smoothing);
		ArrayList<double[]> Curvaturepoints = new ArrayList<double[]>();

		final RansacFunction segment = Tracking.findQuadLinearFunction(pointlist, mixedfunction, maxError,
				minNumInliers);

		if (segment != null) {
			double perimeter = 0.5;
			double Kappa = 0;

			for (int index = 0; index < segment.inliers.size() - 1; ++index) {

				PointFunctionMatch p = segment.inliers.get(index);
				PointFunctionMatch pnext = segment.inliers.get(index + 1);
				double dx = Math.abs(p.getP1().getW()[0] - pnext.getP1().getW()[0]);

				double firstderiv = segment.mixedfunction.getB().predictFirstderivative(p.getP1().getW()[0])
						* segment.mixedfunction.getLambda()
						+ segment.mixedfunction.getA().predictFirstderivative(p.getP1().getW()[0])
								* (1 - segment.mixedfunction.getLambda());

				perimeter += Math.sqrt(1 + firstderiv * firstderiv) * dx;

			}

			for (int index = 0; index < segment.inliers.size(); ++index) {
				PointFunctionMatch p = segment.inliers.get(index);
				double secderiv = segment.mixedfunction.getB().predictSecondderivative(p.getP1().getW()[0])
						* segment.mixedfunction.getLambda()
						+ segment.mixedfunction.getA().predictSecondderivative(p.getP1().getW()[0])
								* (1 - segment.mixedfunction.getLambda());
				double firstderiv = segment.mixedfunction.getB().predictFirstderivative(p.getP1().getW()[0])
						* segment.mixedfunction.getLambda()
						+ segment.mixedfunction.getA().predictFirstderivative(p.getP1().getW()[0])
								* (1 - segment.mixedfunction.getLambda());
				Kappa = secderiv / Math.pow((1 + firstderiv * firstderiv), 3.0 / 2.0);

				long[] posf = new long[] { (long) p.getP1().getW()[0], (long) p.getP1().getW()[1] };
				net.imglib2.Point point = new net.imglib2.Point(posf);
				
				long[] centerloc = new long[] { (long) center.getDoublePosition(0), (long)center.getDoublePosition(1) };
				net.imglib2.Point inpoint = new net.imglib2.Point(centerloc);
				Pair<Double, Double> Intensity = getIntensity(point, inpoint);
				Curvaturepoints.add(new double[] { p.getP1().getW()[0], p.getP1().getW()[1], Math.max(0,Kappa), perimeter,
						Kappa, Intensity.getA(), Intensity.getB() });

			}

			RegressionFunction finalfunctionransac = new RegressionFunction(segment.mixedfunction, Curvaturepoints,
					segment.inliers, segment.candidates);

			return new ValuePair<RegressionFunction, ArrayList<double[]>>(finalfunctionransac, Curvaturepoints);

		}

		else

			return null;

	}

	/**
	 * 
	 * Interpolate from (x, y) to (x, y) + 1 by filling up the values in between
	 * 
	 */

	public Pair<double[], double[]> InterpolateValues(final double[] Xcurr, final double[] Xnext,
			Threepointfit regression) {

		double minX = Xcurr[0] < Xnext[0] ? Xcurr[0] : Xnext[0];
		double maxX = Xcurr[0] > Xnext[0] ? Xcurr[0] : Xnext[0];

		double interpolant = 0.1;
		double X = minX;
		double Y = regression.predict(X);

		int steps = (int) ((maxX - minX) / interpolant);
		if (steps > 0) {
			double[] returnValX = new double[steps];
			double[] returnValY = new double[steps];

			returnValX[0] = X;
			returnValY[0] = Y;

			for (int i = 1; i < steps; ++i) {

				returnValX[i] = X + i * interpolant;
				returnValY[i] = regression.predict(returnValX[i]);

			}

			Pair<double[], double[]> interpolXY = new ValuePair<double[], double[]>(returnValX, returnValY);

			return interpolXY;
		}

		else {
			Pair<double[], double[]> interpolXY = new ValuePair<double[], double[]>(new double[] { X, Y },
					new double[] { X, Y });

			return interpolXY;

		}
	}

	/**
	 * 
	 * Evenly or unevenly spaced data derivative is computed via Lagrangian
	 * interpolation
	 * 
	 * @param previousCord
	 * @param currentCord
	 * @param nextCord
	 */
	public double[] InterpolatedFirstderiv(double[] previousCord, double[] currentCord, double[] nextCord) {

		double y0 = previousCord[1];
		double y1 = currentCord[1];
		double y2 = nextCord[1];

		double x0 = previousCord[0];
		double x1 = currentCord[0];
		double x2 = nextCord[0];

		double x01 = x0 - x1;
		double x02 = x0 - x2;
		double x12 = x1 - x2;
		if (x01 != 0 && x02 != 0 && x12 != 0) {
			double diffatx0 = y0 * (x01 + x02) / (x01 * x02) - y1 * x02 / (x01 * x12) + y2 * x01 / (x02 * x12);
			double diffatx2 = -y0 * x12 / (x01 * x02) + y1 * x02 / (x01 * x12) - y2 * (x02 + x12) / (x02 * x12);
			double diffatx1 = y0 * (x12) / (x01 * x02) + y1 * (1.0 / x12 - 1.0 / x01) - y2 * x01 / (x02 * x12);

			double[] threepointdiff = { diffatx0, diffatx1, diffatx2 };

			return threepointdiff;
		} else

			return new double[] { 0, 0, 0 };

	}

	/**
	 * 
	 * Compute perimeter of a curve by adding up the distance between ordered set of
	 * points
	 * 
	 * @param orderedtruths
	 * @param ndims
	 * @return
	 */
	public double getPerimeter(List<RealLocalizable> orderedtruths, int ndims) {

		double perimeter = 0;
		for (int index = 1; index < orderedtruths.size(); ++index) {

			double[] lastpoint = new double[ndims];
			double[] currentpoint = new double[ndims];

			orderedtruths.get(index - 1).localize(lastpoint);
			orderedtruths.get(index).localize(currentpoint);
			perimeter += Distance.DistanceSq(lastpoint, currentpoint);

		}

		return perimeter;
	}

	/**
	 * Obtain intensity in the user defined
	 * 
	 * @param point
	 * @return
	 */

	public Pair<Double, Double> getIntensity(Localizable point, Localizable centerpoint) {

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
		
		 HyperSphere< FloatType > hyperSphere = new HyperSphere<FloatType>( parent.CurrentViewOrig, ranac, (int)parent.insidedistance );
		HyperSphereCursor<FloatType> localcursor = hyperSphere.localizingCursor();
		int Area = 1;
		while(localcursor.hasNext()) {
			
			localcursor.fwd();
			
			ranacsec.setPosition(localcursor);
			
			ranacsec.localize(currentPosition);
			
			
			double currentdistance = getDistance(localcursor, centerpoint);
			if ((currentdistance - mindistance) <= parent.insidedistance) {
			Intensity += localcursor.get().getRealDouble();
			IntensitySec += ranacsec.get().getRealDouble();
			Area++;
			}
		}
	
		
			return new ValuePair<Double, Double>(Intensity/ Area, IntensitySec/Area);
		}
	

	public double getDistance(Localizable point, Localizable centerpoint) {
		
		double distance = 0;
		
		int ndims = point.numDimensions();
		
		
		for (int i = 0; i < ndims; ++i) {
			
			distance+= (point.getDoublePosition(i) - centerpoint.getDoublePosition(i)) * (point.getDoublePosition(i) - centerpoint.getDoublePosition(i)) ;
			
		}
		
		return Math.sqrt(distance);
		
	}
	
}

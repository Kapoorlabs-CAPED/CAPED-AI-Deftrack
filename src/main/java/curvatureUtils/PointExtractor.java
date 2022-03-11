package curvatureUtils;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;


import curvatureFinder.LineProfileCircle;
import ellipsoidDetector.Distance;
import ellipsoidDetector.Intersectionobject;
import ij.gui.EllipseRoi;
import ij.gui.Line;
import ij.gui.OvalRoi;
import ij.gui.Roi;
import net.imglib2.RealLocalizable;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;
import pluginTools.InteractiveSimpleEllipseFit;
import ransacPoly.QuadraticFunction;
import ransacPoly.RegressionFunction;
import utility.Curvatureobject;
import utility.DisplayAuto;
import varun_algorithm_ransac_Ransac.DisplayasROI;

public class PointExtractor {

	/**
	 * The functions can come from either regression or from Ransac, this routine
	 * converts the curvature object to intersection object
	 * 
	 * @param localCurvature
	 * @param functions
	 * @return
	 */
	public static Pair<Intersectionobject, Intersectionobject> CurvaturetoIntersection(final InteractiveSimpleEllipseFit parent, final ArrayList<Curvatureobject> localCurvature,
			final ArrayList<RegressionFunction> functions, final ConcurrentHashMap<Integer, ArrayList<LineProfileCircle>> LineScanIntensity, final RealLocalizable centerpoint, double smoothing) {

		ArrayList<Line> resultlineroi = new ArrayList<Line>();
		ArrayList<double[]> Sparselinelist = new ArrayList<double[]>();
		ArrayList<double[]> linelist = new ArrayList<double[]>();
		double[] X = new double[localCurvature.size()];
		double[] Y = new double[localCurvature.size()];
		double[] Z = new double[localCurvature.size()];
		double[] Zdist = new double[localCurvature.size()];
		double[] I = new double[localCurvature.size()];
		double[] Isec = new double[localCurvature.size()];
		int celllabel, t, z;
		double perimeter;
		celllabel = localCurvature.get(0).Label;
		t = localCurvature.get(0).t;
		z = localCurvature.get(0).z;
		perimeter = localCurvature.get(0).perimeter;
		

		for (int i = 0; i < functions.size(); ++i) {

			RegressionFunction regression = functions.get(i);
			
			for (int index = 0; index < regression.Curvaturepoints.size() - 1; ++index) {

				
				int xs = (int) regression.Curvaturepoints.get(index)[0];
				int xe = (int) regression.Curvaturepoints.get(index + 1)[0];

				int ys = 0;
				int ye = 0;
				// If the method of fitting a function was regression
				if (regression.regression != null) {

					ys = (int) regression.regression.predict(xs);
					ye = (int) regression.regression.predict(xe);

				}
				// If the method of fitting a function was Ransac
				else if (regression.mixedfunction != null) {

					ys = (int) (smoothing * (regression.mixedfunction.getB()).predict(xs)
							+ (1 - smoothing) * regression.mixedfunction.getA().predict(xs));
					ye = (int) (smoothing * (regression.mixedfunction.getB()).predict(xe)
							+ (1 - smoothing) * regression.mixedfunction.getA().predict(xe));

				}

				else if (regression.back != null) {
					ys = (int) (regression.back).predict(xs);
					ye = (int) (regression.back).predict(xe);

				}

				// Store the information to draw a line as a lineroi
				Line line = new Line(xs, ys, xe, ye);
				resultlineroi.add(line);
			}
			

			

	
		
			
			for (int index = 0; index < regression.Curvaturepoints.size() ; ++index) {
				
				
				X[index] = regression.Curvaturepoints.get(index)[0];
				Y[index] = regression.Curvaturepoints.get(index)[1];
				Z[index] = regression.Curvaturepoints.get(index)[2];
				I[index] = regression.Curvaturepoints.get(index)[4];
				Isec[index] = regression.Curvaturepoints.get(index)[5];
				Zdist[index] = regression.Curvaturepoints.get(index)[6];
				// Make the line list for making intersection object
				Sparselinelist.add(new double[] { X[index], Y[index], Z[index], I[index], Isec[index], perimeter, Zdist[index] });
				
				
					
					
				
			}			
			
			

		}

		for (int index = 0; index < localCurvature.size() ; ++index) {

			X[index] = localCurvature.get(index).cord[0];
			Y[index] = localCurvature.get(index).cord[1];
			Z[index] = localCurvature.get(index).radiusCurvature;
			Zdist[index] = localCurvature.get(index).distCurvature;
			I[index] = localCurvature.get(index).Intensity;
			Isec[index] = localCurvature.get(index).SecIntensity;
			// Make the line list for making intersection object
			linelist.add(new double[] { X[index], Y[index], Z[index], I[index], Isec[index], perimeter, Zdist[index] });

		}

		// Compute the geometric mean of the object, which we would need for tracking
		double[] mean = new double[] { centerpoint.getDoublePosition(0), centerpoint.getDoublePosition(1) };

		Intersectionobject currentIntersection = new Intersectionobject(mean,LineScanIntensity,linelist, perimeter, celllabel, t, z);
		
		Intersectionobject SparsecurrentIntersection = new Intersectionobject(mean,LineScanIntensity,Sparselinelist,  perimeter, celllabel, t, z);
		
		
		return new ValuePair<Intersectionobject, Intersectionobject> (currentIntersection , SparsecurrentIntersection);

	}

	/**
	 * Take mean of X, Y set of values
	 * 
	 * @param X
	 * @param Y
	 * @return
	 */

	public static double[] GeometricCenter(double[] X, double[] Y) {

		int length = X.length;
		double Xmean = 0;
		double Ymean = 0;

		for (int i = 0; i < length; ++i) {

			Xmean += X[i];
			Ymean += Y[i];

		}

		double[] mean = new double[] { Xmean / length, Ymean / length };

		return mean;

	}

}

package ransac.Lengthdistro;

import java.awt.Dimension;
import java.io.File;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.title.TextTitle;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import mpicbg.models.NotEnoughDataPointsException;
import mpicbg.models.Point;

import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;
import ransac.FLSobject;
import ransac.Display.DisplayPoints;
import ransac.loadFiles.Tracking;
import ransacPoly.InterpolatedPolynomial;

/**
 * 
 * Compute the length distribution at a certain time point or give an averaged
 * distribution over time Take logarithm of distribution and fit a straight line
 * to determine the fit parameters
 * 
 * @author aimachine
 *
 */

public class LengthDistribution {

	/**
	 * Get the maximum length in the file
	 * 
	 * @param file
	 * @return
	 */

	public static double Lengthdistro(File file) {

		ArrayList<FLSobject> currentobject = Tracking.loadMTStat(file);

		double maxlength = 0;

		if (currentobject != null) {
			for (int index = 0; index < currentobject.size(); ++index) {

				for (int secindex = 0; secindex < currentobject.size(); ++secindex) {

					maxlength = Math.max(currentobject.get(index).length, currentobject.get(secindex).length);

				}
			}

		}

		return maxlength;

	}

	/**
	 * Returns a length list at a certain time point
	 * 
	 * @param file
	 * @param framenumber
	 * @return
	 */

	public static ArrayList<Pair<Integer, Double>> LengthdistroatTime(File file, final int framenumber) {

		ArrayList<FLSobject> currentobject = Tracking.loadMTStat(file);

		ArrayList<Pair<Integer, Double>> lengthlist = new ArrayList<Pair<Integer, Double>>();

		if (currentobject != null) {
			for (int index = 0; index < currentobject.size(); ++index) {

				if (currentobject.get(index).Framenumber == framenumber) {
					lengthlist.add(new ValuePair<Integer, Double>(currentobject.get(index).seedID,
							currentobject.get(index).length));

				}

			}

		}

		return lengthlist;

	}

	/**
	 * 
	 * Compute average length distribution from all the movies
	 * 
	 * 
	 * @param AllMovies
	 * @param calibration
	 */

	public static void GetLengthDistributionArray(ArrayList<File> AllMovies, double[] calibration) {

		ArrayList<Double> maxlist = new ArrayList<Double>();
		for (int i = 0; i < AllMovies.size(); ++i) {

			double maxlength = LengthDistribution.Lengthdistro(AllMovies.get(i));

			if (maxlength != Double.NaN && maxlength > 0)
				maxlist.add(maxlength);

		}
		Collections.sort(maxlist);

		int max = (int) Math.round(maxlist.get(maxlist.size() - 1)) + 1;
		XYSeries counterseries = new XYSeries("MT length distribution");
		XYSeries Logcounterseries = new XYSeries("MT Log length distribution");
		final ArrayList<Point> points = new ArrayList<Point>();
		for (int length = 0; length < max; ++length) {

			HashMap<Integer, Integer> frameseed = new HashMap<Integer, Integer>();

			for (int i = 0; i < AllMovies.size(); ++i) {

				File file = AllMovies.get(i);

				double currentlength = LengthDistribution.Lengthdistro(file);

				ArrayList<FLSobject> currentobject = Tracking.loadMTStat(file);

				if (currentlength > length) {

					MakeSeed(currentobject, frameseed, length);
				}

			}

			// Get maxima length, count
			int maxvalue = Integer.MIN_VALUE;

			for (int key : frameseed.keySet()) {

				int Count = frameseed.get(key);

				if (Count >= maxvalue)
					maxvalue = Count;
			}

			if (maxvalue != Integer.MIN_VALUE) {
				counterseries.add(length, maxvalue);

				if (maxvalue > 0) {
					Logcounterseries.add((length), Math.log(maxvalue));
					points.add(new Point(new double[] { length, Math.log(maxvalue) }));
				}

			}
		}

		final XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(counterseries);
		final XYSeriesCollection Logdataset = new XYSeriesCollection();
		Logdataset.addSeries(Logcounterseries);

		final JFreeChart chart = ChartFactory.createScatterPlot("MT length distribution", "Length (micrometer)",
				"Number of MT", dataset);

		// Fitting line to log of the length distribution
		InterpolatedPolynomial poly = new InterpolatedPolynomial(1);
		try {

			poly.fitFunction(points);

		} catch (NotEnoughDataPointsException e) {

		}
		dataset.addSeries(Tracking.drawexpFunction(poly, counterseries.getMinX(), counterseries.getMaxX(), 0.5,
				"Exponential fit"));
		NumberFormat nf = NumberFormat.getInstance(Locale.ENGLISH);
		nf.setMaximumFractionDigits(3);
		TextTitle legendText = new TextTitle("Mean Length" + " : " + nf.format(-1.0 / poly.getCoefficients(1)) + "  "
				+ "Standard Deviation" + " : " + nf.format(poly.SSE));
		legendText.setPosition(RectangleEdge.RIGHT);

		DisplayPoints.display(chart, new Dimension(800, 500));
		chart.addSubtitle(legendText);

	}

	/**
	 * 
	 * Compute length distribution at a certain time point over all files
	 * 
	 * @param AllMovies
	 * @param calibration
	 * @param framenumber
	 */

	public static void GetLengthDistributionArrayatTime(ArrayList<File> AllMovies, double[] calibration,
			final int framenumber) {

		ArrayList<Double> maxlist = new ArrayList<Double>();
		for (int i = 0; i < AllMovies.size(); ++i) {

			ArrayList<Pair<Integer, Double>> lengthlist = LengthDistribution.LengthdistroatTime(AllMovies.get(i),
					framenumber);

			for (int index = 0; index < lengthlist.size(); ++index) {
				if (lengthlist.get(index).getB() != Double.NaN && lengthlist.get(index).getB() > 0)
					maxlist.add(lengthlist.get(index).getB());

			}
		}
		Collections.sort(maxlist);

		int max = 0;
		if (maxlist.size() > 0)
			max = (int) Math.round(maxlist.get(maxlist.size() - 1)) + 1;
		XYSeries counterseries = new XYSeries("MT length distribution");
		XYSeries Logcounterseries = new XYSeries("MT Log length distribution");
		final ArrayList<Point> points = new ArrayList<Point>();
		for (int length = 0; length < max; ++length) {

			HashMap<Integer, Integer> frameseed = new HashMap<Integer, Integer>();

			for (int i = 0; i < AllMovies.size(); ++i) {

				File file = AllMovies.get(i);

				ArrayList<FLSobject> currentobject = Tracking.loadMTStat(file);

				MakeSeed(currentobject, frameseed, length, framenumber);

			}

			// Get maxima length, count
			int maxvalue = Integer.MIN_VALUE;

			for (int key : frameseed.keySet()) {

				int Count = frameseed.get(key);

				if (Count >= maxvalue)
					maxvalue = Count;
			}

			if (maxvalue != Integer.MIN_VALUE) {
				counterseries.add(length, maxvalue);

				if (maxvalue > 0) {

					Logcounterseries.add((length), Math.log(maxvalue));
					points.add(new Point(new double[] { length, Math.log(maxvalue) }));
				}

			}
		}

		final XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(counterseries);
		final XYSeriesCollection Logdataset = new XYSeriesCollection();
		Logdataset.addSeries(Logcounterseries);

		final JFreeChart chart = ChartFactory.createScatterPlot("MT length distribution", "Length (micrometer)",
				"Number of MT", dataset);

		// Fitting line to log of the length distribution
		InterpolatedPolynomial poly = new InterpolatedPolynomial(1);
		try {

			poly.fitFunction(points);

		} catch (NotEnoughDataPointsException e) {

		}
		dataset.addSeries(Tracking.drawexpFunction(poly, counterseries.getMinX(), counterseries.getMaxX(), 0.5,
				"Exponential fit"));
		NumberFormat nf = NumberFormat.getInstance(Locale.ENGLISH);
		nf.setMaximumFractionDigits(3);
		TextTitle legendText = new TextTitle("Mean Length" + " : " + nf.format(-1.0 / poly.getCoefficients(1)) + "  "
				+ "Standard Deviation" + " : " + nf.format(poly.SSE));
		legendText.setPosition(RectangleEdge.RIGHT);

		DisplayPoints.display(chart, new Dimension(800, 500));
		chart.addSubtitle(legendText);

	}

	
	/**
	 * 
	 * Put frame number and count the microtubules having a certain length at a given time in all movies
	 * 
	 * @param currentobject
	 * @param frameseed
	 * @param length
	 * @param framenumber
	 */
	public static void MakeSeed(final ArrayList<FLSobject> currentobject, final HashMap<Integer, Integer> frameseed,
			final int length, final int framenumber) {

		for (int index = 0; index < currentobject.size(); ++index) {
			ArrayList<Integer> seedlist = new ArrayList<Integer>();
			if (currentobject.get(index).length >= length && currentobject.get(index).Framenumber == framenumber) {
				seedlist.add(currentobject.get(index).seedID);
				if (frameseed.get(currentobject.get(index).Framenumber) != null
						&& frameseed.get(currentobject.get(index).Framenumber) != Double.NaN) {

					int currentcount = frameseed.get(currentobject.get(index).Framenumber);
					frameseed.put(currentobject.get(index).Framenumber, seedlist.size() + currentcount);
				} else if (currentobject.get(index) != null)
					frameseed.put(currentobject.get(index).Framenumber, seedlist.size());

			}

		}

	}

	/**
	 * 
	 * Put frame number and count the microtubules having a certain length at any given time in all movies
	 * 
	 * @param currentobject
	 * @param frameseed
	 * @param length
	 */
	
	public static void MakeSeed(final ArrayList<FLSobject> currentobject, final HashMap<Integer, Integer> frameseed,
			final int length) {

		for (int index = 0; index < currentobject.size(); ++index) {
			ArrayList<Integer> seedlist = new ArrayList<Integer>();
			if (currentobject.get(index).length >= length) {
				seedlist.add(currentobject.get(index).seedID);
				if (frameseed.get(currentobject.get(index).Framenumber) != null
						&& frameseed.get(currentobject.get(index).Framenumber) != Double.NaN) {

					int currentcount = frameseed.get(currentobject.get(index).Framenumber);
					frameseed.put(currentobject.get(index).Framenumber, seedlist.size() + currentcount);
				} else if (currentobject.get(index) != null)
					frameseed.put(currentobject.get(index).Framenumber, seedlist.size());

			}

		}

	}

}

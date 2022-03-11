package ransac.Catastrophy;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.util.ShapeUtils;
import org.jfree.data.xy.XYSeriesCollection;

import ij.measure.ResultsTable;
import mpicbg.models.Point;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;
import ransac.Rateobject;
import ransac.PointFunctionMatch.PointFunctionMatch;
import ransac.loadFiles.Tracking;
import ransacPoly.AbstractFunction2D;
import ransacPoly.LinearFunction;
import ransacPoly.Polynomial;

public class DetectCatastrophy {

	@SuppressWarnings("rawtypes")
	public List<Pair<Float, Float>> ManualCat(
			ArrayList<Pair<AbstractFunction2D,ArrayList<PointFunctionMatch>>> segments, ArrayList<Rateobject> allrates,
			double shrinkrate, ResultsTable rt, double minDistanceCatastrophe, XYSeriesCollection dataset, 
			JFreeChart chart, double[] calibrations, double negtimediff, double averageshrink, int i , int catindex, int negcount, int segment) {

		
		List<Pair<Float, Float>> catstarttimerates = new ArrayList<Pair<Float, Float>>();
		
			
			System.out.println("Overriding Ransac, Detecting without fiting a function");
			
			
		for (int catastrophy = 0; catastrophy < segments.size() - 1; ++catastrophy) {

			final Pair<AbstractFunction2D, ArrayList<PointFunctionMatch>> start = segments.get(catastrophy);
			final Pair<AbstractFunction2D, ArrayList<PointFunctionMatch>> end = segments.get(catastrophy + 1);

			double tStart = start.getB().get(start.getB().size() - 1).getP1().getL()[0];
			double tEnd = end.getB().get(0).getP1().getL()[0];

			final double lStart = start.getB().get(start.getB().size() - 1).getP1().getL()[1];
			final double lEnd = end.getB().get(0).getP1().getL()[1];

			if (Math.abs(lStart - lEnd) >= minDistanceCatastrophe) {

				final double slope = (lEnd - lStart) / (tEnd - tStart);
				final double intercept = lEnd - slope * tEnd;

				LinearFunction linearfunc = new LinearFunction(slope, intercept);
				++i;
				dataset.addSeries(Tracking.drawFunction((Polynomial) linearfunc, tStart, tEnd, 0.1, lStart, lEnd,
						"CManual " + catindex + catastrophy));
				Tracking.setColor(chart, i, new Color(255, 192, 255));
				Tracking.setDisplayType(chart, i, true, false);
				Tracking.setStroke(chart, i, 2f);
				++i;

				double startX = tStart;
				double endX = tEnd;

				double linearrate = linearfunc.getCoefficient(1);

				if (linearrate < 0) {

					negcount++;
					negtimediff += endX - startX;

					shrinkrate = linearrate;
					averageshrink += linearrate;

					rt.incrementCounter();
					rt.addValue("Start time", startX * calibrations[2]);
					rt.addValue("End time", endX * calibrations[2]);
					rt.addValue("Growth Rate", linearrate * calibrations[0] / calibrations[2]);
					Pair<Float, Float> startrate = new ValuePair<Float, Float>((float) startX, (float) linearrate);

					catstarttimerates.add(startrate);

					ArrayList<PointFunctionMatch> p = new ArrayList<PointFunctionMatch>();

					p.add(new PointFunctionMatch(new Point(new double[] { tStart, lStart })));
					p.add(new PointFunctionMatch(new Point(new double[] { tEnd, lEnd })));

					Rateobject rate = new Rateobject(linearrate * calibrations[0] / calibrations[2],
							(int) (startX * calibrations[2]), (int) (endX * calibrations[2]));
					allrates.add(rate);

					dataset.addSeries(Tracking.drawPoints(Tracking.toPairList(p), calibrations,
							"CManual(points) " + catindex + catastrophy));

					Tracking.setColor(chart, i, new Color(255, 192, 255));
					Tracking.setDisplayType(chart, i, false, true);
					Tracking.setShape(chart, i, ShapeUtils.createDiamond(4f));

					++i;
					++segment;
				}

			}
		}
		return catstarttimerates;
		
	}

	
	
}

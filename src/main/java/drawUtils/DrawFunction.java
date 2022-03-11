package drawUtils;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


import fiji.tool.SliceListener;
import fiji.tool.SliceObserver;
import ij.ImagePlus;
import ij.gui.Overlay;
import net.imglib2.Cursor;
import net.imglib2.FinalInterval;
import net.imglib2.Localizable;
import net.imglib2.Point;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.RealPoint;
import net.imglib2.algorithm.region.BresenhamLine;
import net.imglib2.type.numeric.RealType;
import net.imglib2.util.Intervals;
import net.imglib2.view.Views;
import ransacPoly.RegressionFunction;
import varun_algorithm_region.hypersphere.HyperSphere;
import net.imglib2.roi.geom.GeomMasks;
import net.imglib2.roi.geom.GeomMaths;
import net.imglib2.roi.geom.real.Line;

public class DrawFunction {

	public DrawFunction() {
	};

	public ImagePlus imp;
	public static int tolerance = 2;
	public static double slopetolerance = 1.0E-10;

	public DrawFunction(ImagePlus imp) {

		this.imp = imp;
		SliceObserver sliceObserver = new SliceObserver(imp, new ImagePlusListener());
	}

	public static void DrawPolynomial(final ImagePlus imp, final RegressionFunction regression) {

		Overlay overlay = new Overlay();

		imp.setOverlay(overlay);

		for (int index = 0; index < regression.Curvaturepoints.size() - 1; ++index) {
			int xs = (int) regression.Curvaturepoints.get(index)[0];
			int xe = (int) regression.Curvaturepoints.get(index + 1)[0];

			int ys = (int) regression.regression.predict(xs);
			int ye = (int) regression.regression.predict(xe);
			ij.gui.Line line = new ij.gui.Line(xs, ys, xe, ye);
			overlay.add(line);
			line.setStrokeColor(Color.BLUE);

		}

		imp.updateAndDraw();

	}

	public ImagePlus getImp() {
		return this.imp;
	}

	protected class ImagePlusListener implements SliceListener {
		@Override
		public void sliceChanged(ImagePlus arg0) {

			imp.show();
			Overlay o = imp.getOverlay();

			if (getImp().getOverlay() == null) {
				o = new Overlay();
				getImp().setOverlay(o);
			}

			o.clear();
			getImp().getOverlay().clear();

			int time = imp.getSlice();
			System.out.println(time);

		}

	}

	
	
	/**
	 * 
	 * Line Drawing on a Random AccesibleInterval using Brensen
	 * 
	 * @param originalimg
	 * @param startpoint
	 * @param endpoint
	 * @param Intensity
	 */
	public static <T extends RealType<T>> void DrawBrensLines(final RandomAccessibleInterval<T> originalimg,
			final long[] startpoint, final long[] endpoint, final double Intensity) {

		RandomAccess<T> regionranac = originalimg.randomAccess();

		Point pointA = new Point(startpoint);
		Point pointB = new Point(endpoint);
		
		  
		  BresenhamLine<T> newline = new BresenhamLine<T>(regionranac, pointA, pointB);
		  Cursor<T> cursor = newline.copyCursor();
		  
		  
		  while (cursor.hasNext()) {
		  
		  cursor.fwd();
		  
		  cursor.get().setReal(Intensity);
		  
		  }
		 
		
		
		
	}
	
	/**
	 * 
	 * Line Drawing on a Random AccesibleInterval using Geom
	 * 
	 * @param originalimg
	 * @param startpoint
	 * @param endpoint
	 * @param Intensity
	 */
	/*
	public static <T extends RealType<T>> void DrawGeomLines(final RandomAccessibleInterval<T> originalimg,
			final long[] startpoint, final long[] endpoint, final double Intensity) {


		Point pointA = new Point(startpoint);
		Point pointB = new Point(endpoint);


		Line<RealPoint> line = GeomMasks.line(pointA, pointB);

		FinalInterval interval = Intervals.createMinMax(new long[] { (long) line.realMin(0), (long) line.realMin(1),
				(long) line.realMax(0), (long) line.realMax(1) });

		RandomAccessibleInterval<T> region = Views.interval(originalimg, interval);

		Cursor<T> regioncursor = Views.iterable(region).localizingCursor();

		double slope = (endpoint[1] - startpoint[1]) / (endpoint[0] - startpoint[0] + slopetolerance);
		double intercept = endpoint[1] - slope * endpoint[0];

		while (regioncursor.hasNext()) {

			regioncursor.fwd();
			if (Math.abs(
					regioncursor.getLongPosition(1) - slope * regioncursor.getLongPosition(0) - intercept) <= tolerance)
				regioncursor.get().setReal(Intensity);

		}
		
		

	}
	*/
	
	/**
	 * 
	 * Line Drawing on a Random AccesibleInterval using GeomBresn
	 * 
	 * @param originalimg
	 * @param startpoint
	 * @param endpoint
	 * @param Intensity
	 */
	public static <T extends RealType<T>> void DrawGeomBresnLines(final RandomAccessibleInterval<T> originalimg,
			final double[] startpoint, final double[] endpoint, final double Intensity) {


		RealPoint pointA = new RealPoint(startpoint);
		RealPoint pointB = new RealPoint(endpoint);

		RandomAccess<T> regionranac = originalimg.randomAccess();
		List<RealLocalizable> vertexlist = new ArrayList<RealLocalizable>();
		vertexlist.add(pointA);
		vertexlist.add(pointB);
		vertexlist.add(pointA);
		
		List<Localizable> pointlist = GeomMaths.bresenham(vertexlist);
		
		Iterator<Localizable> pointiter = pointlist.iterator();
		
		while(pointiter.hasNext()) {
			
			Localizable currentpoint = pointiter.next();
			
			regionranac.setPosition(currentpoint);
			regionranac.get().setReal(Intensity);
			
		}
		
		
		

	}
	

}

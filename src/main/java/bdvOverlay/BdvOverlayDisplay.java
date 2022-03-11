package bdvOverlay;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

import bdv.BigDataViewer;
import bdv.util.AxisOrder;
import bdv.util.Bdv;
import bdv.util.BdvFunctions;
import bdv.util.BdvOverlay;
import curvatureUtils.ClockDisplayer;
import ij.gui.TextRoi;
import net.imglib2.realtransform.AffineTransform2D;
import net.imglib2.util.Pair;
import pluginTools.InteractiveSimpleEllipseFit;

public class BdvOverlayDisplay {

	ArrayList<ClockDisplayer> masterclock;
	InteractiveSimpleEllipseFit parent;
	Bdv bdv;

	public BdvOverlayDisplay(InteractiveSimpleEllipseFit parent, ArrayList<ClockDisplayer> masterclock, Bdv bdv) {
		this.bdv = bdv;
		this.parent = parent;
		this.masterclock = masterclock;

	}

	public BdvOverlay GetDisplay() {

		final BdvOverlay overlay = new BdvOverlay() {

			@Override
			protected void draw(Graphics2D g) {

				g.setColor(new Color(info.getColor().get()));

				AffineTransform2D transform = new AffineTransform2D();
				this.getCurrentTransform2D(transform);
				for (int i = 0; i < masterclock.size(); ++i) {

					Pair<double[], double[]> startend = masterclock.get(i).startendline;

					String name = masterclock.get(i).name;

					int ndims = startend.getA().length;
					double[] transformstart = new double[ndims];
					double[] transformend = new double[ndims];
					transform.apply(startend.getA(), transformstart);
					transform.apply(startend.getB(), transformend);

					g.drawString(name, (long) transformstart[0], (long) transformstart[1]);
					g.drawLine((int) transformstart[0], (int) transformstart[1], (int) transformend[0],
							(int) transformend[1]);
					

				}
			}

		};

		BdvFunctions.showOverlay(overlay, "overlay", Bdv.options().addTo(bdv));

		return overlay;

	}

}

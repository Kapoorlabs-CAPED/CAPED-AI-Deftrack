package curvatureFinder;

import java.awt.Color;
import java.util.List;

import ij.gui.Arrow;
import ij.gui.OvalRoi;
import net.imglib2.RealLocalizable;
import net.imglib2.util.Pair;
import pluginTools.InteractiveSimpleEllipseFit;

public class DisplayListOverlay {

	
	
	public static void ArrowDisplay(final InteractiveSimpleEllipseFit parent,Pair<RealLocalizable, List<RealLocalizable>> Ordered, String uniqueID) {
		
		for (int i = 0; i < Ordered.getB().size() - 10; i += 10) {

			double X = Ordered.getB().get(i).getDoublePosition(0);
			double Y = Ordered.getB().get(i).getDoublePosition(1);

			double nextX = Ordered.getB().get(i + 10).getDoublePosition(0);
			double nextY = Ordered.getB().get(i + 10).getDoublePosition(1);

			Arrow line = new Arrow(X, Y, nextX, nextY);
			line.setStrokeWidth(0.01);
			parent.overlay.add(line);
		}

		OvalRoi oval = new OvalRoi((int) Ordered.getA().getDoublePosition(0), (int) Ordered.getA().getDoublePosition(1),
				10, 10);
		oval.setStrokeWidth(10);
		oval.setStrokeColor(Color.GREEN);
		parent.overlay.add(oval);
		parent.imp.updateAndDraw();
		parent.Refcord = Ordered.getA();

		parent.AllRefcords.put(uniqueID, parent.Refcord);
	}
	
}

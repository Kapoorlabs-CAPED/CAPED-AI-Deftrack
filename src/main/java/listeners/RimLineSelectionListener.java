package listeners;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import ij.ImagePlus;
import ij.gui.OvalRoi;
import ij.gui.Overlay;
import ij.plugin.frame.RoiManager;
import net.imglib2.img.display.imagej.ImageJFunctions;
import pluginTools.InteractiveSimpleEllipseFit;

public class RimLineSelectionListener implements ActionListener {

	
	final InteractiveSimpleEllipseFit parent;
	RoiManager roimanager;
	
	
	public RimLineSelectionListener(final InteractiveSimpleEllipseFit parent) {
		
    this.parent = parent;
		
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		 roimanager = RoiManager.getInstance();

			if (roimanager == null) {
				roimanager = new RoiManager();
			}
		
			LineSelectDialog();
	}
	
	
	public void LineSelectDialog() {
		
		ImagePlus localimp = ImageJFunctions.show(parent.CurrentViewOrig);
		parent.overlay = localimp.getOverlay();

		if (parent.overlay == null) {

			parent.overlay = new Overlay();
			localimp.setOverlay(parent.overlay);
		}
		
		OvalRoi oval = new OvalRoi(parent.globalMaxcord.getFloatPosition(0),parent.globalMaxcord.getFloatPosition(1), 10, 10);
		oval.setFillColor(Color.RED);
		parent.overlay.add(oval);
		
		localimp.updateAndDraw();
		
	}

}

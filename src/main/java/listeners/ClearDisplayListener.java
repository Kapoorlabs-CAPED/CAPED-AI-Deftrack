package listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import mpicbg.imglib.image.display.Display;
import pluginTools.InteractiveSimpleEllipseFit;
import utility.DisplayAuto;

public class ClearDisplayListener implements ActionListener {

	
	final InteractiveSimpleEllipseFit parent;

	public ClearDisplayListener(final InteractiveSimpleEllipseFit parent) {

		this.parent = parent;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		String uniqueID = Integer.toString(parent.thirdDimension) + Integer.toString(parent.fourthDimension);
		
		
		// Remove the current rois
		parent.ZTRois.remove(uniqueID);
		DisplayAuto.Display(parent);
		parent.imp.updateAndDraw();
		
	}
	
	
	
	
	
}

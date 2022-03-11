package interactivePreprocessing;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import watershedGUI.CovistoWatershedPanel;

public class PREdist implements ItemListener {

	final InteractiveMethods parent;

	public PREdist(final InteractiveMethods parent) {

		this.parent = parent;

	}

	@Override
	public void itemStateChanged(final ItemEvent arg0) {

		if (arg0.getStateChange() == ItemEvent.DESELECTED) {
			CovistoWatershedPanel.disttransform = false;
		    CovistoWatershedPanel.displayDist.setEnabled(false);	
		    parent.displayDistTransimg = false;
			
		}
		else if (arg0.getStateChange() == ItemEvent.SELECTED) {
			CovistoWatershedPanel.disttransform = true;
			 CovistoWatershedPanel.displayDist.setEnabled(true);	
			 parent.displayDistTransimg = true;
		}

	}

}

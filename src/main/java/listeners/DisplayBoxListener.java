package listeners;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import pluginTools.InteractiveSimpleEllipseFit;

public class DisplayBoxListener  implements ItemListener {
	
	final InteractiveSimpleEllipseFit parent;
	
	public DisplayBoxListener(final InteractiveSimpleEllipseFit parent) {
		
		this.parent = parent;
	}
	
	@Override
	public void itemStateChanged(final ItemEvent arg0) {
		
		if (arg0.getStateChange() == ItemEvent.DESELECTED) {
	        
			parent.displayIntermediateBox = false;
			parent.displayIntermediate = false;
		}
		
        else if (arg0.getStateChange() == ItemEvent.SELECTED) {
		
		parent.displayIntermediateBox = true;
		parent.displayIntermediate = true;
		
	}

	
	}
}

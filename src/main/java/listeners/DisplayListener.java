package listeners;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import pluginTools.InteractiveSimpleEllipseFit;

public class DisplayListener  implements ItemListener {
	
	final InteractiveSimpleEllipseFit parent;
	
	public DisplayListener(final InteractiveSimpleEllipseFit parent) {
		
		this.parent = parent;
	}
	
	@Override
	public void itemStateChanged(final ItemEvent arg0) {
		
		if (arg0.getStateChange() == ItemEvent.DESELECTED) {
	        
			parent.displayIntermediate = false;
		}
		
        else if (arg0.getStateChange() == ItemEvent.SELECTED) {
		
		parent.displayIntermediate = true;
		
		
	}

	
	}
}

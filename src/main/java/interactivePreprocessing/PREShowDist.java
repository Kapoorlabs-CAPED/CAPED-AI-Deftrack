package interactivePreprocessing;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class PREShowDist implements ItemListener {
	
	
	final InteractiveMethods parent;
	
	public PREShowDist (final InteractiveMethods parent) {
		
		this.parent = parent;
		
	}
	
	
	@Override
	public void itemStateChanged(final ItemEvent arg0) {

		if (arg0.getStateChange() == ItemEvent.DESELECTED)
			parent.displayDistTransimg = false;
		else if (arg0.getStateChange() == ItemEvent.SELECTED)
			parent.displayDistTransimg = true;

	}

}

package interactivePreprocessing;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class PREShowBinary implements ItemListener {
	
	
	final InteractiveMethods parent;
	
	public PREShowBinary (final InteractiveMethods parent) {
		
		this.parent = parent;
		
	}
	
	
	@Override
	public void itemStateChanged(final ItemEvent arg0) {

		if (arg0.getStateChange() == ItemEvent.DESELECTED)
			parent.displayBinaryimg = false;
		else if (arg0.getStateChange() == ItemEvent.SELECTED)
			parent.displayBinaryimg = true;

	}
}

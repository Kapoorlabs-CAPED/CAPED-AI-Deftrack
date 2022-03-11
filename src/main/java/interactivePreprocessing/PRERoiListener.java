package interactivePreprocessing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import interactivePreprocessing.InteractiveMethods.ValueChange;


public class PRERoiListener implements ActionListener {
	
	final InteractiveMethods parent;
	
	public PRERoiListener(final InteractiveMethods parent) {
		
		this.parent = parent;
	}

	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		parent.updatePreview(ValueChange.PREROI);
		
	}
	
	
	
}

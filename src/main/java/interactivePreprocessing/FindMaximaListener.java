package interactivePreprocessing;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import dogGUI.CovistoDogPanel;
import interactivePreprocessing.InteractiveMethods.ValueChange;

public class FindMaximaListener implements ItemListener {
	
	final InteractiveMethods  parent;
	public FindMaximaListener( InteractiveMethods parent) {
		
		this.parent = parent;
	}
	
	



	@Override
	public void itemStateChanged(final ItemEvent arg0) {
		
		
		
		
		if (arg0.getStateChange() == ItemEvent.DESELECTED) {
			CovistoDogPanel.lookForMinima = false;
			CovistoDogPanel.lookForMaxima = false;
			
		} else if (arg0.getStateChange() == ItemEvent.SELECTED) {
			CovistoDogPanel.lookForMinima = false;
			CovistoDogPanel.lookForMaxima = true;
			parent.updatePreview(ValueChange.DOG);
		}

	}
	

}


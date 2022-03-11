package interactivePreprocessing;

import java.awt.TextComponent;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;

import dogGUI.CovistoDogPanel;

public class PreTimeDistThresholdListener implements TextListener {

	final InteractiveMethods parent;
	
	final double timethreshold;
	
	public PreTimeDistThresholdListener(final InteractiveMethods parent, final double timethreshold) {
		
		this.parent = parent;
		
		this.timethreshold = timethreshold;
		
	}
	
	@Override
	public void textValueChanged(TextEvent e) {
		
		final TextComponent tc = (TextComponent)e.getSource();
	    String s = tc.getText();
	    
	    if(s.length() > 0)
	    	CovistoDogPanel.timethreshold = Float.parseFloat(s);
		
	}

}


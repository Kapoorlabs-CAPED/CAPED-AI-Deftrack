package interactivePreprocessing;

import java.awt.TextComponent;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;

import dogGUI.CovistoDogPanel;

public class PreDistThresholdListener implements TextListener {

	final InteractiveMethods parent;
	
	final double distthreshold;
	
	public PreDistThresholdListener(final InteractiveMethods parent, final double distthreshold) {
		
		this.parent = parent;
		
		this.distthreshold = distthreshold;
		
	}
	
	@Override
	public void textValueChanged(TextEvent e) {
		
		final TextComponent tc = (TextComponent)e.getSource();
	    String s = tc.getText();
	    
	    if(s.length() > 0)
	    	CovistoDogPanel.distthreshold = Float.parseFloat(s);
		
	}

}

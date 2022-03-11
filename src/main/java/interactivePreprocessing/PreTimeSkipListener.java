package interactivePreprocessing;

import java.awt.TextComponent;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;

import dogGUI.CovistoDogPanel;

public class PreTimeSkipListener implements TextListener {

	final InteractiveMethods parent;
	
	final int timeblock;
	
	public PreTimeSkipListener(final InteractiveMethods parent, final int timeblock) {
		
		this.parent = parent;
		
		this.timeblock = timeblock;
		
	}
	
	@Override
	public void textValueChanged(TextEvent e) {
		
		final TextComponent tc = (TextComponent)e.getSource();
	    String s = tc.getText();
	    
	    if(s.length() > 0)
	    	CovistoDogPanel.timeblock = Integer.parseInt(s);
		
	}

}



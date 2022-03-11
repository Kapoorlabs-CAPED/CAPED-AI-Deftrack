package interactivePreprocessing;

import java.awt.TextComponent;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;

import kalmanGUI.CovistoKalmanPanel;

public class PRELostFrameListener implements TextListener {

	public InteractiveMethods parent;
	
	public PRELostFrameListener(final InteractiveMethods parent) {
		
		this.parent = parent;
	}

	@Override
	public void textValueChanged(TextEvent e) {
		final TextComponent tc = (TextComponent)e.getSource();
	    String s = tc.getText();
	   
	    if (s.length() > 0)
	    	CovistoKalmanPanel.maxframegap = Integer.parseInt(s);
		
		
	}
	
}

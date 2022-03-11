package interactivePreprocessing;

import java.awt.TextComponent;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;

import snakeGUI.CovistoSnakePanel;

public class GradientListener implements TextListener  {
	
	
	final InteractiveMethods parent;
	
	public GradientListener(final InteractiveMethods parent) {
		
		
		this.parent = parent;
		
	}
	
	@Override
	public void textValueChanged(TextEvent e) {
		final TextComponent tc = (TextComponent)e.getSource();
	    String s = tc.getText();
	   
	    if (s.length() > 0)
		CovistoSnakePanel.Gradthresh = Integer.parseInt(s);
		
	}

}

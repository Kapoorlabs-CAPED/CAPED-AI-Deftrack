package listeners;

import java.awt.TextComponent;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;

import pluginTools.InteractiveSimpleEllipseFit;

public class BlackBorderListener implements TextListener {
	
	
	final InteractiveSimpleEllipseFit parent;
	
	public BlackBorderListener(final InteractiveSimpleEllipseFit parent) {
	
		this.parent = parent;
	
	}
	
	
	@Override
	public void textValueChanged(TextEvent e) {
		
		final TextComponent tc = (TextComponent)e.getSource();
	    String s = tc.getText();
		if (s.contains("-")) {
			s.replace("-", "").trim();
		}

		int neg = 1;
		if (s.length() > 0 && s.charAt(0) == '-') {
			s = s.substring(1).trim();
			neg = -1;
		}
		
			    
						if (s.length() > 0)
							parent.borderpixel = (int) Float.parseFloat(s);
						
						parent.borderpixel = neg * parent.borderpixel;
		
	}
	
	

}

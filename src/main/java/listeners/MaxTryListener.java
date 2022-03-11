package listeners;

import java.awt.TextComponent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;

import ij.IJ;
import pluginTools.InteractiveSimpleEllipseFit;
import pluginTools.InteractiveSimpleEllipseFit.ValueChange;
import utility.ShowView;

public class MaxTryListener implements TextListener {

	final InteractiveSimpleEllipseFit parent;

	public MaxTryListener(final InteractiveSimpleEllipseFit parent) {

		this.parent = parent;

	}

	@Override
	public void textValueChanged(TextEvent e) {
		final TextComponent tc = (TextComponent) e.getSource();

		
		
				String s = tc.getText();
			
						parent.maxtry = Integer.parseInt(s);
				
				

			
		};
		
		

	}

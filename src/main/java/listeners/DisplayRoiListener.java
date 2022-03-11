package listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import pluginTools.InteractiveSimpleEllipseFit;
import pluginTools.InteractiveSimpleEllipseFit.ValueChange;

public class DisplayRoiListener implements ActionListener{
	
	
	final InteractiveSimpleEllipseFit parent;
	
	
	public DisplayRoiListener (final InteractiveSimpleEllipseFit parent) {
		
		this.parent = parent;
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		
		
		parent.updatePreview(ValueChange.DISPLAYROI);
		
	}
	
	
	
}

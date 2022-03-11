package listeners;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import pluginTools.IlastikEllipseFileChooser;

public class DoubleChannelListener implements ItemListener {

	public final IlastikEllipseFileChooser parent;
	
	public DoubleChannelListener( final IlastikEllipseFileChooser parent) {
		
		this.parent = parent;
	}
	
	@Override
	public void itemStateChanged(ItemEvent e) {
		
		if (e.getStateChange() == ItemEvent.SELECTED) {
			parent.twochannel = true;
		parent.Panelfileoriginal.add(parent.ChoosesecImage,  new GridBagConstraints(3, 1, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
	
		parent.Panelfileoriginal.validate();
		parent.Panelfileoriginal.repaint();
		
		}
		
		else if (e.getStateChange() == ItemEvent.DESELECTED) {
		parent.twochannel = false;
			parent.Panelfileoriginal.remove(parent.ChoosesecImage);
			
			parent.Panelfileoriginal.validate();
			parent.Panelfileoriginal.repaint();
			
			
		}
		
		
		
		
	}

}

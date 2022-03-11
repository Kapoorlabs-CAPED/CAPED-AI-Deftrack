package listeners;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import pluginTools.IlastikEllipseFileChooser;

public class DoubleChannelBatchListener implements ItemListener {

	public final IlastikEllipseFileChooser parent;
	
	public DoubleChannelBatchListener( final IlastikEllipseFileChooser parent) {
		
		this.parent = parent;
	}
	
	@Override
	public void itemStateChanged(ItemEvent e) {
		
		if (e.getStateChange() == ItemEvent.SELECTED) {
			parent.twochannel = true;
		parent.Panelfileoriginal.add(parent.DirB,  new GridBagConstraints(3, 1, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));

		parent.Panelfileoriginal.add(parent.channelB, new GridBagConstraints(3, 2, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
		parent.Panelfileoriginal.add(parent.channelBidentifier, new GridBagConstraints(3, 3, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
		
		
		parent.Panelfileoriginal.repaint();
		parent.Panelfileoriginal.validate();
		
		
		}
		
		else if (e.getStateChange() == ItemEvent.DESELECTED) {
		parent.twochannel = false;
			parent.Panelfileoriginal.remove(parent.DirB);
			parent.Panelfileoriginal.remove(parent.channelB);
			parent.Panelfileoriginal.remove(parent.channelBidentifier);
			parent.Panelfileoriginal.repaint();
			parent.Panelfileoriginal.validate();
			
			
			
		}
		
		
		
		
	}

}

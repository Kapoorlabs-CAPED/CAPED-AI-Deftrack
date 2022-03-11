package batchMode;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import pluginTools.InteractiveSimpleEllipseFit;

public class SaveBatchListener implements ActionListener {

	
	
	final InteractiveSimpleEllipseFit parent;
	
	public SaveBatchListener(final InteractiveSimpleEllipseFit parent) {
		
		this.parent = parent;
		
	}
	
	@Override
	public void actionPerformed(final ActionEvent arg0) {
		
		
		CreateINIfile recordparam = new CreateINIfile(parent);
		recordparam.RecordParent();
		
		
	}
	
	
}

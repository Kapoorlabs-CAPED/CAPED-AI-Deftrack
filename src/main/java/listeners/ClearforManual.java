package listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import pluginTools.InteractiveSimpleEllipseFit;
import utility.DisplayAuto;

public class ClearforManual implements ActionListener {
	
	final InteractiveSimpleEllipseFit parent;

	public ClearforManual(final InteractiveSimpleEllipseFit parent) {

		this.parent = parent;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		

		int z = parent.thirdDimension;
		int t = parent.fourthDimension;
		String uniqueID = Integer.toString(z) + Integer.toString(t);
		parent.ZTRois.remove(uniqueID);
		parent.ALLIntersections.remove(uniqueID);
		if(parent.roimanager!=null)
		parent.roimanager.runCommand("Delete");
		DisplayAuto.Display(parent);
		
	}

}


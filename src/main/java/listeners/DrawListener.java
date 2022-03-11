package listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;

import pluginTools.InteractiveSimpleEllipseFit;

public class DrawListener implements ActionListener {
	
	final InteractiveSimpleEllipseFit parent;
	final JComboBox<String> choice;
	
	public DrawListener(final InteractiveSimpleEllipseFit parent, final JComboBox<String> choice ) {
		
		
		this.parent = parent;
		this.choice = choice;
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		
		int selectedindex = choice.getSelectedIndex();
		
		if (selectedindex == 1) {
			parent.minpercent = parent.minpercentINIArc;
			parent.inputFieldminpercent.setText(Float.toString(parent.minpercent));	
			parent.insideCutoff =  2 * parent.insideCutoffmin;
			parent.insideslider.setValue(utility.Slicer.computeScrollbarPositionFromValue(parent.insideCutoff, parent.insideCutoffmin, parent.insideCutoffmax, parent.scrollbarSize));

		}
		else {
			parent.minpercent = (float) (parent.minpercentINI );
			parent.inputFieldminpercent.setText(Float.toString(parent.minpercent));	
			parent.insideCutoff = parent.insideCutoffmin;
			parent.insideslider.setValue(utility.Slicer.computeScrollbarPositionFromValue(parent.insideCutoff, parent.insideCutoffmin, parent.insideCutoffmax, parent.scrollbarSize));
			
		}
		
		parent.Angleselect.repaint();
		parent.Angleselect.validate();
		parent.panelFirst.repaint();
		parent.panelFirst.validate();
		
		
	}
	
	
	
	
	
	

}

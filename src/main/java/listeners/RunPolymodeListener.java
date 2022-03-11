package listeners;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import comboSliderTextbox.SliderBoxGUI;
import pluginTools.InteractiveSimpleEllipseFit;

public class RunPolymodeListener implements ItemListener {

	InteractiveSimpleEllipseFit parent;

	public RunPolymodeListener(InteractiveSimpleEllipseFit parent) {
		this.parent = parent;
	}

	@Override
	public void itemStateChanged(final ItemEvent arg0) {

		if (arg0.getStateChange() == ItemEvent.DESELECTED) {
			
			parent.circlefits = true;
			parent.polynomialfits = false;

		}

		else if (arg0.getStateChange() == ItemEvent.SELECTED) {

			parent.circlefits = false;
			parent.polynomialfits = true;
			
			
			parent.Angleselect.removeAll();
			parent.Angleselect.add(parent.degreeText, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
					GridBagConstraints.HORIZONTAL, parent.insets, 0, 0));

			parent.Angleselect.add(parent.degreeField, new GridBagConstraints(0, 1, 2, 1, 0.0, 0.0, GridBagConstraints.EAST,
					GridBagConstraints.HORIZONTAL, parent.insets, 0, 0));
			
			parent.Angleselect.add(parent.secdegreeText, new GridBagConstraints(5, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, parent.insets, 0, 0));

			parent.Angleselect.add(parent.secdegreeField, new GridBagConstraints(5, 1, 2, 1, 0.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, parent.insets, 0, 0));
			
			parent.Angleselect.add(parent.smoothText, new GridBagConstraints(0, 2, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
					GridBagConstraints.HORIZONTAL, parent.insets, 0, 0));

			parent.Angleselect.add(parent.smoothslider, new GridBagConstraints(0, 3, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
					GridBagConstraints.HORIZONTAL, parent.insets, 0, 0));
			
		
			
			SliderBoxGUI combocutoff = new SliderBoxGUI(parent.insidestring, parent.insideslider, parent.cutoffField, parent.insideText, parent.scrollbarSize, parent.insideCutoff, parent.insideCutoffmax);
			
			parent.Angleselect.add(combocutoff.BuildDisplay(), new GridBagConstraints(0, 4, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
					GridBagConstraints.HORIZONTAL, parent.insets, 0, 0));

			SliderBoxGUI combominInlier = new SliderBoxGUI(parent.mininlierstring, parent.minInlierslider, parent.minInlierField, parent.minInlierText, parent.scrollbarSize, parent.minNumInliers, parent.minNumInliersmax);
			
			parent.Angleselect.add(combominInlier.BuildDisplay(), new GridBagConstraints(5, 4, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
					GridBagConstraints.HORIZONTAL, parent.insets, 0, 0));
			
		//	parent.Angleselect.add(parent.CurrentCurvaturebutton, new GridBagConstraints(0, 5, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
		//			GridBagConstraints.HORIZONTAL, parent.insets, 0, 0));
			parent.Angleselect.add(parent.Curvaturebutton, new GridBagConstraints(0, 6, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
					GridBagConstraints.HORIZONTAL, parent.insets, 0, 0));

			parent.Angleselect.setBorder(parent.circletools);
			parent.Angleselect.setPreferredSize(new Dimension(parent.SizeX , parent.SizeY ));
			parent.panelFirst.add(parent.Angleselect, new GridBagConstraints(5, 1, 5, 1, 0.0, 0.0, GridBagConstraints.CENTER,
					GridBagConstraints.HORIZONTAL, parent.insets, 0, 0));
			   parent.Angleselect.validate();
			   parent.Angleselect.repaint();
			
		}

	}

}
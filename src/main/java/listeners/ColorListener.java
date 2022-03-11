package listeners;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;

import ij.IJ;
import pluginTools.InteractiveSimpleEllipseFit;

public class ColorListener implements ActionListener {

	final InteractiveSimpleEllipseFit parent;
	final JComboBox<String> choice;

	public ColorListener(final InteractiveSimpleEllipseFit parent, final JComboBox<String> choice) {

		this.parent = parent;
		this.choice = choice;

	}

	@Override
	public void actionPerformed(ActionEvent e) {

		int selectedindex = choice.getSelectedIndex();

		if (selectedindex == 0) {

			parent.colorChange = Color.GRAY;
		}
		if (selectedindex == 1) {

			parent.colorChange = Color.RED;
		}
		if (selectedindex == 2) {

			parent.colorChange = Color.BLUE;
		}
		if (selectedindex == 3) {

			parent.colorChange = Color.PINK;
		}
	
		

			
		parent.Angleselect.repaint();
		parent.Angleselect.validate();
		parent.panelFirst.repaint();
		parent.panelFirst.validate();

	}

}

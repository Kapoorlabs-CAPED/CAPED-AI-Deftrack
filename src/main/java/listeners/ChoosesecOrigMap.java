package listeners;


import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;

import ij.WindowManager;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.real.FloatType;
import pluginTools.IlastikEllipseFileChooser;

public class ChoosesecOrigMap implements ActionListener {

	
	final IlastikEllipseFileChooser parent;
	final JComboBox<String> choice;
	
	public ChoosesecOrigMap(final IlastikEllipseFileChooser parent, final JComboBox<String> choice) {
		
		this.parent = parent;
		this.choice = choice;
		
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {

		String imagename = (String) choice.getSelectedItem();
		
	 parent.twochannel = true;
		
	    	parent.impSec = WindowManager.getImage(imagename);
	    	
	
			
			
			
		
	}
	
	
	
}

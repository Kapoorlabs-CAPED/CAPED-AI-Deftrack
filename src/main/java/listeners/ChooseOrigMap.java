package listeners;


import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import javax.swing.JComboBox;

import ij.WindowManager;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.real.FloatType;
import pluginTools.IlastikEllipseFileChooser;

public class ChooseOrigMap implements ActionListener {

	
	final IlastikEllipseFileChooser parent;
	final JComboBox<String> choice;
	
	public ChooseOrigMap(final IlastikEllipseFileChooser parent, final JComboBox<String> choice) {
		
		this.parent = parent;
		this.choice = choice;
		
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {

		String imagename = (String) choice.getSelectedItem();
		
	
		
	    	parent.impOrig = WindowManager.getImage(imagename);
	    	
	  //  	if(parent.impOrig==null)
	    //	 	parent.impOrig = parent.impA;
			
			if(parent.impOrig!=null) {
			parent.calibration = parent.impOrig.getCalibration().pixelWidth;
			parent.Wavesize = parent.impOrig.getCalibration().frameInterval;
			if (parent.Wavesize == 0)
				parent.Wavesize = 1;
			DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.getDefault());
			otherSymbols.setDecimalSeparator('.');
			otherSymbols.setGroupingSeparator(','); 
			DecimalFormat df = new DecimalFormat(("#.###"), otherSymbols);
			parent.Fieldwavesize.setText(String.valueOf(df.format(parent.Wavesize))); 
			parent.inputFieldcalX.setText(String.valueOf(df.format(parent.calibration)));
	}
	
	}
	
}

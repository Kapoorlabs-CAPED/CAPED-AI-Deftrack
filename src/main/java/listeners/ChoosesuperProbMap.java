package listeners;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.Iterator;

import javax.swing.JComboBox;

import ij.ImageStack;
import ij.WindowManager;
import io.scif.config.SCIFIOConfig;
import io.scif.config.SCIFIOConfig.ImgMode;
import io.scif.img.ImgIOException;
import io.scif.img.ImgOpener;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;
import pluginTools.IlastikEllipseFileChooser;

public class ChoosesuperProbMap implements ActionListener {

	
	final IlastikEllipseFileChooser parent;
	final JComboBox<String> choice;
	
	public ChoosesuperProbMap(final IlastikEllipseFileChooser parent, final JComboBox<String> choice) {
		
		this.parent = parent;
		this.choice = choice;
		
	}
	
	@Override
	public void actionPerformed(ActionEvent e)  {

		String imagename = (String) choice.getSelectedItem();
		
		
	    	parent.impsuper = WindowManager.getImage(imagename);
			


		

	}	
	
	

		
	
	
	
}

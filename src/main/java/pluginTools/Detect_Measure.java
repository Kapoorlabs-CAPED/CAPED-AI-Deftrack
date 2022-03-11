package pluginTools;

import javax.swing.JFrame;

import ij.ImageJ;
import ij.plugin.PlugIn;

public class Detect_Measure implements PlugIn {

	
	@Override
	public void run(String arg) {
		
			new ImageJ();
			

			    JFrame frame = new JFrame("");
			  EllipseFileChooser panel = new EllipseFileChooser();
			 
			    frame.getContentPane().add(panel,"Center");
			    frame.setSize(panel.getPreferredSize());
			    
		}
	
	
	
}

package pluginTools;

import javax.swing.JFrame;

import ij.ImageJ;
import ij.ImagePlus;
import ij.io.Opener;

public class InteractiveIlastikEllipseFit {

	public static void main(String[] args) {

		new ImageJ();
		JFrame frame = new JFrame("");

		ImagePlus impB = new Opener()
				.openImage("/Users/aimachine/Documents/OzEtrack/ColorTest.tif");
		impB.show();

		ImagePlus impA = new Opener()
				.openImage("/Users/aimachine/Documents/OzEtrack/ColorTest.tif");
		impA.show();

		IlastikEllipseFileChooser panel = new IlastikEllipseFileChooser();

		frame.getContentPane().add(panel, "Center");
		frame.setSize(panel.getPreferredSize());

	}

}

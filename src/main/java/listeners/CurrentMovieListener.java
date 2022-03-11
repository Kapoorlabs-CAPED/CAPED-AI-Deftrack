package listeners;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import ij.IJ;
import ij.WindowManager;
import pluginTools.EllipseFileChooser;

public class CurrentMovieListener implements ActionListener {

	final EllipseFileChooser parent;

	public CurrentMovieListener(EllipseFileChooser parent) {

		this.parent = parent;

	}

	@Override
	public void actionPerformed(final ActionEvent arg0) {

		parent.impA = WindowManager.getCurrentImage();
		
		if(parent.impA!=null)
		parent.DoneCurr(parent.Cardframe);
	}

}

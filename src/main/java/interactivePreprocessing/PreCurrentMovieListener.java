package interactivePreprocessing;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import ij.IJ;


public class PreCurrentMovieListener implements ActionListener {

	final PreprocessingFileChooser  parent;

	public PreCurrentMovieListener(PreprocessingFileChooser  parent) {

		this.parent = parent;

	}

	@Override
	public void actionPerformed(final ActionEvent arg0) {

		parent.impA = IJ.getImage();
		
		if(parent.impA!=null)
		parent.DoneCurr(parent.Cardframe);
	}

}

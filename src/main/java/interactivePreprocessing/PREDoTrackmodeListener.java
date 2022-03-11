package interactivePreprocessing;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class PREDoTrackmodeListener implements ItemListener {
	
	public final PreprocessingFileChooser parent;
	
	public PREDoTrackmodeListener(final PreprocessingFileChooser parent) {
		
		this.parent = parent;
		
	}

	@Override
	public void itemStateChanged(final ItemEvent arg0) {

		if (arg0.getStateChange() == ItemEvent.DESELECTED) {
			parent.TrackandSeg = false;
			parent.onlySeg = true;
		}
		else if (arg0.getStateChange() == ItemEvent.SELECTED) {
			parent.TrackandSeg = true;
			parent.onlySeg = false;
		}

	}
	
	
}

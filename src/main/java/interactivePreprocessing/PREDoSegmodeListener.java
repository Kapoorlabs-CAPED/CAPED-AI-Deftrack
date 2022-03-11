package interactivePreprocessing;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class PREDoSegmodeListener implements ItemListener {
	
	public final PreprocessingFileChooser parent;
	
	public PREDoSegmodeListener(final PreprocessingFileChooser parent) {
		
		this.parent = parent;
		
	}

	@Override
	public void itemStateChanged(final ItemEvent arg0) {

		if (arg0.getStateChange() == ItemEvent.DESELECTED) {
			parent.onlySeg = false;
			parent.TrackandSeg = true;
			
		}
		else if (arg0.getStateChange() == ItemEvent.SELECTED) {
			parent.onlySeg = true;
			parent.TrackandSeg = false;
			
		}

	}
	
	
}

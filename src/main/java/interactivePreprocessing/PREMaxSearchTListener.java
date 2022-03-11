package interactivePreprocessing;

import java.awt.Label;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import javax.swing.JScrollBar;

import interactivePreprocessing.InteractiveMethods.ValueChange;
import kalmanGUI.CovistoKalmanPanel;
import nearestNeighbourGUI.CovistoNearestNPanel;

public class PREMaxSearchTListener implements AdjustmentListener {
	
	final Label label;
	final String string;
	final InteractiveMethods parent;
	final float min, max;
	final int scrollbarSize;
	final JScrollBar scrollbar;
	
	
	public PREMaxSearchTListener(final InteractiveMethods parent, final Label label, final String string, final float min, final float max, final int scrollbarSize, final JScrollBar scrollbar) {
		
		this.parent = parent;
		this.label = label;
		this.string = string;
		this.min = min;
		this.max = max;
		this.scrollbarSize = scrollbarSize;
		this.scrollbar = scrollbar;
		
		scrollbar.setBlockIncrement(utility.CovistoSlicer.computeScrollbarPositionFromValue(2, min, max, scrollbarSize));
		scrollbar.setUnitIncrement(utility.CovistoSlicer.computeScrollbarPositionFromValue(2, min, max, scrollbarSize));
	}
	
	
	
	@Override
	public void adjustmentValueChanged(final AdjustmentEvent event) {
		CovistoKalmanPanel.maxSearchradius = utility.ETrackScrollbarUtils.computeValueFromScrollbarPosition(event.getValue(), min, max, scrollbarSize);

		
			scrollbar.setValue(utility.ETrackScrollbarUtils.computeScrollbarPositionFromValue(CovistoKalmanPanel.maxSearchradius, min, max, scrollbarSize));

			label.setText(string +  " = "  + parent.nf.format(CovistoKalmanPanel.maxSearchradius));
			
			
	
	}
	

}

package listeners;

import java.awt.Label;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import javax.swing.JScrollBar;

import interactivePreprocessing.InteractiveMethods.ValueChange;
import pluginTools.InteractiveSimpleEllipseFit;

public class ETrackMaxSearchListener implements AdjustmentListener {
	
	final Label label;
	final String string;
	final InteractiveSimpleEllipseFit parent;
	final float min, max;
	final int scrollbarSize;
	final JScrollBar scrollbar;
	
	
	public ETrackMaxSearchListener(final InteractiveSimpleEllipseFit parent, final Label label, final String string, final float min, final float max, final int scrollbarSize, final JScrollBar scrollbar) {
		
		this.parent = parent;
		this.label = label;
		this.string = string;
		this.min = min;
		this.max = max;
		this.scrollbarSize = scrollbarSize;
		this.scrollbar = scrollbar;
		
		scrollbar.setBlockIncrement(utility.Slicer.computeScrollbarPositionFromValue(2, min, max, scrollbarSize));
		scrollbar.setUnitIncrement(utility.Slicer.computeScrollbarPositionFromValue(2, min, max, scrollbarSize));
	}
	
	
	
	@Override
	public void adjustmentValueChanged(final AdjustmentEvent event) {
		    parent.maxSearchradiusS = (int) utility.ETrackScrollbarUtils.computeValueFromScrollbarPosition(event.getValue(), min, max, scrollbarSize);

		    parent.maxSearchradius = parent.maxSearchradiusS;
		
			scrollbar.setValue(utility.ETrackScrollbarUtils.computeScrollbarPositionFromValue(parent.maxSearchradiusS, min, max, scrollbarSize));

			label.setText(string +  " = "  + parent.nf.format(parent.maxSearchradiusS));

	
	}
	

}

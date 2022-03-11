package listeners;

import java.awt.Label;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import javax.swing.JScrollBar;

import pluginTools.InteractiveSimpleEllipseFit;
import pluginTools.InteractiveSimpleEllipseFit.ValueChange;

public class OutsideCutoffListener implements AdjustmentListener {
	final Label label;
	final String string;
	InteractiveSimpleEllipseFit parent;
	final float min, max;
	final int scrollbarSize;

	final JScrollBar deltaScrollbar;

	public OutsideCutoffListener(final InteractiveSimpleEllipseFit parent, final Label label, final String string, final float min, final float max,
			final int scrollbarSize, final JScrollBar deltaScrollbar) {
		this.label = label;
		this.parent = parent;
		this.string = string;
		this.min = min;
		this.max = max;
		this.scrollbarSize = scrollbarSize;

		this.deltaScrollbar = deltaScrollbar;
	}

	@Override
	public void adjustmentValueChanged(AdjustmentEvent e) {
		parent.outsideCutoff = parent.insideCutoff;  
				//utility.Slicer.computeValueFromScrollbarPosition(e.getValue(), min, max, scrollbarSize);
		deltaScrollbar
		.setValue(utility.Slicer.computeScrollbarPositionFromValue(parent.outsideCutoff, min, max, scrollbarSize));

		label.setText(string +  " = "  + parent.nf.format(parent.outsideCutoff));
		parent.panelFirst.validate();
		parent.panelFirst.repaint();
	
		
	}
	

	
	
	
	
}

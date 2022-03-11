
package listeners;

import java.awt.Label;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import javax.swing.JScrollBar;

import pluginTools.InteractiveSimpleEllipseFit;
import pluginTools.InteractiveSimpleEllipseFit.ValueChange;
import utility.ShowView;

public class SmoothSliderListener implements AdjustmentListener {
	final Label label;
	final String string;
	InteractiveSimpleEllipseFit parent;
	final float min;
	final int scrollbarSize;

	float max;
	final JScrollBar deltaScrollbar;

	public SmoothSliderListener(final InteractiveSimpleEllipseFit parent, final Label label, final String string, final float min, float max,
			final int scrollbarSize, final JScrollBar deltaScrollbar) {
		this.label = label;
		this.parent = parent;
		this.string = string;
		this.min = min;
	
		this.scrollbarSize = scrollbarSize;

		if(parent.curveautomode || parent.curvesupermode)
			deltaScrollbar.addMouseListener( new CurvatureMouseListener( parent ) );
		this.deltaScrollbar = deltaScrollbar;
		deltaScrollbar.setBlockIncrement(1);
	}

	@Override
	public void adjustmentValueChanged(AdjustmentEvent e) {
		
		max = parent.smoothslidermax;
		
		parent.smoothing =  (float) utility.Slicer.computeValueFromScrollbarPosition(e.getValue(), min, max, scrollbarSize);
	
		deltaScrollbar
				.setValue(utility.Slicer.computeScrollbarPositionFromValue((float)parent.smoothing, min, max, scrollbarSize));

		label.setText(string +  " = "  +parent.nf.format(parent.smoothing));
		parent.panelFirst.validate();
		parent.panelFirst.repaint();
	
		
	}
	

	
	
	
	
}
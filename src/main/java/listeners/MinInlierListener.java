
package listeners;

import java.awt.Label;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import javax.swing.JScrollBar;

import pluginTools.EllipseTrack;
import pluginTools.InteractiveSimpleEllipseFit;
import pluginTools.InteractiveSimpleEllipseFit.ValueChange;
import utility.ShowView;

public class MinInlierListener implements AdjustmentListener {
	final Label label;
	final String string;
	InteractiveSimpleEllipseFit parent;
	final float min;
	final int scrollbarSize;

	final JScrollBar deltaScrollbar;

	public MinInlierListener(final InteractiveSimpleEllipseFit parent, final Label label, final String string, final float min,
			final int scrollbarSize, final JScrollBar deltaScrollbar) {
		this.label = label;
		this.parent = parent;
		this.string = string;
		this.min = min;
		
		this.scrollbarSize = scrollbarSize;
		deltaScrollbar.addMouseListener( new CurvatureMouseListener( parent ) );
		this.deltaScrollbar = deltaScrollbar;
		deltaScrollbar.setBlockIncrement(1);
	
	}

	@Override
	public void adjustmentValueChanged(AdjustmentEvent e) {
		
		float max = parent.minNumInliersmax;
		parent.minNumInliers =  (int) utility.Slicer.computeValueFromScrollbarPosition(e.getValue(), min, max, scrollbarSize);
	
		
		
		
		deltaScrollbar
				.setValue(utility.Slicer.computeScrollbarPositionFromValue(parent.minNumInliers, min, max, scrollbarSize));

		label.setText(string +  " = "  + parent.nf.format(parent.minNumInliers));
		
	 
		
		parent.minInlierField.setText(Integer.toString(Math.round(parent.minNumInliers)));
		parent.panelFirst.validate();
		parent.panelFirst.repaint();
		
		
	}
	

	
	
	
	
}
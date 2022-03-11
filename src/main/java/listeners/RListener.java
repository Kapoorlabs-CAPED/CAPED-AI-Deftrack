package listeners;

import java.awt.Label;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import javax.swing.JScrollBar;

import pluginTools.InteractiveSimpleEllipseFit;
import pluginTools.InteractiveSimpleEllipseFit.ValueChange;
import utility.ShowView;


	
	public class RListener implements AdjustmentListener {
		final Label label;
		final String string;
		InteractiveSimpleEllipseFit parent;
		final float min, max;
		final int scrollbarSize;

		final JScrollBar deltaScrollbar;

		public RListener(final InteractiveSimpleEllipseFit parent, final Label label, final String string, final float min, final float max,
				final int scrollbarSize, final JScrollBar deltaScrollbar) {
			this.label = label;
			this.parent = parent;
			this.string = string;
			this.min = min;
			this.max = max;
			this.scrollbarSize = scrollbarSize;
			deltaScrollbar.addMouseMotionListener(new EllipseNonStandardMouseListener(parent, ValueChange.RADIUS));
			this.deltaScrollbar = deltaScrollbar;
			deltaScrollbar.setBlockIncrement(1);
		}

		@Override
		public void adjustmentValueChanged(AdjustmentEvent e) {
			parent.radius = (int) utility.Slicer.computeValueFromScrollbarPosition(e.getValue(), min, max, scrollbarSize);
		
			deltaScrollbar
			.setValue(utility.Slicer.computeScrollbarPositionFromValue(parent.radius, min, max, scrollbarSize));

			label.setText(string +  " = "  + parent.radius);
			parent.panelFirst.validate();
			parent.panelFirst.repaint();
		
		}
		
	
	
	
}
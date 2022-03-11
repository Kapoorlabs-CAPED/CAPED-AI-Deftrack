package listeners;

import java.awt.Label;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import javax.swing.JScrollBar;

import ij.IJ;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.logic.BitType;
import pluginTools.InteractiveSimpleEllipseFit;
import pluginTools.InteractiveSimpleEllipseFit.ValueChange;
import utility.ShowView;


public class TimeListener implements AdjustmentListener {
	final Label label;
	final String string;
	InteractiveSimpleEllipseFit parent;
	final float min, max;
	final int scrollbarSize;

	final JScrollBar deltaScrollbar;

	public TimeListener(final InteractiveSimpleEllipseFit parent, final Label label, final String string, final float min, final float max,
			final int scrollbarSize, final JScrollBar deltaScrollbar) {
		this.label = label;
		this.parent = parent;
		this.string = string;
		this.min = min;
		this.max = max;
		this.scrollbarSize = scrollbarSize;

		this.deltaScrollbar = deltaScrollbar;
		deltaScrollbar.addMouseMotionListener(new EllipseNonStandardMouseListener(parent, ValueChange.FOURTHDIMmouse));
		deltaScrollbar.addMouseListener(new EllipseStandardMouseListener(parent, ValueChange.FOURTHDIMmouse));
		deltaScrollbar.setBlockIncrement(utility.Slicer.computeScrollbarPositionFromValue(2, min, max, scrollbarSize));
		deltaScrollbar.setUnitIncrement(utility.Slicer.computeScrollbarPositionFromValue(2, min, max, scrollbarSize));
	}

	@Override
	public void adjustmentValueChanged(AdjustmentEvent e) {
		
		parent.fourthDimension = (int) Math.round(utility.Slicer.computeValueFromScrollbarPosition(e.getValue(), min, max, scrollbarSize));


		deltaScrollbar
		.setValue(utility.Slicer.computeScrollbarPositionFromValue(parent.fourthDimension, min, max, scrollbarSize));
		
		label.setText(string +  " = "  + parent.fourthDimension);

		parent.inputFieldT.setText(Integer.toString((int)parent.fourthDimension));
		parent.panelFirst.validate();
		parent.panelFirst.repaint();
		
		ShowView show = new ShowView(parent);
		show.shownewT();

	}
	


}
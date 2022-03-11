package interactivePreprocessing;

import java.awt.Label;
import java.awt.Scrollbar;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import javax.swing.JScrollBar;

import dogGUI.CovistoDogPanel;
import interactivePreprocessing.InteractiveMethods.ValueChange;
import mserGUI.CovistoMserPanel;


public class PreSigmaListener implements AdjustmentListener {
	final Label label;
	final InteractiveMethods parent;
	final String string;
	final float min, max;
	final int scrollbarSize;
	final JScrollBar sigmaScrollbar1;

	public PreSigmaListener(final InteractiveMethods parent, final Label label,final String string, final float min, final float max, final int scrollbarSize,
			final JScrollBar sigmaScrollbar1) {
		this.label = label;
		this.min = min;
		this.max = max;
		this.string = string;
		this.scrollbarSize = scrollbarSize;
		this.parent = parent;
		this.sigmaScrollbar1 = sigmaScrollbar1;
		sigmaScrollbar1.addMouseListener( new CovistoStandardMouseListener( parent, ValueChange.DOG ) );
	}



	@Override
	public void adjustmentValueChanged(final AdjustmentEvent event) {
		    CovistoDogPanel.sigma = utility.ETrackScrollbarUtils.computeValueFromScrollbarPosition(event.getValue(), min, max, scrollbarSize);

		
		
			sigmaScrollbar1.setValue(utility.ETrackScrollbarUtils.computeScrollbarPositionFromValue(CovistoDogPanel.sigma, min, max, scrollbarSize));

			label.setText(string +  " = "  + parent.nf.format(CovistoDogPanel.sigma));
		
	
	}
}

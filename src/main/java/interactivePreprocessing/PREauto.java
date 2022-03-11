package interactivePreprocessing;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import interactivePreprocessing.InteractiveMethods.ValueChange;
import preProcessing.GlobalThresholding;
import watershedGUI.CovistoWatershedPanel;

public class PREauto implements ItemListener {
	
final InteractiveMethods parent;
	
	public PREauto (final InteractiveMethods parent) {
		
		this.parent = parent;
		
	}
	
	
	@Override
	public void itemStateChanged(final ItemEvent arg0) {

		if (arg0.getStateChange() == ItemEvent.DESELECTED)
			parent.autothreshwater = false;
		else if (arg0.getStateChange() == ItemEvent.SELECTED) {
			parent.autothreshwater = true;
			CovistoWatershedPanel.thresholdWater = (float) ( GlobalThresholding.AutomaticThresholding(parent.CurrentView));
			CovistoWatershedPanel.thresholdWaterslider.setValue(utility.ETrackScrollbarUtils.computeScrollbarPositionFromValue(CovistoWatershedPanel.thresholdWater, CovistoWatershedPanel.thresholdMinWater, CovistoWatershedPanel.thresholdMaxWater, CovistoWatershedPanel.scrollbarSize));
			CovistoWatershedPanel.watertext.setText(CovistoWatershedPanel.waterstring +  " = "  + CovistoWatershedPanel.thresholdWater );
			CovistoWatershedPanel.thresholdWaterslider.validate();
			CovistoWatershedPanel.thresholdWaterslider.repaint();
			
			
		}

	}


}

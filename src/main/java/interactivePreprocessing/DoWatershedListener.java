package interactivePreprocessing;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import dogGUI.CovistoDogPanel;
import interactivePreprocessing.InteractiveMethods.ValueChange;
import mserGUI.CovistoMserPanel;
import watershedGUI.CovistoWatershedPanel;

public class DoWatershedListener implements ItemListener {
	
	final InteractiveMethods  parent;
	
	public DoWatershedListener( InteractiveMethods parent) {
		
		this.parent = parent;
		
	}
	
	
	@Override
	public void itemStateChanged(final ItemEvent arg0) {
		
		
		
		
		if (arg0.getStateChange() == ItemEvent.DESELECTED) {
			parent.showDOG = false;
			parent.showMSER = false;
			parent.showWatershed = false;
		} else if (arg0.getStateChange() == ItemEvent.SELECTED) {
			parent.showDOG = false;
			parent.showMSER = false;
			parent.showWatershed = true;
			
			CovistoDogPanel.sigmaslider.setVisible(false);
			CovistoDogPanel.thresholdslider.setVisible(false);
			CovistoDogPanel.findmaxima.setVisible(false);
			CovistoDogPanel.findminima.setVisible(false);
			
			CovistoMserPanel.deltaS.setVisible(false);
			CovistoMserPanel.Unstability_ScoreS.setVisible(false);
			CovistoMserPanel.minDiversityS.setVisible(false);
			CovistoMserPanel.minSizeS.setVisible(false);
			CovistoMserPanel.maxSizeS.setVisible(false);
			CovistoMserPanel.findminimaMser.setVisible(false);
			CovistoMserPanel.findmaximaMser.setVisible(false);
			
			
			
			CovistoWatershedPanel.displayWater.setVisible(true);
			CovistoWatershedPanel.displayBinary.setVisible(true);
			CovistoWatershedPanel.displayDist.setVisible(true);
			CovistoWatershedPanel.autothreshold.setVisible(true);
			CovistoWatershedPanel.thresholdWaterslider.setVisible(true);
			
			parent.SnakePanel.setVisible(false);
		
			
		}

	}
	

}

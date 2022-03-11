package interactivePreprocessing;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import dogGUI.CovistoDogPanel;
import interactivePreprocessing.InteractiveMethods.ValueChange;
import mserGUI.CovistoMserPanel;
import watershedGUI.CovistoWatershedPanel;

public class DoMSERListener implements ItemListener {
	
	final InteractiveMethods  parent;
	
	public DoMSERListener( InteractiveMethods parent) {
		
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
			parent.showMSER = true;
			parent.showWatershed = false;
			CovistoDogPanel.sigmaslider.setVisible(false);
			CovistoDogPanel.thresholdslider.setVisible(false);
			CovistoDogPanel.findmaxima.setVisible(false);
			CovistoDogPanel.findminima.setVisible(false);
			
			
			
			
			CovistoMserPanel.deltaS.setVisible(true);
			CovistoMserPanel.Unstability_ScoreS.setVisible(true);
			CovistoMserPanel.minDiversityS.setVisible(true);
			CovistoMserPanel.minSizeS.setVisible(true);
			CovistoMserPanel.maxSizeS.setVisible(true);
			CovistoMserPanel.findminimaMser.setVisible(true);
			CovistoMserPanel.findmaximaMser.setVisible(true);
			
			
			CovistoWatershedPanel.displayWater.setVisible(false);
			CovistoWatershedPanel.displayBinary.setVisible(false);
			CovistoWatershedPanel.displayDist.setVisible(false);
			CovistoWatershedPanel.autothreshold.setVisible(false);
			CovistoWatershedPanel.thresholdWaterslider.setVisible(false);
			
			parent.SnakePanel.setVisible(true);
			
		}

	}
	

}

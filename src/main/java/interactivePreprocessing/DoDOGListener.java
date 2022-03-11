package interactivePreprocessing;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import dogGUI.CovistoDogPanel;
import interactivePreprocessing.InteractiveMethods.ValueChange;
import mserGUI.CovistoMserPanel;
import watershedGUI.CovistoWatershedPanel;

public class DoDOGListener implements ItemListener {
	
	final InteractiveMethods  parent;
	
	public DoDOGListener( InteractiveMethods parent) {
		
		this.parent = parent;
		
	}
	
	
	@Override
	public void itemStateChanged(final ItemEvent arg0) {
		
		
		
		
		if (arg0.getStateChange() == ItemEvent.DESELECTED) {
			parent.showDOG = false;
			parent.showMSER = false;
			parent.showWatershed = false;
		} else if (arg0.getStateChange() == ItemEvent.SELECTED) {
			parent.showDOG = true;
			parent.showMSER = false;
			parent.showWatershed = false;
			
			CovistoDogPanel.sigmaslider.setVisible(true);
			CovistoDogPanel.thresholdslider.setVisible(true);
			CovistoDogPanel.findmaxima.setVisible(true);
			CovistoDogPanel.findminima.setVisible(true);
			
			CovistoMserPanel.deltaS.setVisible(false);
			CovistoMserPanel.Unstability_ScoreS.setVisible(false);
			CovistoMserPanel.minDiversityS.setVisible(false);
			CovistoMserPanel.minSizeS.setVisible(false);
			CovistoMserPanel.maxSizeS.setVisible(false);
			CovistoMserPanel.findminimaMser.setVisible(false);
			CovistoMserPanel.findmaximaMser.setVisible(false);
			
			CovistoWatershedPanel.displayWater.setVisible(false);
			CovistoWatershedPanel.displayBinary.setVisible(false);
			CovistoWatershedPanel.displayDist.setVisible(false);
			CovistoWatershedPanel.autothreshold.setVisible(false);
			CovistoWatershedPanel.thresholdWaterslider.setVisible(false);
			
			parent.SnakePanel.setVisible(true);
		
		}

	}
	

}

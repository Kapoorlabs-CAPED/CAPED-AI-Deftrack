package interactivePreprocessing;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import ij.gui.GenericDialog;
import snakeGUI.CovistoSnakePanel;

public class AdvancedSnakeListener implements ItemListener {
	
	
	final InteractiveMethods parent;
	
	
	public AdvancedSnakeListener(final InteractiveMethods parent) {
		
		this.parent = parent;
		
	}


	@Override
	public void itemStateChanged(ItemEvent e) {
		
		if (e.getStateChange() == ItemEvent.DESELECTED) {
			
		parent.advancedSnake = false;	
			
		}
		
       if (e.getStateChange() == ItemEvent.SELECTED) {
			
    	   parent.advancedSnake = true;
			AdvancedDialog();
			
		}
		
}
	
	public boolean AdvancedDialog() {

		// dialog
		GenericDialog gd = new GenericDialog("Snake Advanced");
		gd.addNumericField("Distance_Search", CovistoSnakePanel.DistMax, 0);
		gd.addNumericField("Displacement_min", CovistoSnakePanel.Displacement_min, 2);
		gd.addNumericField("Displacement_max", CovistoSnakePanel.Displacement_max, 2);
		gd.addNumericField("Threshold_dist_positive", CovistoSnakePanel.Threshold_dist_positive, 0);
		gd.addNumericField("Threshold_dist_negative", CovistoSnakePanel.Threshold_dist_negative, 0);
		gd.addNumericField("Inv_alpha_min", CovistoSnakePanel.Inv_alpha_min, 2);
		gd.addNumericField("Inv_alpha_max", CovistoSnakePanel.Inv_alpha_max, 2);
		gd.addNumericField("Reg_min", CovistoSnakePanel.regmin, 2);
		gd.addNumericField("Reg_max", CovistoSnakePanel.regmax, 2);
		gd.addNumericField("Mul_factor", CovistoSnakePanel.Mul_factor, 4);
		// show dialog
		gd.showDialog();

		CovistoSnakePanel.DistMax = (int) gd.getNextNumber();
		CovistoSnakePanel.Displacement_min = gd.getNextNumber();
		CovistoSnakePanel.Displacement_max = gd.getNextNumber();
		CovistoSnakePanel.Threshold_dist_positive = gd.getNextNumber();
		CovistoSnakePanel.Threshold_dist_negative = gd.getNextNumber();
		CovistoSnakePanel.Inv_alpha_min = gd.getNextNumber();
		CovistoSnakePanel.Inv_alpha_max = gd.getNextNumber();
		CovistoSnakePanel.regmin = gd.getNextNumber();
		CovistoSnakePanel.regmax = gd.getNextNumber();
		CovistoSnakePanel.Mul_factor = gd.getNextNumber();

		return !gd.wasCanceled();

	}
	
	
	

}

package interactivePreprocessing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.SwingUtilities;

import watershed3D.WatershedAll;
import watershedCurrent.WatershedCurrent;

public class PREApplyWaterCurrentListener implements ActionListener {

	final InteractiveMethods parent;
	
	public PREApplyWaterCurrentListener(final InteractiveMethods parent) {
		

		this.parent = parent;
	}
	
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {

				goWater();

			}

		});
		
	}
	
	
	public void goWater() {


		WatershedCurrent dowater = new WatershedCurrent(parent);
		dowater.execute();

	}
	
}
	
package interactivePreprocessing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.SwingUtilities;

import dog3D.DogAll;
import timeGUI.CovistoTimeselectPanel;
import zGUI.CovistoZselectPanel;

public class PREApplyDog3DListener implements ActionListener {
	
	final InteractiveMethods parent;
	
	public PREApplyDog3DListener(final InteractiveMethods parent) {
		
		this.parent = parent;
		
	}
	


	@Override
	public void actionPerformed(final ActionEvent arg0) {

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {

				go();

			}

		});

	}

	public void go() {

	


	
		
		DogAll dosnake = new DogAll(parent);
		dosnake.execute();

	}

}

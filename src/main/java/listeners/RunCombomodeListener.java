package listeners;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import pluginTools.InteractiveSimpleEllipseFit;

public class RunCombomodeListener implements ItemListener {

		InteractiveSimpleEllipseFit parent;

		public RunCombomodeListener(InteractiveSimpleEllipseFit parent) {
			this.parent = parent;
		}

		@Override
		public void itemStateChanged(final ItemEvent arg0) {

			if (arg0.getStateChange() == ItemEvent.DESELECTED) {

				parent.pixelcelltrackcirclefits = false;
				parent.distancemethod = false;
				parent.combomethod = false;

			}

			else if (arg0.getStateChange() == ItemEvent.SELECTED) {

				parent.pixelcelltrackcirclefits = false;
				parent.combomethod = true;
				parent.distancemethod = false;
				parent.resolution = 1; // Integer.parseInt(parent.resolutionField.getText());
			}

		}

	
}

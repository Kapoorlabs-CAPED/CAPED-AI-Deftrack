package listeners;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import pluginTools.IlastikEllipseFileChooser;

public class CurveSimplemodeListener implements ItemListener {

	
	public final IlastikEllipseFileChooser parent;
	
	public CurveSimplemodeListener(final IlastikEllipseFileChooser parent) {
		
           this.parent = parent;
		
	}
	
	
	@Override
	public void itemStateChanged(ItemEvent e) {
	
      if(e.getStateChange() == ItemEvent.DESELECTED) {
			
			parent.superpixel = false;
			parent.simple = false;
			parent.curvesuper = false;
			parent.curvesimple = false;
		}
		
		else if (e.getStateChange() == ItemEvent.SELECTED) {
			
			parent.superpixel = false;
			parent.simple = false;
			parent.curvesuper = false;
			parent.curvesimple = true;
			
			parent.Panelsuperfile.setEnabled(false);
			parent.ChoosesuperImage.setEnabled(false);
			parent.panelFirst.validate();
			parent.panelFirst.repaint();
			
			
		}
		
		
		
	}
	
	
	

}

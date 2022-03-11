package listeners;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import pluginTools.IlastikEllipseFileChooser;

public class CurveSupermodeListener implements ItemListener {
	
	
	public final IlastikEllipseFileChooser parent;
	
	public CurveSupermodeListener(final IlastikEllipseFileChooser parent) {
		
		
		this.parent = parent;
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		
		if(e.getStateChange() == ItemEvent.DESELECTED) {
			
			parent.curvesuper = false;
			parent.curvesimple = false;
			parent.superpixel = false;
			parent.simple = false;
			parent.Godouble.setEnabled(false);
		}
		
		else if (e.getStateChange() == ItemEvent.SELECTED) {
			
			parent.curvesuper = true;
			parent.curvesimple = false;
			parent.superpixel = false;
			parent.simple = false;
			
			parent.Godouble.setEnabled(true);
			parent.Panelsuperfile.setEnabled(true);
			parent.ChoosesuperImage.setEnabled(true);
			
		}
	}
	
	
	

}

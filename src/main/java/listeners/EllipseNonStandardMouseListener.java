package listeners;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import pluginTools.InteractiveSimpleEllipseFit;
import pluginTools.InteractiveSimpleEllipseFit.ValueChange;


public class EllipseNonStandardMouseListener implements MouseMotionListener
{
	final InteractiveSimpleEllipseFit parent;
	final ValueChange change;

	public EllipseNonStandardMouseListener( final InteractiveSimpleEllipseFit parent, final ValueChange change )
	{
		this.parent = parent;
		this.change = change;
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
  
		

		parent.updatePreview(change);
		
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		
	}
	
}

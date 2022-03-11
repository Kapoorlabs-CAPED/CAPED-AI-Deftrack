package interactivePreprocessing;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import interactivePreprocessing.InteractiveMethods.ValueChange;



public class PreNonStandardMouseListener implements MouseMotionListener
{
	final InteractiveMethods parent;
	final ValueChange change;

	public PreNonStandardMouseListener( final InteractiveMethods parent, final ValueChange change )
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

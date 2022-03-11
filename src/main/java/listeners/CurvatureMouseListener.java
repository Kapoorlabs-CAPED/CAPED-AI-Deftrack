package listeners;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import pluginTools.InteractiveSimpleEllipseFit;

/**
 * Updates when mouse is released
 * 
 * @author spreibi
 *
 */
public class CurvatureMouseListener implements MouseListener
{
	final InteractiveSimpleEllipseFit parent;

	public CurvatureMouseListener( final InteractiveSimpleEllipseFit parent )
	{
		this.parent = parent;
	}

	@Override
	public void mouseReleased( MouseEvent arg0 )
	{
		
		parent.StartCurvatureComputingCurrent();
	}

	@Override
	public void mousePressed( MouseEvent arg0 ){}

	@Override
	public void mouseExited( MouseEvent arg0 ) {}

	@Override
	public void mouseEntered( MouseEvent arg0 ) {}

	@Override
	public void mouseClicked( MouseEvent arg0 ) {}
}

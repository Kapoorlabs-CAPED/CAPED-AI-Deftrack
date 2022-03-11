package visualization;

import static visualization.CovistoModelView.KEY_ThreeDRoiobject_COLORING;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;


import ij.ImagePlus;
import ij.gui.Roi;
import linkers.Model3D;
import utility.CovistoUtils;
import utility.ThreeDRoiobject;
import utility.ThreeDRoiobjectCollection;

/**
 * The overlay class in charge of drawing the ThreeDRoiobject images on the hyperstack
 * window.
 *
 * @author Jean-Yves Tinevez &lt;jeanyves.tinevez@gmail.com&gt; 2010 - 2011
 */
public class ThreeDRoiobjectOverlay extends Roi
{

	private static final long serialVersionUID = 1L;

	private static final Font LABEL_FONT = new Font( "Arial", Font.BOLD, 12 );

	private static final boolean DEBUG = false;

	public ThreeDRoiobject editingThreeDRoiobject;

	protected final double[] calibration;

	protected Composite composite = AlphaComposite.getInstance( AlphaComposite.SRC_OVER );

	protected FontMetrics fm;

	protected Collection< ThreeDRoiobject > ThreeDRoiobjectSelection = new ArrayList<>();

	protected Map< String, Object > displaySettings;

	protected final Model3D model;

	/*
	 * CONSTRUCTOR
	 */

	public ThreeDRoiobjectOverlay( final Model3D model, final ImagePlus imp, final Map< String, Object > displaySettings )
	{
		super( 0, 0, imp );
		this.model = model;
		this.imp = imp;
		this.calibration = CovistoUtils.getSpatialCalibration( imp );
		this.displaySettings = displaySettings;
	}

	/*
	 * METHODS
	 */

	@Override
	public void drawOverlay( final Graphics g )
	{
		final int xcorner = ic.offScreenX( 0 );
		final int ycorner = ic.offScreenY( 0 );
		final double magnification = getMagnification();
		final ThreeDRoiobjectCollection ThreeDRoiobjects = model.getThreeDRoiobjects();

		final boolean ThreeDRoiobjectVisible = ( Boolean ) displaySettings.get( CovistoModelView.KEY_ThreeDRoiobjectS_VISIBLE );
		if ( !ThreeDRoiobjectVisible || ThreeDRoiobjects.getNThreeDRoiobjects( true ) == 0 ) {
			return;
		}

		final boolean doLimitDrawingDepth = ( Boolean ) displaySettings.get( CovistoModelView.KEY_LIMIT_DRAWING_DEPTH );
		final double drawingDepth = ( Double ) displaySettings.get( CovistoModelView.KEY_DRAWING_DEPTH );
		final int trackDisplayMode = ( Integer ) displaySettings.get( CovistoModelView.KEY_TRACK_DISPLAY_MODE );
		final boolean selectionOnly = ( trackDisplayMode == CovistoModelView.TRACK_DISPLAY_MODE_SELECTION_ONLY );

		final Graphics2D g2d = ( Graphics2D ) g;
		// Save graphic device original settings
		final AffineTransform originalTransform = g2d.getTransform();
		final Composite originalComposite = g2d.getComposite();
		final Stroke originalStroke = g2d.getStroke();
		final Color originalColor = g2d.getColor();
		final Font originalFont = g2d.getFont();

		g2d.setComposite( composite );
		g2d.setFont( LABEL_FONT );
		g2d.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
		fm = g2d.getFontMetrics();

		final double zslice = ( imp.getSlice() - 1 ) * calibration[ 2 ];
		final double lMag = magnification;
		final int frame = imp.getFrame() - 1;

		// Deal with normal ThreeDRoiobjects.
		@SuppressWarnings( "unchecked" )
		final FeatureColorGenerator< ThreeDRoiobject > colorGenerator = ( FeatureColorGenerator< ThreeDRoiobject > ) displaySettings.get( KEY_ThreeDRoiobject_COLORING );
		g2d.setStroke( new BasicStroke( 1.0f ) );

		if ( selectionOnly && null != ThreeDRoiobjectSelection)
		{
			// Track display mode only displays selection.

			for ( final ThreeDRoiobject ThreeDRoiobject : ThreeDRoiobjectSelection )
			{
				if ( ThreeDRoiobject == editingThreeDRoiobject )
				{
					continue;
				}
				final int sFrame = ThreeDRoiobject.getFeature( ThreeDRoiobject.Time ).intValue();
				if ( sFrame != frame )
				{
					continue;
				}

				final double z = ThreeDRoiobject.getFeature( ThreeDRoiobject.ZPOSITION ).doubleValue();
				if ( doLimitDrawingDepth && Math.abs( z - zslice ) > drawingDepth )
				{
					continue;
				}
				
				final Color color = colorGenerator.color( ThreeDRoiobject );
				g2d.setColor( color );
				drawThreeDRoiobject( g2d, ThreeDRoiobject, zslice, xcorner, ycorner, lMag );
			}

		}
		else
		{
			// Other track displays.

			for ( final Iterator< ThreeDRoiobject > iterator = ThreeDRoiobjects.iterator( frame, true ); iterator.hasNext(); )
			{
				final ThreeDRoiobject ThreeDRoiobject = iterator.next();

				if ( editingThreeDRoiobject == ThreeDRoiobject || ( ThreeDRoiobjectSelection != null && ThreeDRoiobjectSelection.contains( ThreeDRoiobject ) ) )
				{
					continue;
				}

				final Color color = colorGenerator.color( ThreeDRoiobject );
				g2d.setColor( color );

				final double z = ThreeDRoiobject.getFeature( ThreeDRoiobject.ZPOSITION ).doubleValue();
				if ( doLimitDrawingDepth && Math.abs( z - zslice ) > drawingDepth )
				{
					continue;
				}

				drawThreeDRoiobject( g2d, ThreeDRoiobject, zslice, xcorner, ycorner, lMag );
			}

			// Deal with ThreeDRoiobject selection
			if ( null != ThreeDRoiobjectSelection )
			{
				g2d.setStroke( new BasicStroke( 2.0f ) );
				g2d.setColor( CovistoModelView.DEFAULT_HIGHLIGHT_COLOR );
				for ( final ThreeDRoiobject ThreeDRoiobject : ThreeDRoiobjectSelection )
				{
					if ( ThreeDRoiobject == editingThreeDRoiobject )
					{
						continue;
					}
					final int sFrame = ThreeDRoiobject.getFeature( ThreeDRoiobject.Time ).intValue();
					if ( DEBUG )
					{
						System.out.println( "[ThreeDRoiobjectOverlay] For ThreeDRoiobject " + ThreeDRoiobject + " in selection, found frame " + sFrame );
					}
					if ( sFrame != frame )
					{
						continue;
					}
					drawThreeDRoiobject( g2d, ThreeDRoiobject, zslice, xcorner, ycorner, lMag );
				}
			}
		}

		drawExtraLayer( g2d, frame );

		// Deal with editing ThreeDRoiobject - we always draw it with its center at the
		// current z, current t
		// (it moves along with the current slice)
		if ( null != editingThreeDRoiobject )
		{
			g2d.setColor( CovistoModelView.DEFAULT_HIGHLIGHT_COLOR );
			g2d.setStroke( new BasicStroke( 1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f, new float[] { 5f, 5f }, 0 ) );
			final double x = editingThreeDRoiobject.getFeature( ThreeDRoiobject.XPOSITION );
			final double y = editingThreeDRoiobject.getFeature( ThreeDRoiobject.YPOSITION );
			final double radius = editingThreeDRoiobject.getFeature( ThreeDRoiobject.Size ) / calibration[ 0 ] * lMag;
			// In pixel units
			final double xp = x / calibration[ 0 ] + 0.5d;
			final double yp = y / calibration[ 1 ] + 0.5d;
			// Scale to image zoom
			final double xs = ( xp - xcorner ) * lMag;
			final double ys = ( yp - ycorner ) * lMag;
			final double radiusRatio = ( Double ) displaySettings.get( CovistoModelView.KEY_ThreeDRoiobject_RADIUS_RATIO );
			g2d.drawOval( ( int ) Math.round( xs - radius * radiusRatio ), ( int ) Math.round( ys - radius * radiusRatio ), ( int ) Math.round( 2 * radius * radiusRatio ), ( int ) Math.round( 2 * radius * radiusRatio ) );
		}

		// Restore graphic device original settings
		g2d.setTransform( originalTransform );
		g2d.setComposite( originalComposite );
		g2d.setStroke( originalStroke );
		g2d.setColor( originalColor );
		g2d.setFont( originalFont );
	}

	/**
	 * @param g2d 
	 * @param frame  
	 */
	protected void drawExtraLayer( final Graphics2D g2d, final int frame )
	{}

	public void setThreeDRoiobjectSelection( final Collection< ThreeDRoiobject > ThreeDRoiobjects )
	{
		this.ThreeDRoiobjectSelection = ThreeDRoiobjects;
	}

	protected void drawThreeDRoiobject( final Graphics2D g2d, final ThreeDRoiobject ThreeDRoiobject, final double zslice, final int xcorner, final int ycorner, final double magnification )
	{
		final double x = ThreeDRoiobject.getFeature( ThreeDRoiobject.XPOSITION );
		final double y = ThreeDRoiobject.getFeature( ThreeDRoiobject.YPOSITION );
		final double z = ThreeDRoiobject.getFeature( ThreeDRoiobject.ZPOSITION );
		final double dz2 = ( z - zslice ) * ( z - zslice );
		final double radiusRatio = ( Double ) displaySettings.get( CovistoModelView.KEY_ThreeDRoiobject_RADIUS_RATIO );
		final double radius = ThreeDRoiobject.getFeature( ThreeDRoiobject.Size ) * radiusRatio;
		// In pixel units
		final double xp = x / calibration[ 0 ] + 0.5f;
		final double yp = y / calibration[ 1 ] + 0.5f; // so that ThreeDRoiobject centers
		// are displayed on the
		// pixel centers
		// Scale to image zoom
		final double xs = ( xp - xcorner ) * magnification;
		final double ys = ( yp - ycorner ) * magnification;

		if ( dz2 >= radius * radius )
		{
			g2d.fillOval( ( int ) Math.round( xs - 2 * magnification ), ( int ) Math.round( ys - 2 * magnification ), ( int ) Math.round( 4 * magnification ), ( int ) Math.round( 4 * magnification ) );
		}
		else
		{
			final double apparentRadius = Math.sqrt( radius * radius - dz2 ) / calibration[ 0 ] * magnification;
			g2d.drawOval( ( int ) Math.round( xs - apparentRadius ), ( int ) Math.round( ys - apparentRadius ), ( int ) Math.round( 2 * apparentRadius ), ( int ) Math.round( 2 * apparentRadius ) );
			final boolean ThreeDRoiobjectNameVisible = ( Boolean ) displaySettings.get( CovistoModelView.KEY_DISPLAY_ThreeDRoiobject_NAMES );
			if ( ThreeDRoiobjectNameVisible )
			{
				final String str = ThreeDRoiobject.toString();

				final int xindent = fm.stringWidth( str );
				int xtext = ( int ) ( xs + apparentRadius + 5 );
				if ( xtext + xindent > imp.getWindow().getWidth() )
				{
					xtext = ( int ) ( xs - apparentRadius - 5 - xindent );
				}

				final int yindent = fm.getAscent() / 2;
				final int ytext = ( int ) ys + yindent;

				g2d.drawString( ThreeDRoiobject.toString(), xtext, ytext );
			}
		}
	}
}

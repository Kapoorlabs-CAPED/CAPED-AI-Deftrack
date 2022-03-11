package visualization;


import java.util.HashMap;
import java.util.Map;

import linkers.Model3D;
import utility.ModelChangeListener;
import utility.ThreeDRoiobject;

/**
 * An abstract class for ThreeDRoiobject displayers, that can overlay detected ThreeDRoiobjects and
 * tracks on top of the image data.
 * <p>
 *
 * @author Jean-Yves Tinevez &lt;jeanyves.tinevez@gmail.com&gt; Jan 2011
 */
public abstract class AbstractCovistoModelView implements SelectionChangeListener, CovistoModelView, ModelChangeListener
{

	/*
	 * FIELDS
	 */

	/**
	 * A map of String/Object that configures the look and feel of the display.
	 */
	protected Map< String, Object > displaySettings;

	/** The model displayed by this class. */
	protected Model3D model;

	protected final SelectionModel selectionModel;

	/*
	 * PROTECTED CONSTRUCTOR
	 */

	protected AbstractCovistoModelView( final Model3D model, final SelectionModel selectionModel )
	{
		this.selectionModel = selectionModel;
		this.model = model;
		this.displaySettings = initDisplaySettings( model );
		model.addModelChangeListener( this );
		selectionModel.addSelectionChangeListener( this );
	}

	/*
	 * PUBLIC METHODS
	 */

	@Override
	public void setDisplaySettings( final String key, final Object value, final int trackID )
	{
		displaySettings.put( key, value );
	}

	@Override
	public Object getDisplaySettings( final String key )
	{
		return displaySettings.get( key );
	}

	@Override
	public Map< String, Object > getDisplaySettings()
	{
		return displaySettings;
	}

	/**
	 * This needs to be overriden for concrete implementation to display
	 * selection.
	 */
	@Override
	public void selectionChanged( final SelectionChangeEvent event )
	{
		// Center on selection if we added one ThreeDRoiobject exactly
		final Map< ThreeDRoiobject, Boolean > ThreeDRoiobjectsAdded = event.getThreeDRoiobjects();
		if ( ThreeDRoiobjectsAdded != null && ThreeDRoiobjectsAdded.size() == 1 )
		{
			final boolean added = ThreeDRoiobjectsAdded.values().iterator().next();
			if ( added )
			{
				final ThreeDRoiobject ThreeDRoiobject = ThreeDRoiobjectsAdded.keySet().iterator().next();
				centerViewOn( ThreeDRoiobject );
			}
		}
	}

	@Override
	public Model3D getModel()
	{
		return model;
	}

	/**
	 * Provides default display settings.
	 *
	 * @param lModel
	 *            the model this view operate on. Needed for some display
	 *            settings.
	 */
	protected Map< String, Object > initDisplaySettings( final Model3D lModel )
	{
		final Map< String, Object > lDisplaySettings = new HashMap<>( 11 );
		lDisplaySettings.put( KEY_COLOR, DEFAULT_ThreeDRoiobject_COLOR );
		lDisplaySettings.put( KEY_HIGHLIGHT_COLOR, DEFAULT_HIGHLIGHT_COLOR );
		lDisplaySettings.put( KEY_ThreeDRoiobjectS_VISIBLE, true );
		lDisplaySettings.put( KEY_DISPLAY_ThreeDRoiobject_NAMES, false );
		lDisplaySettings.put( KEY_ThreeDRoiobject_COLORING, new DummyThreeDRoiobjectColorGenerator() );
		lDisplaySettings.put( KEY_ThreeDRoiobject_RADIUS_RATIO, 1.0d );
		lDisplaySettings.put( KEY_TRACKS_VISIBLE, true );
		lDisplaySettings.put( KEY_TRACK_DISPLAY_MODE, DEFAULT_TRACK_DISPLAY_MODE );
		lDisplaySettings.put( KEY_TRACK_DISPLAY_DEPTH, DEFAULT_TRACK_DISPLAY_DEPTH );
		lDisplaySettings.put( KEY_TRACK_COLORING, new DummyTrackColorGenerator() );
		lDisplaySettings.put( KEY_COLORMAP, DEFAULT_COLOR_MAP );
		lDisplaySettings.put( KEY_LIMIT_DRAWING_DEPTH, DEFAULT_LIMIT_DRAWING_DEPTH );
		lDisplaySettings.put( KEY_DRAWING_DEPTH, DEFAULT_DRAWING_DEPTH );
		return lDisplaySettings;
	}

	

}

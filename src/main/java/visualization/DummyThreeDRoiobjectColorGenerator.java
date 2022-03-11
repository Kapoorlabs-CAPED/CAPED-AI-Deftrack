package visualization;


import static visualization.CovistoModelView.DEFAULT_ThreeDRoiobject_COLOR;
import java.awt.Color;

import utility.ThreeDRoiobject;

/**
 * A dummy spot color generator that always return the default color.
 *
 * @author Jean-Yves Tinevez - 2013
 */
public class DummyThreeDRoiobjectColorGenerator implements FeatureColorGenerator< ThreeDRoiobject >
{

	@Override
	public Color color( final ThreeDRoiobject obj )
	{
		return DEFAULT_ThreeDRoiobject_COLOR;
	}

	@Override
	public void setFeature( final String feature )
	{}

	@Override
	public void terminate()
	{}

	@Override
	public void activate()
	{}

	@Override
	public String getFeature()
	{
		return ColorByFeatureGUIPanel.UNIFORM_KEY;
	}

	@Override
	public double getMin()
	{
		return Double.NaN;
	}

	@Override
	public double getMax()
	{
		return Double.NaN;
	}

	@Override
	public void setMinMax( final double min, final double max )
	{}

	@Override
	public void autoMinMax()
	{}

	@Override
	public void setAutoMinMaxMode( final boolean autoMode )
	{}

	@Override
	public boolean isAutoMinMaxMode()
	{
		return false;
	}

	@Override
	public void setFrom( final MinMaxAdjustable minMaxAdjustable )
	{}

}

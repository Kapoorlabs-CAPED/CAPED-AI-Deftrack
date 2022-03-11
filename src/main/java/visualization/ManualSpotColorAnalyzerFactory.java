package visualization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;

import net.imagej.ImgPlus;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import utility.ThreeDRoiobject;
import utility.ThreeDRoiobjectAnalyzer;
import utility.ThreeDRoiobjectAnalyzerFactory;

import org.scijava.plugin.Plugin;

import linkers.Dimension;
import linkers.Model3D;



@Plugin( type = ThreeDRoiobjectAnalyzerFactory.class )
public class ManualSpotColorAnalyzerFactory< T extends RealType< T > & NativeType< T >> implements ThreeDRoiobjectAnalyzerFactory< T >
{

	public static final String FEATURE = "MANUAL_COLOR";

	public static final String KEY = "MANUAL_SPOT_COLOR_ANALYZER";

	static final List< String > FEATURES = new ArrayList< >( 1 );

	static final Map< String, String > FEATURE_SHORT_NAMES = new HashMap< >( 1 );

	static final Map< String, String > FEATURE_NAMES = new HashMap< >( 1 );

	static final Map< String, Dimension > FEATURE_DIMENSIONS = new HashMap< >( 1 );

	static final Map< String, Boolean > IS_INT = new HashMap< >( 1 );

	static final String INFO_TEXT = "<html>A dummy analyzer for the feature that stores the color manually assigned to each spot.</html>";

	static final String NAME = "Manual spot color analyzer";

	private static final Double DEFAULT_COLOR_VALUE = Double.valueOf( CovistoModelView.DEFAULT_UNASSIGNED_FEATURE_COLOR.getRGB() );

	static
	{
		FEATURES.add( FEATURE );
		FEATURE_SHORT_NAMES.put( FEATURE, "Spot color" );
		FEATURE_NAMES.put( FEATURE, "Manual spot color" );
		FEATURE_DIMENSIONS.put( FEATURE, Dimension.NONE );
		IS_INT.put( FEATURE, Boolean.TRUE );
	}

	@Override
	public String getKey()
	{
		return KEY;
	}

	@Override
	public List< String > getFeatures()
	{
		return FEATURES;
	}

	@Override
	public Map< String, String > getFeatureShortNames()
	{
		return FEATURE_SHORT_NAMES;
	}

	@Override
	public Map< String, String > getFeatureNames()
	{
		return FEATURE_NAMES;
	}

	@Override
	public Map< String, Dimension > getFeatureDimensions()
	{
		return FEATURE_DIMENSIONS;
	}

	@Override
	public String getInfoText()
	{
		return INFO_TEXT;
	}

	@Override
	public Map< String, Boolean > getIsIntFeature()
	{
		return IS_INT;
	}

	@Override
	public boolean isManualFeature()
	{
		return true;
	}

	@Override
	public ImageIcon getIcon()
	{
		return null;
	}

	@Override
	public String getName()
	{
		return NAME;
	}

	@Override
	public ThreeDRoiobjectAnalyzer< T > getAnalyzer( final Model3D model, final ImgPlus< T > img, final int frame, final int channel )
	{
		return new ThreeDRoiobjectAnalyzer< T >()
				{

			private long processingTime;

			@Override
			public boolean checkInput()
			{
				return true;
			}

			@Override
			public boolean process()
			{
				final long start = System.currentTimeMillis();
				for ( final ThreeDRoiobject spot : model.getThreeDRoiobjects().iterable( false ) )
				{
					if ( null == spot.getFeature( FEATURE ) )
					{
						spot.putFeature( FEATURE, DEFAULT_COLOR_VALUE );
					}
				}
				final long end = System.currentTimeMillis();
				processingTime = end - start;
				return true;
			}

			@Override
			public String getErrorMessage()
			{
				return "";
			}

			@Override
			public long getProcessingTime()
			{
				return processingTime;
			}
				};
	}
}

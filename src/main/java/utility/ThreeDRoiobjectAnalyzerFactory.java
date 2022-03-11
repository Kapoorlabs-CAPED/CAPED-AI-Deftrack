package utility;


import linkers.Model3D;
import net.imagej.ImgPlus;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;

/**
 * Interface for factories that can generate a {@link SpotAnalyzer} configured
 * to operate on a specific frame of a model.
 * <p>
 * Concrete implementation should declare what features they can compute
 * numerically, and make this info available in the
 * {@link fiji.plugin.trackmate.providers.SpotAnalyzerProvider} that returns
 * them.
 * <p>
 * Feature key names are for historical reason all capitalized in an enum
 * manner. For instance: POSITION_X, MAX_INTENSITY, etc... They must be suitable
 * to be used as a attribute key in an xml file.
 *
 * @author Jean-Yves Tinevez - 2012
 */
public interface ThreeDRoiobjectAnalyzerFactory< T extends RealType< T > & NativeType< T > > extends FeatureAnalyzer3D
{

	/**
	 * Returns a configured {@link SpotAnalyzer} ready to operate on the given
	 * frame (0-based) and given channel (0-based). The target frame image and
	 * the target spots are retrieved from the {@link Model} thanks to the given
	 * frame and channel index.
	 *
	 * @param model
	 *            the {@link Model} to take the spots from.
	 * @param img
	 *            the 5D (X, Y, Z, C, T) source image.
	 * @param frame
	 *            the target frame to operate on.
	 * @param channel
	 *            the target channel to operate on.
	 */
	public ThreeDRoiobjectAnalyzer< T > getAnalyzer( final Model3D model, ImgPlus< T > img, int frame, int channel );

}

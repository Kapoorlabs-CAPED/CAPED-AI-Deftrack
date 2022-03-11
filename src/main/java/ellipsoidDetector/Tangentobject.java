package ellipsoidDetector;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import net.imglib2.util.Pair;
import varun_algorithm_ransac_Ransac.Ellipsoid;

public class Tangentobject {

	
	public final ArrayList<double[]> Intersections;
	public final Pair<Ellipsoid, Ellipsoid> ellipsepair;
	public final int t;
	public final int z;
	private final ConcurrentHashMap< String, Double > features = new ConcurrentHashMap< String, Double >();
	
	public Tangentobject(final ArrayList<double[]> Intersections, final Pair<Ellipsoid, Ellipsoid> ellipsepair, final int t, final int z) {
		
		putFeature(FRAME, Double.valueOf(t));
		putFeature(Z, Double.valueOf(z));
		this.Intersections = Intersections;
		this.ellipsepair = ellipsepair;
		this.t = t;
		this.z = z;
		
	}
	
	
	

	/** The label of the blob position feature. */
	public static final String Z = "Z";

	/** The name of the frame feature. */
	public static final String FRAME = "FRAME";
	
	public final Double getFeature( final String feature )
	{
		return features.get( feature );
	}

	/**
	 * Stores the specified feature value for this spot.
	 *
	 * @param feature
	 *            the name of the feature to store, as a {@link String}.
	 * @param value
	 *            the value to store, as a {@link Double}. Using
	 *            <code>null</code> will have unpredicted outcomes.
	 */
	public final void putFeature( final String feature, final Double value )
	{
		features.put( feature, value );
	}
	
	
	
}

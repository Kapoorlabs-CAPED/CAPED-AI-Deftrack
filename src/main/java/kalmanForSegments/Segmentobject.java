package kalmanForSegments;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import ellipsoidDetector.Intersectionobject;
import net.imglib2.AbstractEuclideanSpace;
import net.imglib2.RealLocalizable;

public class Segmentobject extends AbstractEuclideanSpace implements RealLocalizable, Comparable<Segmentobject> {
	
	
	public final int cellLabel;
	
	
	public final int z;
	
	public final int t;
	
	public final int segmentLabel;
	
	public final RealLocalizable centralpoint;
	
	public final RealLocalizable Cellcentralpoint;
	
	public final double Curvature;
	
	public final double IntensityA;
	
	public final double IntensityB;
	
	public final double Perimeter;
	
	public final ArrayList<double[]> sublist;
	
	public static AtomicInteger IDcounter = new AtomicInteger( -1 );
	
	private final int ID;
	
	private String name;
	
	private final ConcurrentHashMap< String, Double > features = new ConcurrentHashMap< String, Double >();
	
	
	public Segmentobject (final ArrayList<double[]> sublist, final RealLocalizable Cellcentralpoint,  final RealLocalizable centralpoint, 
			final double Curvature, final double IntensityA, final double IntensityB, final double Perimeter,  
			final int segmentLabel, final int cellLabel, final int z, final int t) {
		
		
		super(3);
		
		this.cellLabel = cellLabel;
		
		this.segmentLabel = segmentLabel;
		
		this.sublist = sublist;
		
		this.centralpoint = centralpoint;
		
		this.Cellcentralpoint = Cellcentralpoint;
		
		this.ID = IDcounter.incrementAndGet();
		
		this.t = t;
		
		this.z = z;
		
		this.Curvature = Curvature;
		
		this.IntensityA = IntensityA;
		
		this.IntensityB = IntensityB;
		
		this.Perimeter = Perimeter;
		
		this.name = "ID" + ID;
		
		putFeature(IntensityAlabel , IntensityA);
		putFeature(IntensityBlabel , IntensityB);
		putFeature(Curvaturelabel, Curvature);
		
		putFeature(Celllabel , (double)cellLabel);
		putFeature(Segmentlabel , (double)segmentLabel);
		putFeature(Time , (double)t);
		putFeature(Z , (double)z);
		putFeature(XPOSITION , centralpoint.getDoublePosition(0));
		putFeature(YPOSITION , centralpoint.getDoublePosition(1));
		
		
	}
	
	public void setName( final String name )
	{
		this.name = name;
	}

	public int ID()
	{
		return ID;
	}
	
	
	
	/** The name of the blob X position feature. */
	public static final String XPOSITION = "XPOSITION";

	/** The name of the blob Y position feature. */
	public static final String YPOSITION = "YPOSITION";
	
	/** The name of the blob Y position feature. */
	public static final String Time = "Time";
	/** The name of the blob Y position feature. */
	public static final String Z = "Z";

	/** The name of the frame feature. */
	public static final String Segmentlabel = "Segmentlabel";

	/** The name of the frame feature. */
	public static final String Celllabel = "Celllabel";
	

	/** The name of the frame feature. */
	public static final String Curvaturelabel = "Curvaturelabel";

	/** The name of the frame feature. */
	public static final String IntensityAlabel = "IntensityAlabel";
	
	/** The name of the frame feature. */
	public static final String IntensityBlabel = "IntensityBlabel";

	

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
	
	
	/**
	 * Returns the difference between the location of two blobs, this operation
	 * returns ( <code>A.diffTo(B) = - B.diffTo(A)</code>)
	 *
	 * @param target
	 *            the Blob to compare to.
	 * @param int
	 *            n n = 0 for X- coordinate, n = 1 for Y- coordinate
	 * @return the difference in co-ordinate specified.
	 */
	public double diffTo(final Segmentobject target, int n) {

		final double thisBloblocation = centralpoint.getDoublePosition(n);
		final double targetBloblocation = target.centralpoint.getDoublePosition(n);
		return thisBloblocation - targetBloblocation;
	}
	/**
	 * Returns the squared distance between two blobs.
	 *
	 * @param target
	 *            the Blob to compare to.
	 *
	 * @return the distance to the current blob to target blob specified.
	 */

	public double squareDistanceTo(Segmentobject target) {
		// Returns squared distance between the source Blob and the target Blob.

		double[] sourceLocation = new double[centralpoint.numDimensions()];
		double[] targetLocation = new double[target.centralpoint.numDimensions()];
		
		centralpoint.localize(sourceLocation);
		target.centralpoint.localize(targetLocation);
		
		

		double distance = 0;

		for (int d = 0; d < sourceLocation.length; ++d) {

			distance += (sourceLocation[d] - targetLocation[d]) * (sourceLocation[d] - targetLocation[d]);
		}
		return distance;
	}
	
	@Override
	public int compareTo(Segmentobject o) {

		return hashCode() - o.hashCode();
	}


	@Override
	public double getDoublePosition(int arg0) {

		
		return centralpoint.getDoublePosition(arg0);
	}


	@Override
	public float getFloatPosition(int arg0) {

		return centralpoint.getFloatPosition(arg0);
	}


	@Override
	public void localize(float[] position) {
		int n = position.length;
		for (int d = 0; d < n; ++d)
			position[d] = getFloatPosition(d);
		
	}

	@Override
	public void localize(double[] position) {
		int n = position.length;
		for (int d = 0; d < n; ++d)
			position[d] = getFloatPosition(d);
		
	}
	

}

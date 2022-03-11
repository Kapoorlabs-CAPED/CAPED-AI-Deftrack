package utility;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import net.imglib2.AbstractEuclideanSpace;
import net.imglib2.RealLocalizable;

public class Curvatureobject extends AbstractEuclideanSpace implements RealLocalizable, Comparable<Curvatureobject> {

	
	/*
	 * FIELDS
	 */

	public static AtomicInteger IDcounter = new AtomicInteger( -1 );

	/** Store the individual features, and their values. */

	/** A user-supplied name for this spot. */
	private String name;

	/** This spot ID. */
	private final int ID;
	
	
	public final double radiusCurvature;
	public final double distCurvature;
	public final double perimeter;
	public final int t;
	public final int z;
	public final double[] cord;

	public final int Label;
	public final double Intensity;
	public final double SecIntensity;
	
	public Curvatureobject(final double radiusCurvature, final double distCurvature, final double perimeter, final double Intensity, final double SecIntensity, final int Label, final double[] cord, final int t, final int z) {
		super(3);
		this.radiusCurvature = radiusCurvature;
		this.distCurvature = distCurvature;
		this.perimeter = perimeter;
		this.SecIntensity = SecIntensity;
		this.cord = cord;
		this.Intensity = Intensity;
		this.t = t;
		this.z = z;
		this.ID = IDcounter.incrementAndGet();
		this.name = "ID" + ID;
		this.Label = Label;
		
	};
		
	
	
	/*
	 * STATIC KEYS
	 */
	/**
	 * Set the name of this Spot.
	 * 
	 * @param name
	 *            the name to use.
	 */
	public void setName( final String name )
	{
		this.name = name;
	}

	public int ID()
	{
		return ID;
	}

	@Override
	public String toString()
	{
		String str;
		if ( null == name || name.equals( "" ) )
			str = "ID" + ID;
		else
			str = name;
		return str;
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
	
	public static final String Radius = "Radius";

	public static final String Perimeter = "Perimeter";
	
	public static final String LABEL = "LABEL";

	public static final String Time = "Time";
	
	public static final String Z = "Z";
	
	public static final String LocationX = "LocationX";
	
	public static final String LocationY = "LocationY";
	

	
	


	
	@Override
	public int compareTo(Curvatureobject o) {
		return hashCode() - o.hashCode();
	}

	@Override
	public double getDoublePosition(int d) {
		return (float) getDoublePosition(d);
	}

	@Override
	public float getFloatPosition(int d) {
		return (float) getDoublePosition(d);
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

package visualization;

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;

public class DrawCircles {

	
	
	public static void drawCircle(RandomAccessibleInterval<BitType>imgout, double[] min, double[] max, double[] center, double radius) {
		int n = imgout.numDimensions();
		double[] realpos = new double[n];
		double[] size = new double[n];
		double[] location = new double[n];
		double[] position = new double[n];
		double[] iniposition = new double[n];
		double[] backini = new double[n];
		double[] newpos = new double[n];
		double[] backpos = new double[n];
		double[] sigma = new double[n];
		final RandomAccess<BitType> outbound = imgout.randomAccess();
		double stepsize = 0.1;
		int[] setpos = new int[n];
		for (int d = 0; d < n; ++d)
			size[d] = imgout.dimension(d);

		Cursor<BitType> cursor = Views.iterable(imgout).localizingCursor();
		while (cursor.hasNext()) {
			cursor.fwd();
			cursor.localize(location);
			realpos = transformfwd(location, size, min, max);

			// To get a starting point on the circle
			if (Math.pow(realpos[0] - center[0], 2) + Math.pow(realpos[1] - center[1], 2)
					- radius * radius <= 1.0E-50) {
				for (int d = 0; d < n; ++d)
					position[d] = realpos[d];
				break;

			}

		}

		for (int d = 0; d < n; ++d)
			iniposition[d] = position[d];

		double initheta = Math.atan2(iniposition[1] - center[1], iniposition[0] - center[0]);
		double increment = Math.acos((2 * radius * radius - stepsize * stepsize) / (2 * radius * radius));

		backini = transformback(iniposition, size, min, max);
		sigma[0] = 1;
		sigma[1] = 1;
		while (true) {

			// Move the current point along the curve

			newpos[0] = center[0] + radius * Math.cos((initheta - increment));
			newpos[1] = center[1] + radius * Math.sin((initheta - increment));
			initheta = Math.atan2(newpos[1] - center[1], newpos[0] - center[0]);

			// Transform the co-ordinates back as double[]
			backpos = transformback(newpos, size, min, max);

			setpos[0] = (int) Math.round(backpos[0]);
			setpos[1] = (int) Math.round(backpos[1]);

			// To set the pixel intensity
			addGaussian(imgout, backpos, sigma);

			// To make sure that the values transformed back are not out of
			// bounds
			if (backpos[0] < imgout.realMax(0) - imgout.realMin(0) || backpos[0] > imgout.realMin(0)
					|| backpos[1] < imgout.realMax(1) - imgout.realMin(1) || backpos[1] > imgout.realMin(1))

				outbound.setPosition(setpos);

			// Stopping criteria of moving along the circular arc
			if (Math.abs(setpos[0] - (int) Math.round(backini[0])) == 0
					&& Math.abs(setpos[1] - (int) Math.round(backini[1])) == 0)
				break;

			// General Stopping criteria of moving along a curve, when we hit a
			// boundary
			if (newpos[0] >= max[0] || newpos[0] <= min[0] || newpos[1] >= max[1] || newpos[1] <= min[1])

				break;
		}
	}

	public static <T extends RealType<T>> double[] transformfwd(double[] location, double[] size, double[] min,
			double[] max) {

		int n = location.length;

		double[] delta = new double[n];

		final double[] realpos = new double[n];

		for (int d = 0; d < n; ++d){
			
			delta[d] = (max[d] - min[d]) / size[d];

			realpos[d] = location[d] * delta[d] + min[d];
		}
		return realpos;

	}

	public static <T extends RealType<T>> double[] transformback(double[] location, double[] size, double[] min,
			double[] max) {

		int n = location.length;

		double[] delta = new double[n];

		final double[] realpos = new double[n];

		for (int d = 0; d < n; ++d){
			
			delta[d] = (max[d] - min[d]) / size[d];
		    
			realpos[d] = (location[d] - min[d]) / delta[d];
		}
		return realpos;

	}
	
	final public static void addGaussian( final RandomAccessibleInterval< BitType > image, final double[] location, final double[] sigma)
	{
	final int numDimensions = image.numDimensions();
	final int[] size = new int[ numDimensions ];

	final long[] min = new long[ numDimensions ];
	final long[] max = new long[ numDimensions ];

	final double[] two_sq_sigma = new double[ numDimensions ];

	for ( int d = 0; d < numDimensions; ++d )
	{
	size[ d ] = 2 * getSuggestedKernelDiameter( sigma[ d ] );
	min[ d ] = (int)Math.round( location[ d ] ) - size[ d ] / 2;
	max[ d ] = min[ d ] + size[ d ] - 1;
	two_sq_sigma[ d ] =  sigma[ d ] * sigma[ d ];
	}

	final RandomAccessible< BitType > infinite = Views.extendZero( image );
	final RandomAccessibleInterval< BitType > interval = Views.interval( infinite, min, max );
	final IterableInterval< BitType > iterable = Views.iterable( interval );
	final Cursor< BitType > cursor = iterable.localizingCursor();
	
	
	
	while ( cursor.hasNext() )
	{
	cursor.fwd();


	
	
	
	cursor.get().setOne();;
	
	
	}
	
	
	
	
	}
	
	
	
	
	
	
	final public static void addGaussian( final IterableInterval< FloatType > image, final double Amplitude,
			final double[] location, final double[] sigma)
	{
	final int numDimensions = image.numDimensions();
	final int[] size = new int[ numDimensions ];

	final long[] min = new long[ numDimensions ];
	final long[] max = new long[ numDimensions ];


	for ( int d = 0; d < numDimensions; ++d )
	{
	size[ d ] = getSuggestedKernelDiameter( sigma[ d ] ) * 2;
	min[ d ] = (int)Math.round( location[ d ] ) - size[ d ]/2;
	max[ d ] = min[ d ] + size[ d ] - 1;
	
	}

	
	final Cursor< FloatType > cursor = image.localizingCursor();
	while ( cursor.hasNext() )
	{
	cursor.fwd();

	double value = Amplitude;

	for ( int d = 0; d < numDimensions; ++d )
	{
	final double x = location[ d ] - cursor.getIntPosition( d );
	value *= Math.exp( -(x * x) / (sigma[ d ] * sigma[ d ] ) );
	}
	
	
	cursor.get().set( cursor.get().get() + (float)value );
	
	
	}
	
	}

	public static int getSuggestedKernelDiameter( final double sigma )
	{
	int size = 0;
    int cutoff = 5; // This number means cutoff is chosen to be cutoff times sigma. 
    if ( sigma > 0 )
	size = Math.max( cutoff, ( 2 * ( int ) ( cutoff * sigma + 0.5 ) + 1 ) );

	return size;
	}
	
	
	
	
}

package testUtils;

import java.awt.Dimension;

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;

public class DrawCircles {

	public static void DrawEllipseCurvature(RandomAccessibleInterval<FloatType> CurvatureImage, double[] center, double[] radii) {
		
		
	int n = CurvatureImage.numDimensions();
		
		double[] size = new double[n];
		double[] newpos = new double[n];
		double[] sigma = new double[n];
		double Curvature = 0;
		double AsqbyBsq = radii[0] * radii[0] / (radii[1] * radii[1]);
		double BsqbyAsq = 1.0 / AsqbyBsq;
		for (int d = 0; d < n; ++d)
			size[d] = CurvatureImage.dimension(d);
		sigma[0] = 2;
		sigma[1] = 2;
		
		for (int theta = 0; theta < 360; ++theta) {
			
			
				newpos[0] = center[0] + radii[0] * Math.cos(Math.toRadians(theta));
				newpos[1] = center[1] + radii[1] * Math.sin(Math.toRadians(theta));
				double Numerator = Math.abs(radii[0] * radii[1]);
				double Denominator = AsqbyBsq * (newpos[1] - center[1]) *  (newpos[1] - center[1]) + BsqbyAsq * (newpos[0] - center[0]) *  (newpos[0] - center[0]);
				Curvature = Numerator / Math.pow(Denominator, 3.0/2.0);
				addFixed(CurvatureImage, newpos, Curvature);
			
		}
		// The image with ellipse has been created with Intensity being the Curvature Value
		
	}
	
	public static void drawCircleCurvature(RandomAccessibleInterval<FloatType> imgout, double[] center, double radius) {
		
		int n = imgout.numDimensions();
		
		double[] size = new double[n];
		double[] newpos = new double[n];
		double[] sigma = new double[n];
		
		for (int d = 0; d < n; ++d)
			size[d] = imgout.dimension(d);
		sigma[0] = 1;
		sigma[1] = 1;
		
		for (int theta = 0; theta < 360; ++theta) {
			
			
				newpos[0] = center[0] + radius * Math.cos(Math.toRadians(theta));
				newpos[1] = center[1] + radius * Math.sin(Math.toRadians(theta));
				addGaussian(imgout, newpos, sigma , 1);
			
		}
		
		
	}

	final public static void addGaussian( final RandomAccessibleInterval< FloatType > image, final double[] location, final double[] sigma, double Intensity)
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

	final RandomAccessible< FloatType > infinite = Views.extendZero( image );
	final RandomAccessibleInterval< FloatType > interval = Views.interval( infinite, min, max );
	final IterableInterval< FloatType > iterable = Views.iterable( interval );
	final Cursor< FloatType > cursor = iterable.localizingCursor();
	
	
	
	while ( cursor.hasNext() )
	{
	cursor.fwd();

	double value = Intensity;

	for ( int d = 0; d < numDimensions; ++d )
	{
	final double x = location[ d ] - cursor.getDoublePosition( d );
	value *= Math.exp( -(x * x) / two_sq_sigma[ d ] );
	
	
	}
	
	
	cursor.get().set( cursor.get().get() + (float)value );
	
	
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
	
	final public static void addFixed( final RandomAccessibleInterval< FloatType > image, 
			final double[] location, double Intensity)
	{

	
	final Cursor< FloatType > cursor = Views.iterable(image).localizingCursor();
	while ( cursor.hasNext() )
	{
	cursor.fwd();

	double value = Intensity;

	if (Math.abs(cursor.getDoublePosition(0) - location[0]) <= 1 && Math.abs(cursor.getDoublePosition(1) - location[1]) <= 1)
	
	cursor.get().set( (float)value );
	
	
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
	
	public static void main(String[] args) {
		
		new ImageJ();
		
		RandomAccessibleInterval<FloatType> img = new ArrayImgFactory<FloatType>().create(new long[] {512, 512}, new FloatType());
		double[] center = {256, 256};
		double radius = 100;
		double[] radii = {100, 80};
		// Draw a Circle
		//drawCircleCurvature(img, center, radius);
		
		// Draw an Ellipse
		DrawEllipseCurvature(img, center, radii);
		
		ImagePlus imp = ImageJFunctions.show(img);
		imp.setTitle("Curvature Actual");
		IJ.run("Fire");
		
	}
	
	
	
}

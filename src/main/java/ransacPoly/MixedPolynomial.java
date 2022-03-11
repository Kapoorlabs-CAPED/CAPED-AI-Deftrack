package ransacPoly;

import java.util.ArrayList;
import java.util.Collection;


import mpicbg.models.IllDefinedDataPointsException;
import mpicbg.models.NotEnoughDataPointsException;
import mpicbg.models.Point;

public class MixedPolynomial <
A extends AbstractFunction< A > & Polynomial< A, Point >,
B extends AbstractFunction< B > & Polynomial< B, Point > >
extends MixedPolynomialFunction< A, B, MixedPolynomial< A, B > >
implements Polynomial< MixedPolynomial< A, B >, Point >
{
private static final long serialVersionUID = 6929934343495578299L;

public Polynomial< ?, Point > interpolatedFunction;

public MixedPolynomial( final A a, final B b, double lambda )
{
super( a, b, lambda );

// use the higher-order polynom to fit a function to interpolated points
if ( a.degree() > b.degree() )
	interpolatedFunction = a.copy();
else
	interpolatedFunction = b.copy();
}

@Override
protected void interpolate( final Collection< Point > points ) throws NotEnoughDataPointsException, IllDefinedDataPointsException
{
final ArrayList< Point > interpolatedPoints = new ArrayList< Point >();

for ( final Point p : points )
{
	final double x = p.getW()[ 0 ];

	final double y1 = a.predict( x );
	final double y2 = b.predict( x );

	interpolatedPoints.add( new Point( new double[]{ x, l1 * y1 + lambda * y2 } ) );
}

interpolatedFunction.fitFunction( interpolatedPoints );
}

@Override
public double predict( final double x ) { return interpolatedFunction.predict( x ); }

@Override
public double predictFirstderivative( final double x ) { return interpolatedFunction.predictFirstderivative( x ); }

@Override
public double predictSecondderivative( final double x ) { return interpolatedFunction.predictSecondderivative( x ); }
@Override
public double distanceTo( final Point point ) { return interpolatedFunction.distanceTo( point ); }

@Override
public int degree() { return interpolatedFunction.degree(); }

@Override
public double getCoefficient( final int j ) { return interpolatedFunction.getCoefficient( j ); }

@Override
public MixedPolynomial< A, B > copy()
{
final MixedPolynomial< A, B > copy = new MixedPolynomial< A, B >( a.copy(), b.copy(), lambda );

// it must be and AbstractFunction since it is A or B
copy.interpolatedFunction = interpolatedFunction.copy();

copy.setCost( getCost() );

return copy;
}
}

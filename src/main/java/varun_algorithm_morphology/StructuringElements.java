/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2016 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
 * John Bogovic, Albert Cardona, Barry DeZonia, Christian Dietz, Jan Funke,
 * Aivar Grislis, Jonathan Hale, Grant Harris, Stefan Helfrich, Mark Hiner,
 * Martin Horn, Steffen Jaensch, Lee Kamentsky, Larry Lindsey, Melissa Linkert,
 * Mark Longair, Brian Northan, Nick Perry, Curtis Rueden, Johannes Schindelin,
 * Jean-Yves Tinevez and Michael Zinsmaier.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */
package varun_algorithm_morphology;

import java.util.ArrayList;
import java.util.List;

import varun_algorithm_neighborhood.CenteredRectangleShape;
import varun_algorithm_neighborhood.DiamondTipsShape;
import varun_algorithm_neighborhood.HorizontalLineShape;
import varun_algorithm_neighborhood.HyperSphereShape;
import varun_algorithm_neighborhood.PeriodicLineShape;
import varun_algorithm_neighborhood.RectangleShape;
import varun_algorithm_neighborhood.Shape;

/**
 * A collection of static utilities to facilitate the creation of morphological
 * structuring elements.
 *
 * @author Jean-Yves Tinevez Sep - Nov 2013
 */
public class StructuringElements
{

	/**
	 * Radius above which it is advantageous <b>in 2D</b> for the diamond
	 * structuring element to be decomposed in a sequence of small
	 * {@link DiamondTipsShape}s rather than in a single, large
	 * {@link DiamondShape}.
	 */
	private static final int HEURISTICS_DIAMOND_RADIUS_2D = 4;

	/**
	 * Radius above which it is advantageous for the diamond structuring element
	 * to be decomposed in a sequence of small {@link DiamondTipsShape}s rather
	 * than in a single, large {@link DiamondShape}.
	 */
	private static final int HEURISTICS_DIAMOND_RADIUS_OTHERSD = 2;

	/*
	 * METHODS
	 */

	/**
	 * Generates a centered disk flat structuring element for morphological
	 * operations.
	 * <p>
	 * The structuring element (strel) is returned as a {@link List} of
	 * {@link Shape}s, for structuring elements can be decomposed to yield a
	 * better performance. In <b>2D</b>, the disk strel can be
	 * <b>approximated</b> by several periodic lines. The resulting strel is
	 * only an approximation of a disk, and this method offers a parameter to
	 * select the level of approximation. For other dimensionalities, no
	 * optimization are available yet and the parameter is ignored.
	 * <p>
	 * This methods relies on heuristics to determine automatically what
	 * decomposition level to use.
	 *
	 * @param radius
	 *            the radius of the disk, so that it extends over
	 *            {@code 2 ?? radius + 1} in all dimensions
	 * @param dimensionality
	 *            the dimensionality of the target problem.
	 * @return a disk structuring element as a new list of {@link Shape}s.
	 */
	public static final List< Shape > disk( final long radius, final int dimensionality )
	{
		final int decomposition;
		/*
		 * My great heuristics, "determined experimentally". I choose the non-0
		 * (expect for small radius) decomposition that was giving the most
		 * resembling disk shape.
		 */
		if ( dimensionality == 2 )
		{
			if ( radius < 4 )
			{
				decomposition = 0;
			}
			else if ( radius < 9 )
			{
				decomposition = 4;
			}
			else if ( radius < 12 )
			{
				decomposition = 6;
			}
			else if ( radius < 17 )
			{
				decomposition = 8;
			}
			else
			{
				decomposition = 6;
			}
		}
		else
		{
			decomposition = 0;
		}
		return disk( radius, dimensionality, decomposition );
	}

	/**
	 * Generates a centered disk flat structuring element for morphological
	 * operations.
	 * <p>
	 * The structuring element (strel) is returned as a {@link List} of
	 * {@link Shape}s, for structuring elements can be decomposed to yield a
	 * better performance. In <b>2D</b>, the disk strel can be
	 * <b>approximated</b> by several periodic lines. The resulting strel is
	 * only an approximation of a disk, and this method offers a parameter to
	 * select the level of approximation. For other dimensionalities, no
	 * optimization are available yet and the parameter is ignored.
	 *
	 * @param radius
	 *            the radius of the disk, so that it extends over
	 *            {@code 2 ?? radius + 1} in all dimensions
	 * @param dimensionality
	 *            the dimensionality of the target problem.
	 * @param decomposition
	 *            the decomposition to use. Only values 0, 4, 6 and 8 are
	 *            accepted:
	 *            <ol start="0">
	 *            <li>No approximation is made and a full dimension-generic disk
	 *            is returned.</li>
	 *            <li value="4">The disk is decomposed in 4 periodic lines, plus
	 *            in some cases 2 horizontal lines.</li>
	 *            <li value="6">The disk is decomposed in 6 periodic lines, plus
	 *            in some cases 2 horizontal lines.</li>
	 *            <li value="8">The disk is decomposed in 8 periodic lines, plus
	 *            in some cases 2 horizontal lines.</li>
	 *            </ol>
	 *            This parameter is ignored for dimensionality other than 2.
	 * @return a disk structuring element as a new list of {@link Shape}s.
	 */
	public static final List< Shape > disk( final long radius, final int dimensionality, final int decomposition )
	{
		if ( dimensionality == 2 )
		{

			if ( decomposition == 0 )
			{
				/*
				 * No approximation
				 */
				final List< Shape > strel = new ArrayList< Shape >( 1 );
				strel.add( new HyperSphereShape( radius ) );
				return strel;
			}
			else if ( decomposition == 8 || decomposition == 4 || decomposition == 6 )
			{
				/*
				 * Rolf Adams, "Radial Decomposition of Discs and Spheres,"
				 * CVGIP: Graphical Models and Image Processing, vol. 55, no. 5,
				 * September 1993, pp. 325-332.
				 */

				final List< int[] > vectors = new ArrayList< int[] >( decomposition );
				switch ( decomposition )
				{
				case 4:
				{
					vectors.add( new int[] { 1, 0 } ); // 0??
					vectors.add( new int[] { 1, 1 } ); // 45??
					vectors.add( new int[] { 0, 1 } ); // 90??
					vectors.add( new int[] { -1, 1 } ); // 135??
					break;
				}
				case 6:
				{
					vectors.add( new int[] { 1, 0 } ); // 0??
					vectors.add( new int[] { 2, 1 } ); // 60??
					vectors.add( new int[] { 1, 2 } ); // 30??
					vectors.add( new int[] { 0, 1 } ); // 90??
					vectors.add( new int[] { -1, 2 } ); // 120??
					vectors.add( new int[] { -2, 1 } ); // 150??
					break;
				}
				case 8:
				{
					vectors.add( new int[] { 1, 0 } ); // 0??
					vectors.add( new int[] { 2, 1 } ); // 60??
					vectors.add( new int[] { 1, 1 } ); // 45??
					vectors.add( new int[] { 1, 2 } ); // 30??
					vectors.add( new int[] { 0, 1 } ); // 90??
					vectors.add( new int[] { -1, 2 } ); // 120??
					vectors.add( new int[] { -1, 1 } ); // 135??
					vectors.add( new int[] { -2, 1 } ); // 150??
					break;
				}
				default:
					throw new IllegalArgumentException( "The decomposition number must be 0, 4, 6 or 8. Got " + decomposition + "." );
				}

				final double theta = Math.PI / ( 2 * decomposition );
				final double radialExtent = 2 * radius / ( 1 / Math.tan( theta ) + 1 / Math.sin( theta ) );
				final List< Shape > lines = new ArrayList< Shape >( decomposition + 2 );

				long actualRadius = 0;
				for ( final int[] vector : vectors )
				{
					final double norm = Math.sqrt( vector[ 0 ] * vector[ 0 ] + vector[ 1 ] * vector[ 1 ] );
					final long span = ( long ) Math.floor( radialExtent / norm );
					lines.add( new PeriodicLineShape( span, vector ) );
					/*
					 * This estimates the actual radius of the final strel.
					 * Because of the digitization on a grid (we used floor()
					 * above), it will be smaller than the desired radius.
					 */
					actualRadius += span * Math.abs( vector[ 0 ] );
				}

				/*
				 * Compensate for the actual strel being too small
				 */
				if ( actualRadius < radius )
				{
					final long dif = radius - actualRadius;
					lines.add( new HorizontalLineShape( dif, 0, false ) );
					lines.add( new HorizontalLineShape( dif, 1, false ) );
				}

				return lines;
			}
			else
			{
				throw new IllegalArgumentException( "The decomposition number must be 0, 4, 6 or 8. Got " + decomposition + "." );
			}

		}
		else
		{
			/*
			 * All other dims
			 */
			final List< Shape > strel = new ArrayList< Shape >( 1 );
			strel.add( new HyperSphereShape( radius ) );
			return strel;
		}

	}

	/**
	 * Generates a centered square flat structuring element for morphological
	 * operations.
	 * <p>
	 * This method specify the square size using its <b>radius</b> to comply to
	 * sibling methods. The extend of the generated square is
	 * {@code 2 ?? radius + 1} in all dimensions.
	 * <p>
	 * The structuring element (strel) is returned as a {@link List} of
	 * {@link Shape}s, for Structuring elements can be decomposed to yield a
	 * better performance. The square strel can be decomposed in a succession of
	 * orthogonal lines and yield the exact same results on any of the
	 * morphological operations. Because the decomposition becomes
	 * dimension-specific, the dimensionality of the target problem must be
	 * specified. <b>Warning:</b> Undesired effects will occur if the specified
	 * dimensionality and target dimensionality do not match. Non-decomposed
	 * version are dimension-generic.
	 *
	 * @param radius
	 *            the radius of the square.
	 * @param dimensionality
	 *            the dimensionality of the target problem.
	 * @param decompose
	 *            if {@code true}, the structuring element will be
	 *            optimized through decomposition.
	 * @return a new structuring element, as a list of {@link Shape}s.
	 */
	public static final List< Shape > square( final int radius, final int dimensionality, final boolean decompose )
	{
		if ( decompose )
		{
			final List< Shape > strels = new ArrayList< Shape >( dimensionality );
			for ( int d = 0; d < dimensionality; d++ )
			{
				strels.add( new HorizontalLineShape( radius, d, false ) );
			}
			return strels;
		}
		else
		{
			final List< Shape > strel = new ArrayList< Shape >( 1 );
			strel.add( new RectangleShape( radius, false ) );
			return strel;
		}
	}

	/**
	 * Generates a centered square flat structuring element for morphological
	 * operations.
	 * <p>
	 * This method specify the square size using its <b>radius</b> to comply to
	 * sibling methods. The extend of the generated square is
	 * {@code 2 ?? radius + 1} in all dimensions.
	 * <p>
	 * The structuring element (strel) is returned as a {@link List} of
	 * {@link Shape}s, for Structuring elements can be decomposed to yield a
	 * better performance. The square strel can be decomposed in a succession of
	 * orthogonal lines and yield the exact same results on any of the
	 * morphological operations. Because the decomposition becomes
	 * dimension-specific, the dimensionality of the target problem must be
	 * specified. <b>Warning:</b> Undesired effects will occur if the specified
	 * dimensionality and target dimensionality do not match. Non-decomposed
	 * version are dimension-generic.
	 * <p>
	 * This method determines whether it is worth returning a decomposed strel
	 * based on simple heuristics.
	 *
	 * @param radius
	 *            the radius of the square.
	 * @param dimensionality
	 *            the dimensionality of the target problem.
	 * @return a new structuring element, as a list of {@link Shape}s.
	 */
	public static final List< Shape > square( final int radius, final int dimensionality )
	{
		/*
		 * I borrow this "heuristic" to decide whether or not we should
		 * decompose to MATLAB: If the number of neighborhood we get by
		 * decomposing is more than half of what we get without decomposition,
		 * then it is not worth doing decomposition.
		 */
		final long decomposedNNeighbohoods = dimensionality * ( 2 * radius + 1 );
		final long fullNNeighbohoods = ( long ) Math.pow( 2 * radius + 1, dimensionality );
		final boolean decompose = ( decomposedNNeighbohoods < fullNNeighbohoods / 2 );
		return square( radius, dimensionality, decompose );
	}

	/**
	 * Generates a symmetric, centered, rectangular flat structuring element for
	 * morphological operations.
	 * <p>
	 * The structuring element (strel) is returned as a {@link List} of
	 * {@link Shape}s, for Structuring elements can be decomposed to yield a
	 * better performance. The rectangle strel can be decomposed in a succession
	 * of orthogonal lines and yield the exact same results on any of the
	 * morphological operations.
	 *
	 * @param halfSpans
	 *            an {@code int[]} array containing the half-span of the
	 *            symmetric rectangle in each dimension. The total extent of the
	 *            rectangle will therefore be {@code 2 ?? halfSpan[d] + 1}
	 *            in each dimension.
	 * @param decompose
	 *            if {@code true}, the strel will be returned as a
	 *            {@link List} of {@link HorizontalLineShape}, indeed performing
	 *            the rectangle decomposition. If {@code false}, the list
	 *            will be made of a single {@link CenteredRectangleShape}.
	 * @return the desired structuring element, as a {@link List} of
	 *         {@link Shape}s.
	 */
	public static final List< Shape > rectangle( final int[] halfSpans, final boolean decompose )
	{
		final List< Shape > strels;
		if ( decompose )
		{
			strels = new ArrayList< Shape >( halfSpans.length );
			for ( int d = 0; d < halfSpans.length; d++ )
			{
				int r = halfSpans[ d ];
				r = Math.max( 0, r );
				if ( r == 0 )
				{ // No need for empty lines
					continue;
				}
				final HorizontalLineShape line = new HorizontalLineShape( r, d, false );
				strels.add( line );
			}
		}
		else
		{

			strels = new ArrayList< Shape >( 1 );
			final CenteredRectangleShape square = new CenteredRectangleShape( halfSpans, false );
			strels.add( square );

		}
		return strels;
	}

	/**
	 * Generates a symmetric, centered, rectangular flat structuring element for
	 * morphological operations.
	 * <p>
	 * The structuring element (strel) is returned as a {@link List} of
	 * {@link Shape}s, for Structuring elements can be decomposed to yield a
	 * better performance. The rectangle strel can be decomposed in a succession
	 * of orthogonal lines and yield the exact same results on any of the
	 * morphological operations. This method uses a simple heuristic to decide
	 * whether to decompose the rectangle or not.
	 *
	 * @param halfSpans
	 *            an {@code int[]} array containing the half-span of the
	 *            symmetric rectangle in each dimension. The total extent of the
	 *            rectangle will therefore be {@code 2 ?? halfSpan[d] + 1}
	 *            in each dimension.
	 * @return the desired structuring element, as a {@link List} of
	 *         {@link Shape}s.
	 */
	public static final List< Shape > rectangle( final int halfSpans[] )
	{
		/*
		 * I borrow this "heuristic" to decide whether or not we should
		 * decompose to MATLAB: If the number of neighborhood we get by
		 * decomposing is more than half of what we get without decomposition,
		 * then it is not worth doing decomposition.
		 */
		long decomposedNNeighbohoods = 0;
		long fullNNeighbohoods = 1;
		for ( int i = 0; i < halfSpans.length; i++ )
		{
			final int l = 2 * halfSpans[ i ] + 1;
			decomposedNNeighbohoods += l;
			fullNNeighbohoods *= l;
		}

		if ( decomposedNNeighbohoods > fullNNeighbohoods / 2 )
		{
			// Do not optimize
			return rectangle( halfSpans, false );
		}
		else
		{
			// Optimize
			return rectangle( halfSpans, true );
		}
	}



	

	/**
	 * Creates a new periodic line structuring element, that will iterate over
	 * {@code 2 ?? span + 1} pixels as follow:
	 *
	 * <pre>
	 * position - span x increments,
	 * ...
	 * position - 2 ?? increments,
	 * position - increments,
	 * position,
	 * position + increments,
	 * position + 2 ?? increments,
	 * ...
	 * position + span x increments
	 * </pre>
	 *
	 * The importance of periodic lines is explained in [1].
	 *
	 * @param span
	 *            the span of the neighborhood, so that it will iterate over
	 *            {@code 2 ?? span + 1} pixels.
	 * @param increments
	 *            the values by which each element of the position vector is to
	 *            be incremented when iterating.
	 * @see <a
	 *      href="http://www.sciencedirect.com/science/article/pii/0167865596000669">[1]</a>
	 *      Jones and Soilles.Periodic lines: Definition, cascades, and
	 *      application to granulometries. Pattern Recognition Letters (1996)
	 *      vol. 17 (10) pp. 1057-1063.
	 */
	public static final Shape periodicLine( final long span, final int[] increments )
	{
		return new PeriodicLineShape( span, increments );
	}
}

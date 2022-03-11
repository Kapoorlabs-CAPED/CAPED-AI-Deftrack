package costMatrix;

import utility.PreRoiobject;

/**
 * Implementation of various cost functions
 * 
 * 
 */

// Cost function base don minimizing the squared distances

public class SquareDistCostFunction implements CostFunction< PreRoiobject, PreRoiobject >
{

	@Override
	public double linkingCost( final PreRoiobject source, final PreRoiobject target )
	{
		return source.squareDistanceTo(target );
	}
	
	
	
	

}

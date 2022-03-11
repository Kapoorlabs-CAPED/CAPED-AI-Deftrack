package costMatrix;

import utility.PreRoiobject;

public class IntensityDiffCostFunction implements CostFunction< PreRoiobject, PreRoiobject >
	{

		
	

	@Override
	public double linkingCost( final PreRoiobject source, final PreRoiobject target )
	{
		return source.IntensityDistanceTo(target );
	}
		

	
}

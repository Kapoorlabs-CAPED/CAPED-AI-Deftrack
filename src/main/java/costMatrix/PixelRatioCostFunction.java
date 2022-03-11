package costMatrix;

import utility.PreRoiobject;

public class PixelRatioCostFunction implements CostFunction< PreRoiobject, PreRoiobject > {

	
	
	/**
	 * Implementation of various cost functions
	 * 
	 * 
	 */

	
		@Override
		public double linkingCost( final PreRoiobject source, final PreRoiobject target )
		{
			return source.numberofPixelsRatioTo(target);
		}
		
		
		
		

	
	
}

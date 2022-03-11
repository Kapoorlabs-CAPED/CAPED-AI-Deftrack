package kalmanForSegments;

import costMatrix.CostFunction;
import ellipsoidDetector.Intersectionobject;

public class ETrackSegmentCostFunction  implements CostFunction< Segmentobject, Segmentobject >
{

	
// Alpha is the weightage given to distance and Beta is the weightage given to the ratio of pixels
	public final double beta;
	public final double alpha;
	
	

	
	public double getAlpha(){
		
		return alpha;
	}
	
  
	public double getBeta(){
		
		return beta;
	}

	public ETrackSegmentCostFunction (double alpha, double beta){
		
		this.alpha = alpha;
		this.beta = beta;
		
	}
	
	
@Override
public double linkingCost( final Segmentobject source, final Segmentobject target )
{
	return source.squareDistanceTo(target);
}
}
	
package varun_algorithm_ransac_Ransac;

import java.util.ArrayList;

import net.imglib2.RealLocalizable;

public class RansacFunctionEllipsoid{

public final Circle function;



public final ArrayList<RealLocalizable> inliers;

public final ArrayList<RealLocalizable> candidates;

/**
 * 
 * A ransac function output containing an ellipsoid function
 * @param function
 * @param linearfunction
 * @param inliers
 */
public RansacFunctionEllipsoid(final Circle function, ArrayList<RealLocalizable> inliers,  ArrayList<RealLocalizable> candidates ) {
	
	
	this.function = function;

	this.inliers = inliers;
	
	this.candidates = candidates;
	
}
}
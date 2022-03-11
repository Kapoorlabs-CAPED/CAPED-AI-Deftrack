package ransacPoly;

import java.util.ArrayList;


import ransac.PointFunctionMatch.PointFunctionMatch;

public class RansacFunction {

	
	public final HigherOrderPolynomialFunction function;
	
	public final MixedPolynomialFunction<HigherOrderPolynomialFunction, HigherOrderPolynomialFunction,MixedPolynomial<HigherOrderPolynomialFunction, HigherOrderPolynomialFunction> > mixedfunction;
	
	public final LinearFunction backup;

	public final ArrayList<PointFunctionMatch> inliers;
	
	public final ArrayList<PointFunctionMatch> candidates;
	
	/**
	 * 
	 * A ransac function output containing a quadratic function
	 * @param function
	 * @param linearfunction
	 * @param inliers
	 */
	public RansacFunction(final HigherOrderPolynomialFunction function, ArrayList<PointFunctionMatch> inliers,  ArrayList<PointFunctionMatch> candidates ) {
		
		
		this.function = function;

		this.backup = null;
		
		this.mixedfunction  = null;
		
		this.inliers = inliers;
		
		this.candidates = candidates;
		
	}

	/**
	 * 
	 * A ransac function output containing a linear function 
	 * @param function
	 * @param linearfunction
	 * @param inliers
	 */
	public RansacFunction(final LinearFunction backup, ArrayList<PointFunctionMatch> inliers,  ArrayList<PointFunctionMatch> candidates ) {
		
		
		this.function = null;

		this.backup = backup;
		
		this.mixedfunction = null;
		
		this.inliers = inliers;
		
		this.candidates = candidates;
		
	}
	/**
	 * 
	 * A ransac function output containing a mix between a linear and a quadratic function 
	 * @param mixedfunction
	 * @param inliers
	 * @param candidates
	 */
	
	public RansacFunction(final MixedPolynomialFunction<HigherOrderPolynomialFunction, HigherOrderPolynomialFunction,MixedPolynomial<HigherOrderPolynomialFunction, HigherOrderPolynomialFunction> > mixedfunction, ArrayList<PointFunctionMatch> inliers,  ArrayList<PointFunctionMatch> candidates ) {
		
		
		this.function = null;

		this.backup = null;
		
		this.mixedfunction = mixedfunction;
		
		this.inliers = inliers;
		
		this.candidates = candidates;
		
	}
    
}

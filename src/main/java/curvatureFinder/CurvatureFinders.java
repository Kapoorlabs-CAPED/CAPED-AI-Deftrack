package curvatureFinder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import curvatureUtils.ClockDisplayer;
import ellipsoidDetector.Intersectionobject;
import net.imglib2.RealLocalizable;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.util.Pair;
import pluginTools.InteractiveSimpleEllipseFit;
import pluginTools.RegressionCurveSegment;
import varun_algorithm.OutputAlgorithm;

public interface CurvatureFinders<T extends RealType<T> & NativeType<T>>
		extends OutputAlgorithm<ConcurrentHashMap<Integer, RegressionCurveSegment>> {

	public RegressionLineProfile getLocalcurvature(ArrayList<double[]> Cordlist,
			RealLocalizable centerpoint, int strideindex);

	

	public Pair<RegressionLineProfile,  ClockDisplayer> getCircleLocalcurvature(ArrayList<double[]> Cordlist,
			RealLocalizable centerpoint, int strideindex, String name);
	
	
	
	public void OverSliderLoop(InteractiveSimpleEllipseFit parent, List<RealLocalizable> Ordered,
			RealLocalizable centerpoint, List<RealLocalizable> truths,
			ArrayList<Intersectionobject> AllCurveintersection, HashMap<Integer, Intersectionobject> AlldenseCurveintersection,
			int ndims, int celllabel, int t, int z);

}

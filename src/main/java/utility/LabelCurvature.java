package utility;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.Map.Entry;
import java.util.Random;

import javax.swing.JProgressBar;

import com.google.common.eventbus.AllowConcurrentEvents;

import curvatureFinder.CurvatureFinderCircleFit;
import curvatureFinder.CurvatureFinderDistance;
import curvatureUtils.DisplaySelected;
import curvatureUtils.PointExtractor;
import ellipsoidDetector.Distance;
import ellipsoidDetector.Intersectionobject;
import ellipsoidDetector.Tangentobject;
import ij.IJ;
import ij.ImagePlus;
import ij.gui.Arrow;
import ij.gui.EllipseRoi;
import ij.gui.Line;
import ij.gui.OvalRoi;
import ij.gui.Roi;
import kalmanForSegments.Segmentobject;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;
import pluginTools.InteractiveSimpleEllipseFit;
import pluginTools.RegressionCurveSegment;
import pluginTools.InteractiveSimpleEllipseFit.ValueChange;
import ransacPoly.RegressionFunction;
import varun_algorithm_ransac_Ransac.*;

public class LabelCurvature implements Callable< HashMap<Integer,Intersectionobject>> {

	final InteractiveSimpleEllipseFit parent;
	final RandomAccessibleInterval<FloatType> ActualRoiimg;
	List<RealLocalizable> truths;
	final int t;
	final int z;
	final int celllabel;
	int percent;
	final ArrayList<Line> resultlineroi;
	final ArrayList<OvalRoi> resultcurvelineroi;
	final ArrayList<OvalRoi> resultallcurvelineroi;
	final ArrayList<EllipseRoi> ellipselineroi;
	final ArrayList<Roi> segmentrect;
	final JProgressBar jpb;
	ArrayList<Intersectionobject> AllCurveintersection;

	ArrayList<Segmentobject> AllCurveSegments;

	public LabelCurvature(final InteractiveSimpleEllipseFit parent,
			final RandomAccessibleInterval<FloatType> ActualRoiimg, List<RealLocalizable> truths,
			ArrayList<Line> resultlineroi, ArrayList<OvalRoi> resultcurvelineroi,
			ArrayList<OvalRoi> resultallcurvelineroi, ArrayList<EllipseRoi> ellipselineroi, ArrayList<Roi> segmentrect,
			ArrayList<Intersectionobject> AllCurveintersection,  
			final int t, final int z, final int celllabel) {

		this.parent = parent;
		this.ActualRoiimg = ActualRoiimg;
		this.truths = truths;
		this.t = t;
		this.z = z;
		this.AllCurveintersection = AllCurveintersection;

		this.jpb = null;
		this.percent = 0;
		this.resultlineroi = resultlineroi;
		this.resultcurvelineroi = resultcurvelineroi;
		this.resultallcurvelineroi = resultallcurvelineroi;
		this.ellipselineroi = ellipselineroi;
		this.celllabel = celllabel;
		this.segmentrect = segmentrect;
	}

	public LabelCurvature(final InteractiveSimpleEllipseFit parent,
			final RandomAccessibleInterval<FloatType> ActualRoiimg, List<RealLocalizable> truths,
			ArrayList<Line> resultlineroi, ArrayList<OvalRoi> resultcurvelineroi,
			ArrayList<OvalRoi> resultallcurvelineroi, ArrayList<EllipseRoi> ellipselineroi, ArrayList<Roi> segmentrect,
			ArrayList<Intersectionobject> AllCurveintersection,
		    final int t, final int z, final JProgressBar jpb,
			final int percent, final int celllabel) {

		this.parent = parent;
		this.ActualRoiimg = ActualRoiimg;
		this.resultlineroi = resultlineroi;
		this.resultcurvelineroi = resultcurvelineroi;
		this.resultallcurvelineroi = resultallcurvelineroi;
		this.ellipselineroi = ellipselineroi;
		this.truths = truths;
		this.t = t;
		this.z = z;
		this.jpb = jpb;
		this.percent = percent;
		this.AllCurveintersection = AllCurveintersection;
		
		this.celllabel = celllabel;
		this.segmentrect = segmentrect;
	}
	
	
	private HashMap<Integer,Intersectionobject> CurvatureFinderChoice() {
		
		 HashMap<Integer,Intersectionobject>  AlldenseCurveintersection = new HashMap<Integer,Intersectionobject>();
		
	
		
		if (parent.pixelcelltrackcirclefits) {
		
		CurvatureFinderCircleFit<FloatType> curvecircle = new CurvatureFinderCircleFit<FloatType>(parent, AllCurveintersection, AlldenseCurveintersection, ActualRoiimg, jpb, percent, celllabel, z, t);
		
		curvecircle.process();
		
		AlldenseCurveintersection = curvecircle.getMap();
		
		}
		
		if(parent.distancemethod) {
			
		CurvatureFinderDistance<FloatType> curvedistance = new CurvatureFinderDistance<FloatType>(parent, AllCurveintersection, AlldenseCurveintersection, ActualRoiimg, jpb, percent, celllabel, z, t);
		
		curvedistance.process();
		
		AlldenseCurveintersection = curvedistance.getMap();
		
	     }
		
		if(parent.combomethod) {
			
			CurvatureFinderCircleFit<FloatType> curvedistance = new CurvatureFinderCircleFit<FloatType>(parent, AllCurveintersection, AlldenseCurveintersection, ActualRoiimg, jpb, percent, celllabel, z, t);
			
			curvedistance.process();
			
			AlldenseCurveintersection = curvedistance.getMap();
			
			
		}
		
		return AlldenseCurveintersection;
	}


	@Override
	public  HashMap<Integer,Intersectionobject> call() throws Exception {
		parent.Allnodes.clear();
		parent.Nodemap.clear();
		parent.Listmap.clear();

		if (parent.fourthDimensionSize != 0 && parent.Accountedframes.size() != 0 && parent.Accountedframes != null)
			utility.ProgressBar.SetProgressBar(jpb, 100 * percent / (parent.pixellist.size()), "Computing Curvature = "
					+ t + "/" + parent.fourthDimensionSize + " Z = " + z + "/" + parent.thirdDimensionSize);
		else if (parent.thirdDimensionSize != 0 && parent.AccountedZ.size() != 0 && parent.AccountedZ != null)
			utility.ProgressBar.SetProgressBar(jpb, 100 * percent / (parent.pixellist.size()),
					"Computing Curvature T/Z = " + z + "/" + parent.thirdDimensionSize);
		else {

			utility.ProgressBar.SetProgressBar(jpb, 100 * (percent) / (parent.pixellist.size()),
					"Computing Curvature ");
		}

		
		HashMap<Integer,Intersectionobject> AlldenseCurveintersection = 	CurvatureFinderChoice();
		
		
		
		return  AlldenseCurveintersection;
	}


	

}

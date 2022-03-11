package curvatureFinder;

import java.util.List;

import javax.swing.JProgressBar;

import batchMode.LocalPrefs;
import ij.IJ;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.type.numeric.real.FloatType;
import pluginTools.InteractiveSimpleEllipseFit;
import varun_algorithm_ransac_Ransac.ConnectedComponentCoordinates;

public class GetCandidatePoints {
	
	static double fcteps = 1.0E-10;
	
	public static List<RealLocalizable> ListofPoints(final InteractiveSimpleEllipseFit parent, final RandomAccessibleInterval<FloatType> ActualRoiimg ,final JProgressBar jpb, int percent, int t, int z) {
		
		parent.Allnodes.clear();
		parent.Nodemap.clear();
		parent.Listmap.clear();

		if (parent.fourthDimensionSize != 0 && parent.Accountedframes.size() != 0 && parent.Accountedframes != null)
			utility.ProgressBar.SetProgressBar(jpb, 100 * percent / (parent.pixellist.size() + fcteps), "Computing Curvature = "
					+ t + "/" + parent.fourthDimensionSize + " Z = " + z + "/" + parent.thirdDimensionSize);
		else if (parent.thirdDimensionSize != 0 && parent.AccountedZ.size() != 0 && parent.AccountedZ != null)
			utility.ProgressBar.SetProgressBar(jpb, 100 * percent / (parent.pixellist.size()+ fcteps),
					"Computing Curvature T/Z = " + z + "/" + parent.thirdDimensionSize);
		else {

			utility.ProgressBar.SetProgressBar(jpb, 100 * (percent) / (parent.pixellist.size()+ fcteps),
					"Computing Curvature ");
		}
		
		
		List<RealLocalizable> truths = ConnectedComponentCoordinates.GetCoordinatesBit(ActualRoiimg);
		parent.boxsize = LocalPrefs.getInt(".Box.int", parent.minNumInliers);
		
	
		//parent.SpecialminInlierField.setText("Box Size = " + " " + Double.toString(parent.boxsize) + " " + "um");
		//parent.SpecialminInlierField.repaint();
		//parent.SpecialminInlierField.validate();
		return truths;
	}

}

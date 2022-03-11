package userTESTING;

import distanceTransform.CreateBinary;
import distanceTransform.CreateDistanceTransform;
import distanceTransform.CreateWatershed;
import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.io.Opener;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.stats.Normalize;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.IntervalView;
import net.imglib2.view.Views;
import preProcessing.FlatFieldOnly;
import preProcessing.Kernels;

public class BorisData {

	
	public static void main(String[] args) {
		
		
		new ImageJ();
		
		
		ImagePlus imp = new Opener().openImage("/Users/aimachine/Documents/Boris_Data/wt-PHGFP_scutellum_18-19hAPF/TESTgfp-pre.tif");
		RandomAccessibleInterval<FloatType> inputimage = ImageJFunctions.convertFloat(imp);
		new Normalize();
		FloatType minval = new FloatType(0);
		FloatType maxval = new FloatType(1);

		
		IJ.runMacroFile("/Users/aimachine/Documents/Boris_Data/wt-PHGFP_scutellum_18-19hAPF/Testwatershed.ijm");
	}
	
	
	
}

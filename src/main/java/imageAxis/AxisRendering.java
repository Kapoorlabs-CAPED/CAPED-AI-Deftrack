package imageAxis;

import ij.IJ;
import ij.ImagePlus;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.real.FloatType;

public class AxisRendering {

	
	
	public static void Reshape(RandomAccessibleInterval<FloatType> image, String title) {
		
		int channels, frames;
		
		ImagePlus imp = ImageJFunctions.wrapFloat(image, title);
		if(imp.getNChannels() > imp.getNFrames()) {
			channels = imp.getNFrames();
		    frames = imp.getNChannels();
		    
		}
		
		else {
			
			channels = imp.getNChannels();
		    frames = imp.getNFrames();
			
		}
		
		IJ.log(imp.getNSlices() + " " + channels + " " + frames);
		imp.setDimensions(channels, imp.getNSlices(), frames);
		imp.show();
		
	}
	
}

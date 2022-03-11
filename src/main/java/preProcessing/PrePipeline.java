package preProcessing;


import mpicbg.imglib.image.display.imagej.ImageJFunctions;
import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.morphology.table2d.Branchpoints;
import net.imglib2.algorithm.morphology.table2d.Clean;
import net.imglib2.algorithm.morphology.table2d.Spur;
import net.imglib2.algorithm.morphology.table2d.Thin;
import net.imglib2.algorithm.morphology.table2d.Thin1;
import net.imglib2.algorithm.morphology.table2d.Thin2;
import net.imglib2.algorithm.neighborhood.Neighborhood;
import net.imglib2.algorithm.neighborhood.RectangleShape;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.type.BooleanType;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Util;
import net.imglib2.view.ExtendedRandomAccessibleInterval;
import net.imglib2.view.Views;

public class PrePipeline {

	public static   RandomAccessibleInterval<FloatType>  Dothinning(final RandomAccessibleInterval<FloatType> inputimage, final int FlatFieldRadi){
		
	
		//PreProcessing Step
		RandomAccessibleInterval<FloatType> inputimagePRE = Utils.FlatFieldOnly(inputimage, FlatFieldRadi); 
		
		float threshold = Otsu.AutomaticThresholding(inputimagePRE);
		Img<BitType> Bitinuputimage = Otsu.Getbinaryimage(inputimagePRE,threshold);
		
		
		RandomAccessibleInterval<FloatType> output = Otsu.convertBittoFloat(Bitinuputimage);
		
		
		
		return output;
	}
	
	public static   RandomAccessibleInterval<FloatType>  SelectClassLabel(final RandomAccessibleInterval<FloatType> inputimage, final int label){
		
	// From Ilastik the label for image
		RandomAccessibleInterval<FloatType> preoutput = Otsu.Getlabelledimage(inputimage, label);
		float threshold = Otsu.AutomaticThresholding(preoutput);
		Img<BitType> Bitinuputimage = Otsu.Getbinaryimage(preoutput,threshold);
		
		RandomAccessibleInterval<FloatType> output = Otsu.convertBittoFloat(Thin.thin(Thin.thin(Bitinuputimage)));
		
		
		return output;
	}
	
	
	
	
}

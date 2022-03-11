package watershedCurrent;

import java.util.concurrent.ExecutionException;

import javax.naming.InitialContext;
import javax.swing.SwingWorker;

import common3D.CommonWater;
import distanceTransform.DistWatershed;
import distanceTransform.WatershedBinary;
import interactivePreprocessing.InteractiveMethods;
import interactivePreprocessing.InteractiveMethods.ValueChange;
import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;
import preProcessing.GetLocalmaxminMT;
import preProcessing.GlobalThresholding;
import timeGUI.CovistoTimeselectPanel;
import watershedGUI.CovistoWatershedPanel;
import zGUI.CovistoZselectPanel;

public class WatershedCurrent extends SwingWorker<Void, Void> {

	final InteractiveMethods parent;

	public WatershedCurrent(final InteractiveMethods parent) {

		this.parent = parent;

	}

	@Override
	protected Void doInBackground() throws Exception {


		
		parent.apply3D = false;
	   RandomAccessibleInterval<FloatType> newimg = new ArrayImgFactory<FloatType>().create(parent.originalimg, new FloatType());
		
		RandomAccessibleInterval<BitType> bitimg = new ArrayImgFactory<BitType>().create(newimg, new BitType());
		
		RandomAccessibleInterval<IntType> intimg = new ArrayImgFactory<IntType>().create(newimg, new IntType());
		
		int t = CovistoTimeselectPanel.fourthDimension;
		int z = CovistoZselectPanel.thirdDimension;
		
		CommonWater.Watershed(parent, newimg, bitimg, intimg, t, z);
		if(parent.displayBinaryimg)
			ImageJFunctions.show(bitimg).setTitle("Binary Image");
		
		if (parent.displayWatershedimg)
			ImageJFunctions.show(intimg).setTitle("Integer Image");
		parent.intimg = intimg;
		
		if (parent.displayDistTransimg)
			ImageJFunctions.show(newimg ).setTitle("Distance Transform Image");
		
		return null;
	}
	
	
	
	@Override
	protected void done() {
		try {
		
			parent.apply3D = false;
			get();
		} catch (ExecutionException | InterruptedException e) {
			e.printStackTrace();
		}

	}
}

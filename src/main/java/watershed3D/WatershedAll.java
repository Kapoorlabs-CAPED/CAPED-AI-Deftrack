package watershed3D;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import common3D.CommonWater;
import distanceTransform.DistWatershed;
import interactivePreprocessing.InteractiveMethods;
import interactivePreprocessing.InteractiveMethods.ValueChange;
import linkers.PRENNsearch;
import net.imglib2.Cursor;
import net.imglib2.Interval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.NativeType;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Intervals;
import net.imglib2.util.Pair;
import net.imglib2.view.IntervalView;
import net.imglib2.view.Views;
import preProcessing.GetLocalmaxminMT;
import preProcessing.GlobalThresholding;
import timeGUI.CovistoTimeselectPanel;
import utility.PreRoiobject;
import utility.ThreeDRoiobject;
import watershedGUI.CovistoWatershedPanel;
import zGUI.CovistoZselectPanel;

public class WatershedAll extends SwingWorker<Void, Void> {

	final InteractiveMethods parent;

	public WatershedAll(final InteractiveMethods parent) {

		this.parent = parent;

	}

	@Override
	protected Void doInBackground() throws Exception {


		
		parent.apply3D = true;
		
		RandomAccessibleInterval<FloatType> newimg = new ArrayImgFactory<FloatType>().create(parent.originalimg, new FloatType());
		
		RandomAccessibleInterval<BitType> bitimg = new ArrayImgFactory<BitType>().create(newimg, new BitType());
		
		RandomAccessibleInterval<IntType> intimg = new ArrayImgFactory<IntType>().create(newimg, new IntType());
		
		for (int t = CovistoTimeselectPanel.fourthDimensionsliderInit; t <= CovistoTimeselectPanel.fourthDimensionSize; ++t) {


			for (int z = CovistoZselectPanel.thirdDimensionsliderInit; z <= CovistoZselectPanel.thirdDimensionSize; ++z) {
				
				CovistoZselectPanel.thirdDimension = z;
				CovistoTimeselectPanel.fourthDimension = t;
				CommonWater.Watershed(parent, newimg, bitimg, intimg, t, z);
			
			}
			
		
		}
		
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

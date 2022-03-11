package common3D;

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

public class CommonWater {

	public static void Watershed( final InteractiveMethods parent,RandomAccessibleInterval<FloatType> newimg,RandomAccessibleInterval<BitType> bitimg,RandomAccessibleInterval<IntType> intimg, int t, int z ) {
		

	
				
				CovistoZselectPanel.thirdDimension = z;
				CovistoTimeselectPanel.fourthDimension = t;
				
				parent.CurrentView = utility.CovistoSlicer.getCurrentView(parent.originalimg, z, CovistoZselectPanel.thirdDimensionSize, t,
						CovistoTimeselectPanel.fourthDimensionSize);
				parent.updatePreview(ValueChange.THIRDDIMmouse);
				
				RandomAccessibleInterval<BitType> currentbitimg = utility.CovistoSlicer.getCurrentView(bitimg, z, CovistoZselectPanel.thirdDimensionSize, t,
						CovistoTimeselectPanel.fourthDimensionSize);
				
				RandomAccessibleInterval<IntType> currentintimg = utility.CovistoSlicer.getCurrentView(intimg, z, CovistoZselectPanel.thirdDimensionSize, t,
						CovistoTimeselectPanel.fourthDimensionSize);
				
				RandomAccessibleInterval<FloatType> currentnewimg = utility.CovistoSlicer.getCurrentView(newimg, z, CovistoZselectPanel.thirdDimensionSize, t,
						CovistoTimeselectPanel.fourthDimensionSize);
				
			processSlice(parent, parent.CurrentView, currentintimg, currentbitimg, currentnewimg);
			
		
		
	
		
		
		
	}
	public static void processSlice(final InteractiveMethods parent, RandomAccessibleInterval< FloatType > slice, RandomAccessibleInterval< IntType > intoutputslice, RandomAccessibleInterval< BitType > bitoutputslice,
			RandomAccessibleInterval< FloatType > distslice) {
		
		
		RandomAccessibleInterval<BitType> bitimg = new ArrayImgFactory<BitType>().create(slice, new BitType());
		
		
		
		if(parent.autothreshwater) {
		CovistoWatershedPanel.thresholdWater = (float) ( GlobalThresholding.AutomaticThresholding(slice));
		CovistoWatershedPanel.thresholdWaterslider.setValue(utility.ETrackScrollbarUtils.computeScrollbarPositionFromValue(CovistoWatershedPanel.thresholdWater, CovistoWatershedPanel.thresholdMinWater, 
				CovistoWatershedPanel.thresholdMaxWater, CovistoWatershedPanel.scrollbarSize));
		CovistoWatershedPanel.watertext.setText(CovistoWatershedPanel.waterstring +  " = "  + CovistoWatershedPanel.thresholdWater );
		CovistoWatershedPanel.thresholdWaterslider.validate();
		CovistoWatershedPanel.thresholdWaterslider.repaint();
		}
		GetLocalmaxminMT.ThresholdingMTBit(slice, bitimg, CovistoWatershedPanel.thresholdWater);
		
		RandomAccessibleInterval<IntType> waterint = null;
		RandomAccessibleInterval<FloatType> distwater = null;
		
	
		if (CovistoWatershedPanel.disttransform) {
		DistWatershed<FloatType> WaterafterDisttransform = new DistWatershed<FloatType>(parent, slice, bitimg,
				parent.jpb, parent.apply3D);
		
		WaterafterDisttransform.execute();
	     waterint =  WaterafterDisttransform.getResult();
		 distwater =  WaterafterDisttransform.getDistanceTransformedimg();
		
		}
		else {
			
			WatershedBinary Wateronly = new WatershedBinary(bitimg);
			Wateronly.process();
			waterint = Wateronly.getResult();
			
		}
		
		Cursor< BitType > bitcursor = Views.iterable(bitoutputslice).localizingCursor();
		Cursor< IntType > intcursor = Views.iterable(intoutputslice).localizingCursor();
		

		
		RandomAccess<BitType> ranac = bitimg.randomAccess();
		RandomAccess<IntType> intranac = waterint.randomAccess();
		
		
		while(bitcursor.hasNext()) {
			
			bitcursor.fwd();
			
			ranac.setPosition(bitcursor);
			
			bitcursor.get().set(ranac.get());
			
			
		}
		
		while(intcursor.hasNext()) {
			
			intcursor.fwd();
			
			intranac.setPosition(intcursor);
			
			intcursor.get().set(intranac.get());
			
			
		}
		if(distwater!=null) {
		Cursor< FloatType > distcursor = Views.iterable(distslice).localizingCursor();
		RandomAccess<FloatType> distranac = distwater.randomAccess();
		while(distcursor.hasNext()) {
			
			distcursor.fwd();
			
			distranac.setPosition(distcursor);
			
			distcursor.get().set(distranac.get());
			
		}
		}
		
	}

}

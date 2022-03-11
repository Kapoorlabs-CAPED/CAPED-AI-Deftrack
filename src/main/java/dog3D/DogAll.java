package dog3D;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.SwingWorker;

import distanceTransform.DistWatershed;
import dogGUI.CovistoDogPanel;
import ij.IJ;
import ij.gui.Roi;
import interactivePreprocessing.InteractiveMethods;
import interactivePreprocessing.InteractiveMethods.ValueChange;
import net.imglib2.Cursor;
import net.imglib2.Point;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.localextrema.RefinedPeak;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;
import preProcessing.GetLocalmaxminMT;
import preProcessing.GlobalThresholding;
import preProcessing.Utils;
import timeGUI.CovistoTimeselectPanel;
import utility.PreRoiobject;
import zGUI.CovistoZselectPanel;

public class DogAll extends SwingWorker<Void, Void> {

	final InteractiveMethods parent;

	public DogAll(final InteractiveMethods parent) {

		this.parent = parent;

	}

	
	
	public class ParallelCalls implements Callable<Void> {

		
		public final InteractiveMethods parent;
		public final int z;
		public final int t;
		public RandomAccessibleInterval<BitType> currentbitimg;
		public RandomAccessibleInterval<BitType> afterremovecurrentbitimg;
		public RandomAccessibleInterval<BitType> Dotafterremovecurrentbitimg;
		
		public ParallelCalls(InteractiveMethods parent,RandomAccessibleInterval<BitType> currentbitimg,RandomAccessibleInterval<BitType> afterremovecurrentbitimg,RandomAccessibleInterval<BitType> Dotafterremovecurrentbitimg, int z, int t) {
			
			
			this.parent = parent;
			this.currentbitimg = currentbitimg;
			this.afterremovecurrentbitimg = afterremovecurrentbitimg;
			this.Dotafterremovecurrentbitimg = Dotafterremovecurrentbitimg;
			this.z = z;
			this.t = t;
			
		}
		
		
		@Override
		public Void call() throws Exception {
		
			double percent = t +z;
			RandomAccessibleInterval<FloatType> CurrentView = utility.CovistoSlicer.getCurrentView(parent.originalimg, z,
					CovistoZselectPanel.thirdDimensionSize, t, CovistoTimeselectPanel.fourthDimensionSize);
			utility.CovsitoProgressBar.CovistoSetProgressBar(parent.jpb, 100 *(percent / (CovistoTimeselectPanel.fourthDimensionSize + CovistoZselectPanel.thirdDimensionSize + 1 )) ,"Computing");
			// UnsignedByteType image created here
			parent.updatePreview(ValueChange.THIRDDIMmouse);
			RandomAccessibleInterval<UnsignedByteType> newimg = utility.CovistoSlicer.PREcopytoByteImage(CurrentView);
		
	
			
			processParallelSlice(newimg, currentbitimg, afterremovecurrentbitimg,Dotafterremovecurrentbitimg,  z, t);
			
			return null;
		}
		
		
		
	}
	
	
	@Override
	protected Void doInBackground() throws Exception {

		parent.apply3D = true;

		RandomAccessibleInterval<BitType> bitimg = new ArrayImgFactory<BitType>().create(parent.originalimg,
				new BitType());
		
		RandomAccessibleInterval<BitType> bitdotimg = new ArrayImgFactory<BitType>().create(parent.originalimg,
				new BitType());
		
		RandomAccessibleInterval<BitType> afterremovebitimg = new ArrayImgFactory<BitType>().create(parent.originalimg,
				new BitType());

		List<Future<Void>> list = new ArrayList<Future<Void>>();
		int nThreads = Runtime.getRuntime().availableProcessors();
		final ExecutorService taskExecutor = Executors.newFixedThreadPool(nThreads);
		for (int t = CovistoTimeselectPanel.fourthDimensionsliderInit; t <= CovistoTimeselectPanel.fourthDimensionSize; ++t) {

			for (int z = CovistoZselectPanel.thirdDimensionsliderInit; z <= CovistoZselectPanel.thirdDimensionSize; ++z) {

				if(IJ.escapePressed()) {
				
					IJ.resetEscape();
					break;
					
				}
				
				
				CovistoZselectPanel.thirdDimension = z;
				CovistoTimeselectPanel.fourthDimension = t;

			
				RandomAccessibleInterval<BitType> currentbitimg = utility.CovistoSlicer.getCurrentView(bitimg, z,
						CovistoZselectPanel.thirdDimensionSize, t, CovistoTimeselectPanel.fourthDimensionSize);
				RandomAccessibleInterval<BitType> afterremovecurrentbitimg = utility.CovistoSlicer.getCurrentView(afterremovebitimg, z,
						CovistoZselectPanel.thirdDimensionSize, t, CovistoTimeselectPanel.fourthDimensionSize);
				RandomAccessibleInterval<BitType> Dotafterremovecurrentbitimg = utility.CovistoSlicer.getCurrentView(bitdotimg, z,
						CovistoZselectPanel.thirdDimensionSize, t, CovistoTimeselectPanel.fourthDimensionSize);
				
			
				ParallelCalls call = new ParallelCalls(parent, currentbitimg, afterremovecurrentbitimg, Dotafterremovecurrentbitimg, z, t);
				
				Future<Void> Futureresult = taskExecutor.submit(call);
				list.add(Futureresult);
			
			}

		}
		
		for (Future<Void> fut : list) {
			
			
			fut.get();
			
		}
		

		ImageJFunctions.show(bitimg).setTitle("Binary Image");
		ImageJFunctions.show(afterremovebitimg).setTitle("Merge Points Binary Image");
		ImageJFunctions.show(bitdotimg).setTitle("Merge Points Binary Image (Dot)");
		utility.CovsitoProgressBar.CovistoSetProgressBar(parent.jpb, "Done");
		
		Savefunction(parent.AllEvents);
		return null;
	}

	

	public void Savefunction(ConcurrentHashMap<Integer, ArrayList<double[]>> Mergedpoints) {
		
		
		
		File file = new File(parent.inputfile + "//" + parent.userfile.replace(".tif","") + ".txt");
		
		FileWriter fw;
		
		try {
			
			
			fw = new FileWriter(file);
			
			BufferedWriter bw = new BufferedWriter(fw);
			
			bw.write(" Time (px), X, Y  \n" );
			
			for(Entry<Integer, ArrayList<double[]>> entry : Mergedpoints.entrySet()) {
				
				
				ArrayList<double[]> timedlist = entry.getValue();
			for(int index = 0; index < timedlist.size(); ++index) {
				
				
				int time = entry.getKey();
				
				double X = timedlist.get(index)[0];
				
				double Y = timedlist.get(index)[1];
				
				
				bw.write(time + "," + parent.nf.format(X) + "," + parent.nf.format(Y) + "\n" );
				
				
			}
			
			
			}
			bw.close();
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}


	protected void processSlice(RandomAccessibleInterval<UnsignedByteType> slice,
			RandomAccessibleInterval<BitType> bitoutputslice, 	RandomAccessibleInterval<BitType> afterremovebitoutputslice,RandomAccessibleInterval<BitType> Dotafterremovecurrentbitimg, int z, int t) {

		
		parent.CurrentPreRoiobject = new ArrayList<PreRoiobject>();
		ComputeDoG<UnsignedByteType> ComputeDOG = new ComputeDoG<UnsignedByteType>(parent, slice, parent.jpb,
				parent.apply3D, z, t);
		ComputeDOG.execute();

		RandomAccessibleInterval<BitType> bitimg = ComputeDOG.getBinaryimg();
		RandomAccessibleInterval<BitType> afterremovebitimg = ComputeDOG.getafterremoveBinaryimg();
		RandomAccessibleInterval<BitType> Dotafterremovebitimg = ComputeDOG.getBinarydotimg();
		
		Cursor<BitType> bitcursor = Views.iterable(bitoutputslice).localizingCursor();

		Cursor<BitType> afterremovebitcursor = Views.iterable(afterremovebitoutputslice).localizingCursor();
		
		Cursor<BitType> Dotafterremovebitcursor = Views.iterable(Dotafterremovecurrentbitimg).localizingCursor();
		
		RandomAccess<BitType> ranac = bitimg.randomAccess();
		
		RandomAccess<BitType> afterremoveranac = afterremovebitimg.randomAccess();
		
		RandomAccess<BitType> Dotafterremoveranac = Dotafterremovebitimg.randomAccess();

		while (bitcursor.hasNext()) {

			bitcursor.fwd();

			ranac.setPosition(bitcursor);

			bitcursor.get().set(ranac.get());

		}
		
		while (afterremovebitcursor.hasNext()) {

			afterremovebitcursor.fwd();

			afterremoveranac.setPosition(afterremovebitcursor);

			afterremovebitcursor.get().set(afterremoveranac.get());

		}
		
		while (Dotafterremovebitcursor.hasNext()) {

			Dotafterremovebitcursor.fwd();

			Dotafterremoveranac.setPosition(Dotafterremovebitcursor);

			Dotafterremovebitcursor.get().set(Dotafterremoveranac.get());

		}
		

	}
	
	
	
	protected void processParallelSlice(RandomAccessibleInterval<UnsignedByteType> slice,
			RandomAccessibleInterval<BitType> bitoutputslice, 	RandomAccessibleInterval<BitType> afterremovebitoutputslice,RandomAccessibleInterval<BitType> Dotafterremovecurrentbitimg, int z, int t) {

		

		
		ComputeParallelDoG<UnsignedByteType> ComputeDOG = new ComputeParallelDoG<>(parent, slice, parent.jpb, parent.apply3D, z, t);
		ComputeDOG.execute();

		RandomAccessibleInterval<BitType> bitimg = ComputeDOG.getBinaryimg();
		RandomAccessibleInterval<BitType> afterremovebitimg = ComputeDOG.getafterremoveBinaryimg();
		RandomAccessibleInterval<BitType> Dotafterremovebitimg = ComputeDOG.getBinarydotimg();
		
		Cursor<BitType> bitcursor = Views.iterable(bitoutputslice).localizingCursor();

		Cursor<BitType> afterremovebitcursor = Views.iterable(afterremovebitoutputslice).localizingCursor();
		
		Cursor<BitType> Dotafterremovebitcursor = Views.iterable(Dotafterremovecurrentbitimg).localizingCursor();
		
		RandomAccess<BitType> ranac = bitimg.randomAccess();
		
		RandomAccess<BitType> afterremoveranac = afterremovebitimg.randomAccess();
		
		RandomAccess<BitType> Dotafterremoveranac = Dotafterremovebitimg.randomAccess();

		while (bitcursor.hasNext()) {

			bitcursor.fwd();

			ranac.setPosition(bitcursor);

			bitcursor.get().set(ranac.get());

		}
		
		while (afterremovebitcursor.hasNext()) {

			afterremovebitcursor.fwd();

			afterremoveranac.setPosition(afterremovebitcursor);

			afterremovebitcursor.get().set(afterremoveranac.get());

		}
		
		while (Dotafterremovebitcursor.hasNext()) {

			Dotafterremovebitcursor.fwd();

			Dotafterremoveranac.setPosition(Dotafterremovebitcursor);

			Dotafterremovebitcursor.get().set(Dotafterremoveranac.get());

		}
		

	}
	
	
	protected void processSliceremove(RandomAccessibleInterval<UnsignedByteType> slice,
			RandomAccessibleInterval<BitType> bitoutputslice, int z, int t) {

		ComputeDoG<UnsignedByteType> ComputeDOG = new ComputeDoG<UnsignedByteType>(parent, slice, parent.jpb,
				parent.apply3D, z, t);
		ComputeDOG.execute();

		RandomAccessibleInterval<BitType> bitimg = ComputeDOG.getafterremoveBinaryimg();
		Cursor<BitType> bitcursor = Views.iterable(bitoutputslice).localizingCursor();

		RandomAccess<BitType> ranac = bitimg.randomAccess();

		while (bitcursor.hasNext()) {

			bitcursor.fwd();

			ranac.setPosition(bitcursor);

			bitcursor.get().set(ranac.get());

		}

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

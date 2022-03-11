package dog3D;

import java.awt.Rectangle;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

import org.ojalgo.type.keyvalue.MapEntry;

import dogGUI.CovistoDogPanel;
import ij.IJ;
import ij.gui.OvalRoi;
import ij.gui.Roi;
import interactivePreprocessing.InteractiveMethods;
import net.imglib2.Cursor;
import net.imglib2.KDTree;
import net.imglib2.Point;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealPoint;
import net.imglib2.RealPointSampleList;
import net.imglib2.algorithm.componenttree.mser.MserTree;
import net.imglib2.algorithm.dog.DogDetection;
import net.imglib2.algorithm.labeling.AllConnectedComponents;
import net.imglib2.algorithm.labeling.Watershed;
import net.imglib2.algorithm.localextrema.RefinedPeak;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.labeling.DefaultROIStrategyFactory;
import net.imglib2.labeling.Labeling;
import net.imglib2.labeling.LabelingROIStrategy;
import net.imglib2.labeling.NativeImgLabeling;
import net.imglib2.neighborsearch.NearestNeighborSearchOnKDTree;
import net.imglib2.roi.labeling.ImgLabeling;
import net.imglib2.type.NativeType;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Pair;
import net.imglib2.util.Util;
import net.imglib2.view.Views;
import timeGUI.CovistoTimeselectPanel;
import utility.PreRoiobject;
import zGUI.CovistoZselectPanel;

public class ComputeDoG<T extends RealType<T> & NativeType<T>> {

	final InteractiveMethods parent;
	final JProgressBar jpb;
	public final RandomAccessibleInterval<T> source;

	public RandomAccessibleInterval<BitType> bitimg;
	public RandomAccessibleInterval<BitType> bitdotimg;
	public RandomAccessibleInterval<BitType> afterremovebitimg;
	
	public boolean apply3D;
	public int z;
	public int t;

	public ComputeDoG(final InteractiveMethods parent, final RandomAccessibleInterval<T> source,
			final JProgressBar jpb, boolean apply3D, int z, int t) {

		this.parent = parent;
		this.source = source;
		this.jpb = jpb;
		this.apply3D = apply3D;
		this.z = z;
		this.t = t;
		
		bitimg = new ArrayImgFactory<BitType>().create(source, new BitType());
		bitdotimg = new ArrayImgFactory<BitType>().create(source, new BitType());
		afterremovebitimg = new ArrayImgFactory<BitType>().create(source, new BitType());
		
	}

	public void execute() {

		final DogDetection.ExtremaType type;
		if (CovistoDogPanel.lookForMaxima)
			type = DogDetection.ExtremaType.MINIMA;
		else
			type = DogDetection.ExtremaType.MAXIMA;
		CovistoDogPanel.sigma2 = utility.ETrackScrollbarUtils.computeSigma2(CovistoDogPanel.sigma, parent.sensitivity);
		final DogDetection<T> newdog = new DogDetection<T>(Views.extendBorder(source),
				parent.interval, new double[] { 1, 1 }, CovistoDogPanel.sigma, CovistoDogPanel.sigma2, type, CovistoDogPanel.threshold, true);

		parent.peaks = newdog.getSubpixelPeaks();
		parent.Rois = utility.FinderUtils.getcurrentRois(parent.peaks, CovistoDogPanel.sigma, CovistoDogPanel.sigma2);
		parent.CurrentPreRoiobject = new ArrayList<PreRoiobject>();
		parent.AfterRemovedRois = new ArrayList<Roi>();
		
		
		
		parent.overlay.clear();
		
		
		for (int index = 0; index < parent.peaks.size(); ++index) {

			Roi or = parent.Rois.get(index);

			or.setStrokeColor(parent.colorDrawDog);
			parent.overlay.add(or);
		}
		
		for (Map.Entry<String, ArrayList<PreRoiobject>> entry : parent.ZTRois.entrySet()) {

			ArrayList<PreRoiobject> current = entry.getValue();
			for (PreRoiobject currentroi : current) {

				if (currentroi.fourthDimension == CovistoTimeselectPanel.fourthDimension && currentroi.thirdDimension == CovistoZselectPanel.thirdDimension) {

					currentroi.rois.setStrokeColor(parent.colorSnake);
					parent.overlay.add(currentroi.rois);
					
				}

			}
		}
		parent.imp.setOverlay(parent.overlay);
		parent.imp.updateAndDraw();
		ArrayList<Roi> Rois = parent.Rois;
		
		
		ArrayList<RefinedPeak<Point>> peaks = parent.peaks;
		
		ArrayList<double[]> points = new ArrayList<double[]>();
		ArrayList<double[]> mergepoints = new ArrayList<double[]>();
	
		
		for(RefinedPeak<Point> peak : peaks ) {
			
			double[] currentpoint = new double[] {(float)peak.getDoublePosition(0), (float)peak.getDoublePosition(1)};
			
			points.add(currentpoint);
		}
		
	
		utility.SortCoordinates.sortByXY(points);
		
		ArrayList<Boolean> allmerged = new ArrayList<Boolean>();

		System.out.println(points.size() + " " + "division before");
		if(points.size() > 0) {
	do{
		

		
	    mergepoints = new ArrayList<double[]>();
	    allmerged = new ArrayList<Boolean>();
		ArrayList<double[]> copylist = new ArrayList<double[]>(points);
		Iterator<double[]> listiter = points.iterator();
		
		
		
		
		while(listiter.hasNext()) {
	
			
			double[] currentpoint = listiter.next();
			
			
		
			
			copylist.remove(currentpoint);
			Pair<double[], Boolean> mergepointbol = utility.FinderUtils.mergeNearestRois(source, copylist, currentpoint, CovistoDogPanel.distthreshold);
			copylist.add(currentpoint);
			
			if(mergepointbol!=null) {
			if(!mergepointbol.getB()) {
			
				mergepoints.add(currentpoint);
			   boolean merged = true;
			   allmerged.add(merged);
			
			}
			else {
				
				double[] mean = new double[] {(currentpoint[0] + mergepointbol.getA()[0])/ 2, (currentpoint[1] + mergepointbol.getA()[1])/ 2 };
				mergepoints.add(mean);
		        listiter.remove();
		}
			
	
			}
			
			if(mergepointbol==null)
				mergepoints.add(currentpoint);
			
		}
		
		RemoveDuplicates(mergepoints);
		
		points = new ArrayList<double[]>();
		points.addAll(mergepoints);
		
	}while(allmerged.size() < points.size());
	
	//System.out.println(allmerged.size() + " " + "merged" + " " + points.size());
		for(double[] center:mergepoints) {
			
			int width = 1;
			int height = 1;
			int radius = (int)CovistoDogPanel.distthreshold;
			Roi Bigroi = new OvalRoi(Util.round(center[0] -(width + radius)/2), Util.round(center[1] - (height + radius)/2 ), Util.round(width + radius),
					Util.round(height + radius));
			parent.AfterRemovedRois.add(Bigroi);
			
		}

	parent.AllEvents.put(z, mergepoints);	
		
	
	if(z >= CovistoDogPanel.timeblock) {
		
		
		ArrayList<double[]> currentlist = parent.AllEvents.get(z);
		ArrayList<double[]> copylist = new ArrayList<double[]>(currentlist);
		for(int i = 1; i <=CovistoDogPanel.timeblock; ++i ) {
		ArrayList<double[]> previouslist = parent.AllEvents.get(z - i);
		
		
		
		copylist = RemoveTimeDuplicates(currentlist, previouslist, copylist);
		
		
		parent.AllEvents.replace(z, copylist);
		
		}
	}
		
		
	else if (z<CovistoDogPanel.timeblock && z >1) {
			

			ArrayList<double[]> currentlist = parent.AllEvents.get(z);
			ArrayList<double[]>	copylist = new ArrayList<double[]>(currentlist);
			for(int i = z - 1; i >=0; --i ) {
			ArrayList<double[]> previouslist = parent.AllEvents.get(z - i);
			
			
			
			copylist = RemoveTimeDuplicates(currentlist, previouslist, copylist);
			
			
			parent.AllEvents.replace(z, copylist);
			
			}
		}
	
		
		for (Roi currentroi : parent.Rois) {

			
			
			
			final double[] geocenter = currentroi.getContourCentroid();
			final Pair<Double, Integer> Intensityandpixels = PreRoiobject.getIntensity(currentroi, source);
			final double intensity = Intensityandpixels.getA();
			final double numberofpixels = Intensityandpixels.getB();
			final double averageintensity = intensity / numberofpixels;
			PreRoiobject currentobject = new PreRoiobject(currentroi,
					new double[] { geocenter[0], geocenter[1], CovistoZselectPanel.thirdDimension }, numberofpixels, intensity,
					averageintensity, CovistoZselectPanel.thirdDimension, CovistoTimeselectPanel.fourthDimension);
			parent.CurrentPreRoiobject.add(currentobject);
		}

		String uniqueID = Integer.toString(z) + Integer.toString(t);
		parent.ZTRois.put(uniqueID, parent.CurrentPreRoiobject);
		
		common3D.BinaryCreation.CreateBinaryRoi(parent, source, bitimg,parent.Rois, z, t);
		common3D.BinaryCreation.CreateBinary(parent, source, afterremovebitimg, z, t);
		common3D.BinaryCreation.CreateBinaryDots(parent, source, bitdotimg, z, t);
		System.out.println(" Division after " + parent.AllEvents.get(z).size());
	}

	}
	public ArrayList<double[]> RemoveTimeDuplicates(ArrayList<double[]> Currentpoints, ArrayList<double[]> Previouspoints, ArrayList<double[]>copylist) {
		
		

		ArrayList<Boolean> allmerged = new ArrayList<Boolean>();
	
		if(Previouspoints!=null) {
		for (int i = 0; i < Previouspoints.size(); ++i) {
			
			double[] previouspoint = Previouspoints.get(i);
			allmerged = new ArrayList<Boolean>();
			
			do {
				  
					Pair<double[], Boolean> mergepointbol = utility.FinderUtils.mergeNearestRois(source, copylist, previouspoint, CovistoDogPanel.timethreshold);
					
					
					if(mergepointbol!=null) {
						if(!mergepointbol.getB()) {
						
						   boolean merged = true;
						   allmerged.add(merged);
						
						}
						else {
							
							copylist.remove(mergepointbol.getA());
					}
					
				
			}
					
			
			
		}while(allmerged.size() < Previouspoints.size());
		
		
		
		}
	
		RemoveDuplicates(copylist);
		}
		return copylist;
		
		
	}
	
	public void RemoveDuplicates(ArrayList<double[]> points) {
		
		int j = 0;

		for (int i = 0; i < points.size(); ++i) {

			j = i + 1;
			while (j < points.size()) {

				if (points.get(i)[0] == points.get(j)[0] && points.get(i)[1] == points.get(j)[1] ) {

					points.remove(j);

				}

				else {
					++j;
				}

			}

		}

	}
		
		
	
	public RandomAccessibleInterval<BitType> getBinaryimg() {

		return bitimg;
	}

	public RandomAccessibleInterval<BitType> getafterremoveBinaryimg() {

		return afterremovebitimg;
	}

	public RandomAccessibleInterval<BitType> getBinarydotimg() {
		
		return bitdotimg;
		
	}

}

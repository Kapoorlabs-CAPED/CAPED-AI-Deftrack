package pluginTools;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingWorker;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import com.google.common.math.Quantiles.Scale;

import batchMode.BatchKymoSave;
import costMatrix.PixelratiowDistCostFunction;
import curvatureFinder.LineProfileCircle;
import curvatureUtils.DisplaySelected;
import drawUtils.DrawFunction;
import ellipsoidDetector.Distance;
import ellipsoidDetector.Intersectionobject;
import ellipsoidDetector.KymoSaveobject;
import hashMapSorter.SortTimeorZ;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.Line;
import ij.gui.Overlay;
import ij.io.FileSaver;
import ij.measure.Calibration;
import imageAxis.AxisRendering;
import kalmanForSegments.Segmentobject;
import kalmanForSegments.TrackSegmentModel;
import kalmanTracker.ETrackCostFunction;
import kalmanTracker.IntersectionobjectCollection;
import kalmanTracker.KFsearch;
import kalmanTracker.NearestNeighbourSearch;
import kalmanTracker.NearestNeighbourSearch2D;
import kalmanTracker.TrackModel;
import listeners.DisplayVisualListener;
import net.imglib2.Cursor;
import net.imglib2.KDTree;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.RealPoint;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.realtransform.Scale2D;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;
import net.imglib2.view.Views;
import pluginTools.InteractiveSimpleEllipseFit.ValueChange;
import track.TrackingFunctions;
import utility.CreateTable;
import utility.Curvatureobject;
import utility.FlagNode;
import utility.Listordereing;
import utility.NNFlagsearchKDtree;
import utility.Roiobject;
import utility.ThreeDRoiobject;

public class ComputeCurvature extends SwingWorker<Void, Void> {

	final InteractiveSimpleEllipseFit parent;
	final JProgressBar jpb;
	final boolean batchmode;
	final File savefile;

	static int extradimension = 50;

	public ComputeCurvature(final InteractiveSimpleEllipseFit parent, final JProgressBar jpb, final boolean batchmode,
			final File savefile) {

		this.parent = parent;

		this.jpb = jpb;

		this.batchmode = batchmode;

		this.savefile = savefile;
	}

	@Override
	protected Void doInBackground() throws Exception {

		HashMap<String, Integer> map = SortTimeorZ.sortByValues(parent.Accountedframes);
		parent.Accountedframes = map;

		HashMap<String, Integer> mapZ = SortTimeorZ.sortByValues(parent.AccountedZ);
		parent.AccountedZ = mapZ;

		parent.inputField.setEnabled(false);
		parent.inputtrackField.setEnabled(false);
		parent.Savebutton.setEnabled(false);
		parent.SaveAllbutton.setEnabled(false);
		parent.ChooseDirectory.setEnabled(false);
		parent.Combomode.setEnabled(false);
		parent.regioninteriorfield.setEnabled(false);
		parent.CurrentCurvaturebutton.setEnabled(false);
		parent.Curvaturebutton.setEnabled(false);
		parent.timeslider.setEnabled(false);
		parent.inputFieldT.setEnabled(false);
		parent.distancemode.setEnabled(false);
		parent.Pixelcelltrackcirclemode.setEnabled(false);
		//parent.resolutionField.setEnabled(false);
		parent.interiorfield.setEnabled(false);
		parent.Displaybutton.setEnabled(false);
		parent.minInlierslider.setEnabled(false);
		parent.minInlierField.setEnabled(false);
		//parent.radiusField.setEnabled(false);
		EllipseTrack newtrack = new EllipseTrack(parent, jpb);
		newtrack.ComputeCurvature();

		return null;

	}



	public static RandomAccessibleInterval<FloatType> MakeDistanceFan(InteractiveSimpleEllipseFit parent,
			HashMap<String, ArrayList<Intersectionobject>> sortedMappair, String TrackID, boolean show) {

		Iterator<Map.Entry<String, Integer>> itZ = parent.AccountedZ.entrySet().iterator();

		RandomAccessibleInterval<FloatType> Blank = new ArrayImgFactory<FloatType>().create(parent.originalimg,
				new FloatType());

		while (itZ.hasNext()) {

			Map.Entry<String, Integer> entry = itZ.next();
			int time = entry.getValue();
			String timeID = entry.getKey();

			RandomAccessibleInterval<FloatType> CurrentBlank = utility.Slicer.getCurrentView(Blank, time,
					parent.thirdDimensionSize, parent.fourthDimension, parent.fourthDimensionSize);

			ArrayList<Intersectionobject> currentlist = sortedMappair.get(TrackID + timeID);

			if (currentlist != null && currentlist.size() > 0) {
				double[] centerpoint = currentlist.get(0).Intersectionpoint;
				for (Intersectionobject currentobject : currentlist) {

					ArrayList<double[]> sortedlinelist = currentobject.linelist;

					double maxdist = GetMaxdist(sortedlinelist, TrackID, parent.combomethod);

					for (int i = 0; i < sortedlinelist.size(); ++i) {
						double distvalue = sortedlinelist.get(i)[2] / maxdist;
						if (parent.combomethod)
							distvalue = sortedlinelist.get(i)[6] / maxdist;

						DrawFunction.DrawGeomBresnLines(CurrentBlank, new double[] { centerpoint[0], centerpoint[1] },
								new double[] { sortedlinelist.get(i)[0], sortedlinelist.get(i)[1] }, distvalue);

					}

				}
			}

		}
		if(show)
		AxisRendering.Reshape(Blank, "Distance-Fan display");

		return Blank;

	}

	public static double GetMaxdist(ArrayList<double[]> linelist, String TrackID, boolean combomethod) {

		double maxdist = Double.MIN_VALUE;

		for (int i = 0; i < linelist.size(); ++i) {

			double distance = linelist.get(i)[2];
			if (combomethod)
				distance = linelist.get(i)[6];
			if (linelist.get(i)[2] >= maxdist)
				maxdist = distance;

		}
		return maxdist;
	}

	public static void MakeInterKymo(InteractiveSimpleEllipseFit parent,
			HashMap<String, ArrayList<Intersectionobject>> sortedMappair, long[] size, String TrackID) {

		RandomAccessibleInterval<FloatType> CurvatureKymo = new ArrayImgFactory<FloatType>().create(size,
				new FloatType());
		RandomAccessibleInterval<FloatType> IntensityAKymo = new ArrayImgFactory<FloatType>().create(size,
				new FloatType());
		RandomAccessibleInterval<FloatType> IntensityBKymo = new ArrayImgFactory<FloatType>().create(size,
				new FloatType());

		RandomAccessibleInterval<FloatType> DistCurvatureKymo = new ArrayImgFactory<FloatType>().create(size,
				new FloatType());

		RandomAccess<FloatType> ranacimageA = IntensityAKymo.randomAccess();

		RandomAccess<FloatType> ranacimageB = IntensityBKymo.randomAccess();
		Iterator<Map.Entry<String, Integer>> itZ = parent.AccountedZ.entrySet().iterator();

		RandomAccess<FloatType> ranac = CurvatureKymo.randomAccess();

		RandomAccess<FloatType> Distranac = DistCurvatureKymo.randomAccess();

		while (itZ.hasNext()) {

			Map.Entry<String, Integer> entry = itZ.next();

			int time = entry.getValue();
			String timeID = entry.getKey();

			ArrayList<Intersectionobject> currentlist = sortedMappair.get(TrackID + timeID);

			ranac.setPosition(time - 1, 0);
			Distranac.setPosition(time - 1, 0);
			ranacimageA.setPosition(time - 1, 0);
			ranacimageB.setPosition(time - 1, 0);
			if (currentlist != null) {
				for (Intersectionobject currentobject : currentlist) {

					int count = 0;

					ArrayList<double[]> sortedlinelist = currentobject.linelist;

					for (int i = 0; i < sortedlinelist.size(); ++i) {

						ranac.setPosition(count, 1);
						ranac.get().set((float) sortedlinelist.get(i)[2]);

						Distranac.setPosition(count, 1);
						Distranac.get().set((float) sortedlinelist.get(i)[6]);

						ranacimageA.setPosition(count, 1);
						ranacimageA.get().setReal(sortedlinelist.get(i)[3]);

						ranacimageB.setPosition(count, 1);
						ranacimageB.get().setReal(sortedlinelist.get(i)[4]);

						count++;

					}

				}
			}

		}
		double[] calibration = new double[] { parent.timecal, parent.calibration };

		String CurvatureTitle = "Curvature Kymo for TrackID: ";
		String DistCurvatureTitle = "";
		Calibration cal = new Calibration();
		cal.setFunction(Calibration.STRAIGHT_LINE, calibration, " ");
		if (parent.pixelcelltrackcirclefits) {
			CurvatureTitle = "CircleFits" + CurvatureTitle;
		}
		if (parent.distancemethod) {
			CurvatureTitle = "DistanceMethod" + CurvatureTitle;
		}

		if (parent.combomethod) {
			CurvatureTitle = "Circle Fits Curvature Kymo for TrackID: ";
			DistCurvatureTitle = "Distance Fits Curvature Kymo for TrackID: ";
			ImagePlus DistCurveimp = ImageJFunctions.show(DistCurvatureKymo);
			DistCurveimp.setTitle(DistCurvatureTitle + TrackID);
			DistCurveimp.setCalibration(cal);

		}

		ImagePlus Curveimp = ImageJFunctions.show(CurvatureKymo);
		Curveimp.setTitle(CurvatureTitle + TrackID);
		Curveimp.setCalibration(cal);

		ImagePlus IntensityAimp = ImageJFunctions.show(IntensityAKymo);
		IntensityAimp.setTitle("Intensity ChA Kymo for TrackID: " + TrackID);
		IntensityAimp.setCalibration(cal);

		if (parent.twochannel) {
			ImagePlus IntensityBimp = ImageJFunctions.show(IntensityBKymo);
			IntensityBimp.setTitle("Intensity ChB for TrackID: " + TrackID);
			IntensityBimp.setCalibration(cal);
			IntensityBimp.updateAndRepaintWindow();
		}
		Curveimp.updateAndRepaintWindow();
		IntensityAimp.updateAndRepaintWindow();

		KymoSaveobject Kymos = new KymoSaveobject(CurvatureKymo, IntensityAKymo, IntensityBKymo);
		if (parent.combomethod)
			Kymos = new KymoSaveobject(CurvatureKymo, DistCurvatureKymo, IntensityAKymo, IntensityBKymo);
		parent.KymoFileobject.put(TrackID, Kymos);

		int hyperslicedimension = 1;
		ArrayList<Pair<Integer, Double>> poslist = new ArrayList<Pair<Integer, Double>>();
		for (long pos = 0; pos < CurvatureKymo.dimension(hyperslicedimension) - 1; ++pos) {

			RandomAccessibleInterval<FloatType> CurveView = Views.hyperSlice(CurvatureKymo, hyperslicedimension, pos);

			RandomAccess<FloatType> Cranac = CurveView.randomAccess();

			Iterator<Map.Entry<String, Integer>> itZSec = parent.AccountedZ.entrySet().iterator();

			double rms = 0;
			int count = 0;
			while (itZSec.hasNext()) {

				Map.Entry<String, Integer> entry = itZSec.next();

				int time = entry.getValue();

				count++;
				Cranac.setPosition(count, 0);

				rms += Cranac.get().get() * Cranac.get().get();

			}
			poslist.add(new ValuePair<Integer, Double>((int) pos, Math.sqrt(rms / parent.AccountedZ.size())));

		}
		parent.StripList.put(TrackID, poslist);
		parent.updatePreview(ValueChange.THIRDDIMmouse);
	}

	public static void SaveLineScanKymo(InteractiveSimpleEllipseFit parent,
			HashMap<String, ArrayList<Intersectionobject>> sortedMappair, long[] size, String TrackID) {

		RandomAccessibleInterval<FloatType> IntensityAKymo = new ArrayImgFactory<FloatType>().create(size,
				new FloatType());
		RandomAccessibleInterval<FloatType> IntensityBKymo = new ArrayImgFactory<FloatType>().create(size,
				new FloatType());
		if (parent.insidedistance > 0) {
			if (parent.KymoLineobject.get(TrackID) != null) {

				IntensityAKymo = parent.KymoLineobject.get(TrackID).LineScanAKymo;

				IntensityBKymo = parent.KymoLineobject.get(TrackID).LineScanBKymo;

			}

			else {

				RandomAccess<FloatType> ranacimageA = IntensityAKymo.randomAccess();

				RandomAccess<FloatType> ranacimageB = IntensityBKymo.randomAccess();
				Iterator<Map.Entry<String, Integer>> itZ = parent.AccountedZ.entrySet().iterator();

				while (itZ.hasNext()) {

					Map.Entry<String, Integer> entry = itZ.next();

					int time = entry.getValue();
					String timeID = entry.getKey();

					ArrayList<Intersectionobject> currentlist = sortedMappair.get(TrackID + timeID);

					ranacimageA.setPosition(time - 1, 0);
					ranacimageB.setPosition(time - 1, 0);
					if (currentlist != null) {
						for (Intersectionobject currentobject : currentlist) {

							int count = 0;

							// System.out.println(currentobject.LineScanIntensity.size() + " Final size" +
							// time);
							ConcurrentHashMap<Integer, ArrayList<LineProfileCircle>> currentprofile = currentobject.LineScanIntensity;

							for (Map.Entry<Integer, ArrayList<LineProfileCircle>> currentsegmentprofile : currentprofile
									.entrySet()) {

								int key = currentsegmentprofile.getKey();

								ArrayList<LineProfileCircle> lineprofile = currentsegmentprofile.getValue();

								for (int i = 0; i < lineprofile.size() + extradimension; ++i) {

									if (count >= size[1])
										break;

									ranacimageA.setPosition(count, 1);
									ranacimageB.setPosition(count, 1);

									if (i < lineprofile.size()) {
										ranacimageA.get().set((float) lineprofile.get(i).intensity);
										ranacimageB.get().set((float) lineprofile.get(i).secintensity);

									}

									else {
										ranacimageA.get()
												.set((float) lineprofile.get(lineprofile.size() - 1).intensity);
										ranacimageB.get()
												.set((float) lineprofile.get(lineprofile.size() - 1).secintensity);

									}

									count++;
								}

							}

						}

					}

				}
			}

			double[] calibration = new double[] { parent.timecal, parent.calibration };
			Calibration cal = new Calibration();
			cal.setFunction(Calibration.STRAIGHT_LINE, calibration, " ");

			KymoSaveobject Kymos = new KymoSaveobject(IntensityAKymo, IntensityBKymo);
			parent.KymoLineobject.put(TrackID, Kymos);

			ImagePlus IntensityAimp = ImageJFunctions.wrapFloat(IntensityAKymo,
					"LineScanCHA Kymo for TrackID: " + TrackID);

			FileSaver fsB = new FileSaver(IntensityAimp);
			if (parent.clockimp.isVisible()) {
				FileSaver fsLine = new FileSaver(parent.clockimp);
				fsLine.saveAsTiff(
						parent.saveFile + "//" + "ClockLineScan_" + parent.inputstring.replaceFirst("[.][^.]+$", "")
								+ "TrackID" + Integer.parseInt(TrackID) + ".tif");
			}
			fsB.saveAsTiff(parent.saveFile + "//" + "Ch1LineScan_" + parent.inputstring.replaceFirst("[.][^.]+$", "")
					+ "TrackID" + Integer.parseInt(TrackID) + ".tif");

			if (parent.twochannel) {
				ImagePlus IntensityBimp = ImageJFunctions.wrapFloat(IntensityBKymo,
						"Intensity ChB Kymo for TrackID: " + TrackID);

				FileSaver fsBB = new FileSaver(IntensityBimp);

				fsBB.saveAsTiff(
						parent.saveFile + "//" + "Ch2LineScan_" + parent.inputstring.replaceFirst("[.][^.]+$", "")
								+ "TrackID" + Integer.parseInt(TrackID) + ".tif");

			}
		}

	}

	public static void SaveInterKymo(InteractiveSimpleEllipseFit parent,
			HashMap<String, ArrayList<Intersectionobject>> sortedMappair, long[] size, String TrackID) {

		DisplayVisualListener display = new DisplayVisualListener(parent, false);
		Pair<RandomAccessibleInterval<FloatType>, RandomAccessibleInterval<FloatType>> Blankprob = display.run();

		String Title = "Distance-Fan display";
		ImagePlus DistFanimp = ImageJFunctions.wrapFloat(Blankprob.getA(), Title + TrackID);

		FileSaver DistfsF = new FileSaver(DistFanimp);

		DistfsF.saveAsTiff(parent.saveFile + "//" + Title + parent.inputstring.replaceFirst("[.][^.]+$", "") + "TrackID"
				+ Integer.parseInt(TrackID) + ".tif");

		String CurvTitle = "ColorCoded-Curvature display";
		
		if (parent.pixelcelltrackcirclefits || parent.combomethod) {
			CurvTitle = "CircleFits" + CurvTitle;
		}
		if (parent.distancemethod) {
			CurvTitle = "DistanceMethod" + CurvTitle;
		}
		
		
		ImagePlus ColorCodedCurv = ImageJFunctions.wrapFloat(Blankprob.getB(), Title + TrackID);

		FileSaver DistfsColor = new FileSaver(ColorCodedCurv);

		DistfsColor.saveAsTiff(parent.saveFile + "//" + CurvTitle + parent.inputstring.replaceFirst("[.][^.]+$", "")
				+ "TrackID" + Integer.parseInt(TrackID) + ".tif");

		RandomAccessibleInterval<FloatType> CurvatureKymo = new ArrayImgFactory<FloatType>().create(size,
				new FloatType());
		RandomAccessibleInterval<FloatType> IntensityAKymo = new ArrayImgFactory<FloatType>().create(size,
				new FloatType());
		RandomAccessibleInterval<FloatType> IntensityBKymo = new ArrayImgFactory<FloatType>().create(size,
				new FloatType());
		RandomAccessibleInterval<FloatType> DistCurvatureKymo = new ArrayImgFactory<FloatType>().create(size,
				new FloatType());

		if (parent.KymoFileobject.get(TrackID) != null) {

			CurvatureKymo = parent.KymoFileobject.get(TrackID).CurvatureKymo;

			IntensityAKymo = parent.KymoFileobject.get(TrackID).IntensityAKymo;

			IntensityBKymo = parent.KymoFileobject.get(TrackID).IntensityBKymo;

			DistCurvatureKymo = parent.KymoFileobject.get(TrackID).DistCurvatureKymo;

		} else {

			RandomAccess<FloatType> ranacimageA = IntensityAKymo.randomAccess();

			RandomAccess<FloatType> ranacimageB = IntensityBKymo.randomAccess();
			Iterator<Map.Entry<String, Integer>> itZ = parent.AccountedZ.entrySet().iterator();

			RandomAccess<FloatType> ranac = CurvatureKymo.randomAccess();

			RandomAccess<FloatType> Distranac = DistCurvatureKymo.randomAccess();

			while (itZ.hasNext()) {

				Map.Entry<String, Integer> entry = itZ.next();

				int time = entry.getValue();
				String timeID = entry.getKey();

				ArrayList<Intersectionobject> currentlist = sortedMappair.get(TrackID + timeID);

				ranac.setPosition(time - 1, 0);
				Distranac.setPosition(time - 1, 0);
				ranacimageA.setPosition(time - 1, 0);
				ranacimageB.setPosition(time - 1, 0);
				if (currentlist != null) {
					for (Intersectionobject currentobject : currentlist) {

						int count = 0;

						ArrayList<double[]> sortedlinelist = currentobject.linelist;

						for (int i = 0; i < sortedlinelist.size(); ++i) {

							ranac.setPosition(count, 1);
							ranac.get().set((float) sortedlinelist.get(i)[2]);

							Distranac.setPosition(count, 1);
							Distranac.get().set((float) sortedlinelist.get(i)[6]);

							ranacimageA.setPosition(count, 1);
							ranacimageA.get().setReal(sortedlinelist.get(i)[3]);

							ranacimageB.setPosition(count, 1);
							ranacimageB.get().setReal(sortedlinelist.get(i)[4]);

							count++;

						}

					}
				}

			}

		}
		double[] calibration = new double[] { parent.timecal, parent.calibration };
		Calibration cal = new Calibration();
		cal.setFunction(Calibration.STRAIGHT_LINE, calibration, " ");

		String SaveTitle = "Curvature_";
		String CurvatureTitle = "Curvature ChA Kymo ";
		if (parent.pixelcelltrackcirclefits) {
			SaveTitle = "CircleFits" + SaveTitle;
			CurvatureTitle = "CircleFits" + CurvatureTitle;
		}
		if (parent.distancemethod) {
			SaveTitle = "DistanceMethod" + SaveTitle;
			CurvatureTitle = "DistanceMethod" + CurvatureTitle;
		}
		String DistCurvatureTitle = "";
		String DistSaveTitle = " ";
		if (parent.combomethod) {

			CurvatureTitle = "Curvature ChA Kymo";
			DistCurvatureTitle = "Dist ChA Kymo";
			SaveTitle = "Curvature_";
			DistSaveTitle = "Distance_";
			ImagePlus DistCurveimp = ImageJFunctions.wrapFloat(DistCurvatureKymo, DistCurvatureTitle + TrackID);

			FileSaver DistfsC = new FileSaver(DistCurveimp);

			DistfsC.saveAsTiff(parent.saveFile + "//" + DistSaveTitle + parent.inputstring.replaceFirst("[.][^.]+$", "")
					+ "TrackID" + Integer.parseInt(TrackID) + ".tif");

		}

		ImagePlus Curveimp = ImageJFunctions.wrapFloat(CurvatureKymo, CurvatureTitle + TrackID);

		FileSaver fsC = new FileSaver(Curveimp);

		fsC.saveAsTiff(parent.saveFile + "//" + SaveTitle + parent.inputstring.replaceFirst("[.][^.]+$", "") + "TrackID"
				+ Integer.parseInt(TrackID) + ".tif");

		ImagePlus IntensityAimp = ImageJFunctions.wrapFloat(IntensityAKymo,
				"Intensity ChA Kymo for TrackID: " + TrackID);

		FileSaver fsB = new FileSaver(IntensityAimp);

		fsB.saveAsTiff(parent.saveFile + "//" + "Ch1Intensity_" + parent.inputstring.replaceFirst("[.][^.]+$", "")
				+ "TrackID" + Integer.parseInt(TrackID) + ".tif");

		if (parent.twochannel) {
			ImagePlus IntensityBimp = ImageJFunctions.wrapFloat(IntensityBKymo,
					"Intensity ChB Kymo for TrackID: " + TrackID);

			FileSaver fsBB = new FileSaver(IntensityBimp);

			fsBB.saveAsTiff(parent.saveFile + "//" + "Ch2Intensity_" + parent.inputstring.replaceFirst("[.][^.]+$", "")
					+ "TrackID" + Integer.parseInt(TrackID) + ".tif");

		}
		if (parent.KymoFileobject.get(TrackID) == null) {
			KymoSaveobject Kymos = new KymoSaveobject(CurvatureKymo, IntensityAKymo, IntensityBKymo);
			if (parent.combomethod)
				Kymos = new KymoSaveobject(CurvatureKymo, DistCurvatureKymo, IntensityAKymo, IntensityBKymo);

			parent.KymoFileobject.put(TrackID, Kymos);

			int hyperslicedimension = 1;
			ArrayList<Pair<Integer, Double>> poslist = new ArrayList<Pair<Integer, Double>>();
			for (long pos = 0; pos < CurvatureKymo.dimension(hyperslicedimension) - 1; ++pos) {

				RandomAccessibleInterval<FloatType> CurveView = Views.hyperSlice(CurvatureKymo, hyperslicedimension,
						pos);

				RandomAccess<FloatType> Cranac = CurveView.randomAccess();

				Iterator<Map.Entry<String, Integer>> itZSec = parent.AccountedZ.entrySet().iterator();

				double rms = 0;
				while (itZSec.hasNext()) {

					Map.Entry<String, Integer> entry = itZSec.next();

					int time = entry.getValue();

					Cranac.setPosition(time - 1, 0);

					rms += Cranac.get().get() * Cranac.get().get();

				}
				poslist.add(new ValuePair<Integer, Double>((int) pos, Math.sqrt(rms / parent.AccountedZ.size())));

			}
			parent.StripList.put(TrackID, poslist);
		}
	}
	
	public static void CreateInterKymo(InteractiveSimpleEllipseFit parent,
			HashMap<String, ArrayList<Intersectionobject>> sortedMappair, long[] size, String TrackID) {

	

		String Title = "Distance-Fan display";

	

		String CurvTitle = "ColorCoded-Curvature display";
		
		if (parent.pixelcelltrackcirclefits || parent.combomethod) {
			CurvTitle = "CircleFits" + CurvTitle;
		}
		if (parent.distancemethod) {
			CurvTitle = "DistanceMethod" + CurvTitle;
		}
		
		


		RandomAccessibleInterval<FloatType> CurvatureKymo = new ArrayImgFactory<FloatType>().create(size,
				new FloatType());
		RandomAccessibleInterval<FloatType> IntensityAKymo = new ArrayImgFactory<FloatType>().create(size,
				new FloatType());
		RandomAccessibleInterval<FloatType> IntensityBKymo = new ArrayImgFactory<FloatType>().create(size,
				new FloatType());
		RandomAccessibleInterval<FloatType> DistCurvatureKymo = new ArrayImgFactory<FloatType>().create(size,
				new FloatType());

		if (parent.KymoFileobject.get(TrackID) != null) {

			CurvatureKymo = parent.KymoFileobject.get(TrackID).CurvatureKymo;

			IntensityAKymo = parent.KymoFileobject.get(TrackID).IntensityAKymo;

			IntensityBKymo = parent.KymoFileobject.get(TrackID).IntensityBKymo;

			DistCurvatureKymo = parent.KymoFileobject.get(TrackID).DistCurvatureKymo;

		} else {

			RandomAccess<FloatType> ranacimageA = IntensityAKymo.randomAccess();

			RandomAccess<FloatType> ranacimageB = IntensityBKymo.randomAccess();
			Iterator<Map.Entry<String, Integer>> itZ = parent.AccountedZ.entrySet().iterator();

			RandomAccess<FloatType> ranac = CurvatureKymo.randomAccess();

			RandomAccess<FloatType> Distranac = DistCurvatureKymo.randomAccess();

			while (itZ.hasNext()) {

				Map.Entry<String, Integer> entry = itZ.next();

				int time = entry.getValue();
				String timeID = entry.getKey();

				ArrayList<Intersectionobject> currentlist = sortedMappair.get(TrackID + timeID);

				ranac.setPosition(time - 1, 0);
				Distranac.setPosition(time - 1, 0);
				ranacimageA.setPosition(time - 1, 0);
				ranacimageB.setPosition(time - 1, 0);
				if (currentlist != null) {
					for (Intersectionobject currentobject : currentlist) {

						int count = 0;

						ArrayList<double[]> sortedlinelist = currentobject.linelist;

						for (int i = 0; i < sortedlinelist.size(); ++i) {

							ranac.setPosition(count, 1);
							ranac.get().set((float) sortedlinelist.get(i)[2]);

							Distranac.setPosition(count, 1);
							Distranac.get().set((float) sortedlinelist.get(i)[6]);

							ranacimageA.setPosition(count, 1);
							ranacimageA.get().setReal(sortedlinelist.get(i)[3]);

							ranacimageB.setPosition(count, 1);
							ranacimageB.get().setReal(sortedlinelist.get(i)[4]);

							count++;

						}

					}
				}

			}

		}
		double[] calibration = new double[] { parent.timecal, parent.calibration };
		Calibration cal = new Calibration();
		cal.setFunction(Calibration.STRAIGHT_LINE, calibration, " ");

		String SaveTitle = "Curvature_";
		String CurvatureTitle = "Curvature ChA Kymo ";
		if (parent.pixelcelltrackcirclefits) {
			SaveTitle = "CircleFits" + SaveTitle;
			CurvatureTitle = "CircleFits" + CurvatureTitle;
		}
		if (parent.distancemethod) {
			SaveTitle = "DistanceMethod" + SaveTitle;
			CurvatureTitle = "DistanceMethod" + CurvatureTitle;
		}
		if (parent.combomethod) {

			CurvatureTitle = "Curvature ChA Kymo";
			SaveTitle = "Curvature_";
			

		}


			KymoSaveobject Kymos = new KymoSaveobject(CurvatureKymo, IntensityAKymo, IntensityBKymo);
			if (parent.combomethod)
				Kymos = new KymoSaveobject(CurvatureKymo, DistCurvatureKymo, IntensityAKymo, IntensityBKymo);

			parent.KymoFileobject.put(TrackID, Kymos);

			int hyperslicedimension = 1;
			ArrayList<Pair<Integer, Double>> poslist = new ArrayList<Pair<Integer, Double>>();
			for (long pos = 0; pos < CurvatureKymo.dimension(hyperslicedimension) - 1; ++pos) {

				RandomAccessibleInterval<FloatType> CurveView = Views.hyperSlice(CurvatureKymo, hyperslicedimension,
						pos);

				RandomAccess<FloatType> Cranac = CurveView.randomAccess();

				Iterator<Map.Entry<String, Integer>> itZSec = parent.AccountedZ.entrySet().iterator();

				double rms = 0;
				while (itZSec.hasNext()) {

					Map.Entry<String, Integer> entry = itZSec.next();

					int time = entry.getValue();

					Cranac.setPosition(time - 1, 0);

					rms += Cranac.get().get() * Cranac.get().get();

				}
				poslist.add(new ValuePair<Integer, Double>((int) pos, Math.sqrt(rms / parent.AccountedZ.size())));

			}
			parent.StripList.put(TrackID, poslist);
		
	}
	

	@Override
	protected void done() {

		parent.CurrentCurvaturebutton.setEnabled(true);
		//parent.radiusField.setEnabled(true);
		parent.Curvaturebutton.setEnabled(true);
		parent.timeslider.setEnabled(true);
		parent.inputFieldT.setEnabled(true);
		parent.distancemode.setEnabled(true);
		parent.Pixelcelltrackcirclemode.setEnabled(true);
		//parent.resolutionField.setEnabled(true);
		parent.interiorfield.setEnabled(true);
		parent.Displaybutton.setEnabled(true);
		parent.minInlierslider.setEnabled(true);
		parent.minInlierField.setEnabled(true);
		parent.regioninteriorfield.setEnabled(true);
		parent.Combomode.setEnabled(true);
		parent.jpb.setIndeterminate(false);
		parent.Cardframe.validate();

		parent.prestack = new ImageStack((int) parent.originalimg.dimension(0), (int) parent.originalimg.dimension(1),
				java.awt.image.ColorModel.getRGBdefault());

		parent.resultDraw.clear();
		parent.Tracklist.clear();
		parent.denseTracklist.clear();

		parent.SegmentTracklist.clear();
		parent.table.removeAll();

		

		TrackingFunctions track = new TrackingFunctions(parent);
		if (parent.ndims > 3) {

			Iterator<Map.Entry<String, Integer>> itZ = parent.AccountedZ.entrySet().iterator();

			while (itZ.hasNext()) {

				int z = itZ.next().getValue();

				SimpleWeightedGraph<Intersectionobject, DefaultWeightedEdge> simplegraph = track.Trackfunction();

				parent.parentgraphZ.put(Integer.toString(z), simplegraph);

				CurvedLineage(parent);

			}

		}

		else {

			SimpleWeightedGraph<Intersectionobject, DefaultWeightedEdge> simpledensegraph = track.Trackdensefunction();

			parent.parentdensegraphZ.put(Integer.toString(1), simpledensegraph);

			CurveddenseLineage(parent);

		}

		try {
			get();
		} catch (InterruptedException e) {

		} catch (ExecutionException e) {

		}
		if (batchmode) {
			BatchKymoSave.KymoSave(parent, savefile, parent.Cardframe);
			parent.imp.close();
		}
	}

	public static void CurvedLineage(InteractiveSimpleEllipseFit parent) {

		DisplaySelected.markAll(parent);
		DisplaySelected.selectAll(parent);

		if (parent.ndims < 3) {

			for (ArrayList<Curvatureobject> local : parent.AlllocalCurvature) {
				Iterator<Curvatureobject> iterator = local.iterator();

				while (iterator.hasNext()) {

					Curvatureobject currentcurvature = iterator.next();

					if (parent.originalimg.numDimensions() > 3) {
						if (currentcurvature.t == parent.fourthDimension) {
							parent.Finalcurvatureresult.put(currentcurvature.Label, currentcurvature);
						}
					} else if (parent.originalimg.numDimensions() <= 3) {
						if (currentcurvature.z == parent.thirdDimension) {
							parent.Finalcurvatureresult.put(currentcurvature.Label, currentcurvature);

						}

					}

				}
			}
			curvatureUtils.CurvatureTable.CreateTableView(parent);

		}

		else {

			for (Map.Entry<String, SimpleWeightedGraph<Intersectionobject, DefaultWeightedEdge>> entryZ : parent.parentgraphZ
					.entrySet()) {

				TrackModel model = new TrackModel(entryZ.getValue());

				int minid = Integer.MAX_VALUE;
				int maxid = Integer.MIN_VALUE;

				for (final Integer id : model.trackIDs(true)) {

					if (id > maxid)
						maxid = id;

					if (id < minid)
						minid = id;

				}

				if (minid != Integer.MAX_VALUE) {

					for (final Integer id : model.trackIDs(true)) {

						Comparator<Pair<String, Intersectionobject>> ThirdDimcomparison = new Comparator<Pair<String, Intersectionobject>>() {

							@Override
							public int compare(final Pair<String, Intersectionobject> A,
									final Pair<String, Intersectionobject> B) {

								return A.getB().z - B.getB().z;

							}

						};

						Comparator<Pair<String, Intersectionobject>> FourthDimcomparison = new Comparator<Pair<String, Intersectionobject>>() {

							@Override
							public int compare(final Pair<String, Intersectionobject> A,
									final Pair<String, Intersectionobject> B) {

								return A.getB().t - B.getB().t;

							}

						};

						model.setName(id, "Track" + id + entryZ.getKey());

						final HashSet<Intersectionobject> Angleset = model.trackIntersectionobjects(id);
						if (Angleset.size() > parent.AccountedZ.size() / 2) {
							Iterator<Intersectionobject> Angleiter = Angleset.iterator();

							while (Angleiter.hasNext()) {

								Intersectionobject currentangle = Angleiter.next();
								parent.Tracklist.add(new ValuePair<String, Intersectionobject>(
										Integer.toString(id) + entryZ.getKey(), currentangle));
							}
							Collections.sort(parent.Tracklist, ThirdDimcomparison);
							if (parent.fourthDimensionSize > 1)
								Collections.sort(parent.Tracklist, FourthDimcomparison);
						}
					}
					for (int id = minid; id <= maxid; ++id) {
						Intersectionobject bestangle = null;
						if (model.trackIntersectionobjects(id) != null
								&& model.trackIntersectionobjects(id).size() > parent.AccountedZ.size() / 2) {

							List<Intersectionobject> sortedList = new ArrayList<Intersectionobject>(
									model.trackIntersectionobjects(id));

							Collections.sort(sortedList, new Comparator<Intersectionobject>() {

								@Override
								public int compare(Intersectionobject o1, Intersectionobject o2) {

									return o1.t - o2.t;
								}

							});

							Collections.sort(sortedList, new Comparator<Intersectionobject>() {

								@Override
								public int compare(Intersectionobject o1, Intersectionobject o2) {

									return o1.z - o2.z;
								}

							});

							Iterator<Intersectionobject> iterator = sortedList.iterator();

							int count = 0;
							while (iterator.hasNext()) {

								Intersectionobject currentangle = iterator.next();
								if (count == 0)
									bestangle = currentangle;
								if (parent.originalimg.numDimensions() > 3) {
									if (currentangle.t == parent.fourthDimension) {
										bestangle = currentangle;
										count++;
										break;
									}
								} else if (parent.originalimg.numDimensions() <= 3) {
									if (currentangle.z == parent.thirdDimension) {
										bestangle = currentangle;
										count++;
										break;

									}

								}

							}

						}

					}
				}
			}

		}

	}

	public static void CurveddenseLineage(InteractiveSimpleEllipseFit parent) {
		DisplaySelected.markAll(parent);
		DisplaySelected.selectAll(parent);
		for (Map.Entry<String, SimpleWeightedGraph<Intersectionobject, DefaultWeightedEdge>> entryZ : parent.parentdensegraphZ
				.entrySet()) {

			TrackModel model = new TrackModel(entryZ.getValue());

			int minid = Integer.MAX_VALUE;
			int maxid = Integer.MIN_VALUE;

			for (final Integer id : model.trackIDs(true)) {

				if (id > maxid)
					maxid = id;

				if (id < minid)
					minid = id;

			}

			if (minid != Integer.MAX_VALUE) {

				for (final Integer id : model.trackIDs(true)) {
					Comparator<Pair<String, Intersectionobject>> ThirdDimcomparison = new Comparator<Pair<String, Intersectionobject>>() {

						@Override
						public int compare(final Pair<String, Intersectionobject> A,
								final Pair<String, Intersectionobject> B) {

							return A.getB().z - B.getB().z;

						}

					};

					Comparator<Pair<String, Intersectionobject>> FourthDimcomparison = new Comparator<Pair<String, Intersectionobject>>() {

						@Override
						public int compare(final Pair<String, Intersectionobject> A,
								final Pair<String, Intersectionobject> B) {

							return A.getB().t - B.getB().t;

						}

					};

					model.setName(id, "Track" + id + entryZ.getKey());

					final HashSet<Intersectionobject> Angleset = model.trackIntersectionobjects(id);

					if (Angleset.size() > parent.AccountedZ.size() / 2) {
						Iterator<Intersectionobject> Angleiter = Angleset.iterator();

						while (Angleiter.hasNext()) {

							Intersectionobject currentangle = Angleiter.next();
							parent.denseTracklist.add(new ValuePair<String, Intersectionobject>(
									Integer.toString(id) + entryZ.getKey(), currentangle));
						}
						Collections.sort(parent.denseTracklist, ThirdDimcomparison);
						if (parent.fourthDimensionSize > 1)
							Collections.sort(parent.denseTracklist, FourthDimcomparison);
					}
				}

				for (int id = minid; id <= maxid; ++id) {
					Intersectionobject bestangle = null;
					if (model.trackIntersectionobjects(id) != null
							&& model.trackIntersectionobjects(id).size() > parent.AccountedZ.size() / 2) {

						List<Intersectionobject> sortedList = new ArrayList<Intersectionobject>(
								model.trackIntersectionobjects(id));

						Collections.sort(sortedList, new Comparator<Intersectionobject>() {

							@Override
							public int compare(Intersectionobject o1, Intersectionobject o2) {

								return o1.t - o2.t;
							}

						});

						Collections.sort(sortedList, new Comparator<Intersectionobject>() {

							@Override
							public int compare(Intersectionobject o1, Intersectionobject o2) {

								return o1.z - o2.z;
							}

						});

						Iterator<Intersectionobject> iterator = sortedList.iterator();

						int count = 0;
						while (iterator.hasNext()) {

							Intersectionobject currentangle = iterator.next();
							if (count == 0)
								bestangle = currentangle;
							if (parent.originalimg.numDimensions() > 3) {
								if (currentangle.t == parent.fourthDimension) {
									bestangle = currentangle;
									count++;
									break;
								}
							} else if (parent.originalimg.numDimensions() <= 3) {
								if (currentangle.z == parent.thirdDimension) {
									bestangle = currentangle;
									count++;
									break;

								}

							}

						}

						parent.Finalresult.put(Integer.toString(id) + entryZ.getKey(), bestangle);
					}

				}
			}
			curvatureUtils.CurvatureTable.CreateTableTrackView(parent);
		}

	}

	public static Pair<HashMap<String, Integer>, HashMap<String, ArrayList<Intersectionobject>>> GetZTTrackList(
			final InteractiveSimpleEllipseFit parent) {

		int maxCurveDim = 0;

		HashMap<String, Integer> maxidcurve = new HashMap<String, Integer>();

		HashMap<String, ArrayList<Intersectionobject>> sortedMap = new HashMap<String, ArrayList<Intersectionobject>>();
		HashSet<String> TrackIDset = new HashSet<String>();
		for (Pair<String, Intersectionobject> preangle : parent.Tracklist) {
			String TrackID = preangle.getA();
			TrackIDset.add(TrackID);
		}

		Iterator<String> iter = TrackIDset.iterator();

		while (iter.hasNext()) {

			String TrackID = iter.next();

			Iterator<Map.Entry<String, Integer>> itZ = parent.AccountedZ.entrySet().iterator();
			while (itZ.hasNext()) {

				Map.Entry<String, Integer> entry = itZ.next();

				int z = entry.getValue();

				String timeID = entry.getKey();

				ArrayList<Intersectionobject> currentframeobject = new ArrayList<Intersectionobject>();
				for (Pair<String, Intersectionobject> currentangle : parent.Tracklist) {

					if (currentangle.getB().z == z && currentangle.getA().equals(TrackID)) {

						currentframeobject.add(currentangle.getB());

					}

					for (int i = 0; i < currentframeobject.size(); ++i) {

						int size = currentframeobject.get(i).linelist.size();

						if (size > maxCurveDim)
							maxCurveDim = size;

					}

				}
				sortedMap.put(TrackID + timeID, currentframeobject);
				maxidcurve.put(TrackID, maxCurveDim);
			}
		}
		return new ValuePair<HashMap<String, Integer>, HashMap<String, ArrayList<Intersectionobject>>>(maxidcurve,
				sortedMap);

	}

	public static Binobject GetZTdenseTrackList(final InteractiveSimpleEllipseFit parent) {

	

		HashMap<String, Integer> maxidcurve = new HashMap<String, Integer>();

		HashMap<String, Double> bincurve = new HashMap<String, Double>();

		HashMap<String, ArrayList<Intersectionobject>> sortedMap = new HashMap<String, ArrayList<Intersectionobject>>();

		Set<String> TrackIDset = new HashSet<String>();
		for (Pair<String, Intersectionobject> preangle : parent.denseTracklist) {
			String TrackID = preangle.getA();
			TrackIDset.add(TrackID);
		}

		Iterator<String> iter = TrackIDset.iterator();

		while (iter.hasNext()) {

			String TrackID = iter.next();
			int maxCurveDim = 0;

			double binwidth = 0;
			Iterator<Map.Entry<String, Integer>> itZ = parent.AccountedZ.entrySet().iterator();
			while (itZ.hasNext()) {

				Map.Entry<String, Integer> entry = itZ.next();

				int z = entry.getValue();

				String timeID = entry.getKey();

				ArrayList<Intersectionobject> currentframeobject = new ArrayList<Intersectionobject>();

				for (Pair<String, Intersectionobject> currentangle : parent.denseTracklist) {

					if (currentangle.getB().z == z && currentangle.getA().equals(TrackID)) {

						currentframeobject.add(currentangle.getB());

					}
					int size = 0;
					for (int i = 0; i < currentframeobject.size(); ++i) {

						 size += currentframeobject.get(i).linelist.size();
						

					}
					size /=  Math.max(currentframeobject.size(), 1);
					
					if(size > maxCurveDim) {
					maxCurveDim = size;
					binwidth = size;
					}

				}

				sortedMap.put(TrackID + timeID, currentframeobject);
				maxidcurve.put(TrackID, maxCurveDim);
				bincurve.put(TrackID, binwidth);

			}
		}

		Binobject binned = new Binobject(maxidcurve, sortedMap, bincurve);

		return binned;

	}

}
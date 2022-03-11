package interactivePreprocessing;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.CardLayout;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.Scrollbar;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


import timeGUI.CovistoTimeselectPanel;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import costMatrix.CostFunction;
import distanceTransform.CreateWatershed;
import distanceTransform.DistWatershed;
import dogGUI.CovistoDogPanel;
import dogSeg.DOGSeg;
import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.Overlay;
import ij.gui.Roi;
import ij.plugin.frame.RoiManager;
import ij.process.ColorProcessor;
import kalmanGUI.CovistoKalmanPanel;
import linkers.Model3D;
import linkers.PRENNsearch;
import mpicbg.imglib.util.Util;
import mserGUI.CovistoMserPanel;
import mserMethods.MSERSeg;
import nearestNeighbourGUI.CovistoNearestNPanel;
import net.imglib2.Cursor;
import net.imglib2.FinalInterval;
import net.imglib2.Point;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.componenttree.mser.MserTree;
import net.imglib2.algorithm.dog.DogDetection;
import net.imglib2.algorithm.localextrema.RefinedPeak;
import net.imglib2.algorithm.stats.Normalize;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Pair;
import net.imglib2.view.Views;
import preProcessing.GetLocalmaxminMT;
import preProcessing.GlobalThresholding;
import preProcessing.Kernels;
import snakeGUI.CovistoSnakePanel;
import utility.PreRoiobject;
import utility.ThreeDRoiobject;
import visualization.CovistoModelView;
import visualization.Draw3DLines;
import visualization.DummyTrackColorGenerator;
import visualization.SelectionModel;
import watershedGUI.CovistoWatershedPanel;
import zGUI.CovistoZselectPanel;

public class InteractiveMethods {

	public NumberFormat nf;
	public RandomAccessibleInterval<FloatType> originalimg;
	public int ndims;

	public int Maxlabel;
	public int thresholdsliderInit = 125;
 
	public ImagePlus imp;
	public boolean showMSER = false;
	public boolean showDOG = false;
	public boolean showWatershed = false;
	public HashMap<String, Integer> Accountedframes;
	public HashMap<String, Integer> AccountedZ;
	public boolean displayWatershedimg = true;
	public boolean displayBinaryimg = false;
	public boolean displayDistTransimg = false;
	public RandomAccessibleInterval<UnsignedByteType> newimg;
	public RandomAccessibleInterval<FloatType> CurrentView;
	
	
	
	public RoiManager roimanager;
	public final int scrollbarSize = 1000;
	public JProgressBar jpb;
	public File inputfile;
	public boolean autothreshwater = false;
	public boolean snakeongoing = false;
	public int Progressmin = 0;
	public int Progressmax = 100;
	public int max = Progressmax;
	public MserTree<UnsignedByteType> newtree;
	public Roi nearestRoiCurr;
	public int rowchoice;
	public static int standardSensitivity = 4;
	public int sensitivity = standardSensitivity;
	public Color colorChange = Color.RED;
	
	public MouseMotionListener ml;
	public MouseListener mvl;
	public int timeMin = 1;
	

	public boolean onlySeg = true;

	public boolean TrackandSeg = false;
	
	public int tablesize;
	

	public ArrayList<Roi> Rois;
	public ArrayList<Roi> AfterRemovedRois;
	public ArrayList<PreRoiobject> CurrentPreRoiobject;
	public ArrayList<Roi> NearestNeighbourRois;
	public ArrayList<Roi> BiggerRois;
	public JTable table;
	public int row;
	
	public CostFunction<ThreeDRoiobject, ThreeDRoiobject> UserchosenCostFunction;
	public Color colorDrawMser = Color.green;
	public Color colorDrawDog = Color.red;
	public Color colorConfirm = Color.blue;
	public Color colorSnake = Color.YELLOW;
	public Color colorTrack = Color.GREEN;
	public Overlay overlay;
	public FinalInterval interval;
	
	public RandomAccessibleInterval<BitType> bitimg;
	
	
	public RandomAccessibleInterval<BitType> Segbitimg;
	public RandomAccessibleInterval<BitType> Segafterremovebitimg;
	
	
	public RandomAccessibleInterval<FloatType> bitimgFloat;
	public RandomAccessibleInterval<IntType> intimg;
	public HashMap<Integer, ArrayList<ThreeDRoiobject>> Timetracks;
	public ArrayList<PreRoiobject> ZTPreRoiobject;
	public ConcurrentHashMap<String, ArrayList<PreRoiobject>> ZTRois;
	public HashMap<Integer, ArrayList<ThreeDRoiobject>> threeDTRois;
    public ConcurrentHashMap<Integer, ArrayList<double[]>> AllEvents;
	public ArrayList<RefinedPeak<Point>> peaks;

	public String uniqueID, ZID, TID;
	public boolean apply3D = false;
	public Model3D model = new Model3D();
	public SelectionModel selmode = new SelectionModel(model);

	public ImageStack prestack;
	public boolean SegMode;

	public ColorProcessor cp = null;
    public final String userfile;
	public boolean AutoSnake = true;
	public boolean advancedSnake = false;

	public static enum ValueChange {

		ALL, MSER, DOG, SNAKE, WATER, DIST, DISTWATER, GAUSS, THRESHOLD, SIGMA, FOURTHDIMmouse, THIRDDIMmouse,
		THIRDDIM, MINDIVERSITY, DELTA, MINSIZE, MAXSIZE, MAXVAR, DARKTOBRIGHT, PREROI, NearestN, Kalman, ALPHA, BETA, ThreeDTrackDisplay, ThreeDTrackDisplayALL;

	}

	

	public InteractiveMethods() {

		nf = NumberFormat.getInstance(Locale.ENGLISH);
		nf.setMaximumFractionDigits(3);
		nf.setGroupingUsed(false);
        this.userfile = null;
	}



	public InteractiveMethods(final RandomAccessibleInterval<FloatType> originalimg, File file, final boolean SegMode,
			final boolean TrackandSeg, String userfile) {

		this.originalimg = originalimg;
		this.inputfile = file;
		this.SegMode = SegMode;
		this.TrackandSeg = TrackandSeg;
		nf = NumberFormat.getInstance(Locale.ENGLISH);
		nf.setMaximumFractionDigits(3);
		nf.setGroupingUsed(false);
		this.userfile = userfile;
		this.ndims = originalimg.numDimensions();
	}

	public void run(String arg0) {
		FloatType minval = new FloatType(0);
		FloatType maxval = new FloatType(255);
		Normalize.normalize(Views.iterable(originalimg), minval, maxval);
		prestack = new ImageStack((int) originalimg.dimension(0), (int) originalimg.dimension(1),
				java.awt.image.ColorModel.getRGBdefault());
		Accountedframes = new HashMap<String, Integer>();
		AccountedZ = new HashMap<String, Integer>();
		AllEvents = new ConcurrentHashMap<Integer, ArrayList<double[]>>();
		AfterRemovedRois = new ArrayList<Roi>(); 
		//universe = new Image3DUniverse((int) originalimg.dimension(0), (int) originalimg.dimension(1));
		jpb = new JProgressBar();
		overlay = new Overlay();
		interval = new FinalInterval(originalimg.dimension(0), originalimg.dimension(1));
		peaks = new ArrayList<RefinedPeak<Point>>();
		ZTRois = new ConcurrentHashMap<String, ArrayList<PreRoiobject>>();
		threeDTRois = new HashMap<Integer, ArrayList<ThreeDRoiobject>>();
		CurrentPreRoiobject = new ArrayList<PreRoiobject>();
		ZTPreRoiobject = new ArrayList<PreRoiobject>();
		Timetracks = new HashMap<Integer, ArrayList<ThreeDRoiobject>>();
		
		Segbitimg = new ArrayImgFactory<BitType>().create(originalimg,
				new BitType());
		
		Segafterremovebitimg =  new ArrayImgFactory<BitType>().create(originalimg,
				new BitType());
		

		if (ndims < 3) {

			CovistoZselectPanel.thirdDimensionSize = 0;
			CovistoTimeselectPanel.fourthDimensionSize = 0;
		}

		if (ndims == 3) {

			CovistoTimeselectPanel.fourthDimension = 0;
			CovistoTimeselectPanel.fourthDimensionsliderInit = 0;
			CovistoZselectPanel.thirdDimension = 1;
			CovistoTimeselectPanel.fourthDimensionSize = 0;
			CovistoZselectPanel.thirdDimensionSize = (int) originalimg.dimension(2);

		}

		if (ndims == 4) {

			CovistoTimeselectPanel.fourthDimension = 1;
			CovistoZselectPanel.thirdDimension = 1;
			CovistoZselectPanel.thirdDimensionSize = (int) originalimg.dimension(2);
			CovistoTimeselectPanel.fourthDimensionSize = (int) originalimg.dimension(3);

			prestack = new ImageStack((int) originalimg.dimension(0), (int) originalimg.dimension(1),
					java.awt.image.ColorModel.getRGBdefault());
		}

		CurrentView = utility.CovistoSlicer.getCurrentView(originalimg, CovistoTimeselectPanel.fourthDimension,
				CovistoZselectPanel.thirdDimensionSize, CovistoZselectPanel.thirdDimension,
				CovistoTimeselectPanel.fourthDimensionSize);

		imp = ImageJFunctions.show(CurrentView);
	
		imp.setTitle("Active image" + " " + "time point : " + CovistoTimeselectPanel.fourthDimension + " " + " Z: "
				+ CovistoZselectPanel.thirdDimension);
		roimanager = RoiManager.getInstance();

		if (roimanager == null) {
			roimanager = new RoiManager();
		}

		updatePreview(ValueChange.ALL);

		Cardframe.repaint();
		Cardframe.validate();
		panelFirst.repaint();
		panelFirst.validate();

		Card();

	}

	public void updatePreview(final ValueChange change) {

		overlay = imp.getOverlay();
		int localthirddim = CovistoZselectPanel.thirdDimension, localfourthdim = CovistoTimeselectPanel.fourthDimension;
		uniqueID = Integer.toString(CovistoZselectPanel.thirdDimension)
				+ Integer.toString(CovistoTimeselectPanel.fourthDimension);
		ZID = Integer.toString(CovistoZselectPanel.thirdDimension);
		TID = Integer.toString(CovistoTimeselectPanel.fourthDimension);
		if (overlay == null) {

			overlay = new Overlay();
			imp.setOverlay(overlay);
		}

		roimanager = RoiManager.getInstance();

		if (roimanager == null) {
			roimanager = new RoiManager();
		}

		

		if (change == ValueChange.SNAKE) {

			overlay.clear();
			Accountedframes.put(TID, CovistoTimeselectPanel.fourthDimension);

			AccountedZ.put(ZID, CovistoZselectPanel.thirdDimension);

			for (Map.Entry<String, ArrayList<PreRoiobject>> entry : ZTRois.entrySet()) {

				ArrayList<PreRoiobject> current = entry.getValue();
				for (PreRoiobject currentroi : current) {

					if (currentroi.fourthDimension == CovistoTimeselectPanel.fourthDimension
							&& currentroi.thirdDimension == CovistoZselectPanel.thirdDimension) {

						currentroi.rois.setStrokeColor(colorSnake);
						overlay.add(currentroi.rois);
						localthirddim = currentroi.thirdDimension;
						localfourthdim = currentroi.fourthDimension;
					}

				}
			}
			imp.setOverlay(overlay);
			imp.updateAndDraw();
			CovistoZselectPanel.zText.setText("Current Z = " + localthirddim);
			CovistoZselectPanel.zgenText.setText("Current Z / T = " + localthirddim);
			CovistoZselectPanel.zslider.setValue(utility.CovistoSlicer.computeScrollbarPositionFromValue(localthirddim,
					CovistoZselectPanel.thirdDimensionsliderInit, CovistoZselectPanel.thirdDimensionSize,
					scrollbarSize));
			CovistoZselectPanel.zslider.repaint();
			CovistoZselectPanel.zslider.validate();

			CovistoTimeselectPanel.timeText.setText("Current T = " + localfourthdim);
			CovistoTimeselectPanel.timeslider.setValue(utility.CovistoSlicer.computeScrollbarPositionFromValue(
					localfourthdim, CovistoTimeselectPanel.fourthDimensionsliderInit,
					CovistoTimeselectPanel.fourthDimensionSize, scrollbarSize));
			CovistoTimeselectPanel.timeslider.repaint();
			CovistoTimeselectPanel.timeslider.validate();

		}

		if (change == ValueChange.PREROI) {

			ZTPreRoiobject.clear();
			for (Roi currentroi : Rois) {

				final double[] geocenter = currentroi.getContourCentroid();
				final Pair<Double, Integer> Intensityandpixels = PreRoiobject.getIntensity(currentroi, CurrentView);
				final double intensity = Intensityandpixels.getA();
				final double numberofpixels = Intensityandpixels.getB();
				final double averageintensity = intensity / numberofpixels;
				PreRoiobject currentobject = new PreRoiobject(currentroi,
						new double[] { geocenter[0], geocenter[1], CovistoZselectPanel.thirdDimension }, numberofpixels,
						intensity, averageintensity, CovistoZselectPanel.thirdDimension,
						CovistoTimeselectPanel.fourthDimension);
				ZTPreRoiobject.add(currentobject);
			}
			Accountedframes.put(TID, CovistoTimeselectPanel.fourthDimension);

			AccountedZ.put(ZID, CovistoZselectPanel.thirdDimension);
			ZTRois.put(uniqueID, ZTPreRoiobject);

			if (overlay != null)
				overlay.clear();

			for (Map.Entry<String, ArrayList<PreRoiobject>> entry : ZTRois.entrySet()) {

				ArrayList<PreRoiobject> current = entry.getValue();
				for (PreRoiobject currentroi : current) {

					if (currentroi.fourthDimension == CovistoTimeselectPanel.fourthDimension
							&& currentroi.thirdDimension == CovistoZselectPanel.thirdDimension) {

						currentroi.rois.setStrokeColor(colorConfirm);
						overlay.add(currentroi.rois);
					}

				}
			}
			imp.setOverlay(overlay);
			imp.updateAndDraw();

		}

		if (change == ValueChange.THIRDDIM) {

			if (imp == null) {
				imp = ImageJFunctions.show(CurrentView);

			}

			else {

				final float[] pixels = (float[]) imp.getProcessor().getPixels();
				final Cursor<FloatType> c = Views.iterable(CurrentView).cursor();

				for (int i = 0; i < pixels.length; ++i)
					pixels[i] = c.next().get();

				imp.updateAndDraw();

			}

			imp.setTitle("Active image" + " " + "time point : " + CovistoTimeselectPanel.fourthDimension + " " + " Z: "
					+ CovistoZselectPanel.thirdDimension);

			newimg = utility.CovistoSlicer.PREcopytoByteImage(CurrentView);

		}

		if (change == ValueChange.FOURTHDIMmouse || change == ValueChange.THIRDDIMmouse) {

			if(!apply3D) {
			
			if (imp == null) {
				imp = ImageJFunctions.show(CurrentView);

			}

			else {

				final float[] pixels = (float[]) imp.getProcessor().getPixels();
				final Cursor<FloatType> c = Views.iterable(CurrentView).cursor();

				for (int i = 0; i < pixels.length; ++i)
					pixels[i] = c.next().get();

				imp.updateAndDraw();

			}

			imp.setTitle("Active image" + " " + "time point : " + CovistoTimeselectPanel.fourthDimension + " " + " Z: "
					+ CovistoZselectPanel.thirdDimension);

			newimg = utility.CovistoSlicer.PREcopytoByteImage(CurrentView);

			if (showMSER ) {

				MSERSeg computeMSER = new MSERSeg(this, jpb);
				computeMSER.execute();

			}

			if (showDOG  ) {

				DOGSeg computeDOG = new DOGSeg(this, jpb);
				computeDOG.execute();
			}
			}
			CovistoZselectPanel.zText.setText("Current Z = " + localthirddim);
			CovistoZselectPanel.zgenText.setText("Current Z / T = " + localthirddim);
			CovistoZselectPanel.zslider.setValue(utility.CovistoSlicer.computeScrollbarPositionFromValue(localthirddim,
					CovistoZselectPanel.thirdDimensionsliderInit, CovistoZselectPanel.thirdDimensionSize,
					scrollbarSize));
			CovistoZselectPanel.zslider.repaint();
			CovistoZselectPanel.zslider.validate();

			CovistoTimeselectPanel.timeText.setText("Current T = " + localfourthdim);
			CovistoTimeselectPanel.timeslider.setValue(utility.CovistoSlicer.computeScrollbarPositionFromValue(
					localfourthdim, CovistoTimeselectPanel.fourthDimensionsliderInit,
					CovistoTimeselectPanel.fourthDimensionSize, CovistoTimeselectPanel.scrollbarSize));
			CovistoTimeselectPanel.timeslider.repaint();
			CovistoTimeselectPanel.timeslider.validate();
		}

		if (change == ValueChange.MSER) {
			if (imp == null) {
				imp = ImageJFunctions.show(CurrentView);

			}

			else {

				final float[] pixels = (float[]) imp.getProcessor().getPixels();
				final Cursor<FloatType> c = Views.iterable(CurrentView).cursor();

				for (int i = 0; i < pixels.length; ++i)
					pixels[i] = c.next().get();

				imp.updateAndDraw();

			}

			imp.setTitle("Active image" + " " + "time point : " + CovistoTimeselectPanel.fourthDimension + " " + " Z: "
					+ CovistoZselectPanel.thirdDimension);

			newimg = utility.CovistoSlicer.PREcopytoByteImage(CurrentView);

			MSERSeg computeMSER = new MSERSeg(this, jpb);
			computeMSER.execute();

		}

		if (change == ValueChange.WATER) {

			if (overlay != null)
				overlay.clear();

			newimg = utility.CovistoSlicer.PREcopytoByteImage(CurrentView);
			bitimg = new ArrayImgFactory<BitType>().create(newimg, new BitType());
			bitimgFloat = new ArrayImgFactory<FloatType>().create(newimg, new FloatType());
			GetLocalmaxminMT.ThresholdingMTBit(CurrentView, bitimg, CovistoWatershedPanel.thresholdWater);
			if (displayBinaryimg && !apply3D)
				ImageJFunctions.show(bitimg);
			DistWatershed<FloatType> WaterafterDisttransform = new DistWatershed<FloatType>(this, CurrentView, bitimg,
					jpb, false);
			WaterafterDisttransform.execute();
			if (displayWatershedimg && !apply3D)
				ImageJFunctions.show(WaterafterDisttransform.getResult());

			if (displayDistTransimg && !apply3D)
				ImageJFunctions.show(WaterafterDisttransform.getDistanceTransformedimg());

		}

		if (change == ValueChange.DOG) {

			if (imp == null) {
				imp = ImageJFunctions.show(CurrentView);

			}

			else {

				final float[] pixels = (float[]) imp.getProcessor().getPixels();
				final Cursor<FloatType> c = Views.iterable(CurrentView).cursor();

				for (int i = 0; i < pixels.length; ++i)
					pixels[i] = c.next().get();

				imp.updateAndDraw();

			}

			imp.setTitle("Active image" + " " + "time point : " + CovistoTimeselectPanel.fourthDimension + " " + " Z: "
					+ CovistoZselectPanel.thirdDimension);

			newimg = utility.CovistoSlicer.PREcopytoByteImage(CurrentView);

			DOGSeg computeDOG = new DOGSeg(this, jpb);
			computeDOG.execute();
		}

	}

	public JFrame Cardframe = new JFrame("Computer Vision Segmentation Tools (CoViSto)");
	public JPanel panelCont = new JPanel();
	public JPanel panelFirst = new JPanel();
	public JPanel panelSecond = new JPanel();
	public JPanel panelThird = new JPanel();
	public JPanel Timeselect = new JPanel();
	public JPanel Zselect = new JPanel();
	public JPanel MserPanel = new JPanel();
	public JPanel DogPanel = new JPanel();
	public JPanel WaterPanel = new JPanel();
	public JPanel SnakePanel = new JPanel();
	public JPanel RoiPanel = new JPanel();
	public JPanel DetectionPanel = new JPanel();
	public JPanel RemovalPanel = new JPanel();

	public JPanel NearestNPanel = new JPanel();
	public JPanel KalmanPanel = new JPanel();

	final String maxSearchstring = "Maximum search radius";

	public JButton Roibutton = new JButton("Confirm current roi selection");

	public CheckboxGroup detection = new CheckboxGroup();
	final Checkbox Watershed = new Checkbox("Do watershedding", detection, showWatershed);
	final Checkbox DOG = new Checkbox("Do DoG detection", detection, showDOG);
	final Checkbox MSER = new Checkbox("Do MSER detection", detection, showMSER);

	public final Insets insets = new Insets(10, 0, 0, 0);
	public final GridBagLayout layout = new GridBagLayout();
	public final GridBagConstraints c = new GridBagConstraints();

	public TextField inputField = new TextField();
	public TextField inputtrackField;

	public int SizeXbig = 400;
	public int SizeXsmall = 200;
	public int SizeX = 400;
	public int SizeY = 200;
	public int SizeYsmall = 200;
	public int SizeYbig = 500;
	public JScrollPane scrollPane;
	public JPanel PanelSelectFile = new JPanel();
	public Border selectfile = new CompoundBorder(new TitledBorder("Select Track"), new EmptyBorder(c.insets));
	public JPanel controlnextthird = new JPanel();
	public JPanel controlprevthird = new JPanel();

	public void Card() {

		CardLayout cl = new CardLayout();

		c.insets = new Insets(5, 5, 5, 5);
		panelCont.setLayout(cl);

		panelCont.add(panelFirst, "1");

		panelCont.add(panelSecond, "2");

		panelCont.add(panelThird, "3");

		panelFirst.setLayout(layout);
		panelSecond.setLayout(layout);
		panelThird.setLayout(layout);
		DetectionPanel.setLayout(layout);
		RoiPanel.setLayout(layout);

		RemovalPanel.setLayout(layout);
		inputField.setColumns(10);

		Border methodborder = new CompoundBorder(new TitledBorder("Choose a segmentation algorithm"),
				new EmptyBorder(c.insets));
		
		

		c.anchor = GridBagConstraints.BOTH;
		c.ipadx = 35;

		c.gridwidth = 10;
		c.gridheight = 10;
		c.gridy = 1;
		c.gridx = 0;

		// Put time slider
		Timeselect = CovistoTimeselectPanel.TimeselectPanel(ndims);

		panelFirst.add(Timeselect, new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));
		// Put z slider

		Zselect = CovistoZselectPanel.ZselectPanel(ndims);

		panelFirst.add(Zselect, new GridBagConstraints(3, 0, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		// Choice of segmentation algorithm panel
		DetectionPanel.add(Watershed, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));
		DetectionPanel.add(DOG, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));
		DetectionPanel.add(MSER, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		DetectionPanel.setBorder(methodborder);
		panelFirst.add(DetectionPanel, new GridBagConstraints(0, 1, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		// Watershed panel
		WaterPanel = CovistoWatershedPanel.WaterPanel();
		panelFirst.add(WaterPanel, new GridBagConstraints(3, 1, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.RELATIVE, insets, 0, 0));

		// Difference of Gaussian detection panel
		DogPanel = CovistoDogPanel.DogPanel();
		panelFirst.add(DogPanel, new GridBagConstraints(0, 3, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		// Mser detection panel
		MserPanel = CovistoMserPanel.MserPanel();
		panelFirst.add(MserPanel, new GridBagConstraints(3, 3, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.RELATIVE, new Insets(10, 10, 0, 10), 0, 0));

		final JButton Zsnakes = new JButton("Apply snakes in Z");
		final JButton Tsnakes = new JButton("Apply snakes in T");
		final JButton Allsnakes = new JButton("Apply snakes in Z & T");

		final JButton SinglethreeD = new JButton("Track in Z for currrent T");

		JPanel controlprev = new JPanel();
		JPanel controlnext = new JPanel();

		controlprev.setLayout(layout);
		controlnext.setLayout(layout);
		controlnextthird.setLayout(layout);
		controlprevthird.setLayout(layout);
		controlprev.add(new JButton(new AbstractAction("\u22b2Prev") {

			@Override
			public void actionPerformed(ActionEvent e) {
				CardLayout cl = (CardLayout) panelCont.getLayout();
				cl.previous(panelCont);
			}
		}));

		controlnext.add(new JButton(new AbstractAction("Next\u22b3") {

			@Override
			public void actionPerformed(ActionEvent e) {
				CardLayout cl = (CardLayout) panelCont.getLayout();
				cl.next(panelCont);
			}
		}));

		controlnextthird.add(new JButton(new AbstractAction("Next\u22b3") {

			@Override
			public void actionPerformed(ActionEvent e) {
				CardLayout cl = (CardLayout) panelCont.getLayout();
				cl.next(panelCont);
			}
		}));
		controlprevthird.add(new JButton(new AbstractAction("\u22b2Prev") {

			@Override
			public void actionPerformed(ActionEvent e) {
				CardLayout cl = (CardLayout) panelCont.getLayout();
				cl.previous(panelCont);
			}
		}));

		// Active contour refinement panel
		SnakePanel = CovistoSnakePanel.SnakePanel(originalimg.numDimensions());
		panelSecond.add(SnakePanel, new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));

		// Tracking panel
		if (!SegMode) {

			NearestNPanel = CovistoNearestNPanel.NearestNPanel();

			KalmanPanel = CovistoKalmanPanel.KalmanPanel();

			panelThird.add(KalmanPanel, new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
					GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
			CovistoKalmanPanel.lostframe.addTextListener(new PRELostFrameListener(this));
			CovistoKalmanPanel.alphaS.addAdjustmentListener(new PREAlphaListener(this, CovistoKalmanPanel.alphaText,
					CovistoKalmanPanel.alphastring, CovistoKalmanPanel.alphaMin, CovistoKalmanPanel.alphaMax,
					CovistoKalmanPanel.scrollbarSize, CovistoKalmanPanel.alphaS));
			CovistoKalmanPanel.betaS.addAdjustmentListener(new PREBetaListener(this, CovistoKalmanPanel.betaText,
					CovistoKalmanPanel.betastring, CovistoKalmanPanel.betaMin, CovistoKalmanPanel.betaMax,
					CovistoKalmanPanel.scrollbarSize, CovistoKalmanPanel.betaS));
			CovistoNearestNPanel.maxSearchNearest.addAdjustmentListener(new PREMaxSearchListener(this,
					CovistoNearestNPanel.maxSearchTextNearest, CovistoNearestNPanel.maxSearchstringNearest,
					CovistoNearestNPanel.maxSearchradiusMinNearest, CovistoNearestNPanel.maxSearchradiusMaxNearest,
					CovistoNearestNPanel.scrollbarSize, CovistoNearestNPanel.maxSearchNearest));
			CovistoKalmanPanel.maxSearchKalman.addAdjustmentListener(new PREMaxSearchTListener(this,
					CovistoKalmanPanel.maxSearchTextKalman, CovistoKalmanPanel.maxSearchstringKalman,
					CovistoKalmanPanel.maxSearchradiusMin, CovistoKalmanPanel.maxSearchradiusMax,
					CovistoKalmanPanel.scrollbarSize, CovistoKalmanPanel.maxSearchSS));
			CovistoKalmanPanel.initialSearchS.addAdjustmentListener(new PREIniSearchListener(this,
					CovistoKalmanPanel.iniSearchText, CovistoKalmanPanel.initialSearchstring,
					CovistoKalmanPanel.initialSearchradiusMin, CovistoKalmanPanel.initialSearchradiusMax,
					CovistoKalmanPanel.scrollbarSize, CovistoKalmanPanel.initialSearchS));
		}

		Watershed.addItemListener(new DoWatershedListener(this));
		DOG.addItemListener(new DoDOGListener(this));
		MSER.addItemListener(new DoMSERListener(this));
		CovistoWatershedPanel.autothreshold.addItemListener(new PREauto(this));
		CovistoWatershedPanel.dodist.addItemListener(new PREdist(this));
		CovistoWatershedPanel.tryWater.addActionListener(new PREApplyWaterCurrentListener(this));
		CovistoDogPanel.findminima.addItemListener(new FindMinimaListener(this));
		CovistoDogPanel.findmaxima.addItemListener(new FindMaximaListener(this));
		CovistoMserPanel.findminimaMser.addItemListener(new FindMinimaMserListener(this));
		CovistoMserPanel.findmaximaMser.addItemListener(new FindMaximaMserListener(this));

		CovistoWatershedPanel.displayBinary.addItemListener(new PREShowBinary(this));
		CovistoWatershedPanel.displayWater.addItemListener(new PREShowWatershed(this));
		CovistoWatershedPanel.displayDist.addItemListener(new PREShowDist(this));
		CovistoDogPanel.AllDog.addActionListener(new PREApplyDog3DListener(this));
		
		
		
		CovistoSnakePanel.advanced.addItemListener(new AdvancedSnakeListener(this));
		CovistoSnakePanel.Snakeiter.addTextListener(new IterationListener(this));
		CovistoSnakePanel.gradientthresh.addTextListener(new GradientListener(this));
		CovistoSnakePanel.maxdist.addTextListener(new MaxdistListener(this));

		CovistoWatershedPanel.thresholdWaterslider.addAdjustmentListener(
				new PreWaterListener(this, CovistoWatershedPanel.watertext, CovistoWatershedPanel.waterstring,
						CovistoWatershedPanel.thresholdMinWater, CovistoWatershedPanel.thresholdMaxWater, scrollbarSize,
						CovistoWatershedPanel.thresholdWaterslider));
		
		
		CovistoDogPanel.sigmaslider.addAdjustmentListener(new PreSigmaListener(this, CovistoDogPanel.sigmaText,
				CovistoDogPanel.sigmastring, CovistoDogPanel.sigmaMin, CovistoDogPanel.sigmaMax,
				CovistoDogPanel.scrollbarSize, CovistoDogPanel.sigmaslider));

		CovistoMserPanel.deltaS.addAdjustmentListener(new PREDeltaListener(this, CovistoMserPanel.deltaText,
				CovistoMserPanel.deltastring, CovistoMserPanel.deltaMin, CovistoMserPanel.deltaMax,
				CovistoMserPanel.scrollbarSize, CovistoMserPanel.deltaS));

		CovistoMserPanel.Unstability_ScoreS.addAdjustmentListener(new PREUnstability_ScoreListener(this,
				CovistoMserPanel.Unstability_ScoreText, CovistoMserPanel.Unstability_Scorestring,
				CovistoMserPanel.Unstability_ScoreMin, CovistoMserPanel.Unstability_ScoreMax,
				CovistoMserPanel.scrollbarSize, CovistoMserPanel.Unstability_ScoreS));

		CovistoMserPanel.minDiversityS.addAdjustmentListener(new PREMinDiversityListener(this,
				CovistoMserPanel.minDivText, CovistoMserPanel.minDivstring, CovistoMserPanel.minDiversityMin,
				CovistoMserPanel.minDiversityMax, CovistoMserPanel.scrollbarSize, CovistoMserPanel.minDiversityS));

		CovistoMserPanel.minSizeS.addAdjustmentListener(new PREMinSizeListener(this, CovistoMserPanel.minSizeText,
				CovistoMserPanel.minSizestring, CovistoMserPanel.minSizemin, CovistoMserPanel.minSizemax,
				CovistoMserPanel.scrollbarSize, CovistoMserPanel.minSizeS));

		CovistoMserPanel.maxSizeS.addAdjustmentListener(new PREMaxSizeListener(this, CovistoMserPanel.maxSizeText,
				CovistoMserPanel.maxSizestring, CovistoMserPanel.minSizemax, CovistoMserPanel.maxSizeInit,
				CovistoMserPanel.scrollbarSize, CovistoMserPanel.maxSizeS));

		CovistoDogPanel.thresholdslider.addAdjustmentListener(new PreThresholdListener(this,
				CovistoDogPanel.thresholdText, CovistoDogPanel.thresholdstring, CovistoDogPanel.thresholdMin,
				CovistoDogPanel.thresholdMax, CovistoDogPanel.scrollbarSize, CovistoDogPanel.thresholdslider));
		
		
		CovistoDogPanel.inputFieldSpot.addTextListener(new PreDistThresholdListener(this, CovistoDogPanel.distthreshold));
		CovistoDogPanel.inputtimedist.addTextListener(new PreTimeDistThresholdListener(this, CovistoDogPanel.timethreshold));
		CovistoDogPanel.inputtimeskip.addTextListener(new PreTimeSkipListener(this, CovistoDogPanel.timeblock));
		
		
		

		CovistoTimeselectPanel.timeslider.addAdjustmentListener(
				new PreTimeListener(this, CovistoTimeselectPanel.timeText, CovistoTimeselectPanel.timestring,
						CovistoTimeselectPanel.fourthDimensionsliderInit, CovistoTimeselectPanel.fourthDimensionSize,
						CovistoTimeselectPanel.scrollbarSize, CovistoTimeselectPanel.timeslider));

		if (ndims > 3)
			CovistoZselectPanel.zslider.addAdjustmentListener(new PreZListener(this, CovistoZselectPanel.zText,
					CovistoZselectPanel.zstring, CovistoZselectPanel.thirdDimensionsliderInit,
					CovistoZselectPanel.thirdDimensionSize, scrollbarSize, CovistoZselectPanel.zslider));
		else
			CovistoZselectPanel.zslider.addAdjustmentListener(new PreZListener(this, CovistoZselectPanel.zgenText,
					CovistoZselectPanel.zgenstring, CovistoZselectPanel.thirdDimensionsliderInit,
					CovistoZselectPanel.thirdDimensionSize, scrollbarSize, CovistoZselectPanel.zslider));

		CovistoZselectPanel.inputFieldZ.addTextListener(new PreZlocListener(this, false));
		CovistoTimeselectPanel.inputFieldT.addTextListener(new PreTlocListener(this, false));

		panelSecond.add(controlprev, new GridBagConstraints(0, 4, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.RELATIVE, new Insets(10, 10, 0, 10), 0, 0));
		if (!SegMode) {
			panelSecond.add(controlnextthird, new GridBagConstraints(2, 4, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
					GridBagConstraints.RELATIVE, new Insets(10, 10, 0, 10), 0, 0));
		}
		panelSecond.setPreferredSize(SnakePanel.getPreferredSize());
		controlnextthird.setEnabled(false);
		panelFirst.add(controlnext, new GridBagConstraints(3, 4, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.RELATIVE, new Insets(10, 10, 0, 10), 0, 0));
		panelThird.add(PanelSelectFile, new GridBagConstraints(0, 4, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));
		panelThird.add(controlprevthird, new GridBagConstraints(0, 6, 3, 1, 0.0, 0.0, GridBagConstraints.ABOVE_BASELINE,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));

		Cardframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		cl.show(panelCont, "1");

		Cardframe.add(panelCont, "Center");
		Cardframe.add(jpb, "Last");
		panelFirst.setVisible(true);
		Cardframe.pack();
		Cardframe.setVisible(true);
	}

	public static void main(String[] args) {

		new ImageJ();
		JFrame frame = new JFrame("");
		PreprocessingFileChooser panel = new PreprocessingFileChooser();

		frame.getContentPane().add(panel, "Center");
		frame.setSize(panel.getPreferredSize());
	}

}

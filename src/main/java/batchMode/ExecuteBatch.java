package batchMode;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import curvatureUtils.Node;
import ellipsoidDetector.Intersectionobject;
import ellipsoidDetector.KymoSaveobject;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.Line;
import ij.gui.OvalRoi;
import ij.gui.Overlay;
import ij.gui.Roi;
import ij.plugin.PlugIn;
import ij.plugin.frame.RoiManager;
import ij.process.ColorProcessor;
import kalmanForSegments.Segmentobject;
import kalmanTracker.TrackModel;
import mpicbg.imglib.util.Util;
import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;
import net.imglib2.view.Views;
import pluginTools.ComputeCurvature;
import pluginTools.InteractiveSimpleEllipseFit;
import pluginTools.InteractiveSimpleEllipseFit.ValueChange;
import ransacPoly.RegressionFunction;
import utility.Curvatureobject;
import utility.DisplayAuto;
import utility.Roiobject;
import utility.ShowResultView;
import utility.Slicer;
import varun_algorithm_ransac_Ransac.Ellipsoid;

public class ExecuteBatch implements PlugIn, Runnable {

	public String batchfolder;
	public String userfile;
	public JProgressBar jpb;
	public final File[] C1_AllImages;
	public final File[] C2_AllImages;
	public final File[] SegmentationImages;
	
	public JLabel label = new JLabel("Progress..");
	
	public JFrame frame = new JFrame();
	public JPanel panel = new JPanel();
	
	public int minNumInliers = LocalPrefs.getInt(".NumberofSegments", 10);
	public int background = LocalPrefs.getInt(".BackgroundLabel.int", 0);

	public int resolution = LocalPrefs.getInt(".Resolution.int", 1);
	public double timecal = LocalPrefs.getDouble(".TimeCalibration", 1);
	public double calibration = LocalPrefs.getDouble(".SpaceCalibration", 1);
    public int boxsize = LocalPrefs.getInt(".Box.int", 11);
	public boolean pixelcelltrackcirclefits = LocalPrefs.getBoolean(".CurvatureViaCircle.boolean", true);
	public boolean distancemethod = LocalPrefs.getBoolean(".CurvatureViaDistance.boolean", false);

	
	public final InteractiveSimpleEllipseFit parent;
	public final String channelA;
	public final String channelB;
	public final String channelSeg;
	public final boolean twochannel;
	

	public float lowprob = 0f;
	public float highprob = 1f;
	public ImagePlus RMStrackImages;
	public String addToName = "EllipseFitsBatchMode";
	public int maxframegap = 10;
	public HashMap<String, ArrayList<Intersectionobject>> sortedMappair = new HashMap<String, ArrayList<Intersectionobject>>();
	
	public ExecuteBatch() {

		this.parent = null;
		this.C1_AllImages = null;
		this.C2_AllImages = null;
		this.channelA = null;
		this.channelB = null;
		twochannel = false;
		this.SegmentationImages = null;
		this.channelSeg = null;
	};

	public ExecuteBatch(final File[] C1_AllImages, final File[] SegmentationImages, final String channelA,
			final String channelSeg, final InteractiveSimpleEllipseFit parent, final File batchdirectory,
			final boolean twochannel) {

		this.C1_AllImages = C1_AllImages;
		this.C2_AllImages = null;
		this.channelA = channelA;
		this.channelB = null;
		this.channelSeg = channelSeg;
		this.parent = parent;
		this.batchfolder = batchdirectory.getParent();
		this.twochannel = twochannel;
		this.SegmentationImages = SegmentationImages;

	}

	public ExecuteBatch(final File[] C1_AllImages, final File[] C2_AllImages, final File[] SegmentationImages,
			final String channelA, final String channelB, final String channelSeg,
			final InteractiveSimpleEllipseFit parent, final File batchdirectory, final boolean twochannel) {

		this.C1_AllImages = C1_AllImages;
		this.C2_AllImages = C2_AllImages;
		this.channelA = channelA;
		this.channelB = channelB;
		this.channelSeg = channelSeg;
		this.parent = parent;
		this.batchfolder = batchdirectory.getParent();
		this.twochannel = twochannel;
		this.SegmentationImages = SegmentationImages;

	}

	public void goTrack() {

		LocalPrefs.setHomeDir(C1_AllImages[0].getParent());
		parent.usefolder = batchfolder;

		LocalPrefs.load(C1_AllImages[0].getParent(), IJ.getApplet());
		
		
		System.out.println("Starting batch mode");
		
		ProgressBatch startbatch = new ProgressBatch(this);
		startbatch.execute();

	}

	@Override
	public void run(String arg) {
		parent.calibration = calibration;
		parent.timecal = timecal;
		parent.resolution = resolution;
		parent.background = background;
		parent.minNumInliers = minNumInliers;
		parent.pixelcelltrackcirclefits = pixelcelltrackcirclefits;
		parent.distancemethod = distancemethod;
		parent.boxsize = boxsize;
	    parent.curvesupermode = true;

	}

	@Override
	public void run() {
		jpb = new JProgressBar();
		goTrack();
	}
	
}

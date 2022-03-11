package pluginTools;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
import java.util.concurrent.ExecutionException;

import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingWorker;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import costMatrix.PixelratiowDistCostFunction;
import curvatureUtils.DisplaySelected;
import ellipsoidDetector.Intersectionobject;
import ij.ImageStack;
import ij.gui.Line;
import kalmanTracker.ETrackCostFunction;
import kalmanTracker.IntersectionobjectCollection;
import kalmanTracker.KFsearch;
import kalmanTracker.NearestNeighbourSearch;
import kalmanTracker.NearestNeighbourSearch2D;
import kalmanTracker.TrackModel;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;
import pluginTools.InteractiveSimpleEllipseFit.ValueChange;
import track.TrackingFunctions;
import utility.CreateTable;
import utility.Curvatureobject;
import utility.Roiobject;
import utility.ThreeDRoiobject;

public class ComputeCurvatureCurrent extends SwingWorker<Void, Void> {

	final InteractiveSimpleEllipseFit parent;
	final JProgressBar jpb;

	public ComputeCurvatureCurrent(final InteractiveSimpleEllipseFit parent, final JProgressBar jpb) {

		this.parent = parent;

		this.jpb = jpb;
	}

	@Override
	protected Void doInBackground() throws Exception {



		EllipseTrack newtrack = new EllipseTrack(parent, jpb);
		newtrack.ComputeCurvatureCurrent();
		
		if (parent.pixellist.size() <= 2) {
			
			//parent.Angleselect.remove(parent.IntegerSegment);
			parent.panelFirst.repaint();
			parent.panelFirst.validate();
		}
		
		System.out.println(parent.pixellist.size());

		return null;

	}



	@Override
	protected void done() {

		parent.jpb.setIndeterminate(false);
		parent.Cardframe.validate();
		
		try {
			get();
		} catch (InterruptedException e) {

		} catch (ExecutionException e) {

		}

        
	}

	
	

}
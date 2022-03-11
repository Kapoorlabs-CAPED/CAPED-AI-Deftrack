package listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import ellipsoidDetector.Intersectionobject;
import ij.IJ;
import kalmanForSegments.Segmentobject;
import net.imglib2.img.display.imagej.ImageJFunctions;
import pluginTools.InteractiveSimpleEllipseFit;

public class CurvatureListener implements ActionListener {

	final InteractiveSimpleEllipseFit parent;

	public CurvatureListener(final InteractiveSimpleEllipseFit parent) {

		this.parent = parent;
	}

	// For curvatrue
	@Override
	public void actionPerformed(ActionEvent e) {

		
		ClearStuff();

		
			parent.StartCurvatureComputing(null);

	}
	
	public  void ClearStuff() {
		
		parent.table.removeAll();
		parent.table.repaint();
		parent.localCurvature.clear();
		parent.AlllocalCurvature.clear();
		parent.KymoFileobject.clear();
		parent.overlay.clear();
		parent.Tracklist.clear();
		if(parent.imp!=null && parent.mvl!=null)
		parent.imp.getCanvas().removeMouseListener(parent.mvl);
		if(parent.imp!=null && parent.ml!=null)
		parent.imp.getCanvas().removeMouseMotionListener(parent.ml);
		parent.starttime = Integer.parseInt(parent.startT.getText());
		parent.endtime = Integer.parseInt(parent.endT.getText());
		parent.resolution = 1; //Integer.parseInt(parent.resolutionField.getText());
		parent.insidedistance =  Integer.parseInt(parent.interiorfield.getText());
		parent.displayCircle.setState(false);
		parent.displaySegments.setState(false);
		parent.displayIntermediate = false;
		parent.displayIntermediateBox = false;
		parent.parentgraph = new SimpleWeightedGraph<Intersectionobject, DefaultWeightedEdge>(
				DefaultWeightedEdge.class);
		parent.parentgraphZ = new HashMap<String, SimpleWeightedGraph<Intersectionobject, DefaultWeightedEdge>>();
		parent.empty = utility.Binarization.CreateBinaryBit(parent.originalimg, parent.lowprob, parent.highprob);
		parent.parentgraphSegZ = new HashMap<String, SimpleWeightedGraph<Segmentobject, DefaultWeightedEdge>>();
		parent.parentdensegraphZ = new HashMap<String, SimpleWeightedGraph<Intersectionobject, DefaultWeightedEdge>>();
		parent.ALLSegments.clear();
		parent.SegmentFinalresult.clear();
		parent.overlay.clear();
		parent.AccountedZ.clear();
		parent.AutostartTime = Integer.parseInt(parent.startT.getText());
		if (parent.AutostartTime <= 0)
			parent.AutostartTime = 1;
		parent.AutoendTime = Integer.parseInt(parent.endT.getText());
		for(int z = parent.AutostartTime; z <= parent.AutoendTime; ++z)
			parent.AccountedZ.put(Integer.toString(z), z);
		
	}

}

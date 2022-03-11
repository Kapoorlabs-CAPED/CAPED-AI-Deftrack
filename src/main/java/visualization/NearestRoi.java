package visualization;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ij.gui.OvalRoi;
import ij.gui.Roi;
import interactivePreprocessing.InteractiveMethods;
import linkers.FlagNode;
import linkers.NNFlagsearchKDtree;
import net.imglib2.KDTree;
import net.imglib2.RealPoint;
import utility.PreRoiobject;

public class NearestRoi {

	
	public static Roi getNearestRois(ArrayList<PreRoiobject> roi, double[] Clickedpoint, final InteractiveMethods parent ) {
		

		
		Roi[] Allrois = new Roi[roi.size()];
		
		for(int index = 0; index < roi.size(); ++index) {
			
			Allrois[index] = roi.get(index).rois;
		}
		
		Roi KDtreeroi = null;

		final List<RealPoint> targetCoords = new ArrayList<RealPoint>(Allrois.length);
		final List<FlagNode<Roi>> targetNodes = new ArrayList<FlagNode<Roi>>(Allrois.length);
		for (int index = 0; index < Allrois.length; ++index) {

			 Roi r = Allrois[index];
			 Rectangle rect = r.getBounds();
			 
			 targetCoords.add( new RealPoint(rect.x + rect.width/2.0, rect.y + rect.height/2.0 ) );
			 

			targetNodes.add(new FlagNode<Roi>(Allrois[index]));

		}

		if (targetNodes.size() > 0 && targetCoords.size() > 0) {

			final KDTree<FlagNode<Roi>> Tree = new KDTree<FlagNode<Roi>>(targetNodes, targetCoords);

			final NNFlagsearchKDtree<Roi> Search = new NNFlagsearchKDtree<Roi>(Tree);


				final double[] source = Clickedpoint;
				final RealPoint sourceCoords = new RealPoint(source);
				Search.search(sourceCoords);
				final FlagNode<Roi> targetNode = Search.getSampler().get();

				KDtreeroi = targetNode.getValue();

		}

		return KDtreeroi;
		
	}
	
	
	
	
	
	
}

package listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

import curvatureUtils.ParallelResultDisplay;
import ellipsoidDetector.Intersectionobject;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;
import pluginTools.Binobject;
import pluginTools.ComputeCurvature;
import pluginTools.InteractiveSimpleEllipseFit;

public class DisplayVisualListener implements ActionListener {

	
	
	final InteractiveSimpleEllipseFit parent;
	final boolean show;
	
	public  DisplayVisualListener(InteractiveSimpleEllipseFit parent, boolean show) {
	      
		this.parent = parent;
		this.show = show;

	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		

		run();

		
		
	}
	
	public  Pair<RandomAccessibleInterval<FloatType>, RandomAccessibleInterval<FloatType>> run() {
		
		Binobject densesortedMappair = ComputeCurvature.GetZTdenseTrackList(parent);
		parent.sortedMappair = densesortedMappair.sortedmap;

	
		String ID = (String) parent.table.getValueAt(parent.row, 0);
		RandomAccessibleInterval<FloatType> Blank = ComputeCurvature.MakeDistanceFan(parent, densesortedMappair.sortedmap, ID, show);

		HashMap<Integer, ArrayList<double[]>> currenthashCurv = new HashMap<Integer, ArrayList<double[]>>();
		for(Pair<String, Intersectionobject> currentCurvature : parent.denseTracklist) {
			

			if (ID.equals(currentCurvature.getA())) {
				
				int time = currentCurvature.getB().z;
				
				ArrayList<double[]> curvature = currentCurvature.getB().linelist;
				
				currenthashCurv.put(time, curvature);
			}
			
		}

	ParallelResultDisplay display = new ParallelResultDisplay(parent, currenthashCurv, show);
	RandomAccessibleInterval<FloatType> probImg = display.ResultDisplayCircleFit();
	
	return new ValuePair<RandomAccessibleInterval<FloatType>, RandomAccessibleInterval<FloatType>>(Blank, probImg);
	
	
	}
	
	
	

}

package listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import ellipsoidDetector.Intersectionobject;
import ij.gui.Line;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;
import pluginTools.InteractiveSimpleEllipseFit;

public class DisplayResults implements ActionListener {

	
	final InteractiveSimpleEllipseFit parent;
	final String ID;
	
	public DisplayResults(final InteractiveSimpleEllipseFit parent, final String ID) {
		
		this.parent = parent;
		this.ID = ID;
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {

		
		for (Pair<String,Intersectionobject> currentangle: parent.Tracklist) {
			
			
			ArrayList<double[]> resultlist = new ArrayList<double[]>();
			if (ID.equals(currentangle.getA())) {
				
				resultlist.add(new double[] {currentangle.getB().t, currentangle.getB().z, currentangle.getB().Intersectionpoint[0], currentangle.getB().Intersectionpoint[1]  });
				parent.resultDraw.put(ID, new ValuePair<ArrayList<double[]>, ArrayList<Line>>(resultlist, currentangle.getB().linerois));
				
				
			}
				
				
		}
		
		
		
		
		
	}

}

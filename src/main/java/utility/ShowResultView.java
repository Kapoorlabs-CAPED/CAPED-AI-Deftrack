package utility;

import ij.IJ;
import pluginTools.InteractiveSimpleEllipseFit;

public class ShowResultView {

	
final InteractiveSimpleEllipseFit parent;
final int time;
final int Z;
	
	public ShowResultView(final InteractiveSimpleEllipseFit parent, final int time, final int Z) {
		
		this.parent = parent;
		this.time = time;
		this.Z = Z;
		
	}
public ShowResultView(final InteractiveSimpleEllipseFit parent, final int Z) {
		
		this.parent = parent;
		this.time = 0;
		this.Z = Z;
		
	}
	
	public void shownew() {

	

			parent.CurrentResultView = utility.Slicer.getCurrentView(parent.originalimg,(int) Z,
					(int)parent.thirdDimensionSize, time,(int) parent.fourthDimensionSize);
			
		

		
	}
	

	
}

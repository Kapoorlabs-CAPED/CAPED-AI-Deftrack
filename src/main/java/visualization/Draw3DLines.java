package visualization;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ij.ImagePlus;
import ij.gui.Line;
import ij.gui.Overlay;
import ij.gui.Roi;
import interactivePreprocessing.InteractiveMethods;
import net.imglib2.Cursor;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;

public class Draw3DLines {

	
	public final InteractiveMethods parent;
	
	public Draw3DLines(final InteractiveMethods parent) {
		
		this.parent = parent;
	}
	public  void DrawLines(HashMap<Integer, ArrayList<double[]>> trackidlist ) {
		
		
		if (parent.imp == null) {
			parent.imp = ImageJFunctions.show(parent.CurrentView);

		}

		else {

			final float[] pixels = (float[]) parent.imp.getProcessor().getPixels();
			final Cursor<FloatType> c = Views.iterable(parent.CurrentView).cursor();

			for (int i = 0; i < pixels.length; ++i)
				pixels[i] = c.next().get();

			parent.imp.updateAndDraw();

		}
		Overlay o = parent.imp.getOverlay();
		
		if(  parent.imp.getOverlay() == null )
		{
			o = new Overlay();
			parent.imp.setOverlay( o ); 
		}


		
		for(Map.Entry<Integer, ArrayList<double[]>> current: trackidlist.entrySet()) {
			
			
			ArrayList<double[]> currenttrack = current.getValue();
			int trackID = current.getKey();
			
			for(int index = 0; index < currenttrack.size() - 1; ++index ) {
			
				double[] XYZ = new double[]{currenttrack.get(index)[0], currenttrack.get(index)[1], currenttrack.get(index)[2]};
				
				int time = (int)currenttrack.get(index)[3];
				
                 double[] XYZnext = new double[]{currenttrack.get(index + 1)[0], currenttrack.get(index + 1)[1], currenttrack.get(index + 1)[2]};
				
				int timenext = (int)currenttrack.get(index + 1)[3];
				
				
				
				if(parent.originalimg.numDimensions() > 3) {
					
					 Line newline = new Line(XYZ[0], XYZ[1], XYZnext[0], XYZnext[1]);
						newline.setStrokeColor(parent.colorTrack);
						newline.setStrokeWidth(0.8);

						o.add(newline);
				}
				
			}
			
			parent.imp.updateAndDraw();
		}
		
		
	}
	
	
}

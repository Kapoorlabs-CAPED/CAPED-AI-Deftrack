package listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import net.imglib2.Cursor;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.view.Views;
import pluginTools.EllipseTrack;
import pluginTools.InteractiveSimpleEllipseFit;
import pluginTools.InteractiveSimpleEllipseFit.ValueChange;
import varun_algorithm_gauss3.Gauss3;

public class DoSmoothingListener implements ActionListener {
	
	
	public final InteractiveSimpleEllipseFit parent;
	
	public DoSmoothingListener(final InteractiveSimpleEllipseFit parent) {
		
		this.parent = parent;
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		
	try {
			
			Gauss3.gauss(parent.gaussradius, Views.extendBorder(parent.originalimg), parent.originalimgsmooth);
			parent.updatePreview(ValueChange.SEG);
			parent.emptysmooth = utility.Binarization.CreateBinaryBit(parent.originalimgsmooth, parent.lowprob, parent.highprob);
			parent.empty = utility.Binarization.CreateBinaryBit(parent.originalimg, parent.lowprob, parent.highprob);
			EllipseTrack newtrack = new EllipseTrack(parent, null);
			newtrack.TestAuto(parent.thirdDimension, parent.fourthDimension);
			
			
		} catch (IncompatibleTypeException es) {

			es.printStackTrace();
		}
		
		
	}
	
}

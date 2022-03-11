package interactivePreprocessing;

import java.awt.Color;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import ij.ImagePlus;
import ij.gui.EllipseRoi;
import ij.gui.Roi;
import ij.process.ColorProcessor;
import interactivePreprocessing.InteractiveMethods;
import interactivePreprocessing.InteractiveMethods.ValueChange;
import mserGUI.CovistoMserPanel;
import net.imglib2.Interval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.componenttree.mser.MserTree;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Intervals;
import net.imglib2.util.Pair;
import net.imglib2.view.Views;
import timeGUI.CovistoTimeselectPanel;
import utility.PreRoiobject;
import zGUI.CovistoZselectPanel;

public class ZMser extends SwingWorker<Void, Void> {

	final InteractiveMethods parent;

	public ZMser(final InteractiveMethods parent) {

		this.parent = parent;

	}

	@Override
	protected Void doInBackground() throws Exception {

		ImagePlus Localimp = ImageJFunctions.show(parent.originalimg);
	//	int count = 0;
	//	int[] totalcount = new int[parent.thirdDimensionSize + 1];
	//	int expectedMT = 61;
		for (int z = CovistoZselectPanel.thirdDimensionsliderInit; z <= CovistoZselectPanel.thirdDimensionSize; ++z) {

			CovistoZselectPanel.thirdDimension = z;

			parent.CurrentView = utility.CovistoSlicer.getCurrentView(parent.originalimg, z, CovistoZselectPanel.thirdDimensionSize,
					CovistoTimeselectPanel.fourthDimension, CovistoTimeselectPanel.fourthDimensionSize);
			parent.updatePreview(ValueChange.THIRDDIM);

			parent.prestack.addSlice(Localimp.getImageStack().getProcessor(z).convertToRGB());
			parent.cp = (ColorProcessor) (parent.prestack.getProcessor(z).duplicate());
			utility.CovsitoProgressBar.CovistoSetProgressBar(parent.jpb, "Computing Component Tree for MSER, Please Wait...");

			if (CovistoMserPanel.darktobright)

				parent.newtree = MserTree.buildMserTree(parent.newimg, CovistoMserPanel.delta, CovistoMserPanel.minSize, CovistoMserPanel.maxSize,
						CovistoMserPanel.Unstability_Score, CovistoMserPanel.minDiversity, true);

			else

				parent.newtree = MserTree.buildMserTree(parent.newimg, CovistoMserPanel.delta, CovistoMserPanel.minSize, CovistoMserPanel.maxSize,
						CovistoMserPanel.Unstability_Score, CovistoMserPanel.minDiversity, false);
			parent.overlay.clear();
			parent.Rois = utility.FinderUtils.getcurrentRois(parent.newtree);

		//	count += (expectedMT - parent.Rois.size());
			
		//	totalcount[z] = expectedMT - parent.Rois.size();
			
			parent.CurrentPreRoiobject = new ArrayList<PreRoiobject>();
			ArrayList<double[]> centerRoi = utility.FinderUtils.getRoiMean(parent.newtree);

			for (int index = 0; index < centerRoi.size(); ++index) {

				Roi or = parent.Rois.get(index);

				or.setStrokeColor(parent.colorDrawMser);
				parent.overlay.add(or);

			}
			for (Roi currentroi : parent.Rois) {

				currentroi.setStrokeColor(Color.red);

				parent.cp.setColor(Color.red);
				parent.cp.setLineWidth(1);
				parent.cp.draw(currentroi);

			}
			parent.imp.setOverlay(parent.overlay);
			parent.imp.updateAndDraw();

			parent.prestack.setPixels(parent.cp.getPixels(), z);
			Localimp.hide();
		}
		
	 //double meanfaliure = count /(parent.thirdDimensionSize );
	 //double stdev = 0;
	 //for (int z = parent.thirdDimensionsliderInit; z <= parent.thirdDimensionSize; ++z) {
		 
	 //	 stdev+= (totalcount[z] - meanfaliure) * (totalcount[z] - meanfaliure);
	// }
	 
	 
    //System.out.println("Count" + " " + count  + "STDEV: " + Math.sqrt(stdev/parent.thirdDimensionSize));
		return null;

	}

	@Override
	protected void done() {

		new ImagePlus("Sim", parent.prestack).show();

		try {
			utility.CovsitoProgressBar.CovistoSetProgressBar(parent.jpb, "Done");
			get();
		} catch (ExecutionException | InterruptedException e) {
			e.printStackTrace();
		}

	}

}

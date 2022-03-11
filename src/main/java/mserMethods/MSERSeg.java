package mserMethods;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

import ij.gui.Roi;
import ij.process.ColorProcessor;
import interactivePreprocessing.InteractiveMethods;
import interactivePreprocessing.InteractiveMethods.ValueChange;
import mserGUI.CovistoMserPanel;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.componenttree.mser.MserTree;
import net.imglib2.type.NativeType;
import net.imglib2.util.Pair;
import timeGUI.CovistoTimeselectPanel;
import utility.PreRoiobject;
import zGUI.CovistoZselectPanel;

public class MSERSeg extends SwingWorker<Void, Void> {

	final InteractiveMethods parent;
	final JProgressBar jpb;

	public MSERSeg(final InteractiveMethods parent, final JProgressBar jpb) {

		this.parent = parent;
		this.jpb = jpb;

	}

	@Override
	protected Void doInBackground() throws Exception {
		if(!parent.snakeongoing) {
		if (CovistoMserPanel.darktobright)

			parent.newtree = MserTree.buildMserTree(parent.newimg, CovistoMserPanel.delta, CovistoMserPanel.minSize, CovistoMserPanel.maxSize,
					CovistoMserPanel.Unstability_Score, CovistoMserPanel.minDiversity, true);

		else

			parent.newtree = MserTree.buildMserTree(parent.newimg, CovistoMserPanel.delta, CovistoMserPanel.minSize, CovistoMserPanel.maxSize,
					CovistoMserPanel.Unstability_Score, CovistoMserPanel.minDiversity, false);
		 parent.overlay.clear();
			parent.Rois = utility.FinderUtils.getcurrentRois(parent.newtree);

			parent.CurrentPreRoiobject = new ArrayList<PreRoiobject>();
			ArrayList<double[]> centerRoi = utility.FinderUtils.getRoiMean(parent.newtree);
			
			
			
			
			for (int index = 0; index < centerRoi.size(); ++index) {

				Roi or = parent.Rois.get(index);

				or.setStrokeColor(parent.colorDrawMser);
				parent.overlay.add(or);
				
			
			}
			for (Roi currentroi: parent.Rois) {
				
				final double[] geocenter = currentroi.getContourCentroid();
				final Pair<Double, Integer> Intensityandpixels = PreRoiobject.getIntensity(currentroi, parent.CurrentView);
				final double intensity = Intensityandpixels.getA();
				final double numberofpixels = Intensityandpixels.getB();
				final double averageintensity = intensity / numberofpixels;
				PreRoiobject currentobject = new PreRoiobject(currentroi, new double[] {geocenter[0], geocenter[1], CovistoZselectPanel.thirdDimension}, 
						numberofpixels, intensity, averageintensity, CovistoZselectPanel.thirdDimension, CovistoTimeselectPanel.fourthDimension);
				parent.CurrentPreRoiobject.add(currentobject);
			}

			for (Map.Entry<String, ArrayList<PreRoiobject>> entry : parent.ZTRois.entrySet()) {

				ArrayList<PreRoiobject> current = entry.getValue();
				for (PreRoiobject currentroi : current) {

					if (currentroi.fourthDimension == CovistoTimeselectPanel.fourthDimension && currentroi.thirdDimension == CovistoZselectPanel.thirdDimension) {

						currentroi.rois.setStrokeColor(parent.colorSnake);
						parent.overlay.add(currentroi.rois);
						
					}

				}
			}
			parent.imp.setOverlay(parent.overlay);
			parent.imp.updateAndDraw();
			
			
			
				utility.CovsitoProgressBar.CovistoSetProgressBar(jpb, "Done");
		}
				if(parent.snakeongoing)
					parent.updatePreview(ValueChange.SNAKE);
		return null;
	}

	@Override
	protected void done() {
		
       
		
		try {
			get();
		} catch (InterruptedException e) {

		} catch (ExecutionException e) {

		}

	}

}

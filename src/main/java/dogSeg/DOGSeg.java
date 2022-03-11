package dogSeg;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

import dogGUI.CovistoDogPanel;
import ij.gui.Roi;
import interactivePreprocessing.InteractiveMethods;
import interactivePreprocessing.InteractiveMethods.ValueChange;
import net.imglib2.algorithm.dog.DogDetection;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Pair;
import net.imglib2.view.Views;
import timeGUI.CovistoTimeselectPanel;
import utility.PreRoiobject;
import zGUI.CovistoZselectPanel;

public class DOGSeg extends SwingWorker<Void, Void> {

	final InteractiveMethods parent;
	final JProgressBar jpb;

	public DOGSeg(final InteractiveMethods parent, final JProgressBar jpb) {

		this.parent = parent;
		this.jpb = jpb;

	}

	@Override
	protected Void doInBackground() throws Exception {
		if (!parent.snakeongoing) {
			final DogDetection.ExtremaType type;
			if (CovistoDogPanel.lookForMaxima)
				type = DogDetection.ExtremaType.MINIMA;
			else
				type = DogDetection.ExtremaType.MAXIMA;
			CovistoDogPanel.sigma2 = utility.ETrackScrollbarUtils.computeSigma2(CovistoDogPanel.sigma, parent.sensitivity);
			final DogDetection<FloatType> newdog = new DogDetection<FloatType>(Views.extendBorder(parent.CurrentView),
					parent.interval, new double[] { 1, 1 }, CovistoDogPanel.sigma, CovistoDogPanel.sigma2, type, CovistoDogPanel.threshold, true);
			parent.overlay.clear();
			parent.peaks = newdog.getSubpixelPeaks();
		}
		return null;
	}

	@Override
	protected void done() {
		if (!parent.snakeongoing) {
			parent.overlay.clear();

			parent.Rois = utility.FinderUtils.getcurrentRois(parent.peaks, CovistoDogPanel.sigma, CovistoDogPanel.sigma2);

			parent.CurrentPreRoiobject = new ArrayList<PreRoiobject>();
			for (int index = 0; index < parent.peaks.size(); ++index) {

				Roi or = parent.Rois.get(index);

				or.setStrokeColor(parent.colorDrawDog);
				parent.overlay.add(or);
			}

			for (Roi currentroi : parent.Rois) {

				final double[] geocenter = currentroi.getContourCentroid();
				final Pair<Double, Integer> Intensityandpixels = PreRoiobject.getIntensity(currentroi,
						parent.CurrentView);
				final double intensity = Intensityandpixels.getA();
				final double numberofpixels = Intensityandpixels.getB();
				final double averageintensity = intensity / numberofpixels;
				PreRoiobject currentobject = new PreRoiobject(currentroi,
						new double[] { geocenter[0], geocenter[1], CovistoZselectPanel.thirdDimension }, numberofpixels, intensity,
						averageintensity, CovistoZselectPanel.thirdDimension, CovistoTimeselectPanel.fourthDimension);
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
		if (parent.snakeongoing)
			parent.updatePreview(ValueChange.SNAKE);
		try {
			get();
		} catch (InterruptedException e) {

		} catch (ExecutionException e) {

		}

	}

}

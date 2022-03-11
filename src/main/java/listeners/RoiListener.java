package listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import ij.gui.Roi;
import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.Type;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.view.Views;
import pluginTools.InteractiveSimpleEllipseFit;
import pluginTools.InteractiveSimpleEllipseFit.ValueChange;
import utility.Roiobject;

public class RoiListener implements ActionListener {

	final InteractiveSimpleEllipseFit parent;

	public RoiListener(final InteractiveSimpleEllipseFit parent) {

		this.parent = parent;
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		parent.updatePreview(ValueChange.ROI);

		RandomAccessibleInterval<BitType> totalimg = new ArrayImgFactory<BitType>()
				.create(new long[] { parent.originalimg.dimension(0), parent.originalimg.dimension(1) }, new BitType());

		Paint(totalimg, parent.uniqueID, (int) parent.thirdDimension, (int) parent.fourthDimension);

		SegmentNew(totalimg, parent.uniqueID, (int) parent.thirdDimension, (int) parent.fourthDimension);

	}

	
	/*
	 * Here we create the Integer image for ground truth segmentation using the rois and putting the intensity as a unique integer value 
	 * for the roi, the fitting is done using the candidate points of the binary image using the regions from the integer image.
	 */
	private void SegmentNew(RandomAccessibleInterval<BitType> current, String id, int z, int t) {

		RandomAccessibleInterval<IntType> totalimg = new ArrayImgFactory<IntType>()
				.create(new long[] { parent.originalimg.dimension(0), parent.originalimg.dimension(1) }, new IntType());
		int regioncount = 1;

		RandomAccess<IntType> intranac = totalimg.randomAccess();

		Roiobject currentobject = parent.ZTRois.get(id);

		Roi[] roilist = currentobject.roilist;

		for (int i = 0; i < roilist.length; ++i) {

			Roi currentroi = roilist[i];

			final float[] xCord = currentroi.getInterpolatedPolygon().xpoints;
			final float[] yCord = currentroi.getInterpolatedPolygon().ypoints;
			int N = xCord.length;

			for (int index = 0; index < N; ++index) {

				int[] newpoint = new int[] { Math.round(xCord[index]), Math.round(yCord[index]) };

				intranac.setPosition(newpoint);
				intranac.get().set(regioncount);
			}

			regioncount++;
		}
		SliceInt(totalimg, (int) parent.thirdDimension, (int) parent.fourthDimension);
	}

	private void SliceInt(RandomAccessibleInterval<IntType> current, int z, int t) {

		final Cursor<IntType> cursor = Views.iterable(current).localizingCursor();
		final RandomAccess<IntType> ranacsec;
		if (parent.originalimg.numDimensions() > 3)
			ranacsec = Views.hyperSlice(Views.hyperSlice(parent.emptyWater, 2, z - 1), 2, t - 1).randomAccess();
		else if (parent.originalimg.numDimensions() > 2 && parent.originalimg.numDimensions() < 4)
			ranacsec = Views.hyperSlice(parent.emptyWater, 2, z - 1).randomAccess();
		else
			ranacsec = parent.emptyWater.randomAccess();
		while (cursor.hasNext()) {

			cursor.fwd();

			ranacsec.setPosition(cursor.getIntPosition(0), 0);
			ranacsec.setPosition(cursor.getIntPosition(1), 1);

			ranacsec.get().set(cursor.get());
		}
	}

	public <T extends Type<T>> void copy(final RandomAccessibleInterval<T> source, final IterableInterval<T> target) {
		// create a cursor that automatically localizes itself on every move
		Cursor<T> targetCursor = target.localizingCursor();
		RandomAccess<T> sourceRandomAccess = source.randomAccess();

		// iterate over the input cursor
		while (targetCursor.hasNext()) {
			// move input cursor forward
			targetCursor.fwd();

			// set the output cursor to the position of the input cursor
			sourceRandomAccess.setPosition(targetCursor);

			// set the value of this pixel of the output image, every Type supports T.set( T
			// type )
			targetCursor.get().set(sourceRandomAccess.get());
		}
	}

	private void Slice(RandomAccessibleInterval<BitType> current, ArrayList<int[]> pointlist, int z, int t) {

		final RandomAccess<BitType> ranac = current.randomAccess();
		for (int[] point : pointlist) {

			ranac.setPosition(point);

			ranac.get().setOne();

		}

		final Cursor<BitType> cursor = Views.iterable(current).localizingCursor();
		final RandomAccess<BitType> ranacsec;
		if (parent.originalimg.numDimensions() > 3)
			ranacsec = Views.hyperSlice(Views.hyperSlice(parent.empty, 2, z - 1), 2, t - 1).randomAccess();
		else if (parent.originalimg.numDimensions() > 2 && parent.originalimg.numDimensions() < 4)
			ranacsec = Views.hyperSlice(parent.empty, 2, z - 1).randomAccess();
		else
			ranacsec = parent.empty.randomAccess();
		while (cursor.hasNext()) {

			cursor.fwd();

			ranacsec.setPosition(cursor.getIntPosition(0), 0);
			ranacsec.setPosition(cursor.getIntPosition(1), 1);

			ranacsec.get().set(cursor.get());
		}
	}

	private void Paint(RandomAccessibleInterval<BitType> current, String id, int z, int t) {

		Roiobject currentobject = parent.ZTRois.get(id);

		if (currentobject != null) {
			ArrayList<int[]> pointlist = new ArrayList<int[]>();

			Roi[] roilist = currentobject.roilist;

			for (int i = 0; i < roilist.length; ++i) {

				Roi currentroi = roilist[i];

				final float[] xCord = currentroi.getInterpolatedPolygon().xpoints;
				final float[] yCord = currentroi.getInterpolatedPolygon().ypoints;

				int N = xCord.length;

				for (int index = 0; index < N; ++index) {

					pointlist.add(new int[] { Math.round(xCord[index]), Math.round(yCord[index]), z, t });
				}

			}
			Slice(current, pointlist, z, t);
		}

	}
}

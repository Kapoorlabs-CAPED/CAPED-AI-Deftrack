package distanceTransform;

import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.ImgFactory;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;
import preProcessing.GlobalThresholding;

public class CreateBinary {

	public static RandomAccessibleInterval<BitType> CreateBinaryImage(RandomAccessibleInterval<FloatType> inputimage,
			final FloatType threshold) {

		final ImgFactory<BitType> factory = net.imglib2.util.Util.getArrayOrCellImgFactory(inputimage, new BitType());
		RandomAccessibleInterval<BitType> binaryimage = factory.create(inputimage, new BitType());

		Cursor<FloatType> cursor = Views.iterable(inputimage).localizingCursor();
		RandomAccess<BitType> ranac = binaryimage.randomAccess();

		while (cursor.hasNext()) {

			cursor.fwd();

			ranac.setPosition(cursor);

			if (cursor.get().compareTo(threshold) >= 1)
				ranac.get().setOne();
			else
				ranac.get().setZero();

		}

		return binaryimage;
	}

	public static RandomAccessibleInterval<BitType> CreateAutoBinaryImage(RandomAccessibleInterval<FloatType> inputimage) {

		final ImgFactory<BitType> factory = net.imglib2.util.Util.getArrayOrCellImgFactory(inputimage, new BitType());
		RandomAccessibleInterval<BitType> binaryimage = factory.create(inputimage, new BitType());

		Cursor<FloatType> cursor = Views.iterable(inputimage).localizingCursor();
		RandomAccess<BitType> ranac = binaryimage.randomAccess();

		Float threshold = GlobalThresholding.AutomaticThresholding(inputimage);

		while (cursor.hasNext()) {

			cursor.fwd();

			ranac.setPosition(cursor);

			if (cursor.get().get() >= (threshold))
				ranac.get().setOne();
			else
				ranac.get().setZero();

		}

		return binaryimage;
	}

}

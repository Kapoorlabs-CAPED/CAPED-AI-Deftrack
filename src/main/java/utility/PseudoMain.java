package utility;

import ij.ImageJ;
import io.scif.img.ImgIOException;
import io.scif.img.ImgOpener;
import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;

public class PseudoMain {

	
	public static RandomAccessibleInterval<FloatType> CreateBinary(RandomAccessibleInterval<FloatType> source, double lowprob,
			double highprob) {

		RandomAccessibleInterval<FloatType> copyoriginal = new ArrayImgFactory<FloatType>().create(source,
				new FloatType());

		final RandomAccess<FloatType> ranac = copyoriginal.randomAccess();
		final Cursor<FloatType> cursor = Views.iterable(source).localizingCursor();

		while (cursor.hasNext()) {

			cursor.fwd();

			ranac.setPosition(cursor);
			if (cursor.get().getRealDouble() > lowprob && cursor.get().getRealDouble() < highprob) {

				ranac.get().set(cursor.get());
			} else {
				ranac.get().set(0);
			}

		}

		return copyoriginal;

	}
	
	
	public static void main(String args[]) throws ImgIOException {
		
		new ImageJ();
		String directory = "/Users/aimachine/Documents/IlastikJLM/datasets_for_ilastic_training/Stage3Training/ThresholdData/RawData/";
		String fileName = "20171027_stage3_1-normalized_Probabilitiesexported_data.tif";
		org.apache.log4j.BasicConfigurator.configure();
		RandomAccessibleInterval<FloatType> image = new ImgOpener().openImgs(directory + fileName , new FloatType()).iterator().next();
		
		double lowprob = 0.65;
		double highprob = 1;
		ImageJFunctions.show(image).setTitle("Original");
		ImageJFunctions.show(CreateBinary(image, lowprob, highprob)).setTitle("Mapped");
		
	}
	
	
}

package utility;

import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;

public class Binarization {
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
	
	 public static RandomAccessibleInterval<BitType> CreateBinaryBit(RandomAccessibleInterval<FloatType> source, double lowprob, double highprob) {
			
			
			RandomAccessibleInterval<BitType> copyoriginal = new ArrayImgFactory<BitType>().create(source, new BitType());
			

			
			final RandomAccess<BitType> ranac =  copyoriginal.randomAccess();
			final Cursor<FloatType> cursor = Views.iterable(source).localizingCursor();
			
			while(cursor.hasNext()) {
				
				cursor.fwd();
				
				ranac.setPosition(cursor);
				if(cursor.get().getRealDouble() > lowprob && cursor.get().getRealDouble() < highprob) {
					
					ranac.get().setOne();
				}
				else {
					ranac.get().setZero();
				}
				
				
			}
			
			
	  
			
			
			return copyoriginal;
			
		}
}

package ellipsoidDetector;

import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;

public class PixelCounter {

	
	public static int CountBrightPixels(RandomAccessibleInterval<FloatType> inputimage, int[] XYcordinates, int Zlocation, float threshold) {
		
		
		RandomAccess<FloatType> ranac = inputimage.randomAccess();
		
	
		int count = 0;
		for (int i = 0; i < XYcordinates.length; ++i) {
			
			
			int[] location = new int[] {XYcordinates[0], XYcordinates[1]};
			ranac.setPosition(location);
			if (ranac.get().get() >= threshold)
			count++;
		}
		
		
		
		return count;
		
		
		
		
	}
	
}

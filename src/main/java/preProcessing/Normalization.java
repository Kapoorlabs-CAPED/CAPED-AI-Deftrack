/*-
 * #%L
 * Microtubule tracker.
 * %%
 * Copyright (C) 2017 MTrack developers.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */
package preProcessing;

import ij.ImageJ;
import ij.ImagePlus;
import ij.io.FileSaver;
import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.stats.Normalize;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;

public class Normalization {

	
	
	public static void main(String[] args){
		
		
		new ImageJ();

		String filepath = "/Users/aimachine/Documents/Tea_Data/Test.tif";
		ImagePlus impA = new ImagePlus( filepath );
		RandomAccessibleInterval<FloatType> img = ImageJFunctions.convertFloat(impA);
		new Normalize();
		FloatType minval = new FloatType(0);
		FloatType maxval = new FloatType(1);
		
		int boundarylabel = 1;
		int outterlabel = 2;
		int innerlabel = 3;
		int backgroundlabel = 4;
		
		// Change the label here for creating a lebelled image
		int label = backgroundlabel;
		
		Normalize.normalize(Views.iterable(img), minval, maxval); 
		
		// Make Labelled image
		
		
		RandomAccessibleInterval<IntType> Intimg = new ArrayImgFactory<IntType>().create(img,
				new IntType());
		
		RandomAccess<IntType> intran = Intimg.randomAccess();
		Cursor<FloatType> cursor = Views.iterable(img).localizingCursor();
		
		while(cursor.hasNext()){

			cursor.next();
			
			intran.setPosition(cursor);
			
			if (cursor.get().get() > 0) {
				
				intran.get().set(outterlabel);
			}
				
			
		
		}		
		
		
		ImageJFunctions.show(Intimg);
	
		
	}
	
}

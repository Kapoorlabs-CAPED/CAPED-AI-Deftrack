package curvatureUtils;

import java.util.ArrayList;

import net.imglib2.Cursor;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;

public class ProcessSliceDisplayCircleFit implements Runnable {

	
	final RandomAccessibleInterval<FloatType> OutputSlice;

    final ArrayList<double[]> TimeCurveList;
	
	public ProcessSliceDisplayCircleFit(final RandomAccessibleInterval<FloatType> OutputSlice,  ArrayList<double[]> TimeCurveList ) {
		
		
		this.OutputSlice = OutputSlice;
		this.TimeCurveList = TimeCurveList;

		
	}
	
	
	@Override
	public void run() {
	
		double[] X = new double[TimeCurveList.size()];
		double[] Y = new double[TimeCurveList.size()];
		double[] Curvature = new double[TimeCurveList.size()];
		double[] Intensity = new double[TimeCurveList.size()];
		double[] IntensitySec = new double[TimeCurveList.size()];
	
		for (int index = 0; index < TimeCurveList.size(); ++index) {
			
			
			X[index] = TimeCurveList.get(index)[0];
			Y[index] = TimeCurveList.get(index)[1];
			Curvature[index] = TimeCurveList.get(index)[2];
			Intensity[index] = TimeCurveList.get(index)[3];
			IntensitySec[index] = TimeCurveList.get(index)[4];
			final Cursor<FloatType> cursor = Views.iterable(OutputSlice).localizingCursor();
			while (cursor.hasNext()) {

				cursor.fwd();

			

					if ((Math.abs(cursor.getFloatPosition(0) - X[index])) <= 1
							&& (Math.abs(cursor.getFloatPosition(1) - Y[index])) <= 1) {

						cursor.get().setReal((Curvature[index]));

					}
					
				

				

			}			

			
		}
		
	}
	
	
	public  double GetMax() {
		
		double MAX = Double.MIN_VALUE;
		
		double[] Curvature = new double[TimeCurveList.size()];
		
		
		
		for (int index = 0; index < TimeCurveList.size(); ++index) {
			
			Curvature[index] = TimeCurveList.get(index)[2];
			
			if(Curvature[index] >= MAX) {
				
				MAX = Curvature[index];
				
			}
			
		}
		
		
		return MAX;
		
	}

}

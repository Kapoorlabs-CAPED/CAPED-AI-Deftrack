package hashMapSorter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SortCoordinates {

	
	public static void sortByXY(ArrayList<double[]> inputlist) {
		
		
	Collections.sort(inputlist, new Comparator<double[]> () {

		@Override
		public int compare(double[] o1, double[] o2) {
			int n = o1.length;
			int i = 0;
			while ( i < n )
			{
				if ( o1[i] != o2[i] ) { return ( int ) Math.signum( o1[i] - o2[i]  ); }
				i++;
			
		}
			return o1.hashCode() - o2.hashCode();
	
		}
	});

		
		
	}
	
	
}

package ransacPoly;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import net.imglib2.util.Pair;
import ransac.PointFunctionMatch.PointFunctionMatch;

public class Sort {

	
	
	public static  void sort(final Pair<? extends AbstractFunction2D, ArrayList<PointFunctionMatch>> segment) {
		Collections.sort(segment.getB(), new Comparator<PointFunctionMatch>() {

			@Override
			public int compare(final PointFunctionMatch o1, final PointFunctionMatch o2) {
				final double t1 = o1.getP1().getL()[0];
				final double t2 = o2.getP1().getL()[0];

				if (t1 < t2)
					return -1;
				else if (t1 == t2)
					return 0;
				else
					return 1;
			}
		});
	}
	
}

package angleUtils;

import java.awt.Color;
import java.util.ArrayList;

import net.imglib2.util.Pair;
import pluginTools.InteractiveSimpleEllipseFit;
import pluginTools.InteractiveSimpleEllipseFit.ValueChange;

public class TableDisplay {


	public static void displayclicked(InteractiveSimpleEllipseFit parent, int trackindex) {

		System.out.println("This is for angle");
		// Make something happen
		parent.row = trackindex;
		String ID =  (String) parent.table.getValueAt(trackindex, 0);
		ArrayList<Pair<String, double[]>> currentresultAngle = new ArrayList<Pair<String, double[]>>();
		for (Pair<String, double[]> currentangle : parent.resultAngle) {

			if (ID.equals(currentangle.getA())) {

				currentresultAngle.add(currentangle);

			}

		}

		parent.dataset.removeAllSeries();

		parent.dataset.addSeries(utility.ChartMaker.drawPoints(currentresultAngle));
		utility.ChartMaker.setColor(parent.chart, 0, new Color(255, 64, 64));
		utility.ChartMaker.setStroke(parent.chart, 0, 2f);
		parent.updatePreview(ValueChange.RESULT);

	}
	
	
}

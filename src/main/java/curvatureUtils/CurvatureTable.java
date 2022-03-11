package curvatureUtils;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JScrollPane;
import javax.swing.JTable;

import ellipsoidDetector.Intersectionobject;
import kalmanForSegments.Segmentobject;
import net.imglib2.RealLocalizable;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;
import pluginTools.Binobject;
import pluginTools.ComputeCurvature;
import pluginTools.InteractiveSimpleEllipseFit;
import utility.Curvatureobject;

public class CurvatureTable {
	public static void CreateTableTrackView(final InteractiveSimpleEllipseFit parent) {

		parent.resultAngle = new ArrayList<Pair<String, double[]>>();
		parent.resultCurvature = new ArrayList<Pair<String, Pair<Integer, ArrayList<double[]>>>>();
		for (Pair<String, Intersectionobject> currentangle : parent.denseTracklist) {
			if (parent.originalimg.numDimensions() > 3) {
				parent.resultAngle.add(new ValuePair<String, double[]>(currentangle.getA(),
						new double[] { currentangle.getB().t, currentangle.getB().perimeter }));

				Pair<Integer, ArrayList<double[]>> timelist = new ValuePair<Integer, ArrayList<double[]>>(
						currentangle.getB().t, currentangle.getB().linelist);
				parent.resultCurvature
						.add(new ValuePair<String, Pair<Integer, ArrayList<double[]>>>(currentangle.getA(), timelist));
				parent.HashresultCurvature.put(currentangle.getB().t, currentangle.getB().linelist);

			} else {
				parent.resultAngle.add(new ValuePair<String, double[]>(currentangle.getA(),
						new double[] { currentangle.getB().z, currentangle.getB().perimeter }));
				Pair<Integer, ArrayList<double[]>> timelist = new ValuePair<Integer, ArrayList<double[]>>(
						currentangle.getB().z, currentangle.getB().linelist);
				parent.resultCurvature
						.add(new ValuePair<String, Pair<Integer, ArrayList<double[]>>>(currentangle.getA(), timelist));
				parent.HashresultCurvature.put(currentangle.getB().z, currentangle.getB().linelist);
			}

		}
		Object[] colnames = new Object[] { "Track Id", "Location X", "Location Y", "Location Z/T", "Perimeter" };

		Object[][] rowvalues = new Object[0][colnames.length];

		rowvalues = new Object[parent.Finalresult.size()][colnames.length];

		parent.table = new JTable(rowvalues, colnames);
		parent.row = 0;
		NumberFormat f = NumberFormat.getInstance();
		for (Map.Entry<String, Intersectionobject> entry : parent.Finalresult.entrySet()) {

			Intersectionobject currentangle = entry.getValue();
			parent.table.getModel().setValueAt(entry.getKey(), parent.row, 0);
			parent.table.getModel().setValueAt(f.format(currentangle.Intersectionpoint[0]), parent.row, 1);
			parent.table.getModel().setValueAt(f.format(currentangle.Intersectionpoint[1]), parent.row, 2);
			parent.table.getModel().setValueAt(f.format(currentangle.z), parent.row, 3);
			parent.table.getModel().setValueAt(f.format(currentangle.perimeter), parent.row, 4);

			parent.row++;
			
			parent.tablesize = parent.row;
		}

		makeGUI(parent);
	}

	//String

	public static void CreateTableView(final InteractiveSimpleEllipseFit parent) {

		Object[] colnames = new Object[] { "Cell Id", "Location X", "Location Y", "Location Z", "Perimeter",
				"Curvature" };

		Object[][] rowvalues = new Object[0][colnames.length];

		rowvalues = new Object[parent.Finalcurvatureresult.size()][colnames.length];

		parent.table = new JTable(rowvalues, colnames);
		parent.row = 0;
		NumberFormat f = NumberFormat.getInstance();
		for (Map.Entry<Integer, Curvatureobject> entry : parent.Finalcurvatureresult.entrySet()) {

			Curvatureobject currentcurvature = entry.getValue();
			parent.table.getModel().setValueAt(entry.getKey(), parent.row, 0);
			parent.table.getModel().setValueAt(f.format(currentcurvature.cord[0]), parent.row, 1);
			parent.table.getModel().setValueAt(f.format(currentcurvature.cord[1]), parent.row, 2);
			parent.table.getModel().setValueAt(f.format(currentcurvature.z), parent.row, 3);
			parent.table.getModel().setValueAt(f.format(currentcurvature.perimeter), parent.row, 4);
			parent.table.getModel().setValueAt(f.format(currentcurvature.radiusCurvature), parent.row, 5);
			parent.row++;

			parent.tablesize = parent.row;
			
			
			
		}

		makeGUI(parent);

	}

	public static void makeGUI(final InteractiveSimpleEllipseFit parent) {

        parent.PanelSelectFile.removeAll();
		
		parent.table.setFillsViewportHeight(true);

		parent.table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		parent.scrollPane = new JScrollPane(parent.table);

		parent.scrollPane.getViewport().add(parent.table);
		parent.scrollPane.setAutoscrolls(true);
		parent.PanelSelectFile.add(parent.scrollPane, BorderLayout.CENTER);

		parent.PanelSelectFile.setBorder(parent.selectfile);


		int size = 100;
		parent.table.getColumnModel().getColumn(0).setPreferredWidth(size);
		parent.table.getColumnModel().getColumn(1).setPreferredWidth(size);
		parent.table.getColumnModel().getColumn(2).setPreferredWidth(size);
		parent.table.getColumnModel().getColumn(3).setPreferredWidth(size);
		parent.table.getColumnModel().getColumn(4).setPreferredWidth(size);
		parent.table.setPreferredScrollableViewportSize(parent.table.getPreferredSize());
		
		parent.table.setMinimumSize(parent.table.getPreferredSize());

		
		parent.scrollPane.setMinimumSize(parent.table.getPreferredSize());
		
		
		parent.table.setFillsViewportHeight(true);
		parent.inputField.setEnabled(true);
		parent.inputtrackField.setEnabled(true);
		parent.Savebutton.setEnabled(true);
		parent.SaveAllbutton.setEnabled(true);
		parent.ChooseDirectory.setEnabled(true);
		parent.Original.repaint();
		parent.Original.validate();
		parent.PanelSelectFile.repaint();
		parent.PanelSelectFile.validate();
		parent.table.repaint();
		parent.table.validate();
		
		parent.panelFirst.add(parent.PanelSelectFile, new GridBagConstraints(0, 2, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL,parent.insets, 0, 0));
		parent.panelFirst.repaint();
		parent.panelFirst.validate();
		parent.Cardframe.pack();
		System.out.println(parent.table.getRowCount());
              for(int tablepos = 0; tablepos< parent.table.getRowCount(); ++tablepos) {
			
			String StringID = (String) parent.table.getValueAt(tablepos, 0);

			int ID = Integer.parseInt(StringID);
			ArrayList<Pair<String, double[]>> currentresultPeri = new ArrayList<Pair<String, double[]>>();
			

			for (Pair<String, double[]> currentperi : parent.resultAngle) {

				if (StringID.equals(currentperi.getA())) {

					currentresultPeri.add(currentperi);

				}

			}

		

			if (parent.imp != null) {
				parent.imp.setOverlay(parent.overlay);
				parent.imp.updateAndDraw();
			}
			Binobject densesortedMappair = ComputeCurvature.GetZTdenseTrackList(parent);
			parent.sortedMappair = densesortedMappair.sortedmap;
			int TimedimensionKymo = parent.AccountedZ.size();

		

			// For dense plot
			HashMap<String, Integer> denseidmap = densesortedMappair.maxid;

			
			int Xkymodimension = denseidmap.get(StringID);
		

				long[] imsize = new long[] { TimedimensionKymo, Xkymodimension + 1 };
				
				ComputeCurvature.CreateInterKymo( parent,densesortedMappair.sortedmap, imsize, StringID);
			parent.tablesize = parent.row;
		}

	

	}

}
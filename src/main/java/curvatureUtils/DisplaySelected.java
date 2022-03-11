package curvatureUtils;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;

import ellipsoidDetector.Distance;
import ij.IJ;
import ij.gui.ImageCanvas;
import ij.gui.Line;
import ij.gui.Roi;
import kalmanTracker.NearestRoi;
import pluginTools.InteractiveSimpleEllipseFit;
import utility.Curvatureobject;
import utility.Roiobject;

public class DisplaySelected {



	public static void selectAll(final InteractiveSimpleEllipseFit parent) {

	

		if (parent.mvl != null)
			parent.imp.getCanvas().removeMouseListener(parent.mvl);
		parent.imp.getCanvas().addMouseListener(parent.mvl = new MouseListener() {

			final ImageCanvas canvas = parent.imp.getWindow().getCanvas();

			@Override
			public void mouseClicked(MouseEvent e) {

			

			}

			@Override
			public void mousePressed(MouseEvent e) {
				
			}

			@Override
			public void mouseReleased(MouseEvent e) {

				int x = canvas.offScreenX(e.getX());
				int y = canvas.offScreenY(e.getY());
				parent.Clickedpoints[0] = x;
				parent.Clickedpoints[1] = y;

				if (SwingUtilities.isLeftMouseButton(e) && e.isShiftDown()) {
					
					
					CurvatureTableDisplay.displayclicked(parent, parent.rowchoice);
					
					

//					if (!parent.jFreeChartFrame.isVisible())
//						parent.jFreeChartFrame = utility.ChartMaker.display(parent.chart, new Dimension(500, 500));

				}
				
			}

			@Override
			public void mouseEntered(MouseEvent e) {

			}

			@Override
			public void mouseExited(MouseEvent e) {

			}
		});

	}

	public static void markAll(final InteractiveSimpleEllipseFit parent) {

		parent.rowchoice = 0;
		parent.row =0;
		if (parent.ml != null)
			parent.imp.getCanvas().removeMouseMotionListener(parent.ml);
		parent.imp.getCanvas().addMouseMotionListener(parent.ml = new MouseMotionListener() {

			final ImageCanvas canvas = parent.imp.getWindow().getCanvas();

			@Override
			public void mouseMoved(MouseEvent e) {

				int x = canvas.offScreenX(e.getX());
				int y = canvas.offScreenY(e.getY());

				final HashMap<Integer, double[]> loc = new HashMap<Integer, double[]>();

				loc.put(0, new double[] { x, y });

				double distmin = Double.MAX_VALUE;
				if (parent.tablesize > 0 && parent.table.getRowCount() > 0) {
					NumberFormat f = NumberFormat.getInstance();
					
					
					for (int row = 0; row < parent.tablesize; ++row) {
						String CordX = (String) parent.table.getValueAt(row, 1);
						String CordY = (String) parent.table.getValueAt(row, 2);

						String CordZ = (String) parent.table.getValueAt(row, 3);

						double dCordX = 0, dCordZ = 0, dCordY = 0;
						try {
							dCordX = f.parse(CordX).doubleValue();

							dCordY = f.parse(CordY).doubleValue();
							dCordZ = f.parse(CordZ).doubleValue();
						} catch (ParseException e1) {

						}
						double dist = Distance.DistanceSq(new double[] { dCordX, dCordY }, new double[] { x, y });
						if (Distance.DistanceSq(new double[] { dCordX, dCordY }, new double[] { x, y }) < distmin
								&& parent.thirdDimension == (int) dCordZ && parent.ndims > 3) {

							parent.rowchoice = row;
							parent.row = row;
							distmin = dist;

						}
						if (Distance.DistanceSq(new double[] { dCordX, dCordY }, new double[] { x, y }) < distmin
								&& parent.ndims <= 3) {

							parent.rowchoice = row;
							parent.row = row;
							distmin = dist;

						}

					}

					parent.table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
						@Override
						public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
								boolean hasFocus, int row, int col) {

							super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
							if (row == parent.rowchoice) {
								setBackground(Color.green);

							} else {
								setBackground(Color.white);
							}
							return this;
						}
					});

					parent.table.validate();
					parent.scrollPane.validate();
					parent.panelFirst.repaint();
					parent.panelFirst.validate();

				}

			}

			@Override
			public void mouseDragged(MouseEvent e) {

			}

		});

	}

	
	public static void HighlightNearest(final InteractiveSimpleEllipseFit parent, double[] Clickedpoint) {
		
		String uniqueID = Integer.toString(parent.thirdDimension) + Integer.toString(parent.fourthDimension);
		
		Roiobject currentobject = parent.ZTRois.get(uniqueID);
		if(currentobject.segmentrect!=null) {
			
			Roi nearestRoi = NearestRoi.getNearestSegmentRois(currentobject, Clickedpoint, parent);
			
			for (Roi otherRois: currentobject.segmentrect) {
				if(otherRois!=nearestRoi)
					otherRois.setStrokeColor(parent.defaultRois);
				else
					otherRois.setStrokeColor(Color.PINK);
				
			}
				
			
			
			
			parent.overlay.add(nearestRoi);
			parent.imp.updateAndDraw();
			
		}
		
		
	}
	
	
	
}

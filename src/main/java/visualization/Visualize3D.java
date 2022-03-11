package visualization;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;

import ij.gui.ImageCanvas;
import ij.gui.Roi;
import interactivePreprocessing.InteractiveMethods;
import interactivePreprocessing.InteractiveMethods.ValueChange;
import utility.PreRoiobject;
import utility.ThreeDRoiobject;
import zGUI.CovistoZselectPanel;

public class Visualize3D {

	final InteractiveMethods parent;
	
	public Visualize3D(final InteractiveMethods parent) {
		
		this.parent = parent;
		
	}
	
	
	public void CreateTable() {
		
	
		
		parent.row = 0;
		NumberFormat f = NumberFormat.getInstance();

	

		parent.panelThird.remove(parent.PanelSelectFile);
		parent.PanelSelectFile.removeAll();
		parent.panelThird.remove(parent.controlprevthird);
		
		parent.PanelSelectFile.repaint();
		parent.PanelSelectFile.validate();
        parent.panelThird.repaint();
        parent.panelThird.validate();
		
		for (Map.Entry<Integer, ArrayList<ThreeDRoiobject>> entry: parent.Timetracks.entrySet()) {
			
			ArrayList<ThreeDRoiobject> currententry = entry.getValue();
			parent.table.getModel().setValueAt(entry.getKey(), parent.row, 0);
			parent.table.getModel().setValueAt(f.format(currententry.get(currententry.size() - 1).geometriccenter[0]), parent.row, 1);
			parent.table.getModel().setValueAt(f.format(currententry.get(currententry.size() - 1).geometriccenter[1]), parent.row, 2);
			parent.table.getModel().setValueAt(f.format(currententry.get(currententry.size() - 1).geometriccenter[2]), parent.row, 3);
			parent.table.getModel().setValueAt(f.format(currententry.get(currententry.size() - 1).totalintensity), parent.row, 4);
			parent.table.getModel().setValueAt(f.format(currententry.get(currententry.size() - 1).averageintensity), parent.row, 5);
			parent.row++;
			
			
			parent.tablesize = parent.row;
			
		}


		parent.PanelSelectFile.add(parent.scrollPane, BorderLayout.CENTER);

		Border trackborder = new CompoundBorder(new TitledBorder("Select track"), new EmptyBorder(parent.c.insets));
		parent.PanelSelectFile.setBorder(trackborder);
		
		
		parent.panelThird.add(parent.PanelSelectFile, new GridBagConstraints(0, 4, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.HORIZONTAL, parent.insets, 0, 0));
		parent.panelThird.add(parent.controlprevthird, new GridBagConstraints(0, 6, 3, 1, 0.0, 0.0, GridBagConstraints.ABOVE_BASELINE,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
		
		
		
		
		
		parent.controlnextthird.setEnabled(true);
		
		parent.panelThird.validate();
		parent.panelThird.repaint();
		
	}
	
	public void set() {
		

		if (parent.mvl != null)
			parent.imp.getCanvas().removeMouseListener(parent.mvl);
		parent.imp.getCanvas().addMouseListener(parent.mvl = new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {

				if (SwingUtilities.isLeftMouseButton(e) && e.isShiftDown()) {
					
					
					
					
					
					
				}
		

			}

			@Override
			public void mousePressed(MouseEvent e) {

			}

			@Override
			public void mouseReleased(MouseEvent e) {

			}

			@Override
			public void mouseEntered(MouseEvent e) {

			}

			@Override
			public void mouseExited(MouseEvent e) {

			}
		});

		
		
	}
	
	public void mark() {
		
		if (parent.ml != null)
			parent.imp.getCanvas().removeMouseMotionListener(parent.ml);
		
		parent.imp.getCanvas().addMouseMotionListener(parent.ml = new MouseMotionListener() {

			final ImageCanvas canvas = parent.imp.getWindow().getCanvas();
			Roi lastnearest = null;

			@Override
			public void mouseMoved(MouseEvent e) {
				
				int x = canvas.offScreenX(e.getX());
				int y = canvas.offScreenY(e.getY());

				final HashMap<Integer, double[]> loc = new HashMap<Integer, double[]>();

				loc.put(0, new double[] { x, y });

				Color roicolor;
				ArrayList<PreRoiobject> currentobject;
				
					roicolor = parent.colorSnake;

					currentobject = parent.ZTRois.get(parent.uniqueID);

				
				parent.nearestRoiCurr = NearestRoi.getNearestRois(currentobject, loc.get(0), parent);

				if (parent.nearestRoiCurr != null) {
					parent.nearestRoiCurr.setStrokeColor(parent.colorConfirm);

					if (lastnearest != parent.nearestRoiCurr && lastnearest != null)
						lastnearest.setStrokeColor(roicolor);

					lastnearest = parent.nearestRoiCurr;

					parent.imp.updateAndDraw();
				}

				double distmin = Double.MAX_VALUE;
				if (parent.tablesize > 0) {
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
						double dist = DistanceSq(new double[] { dCordX, dCordY }, new double[] { x, y });
						if (DistanceSq(new double[] { dCordX, dCordY }, new double[] { x, y }) < distmin
								&& CovistoZselectPanel.thirdDimension == (int) dCordZ && parent.ndims > 3) {

							parent.rowchoice = row;
							distmin = dist;

						}
						if (DistanceSq(new double[] { dCordX, dCordY }, new double[] { x, y }) < distmin
								 && parent.ndims <= 3) {

							parent.rowchoice = row;
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
					parent.scrollPane.getVerticalScrollBar().setValue(parent.table.getY());
					parent.scrollPane.getHorizontalScrollBar().setValue(parent.table.getX());
					parent.table.validate();
					parent.scrollPane.validate();
					parent.panelThird.repaint();
					parent.panelThird.validate();

				}

			}

			@Override
			public void mouseDragged(MouseEvent e) {

			}

		});
	}
	
	public static double DistanceSq(final double[] pointA, final double[] pointB) {

		double distance = 0;
		int numDim = pointA.length;

		for (int d = 0; d < numDim; ++d) {

			distance += (pointA[d] - pointB[d])
					* (pointA[d] - pointB[d]);

		}
		return distance;
	}
	
}

package batchMode;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JFrame;

import curvatureUtils.CurvatureTableDisplay;
import ellipsoidDetector.Intersectionobject;
import ellipsoidDetector.KymoSaveobject;
import ij.IJ;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Pair;
import net.imglib2.view.Views;
import pluginTools.InteractiveSimpleEllipseFit;

public class BatchKymoSave {

	

	public static void KymoSave(InteractiveSimpleEllipseFit parent, File savefile, JFrame frame) {
		
		int XcordLabel = 0;
		int YcordLabel = 1;
		int CurvatureLabel = 2;
		int IntensityALabel = 3;
		int IntensityBLabel = 4;
		int perimeterLabel = 5;

		for(int tablepos = 0; tablepos< parent.table.getRowCount(); ++tablepos) {
			
			String ID = (String) parent.table.getValueAt(tablepos, 0);
			parent.saveFile = savefile;
			try {
				File fichier = new File(
						savefile  + "//" + "Co-ordinates" + parent.addToName +  parent.inputstring.replaceFirst("[.][^.]+$", "") +  "CellID" +ID + ".txt");

				FileWriter fw = new FileWriter(fichier);
				BufferedWriter bw = new BufferedWriter(fw);
				bw.write("\tTrackID: " + "\t"  + ID+ "\n");
				bw.write("\tX-coordinates\tY-coordinates\tTime\tDeformation\tPerimeter\tIntensity\tIntensitySec\n");
				for (Pair<String, Intersectionobject> currentangle : parent.denseTracklist) {
					
					String currentID = currentangle.getA();
					if(currentID.equals(ID)) {
						ArrayList<double[]> linelist = currentangle.getB().linelist;
						for (int index =0; index < linelist.size(); ++index) {
						
						bw.write("\t"+ parent.nf.format(linelist.get(index)[XcordLabel]) +  "\t"  + parent.nf.format(linelist.get(index)[YcordLabel])
								+ "\t" +
								 currentangle.getB().z
	                              + "\t" + 
								parent.nf.format(linelist.get(index)[CurvatureLabel]) + "\t"  + parent.nf.format(linelist.get(index)[perimeterLabel]) + "\t"  
								+ parent.nf.format(linelist.get(index)[IntensityALabel]) +
								
								"\t"   + parent.nf.format(linelist.get(index)[IntensityBLabel]) + 
								"\n");
						
						
					}
					}
				
			}
			
			
	    bw.close();
		fw.close();
		}
		catch (IOException te) {
		}
			
		}
		
	for(int tablepos = 0; tablepos< parent.table.getRowCount(); ++tablepos) {
			
			String ID = (String) parent.table.getValueAt(tablepos, 0);
			parent.saveFile = savefile;
			try {
				File fichier = new File(
						savefile + "//" + parent.addToName + parent.inputstring + "TrackID" + Integer.parseInt(ID) + ".txt");
				System.out.println(fichier);
				FileWriter fw = new FileWriter(fichier);
				BufferedWriter bw = new BufferedWriter(fw);
				
				IJ.log("Choosen Track saved in: " + savefile.getAbsolutePath());
				bw.write("\tTrackID: " + "\t" + ID+ "\n");
				
				bw.write("\tArbritaryUnit\tTime\tDeformation\tIntensity\tIntensitySec\n");
				
				
				KymoSaveobject Kymos = parent.KymoFileobject.get(ID);
				if(Kymos==null) {
					
					CurvatureTableDisplay.saveclicked(parent, tablepos);
					Kymos = parent.KymoFileobject.get(ID);
					
				}
				else
					CurvatureTableDisplay.saveclicked(parent, tablepos);
				
				
				RandomAccessibleInterval<FloatType> CurvatureKymo = Kymos.CurvatureKymo;
				
				RandomAccessibleInterval<FloatType> IntensityAKymo = Kymos.IntensityAKymo;
				
				RandomAccessibleInterval<FloatType> IntensityBKymo = Kymos.IntensityBKymo;
				
				int hyperslicedimension = 1;
				
				for (long pos = 0; pos < CurvatureKymo.dimension(hyperslicedimension); ++pos) {
					
					
					RandomAccessibleInterval< FloatType > CurveView =
	                        Views.hyperSlice( CurvatureKymo, hyperslicedimension, pos );
					
					RandomAccessibleInterval< FloatType > IntensityAView =
	                        Views.hyperSlice( IntensityAKymo, hyperslicedimension, pos );
					
					RandomAccessibleInterval< FloatType > IntensityBView =
	                        Views.hyperSlice( IntensityBKymo, hyperslicedimension, pos );
					
	 				RandomAccess<FloatType> Cranac = CurveView.randomAccess();
					RandomAccess<FloatType> Aranac = IntensityAView.randomAccess();
					RandomAccess<FloatType> Branac = IntensityBView.randomAccess();
					
					Iterator<Map.Entry<String, Integer>> itZ = parent.AccountedZ.entrySet().iterator();
					
					while (itZ.hasNext()) {

						Map.Entry<String, Integer> entry = itZ.next();

						int time = entry.getValue();
						
						Cranac.setPosition(time - 1, 0);
						Aranac.setPosition(time - 1, 0);
						Branac.setPosition(time - 1, 0);
					bw.write("\t"+ pos +  "\t"  + time
					+ "\t" + 
					parent.nf.format(Cranac.get().get())
	                  + "\t" + 
					parent.nf.format(Aranac.get().get()) + "\t" + parent.nf.format(Branac.get().get()) + 
					"\n");
			
					}
				}
				
			    bw.close();
				fw.close();
			}
			
			catch (IOException te) {
			}
			
		}
	frame.dispose();
		
	}
	
}

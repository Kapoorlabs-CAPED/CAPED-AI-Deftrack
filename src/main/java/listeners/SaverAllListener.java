package listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import curvatureUtils.CurvatureTableDisplay;
import ellipsoidDetector.Intersectionobject;
import ellipsoidDetector.KymoSaveobject;
import hashMapSorter.SortCoordinates;
import ij.IJ;
import kalmanForSegments.Segmentobject;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Pair;
import net.imglib2.view.Views;
import pluginTools.InteractiveSimpleEllipseFit;
import utility.Curvatureobject;

public class SaverAllListener implements ActionListener {

	final InteractiveSimpleEllipseFit parent;
	
	
	/**
	 * 
	 * Fields
	 * 
	 */
	
	int XcordLabel = 0;
	int YcordLabel = 1;
	int CurvatureLabel = 2;
	int IntensityALabel = 3;
	int IntensityBLabel = 4;
	int perimeterLabel = 5;
	int DistCurvatureLabel = 6;
	
	public SaverAllListener(final InteractiveSimpleEllipseFit parent) {

		this.parent = parent;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		if (!parent.curveautomode && !parent.curvesupermode)
			OldSave();
		else {
		  KymoSave();
		  DenseSave(); 
		  
		}
	
	IJ.log("All trackes saved in: " + parent.saveFile.getAbsolutePath());
	}
	
	
	public void KymoSave() {
		
		parent.saveFile.mkdir();
	for(int tablepos = 0; tablepos< parent.table.getRowCount(); ++tablepos) {
			
			String ID = (String) parent.table.getValueAt(tablepos, 0);
			
			try {
				
				File fichier = new File(
						parent.saveFile + "//" + parent.addToName + parent.inputstring.replaceFirst("[.][^.]+$", "") + "TrackID" + Integer.parseInt(ID) + ".txt");
				
				FileWriter fw = new FileWriter(fichier);
				BufferedWriter bw = new BufferedWriter(fw);
				bw.write("\tTrackID: " + "\t"  + ID+ "\n");
				if(parent.combomethod)
					bw.write("\tArbritaryUnit\tTime\tDeformation\tIntensity\tIntensitySec\tDistance-Deformation\n");
				else
      				bw.write("\tArbritaryUnit\tTime\tDeformation\tIntensity\tIntensitySec\n");
				
				
				KymoSaveobject Kymos = parent.KymoFileobject.get(ID);
				KymoSaveobject LineKymo = parent.KymoLineobject.get(ID);
		
		      
				
				RandomAccessibleInterval<FloatType> CurvatureKymo = Kymos.CurvatureKymo;
				
				RandomAccessibleInterval<FloatType> DistCurvatureKymo = Kymos.DistCurvatureKymo;
				
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
					RandomAccessibleInterval< FloatType > CurveDView = null;
					
					
					RandomAccess<FloatType> Dranac = null;
					if(DistCurvatureKymo!=null) {
						CurveDView =
                        Views.hyperSlice( DistCurvatureKymo, hyperslicedimension, pos );
						Dranac = CurveDView.randomAccess();
						
					}
					
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
						if(Dranac!=null) { 
							Dranac.setPosition(time - 1, 0);
					bw.write("\t"+ pos +  "\t" + time
					+ "\t" +
					parent.nf.format(Cranac.get().get())
	                  + "\t" +
					parent.nf.format(Aranac.get().get()) + "\t" + parent.nf.format(Branac.get().get()) + "\t" + parent.nf.format(Dranac.get().get()) +
					"\n");
						}
						else {
							bw.write("\t"+ pos +  "\t" + time
									+ "\t" +
									parent.nf.format(Cranac.get().get())
					                  + "\t" +
									parent.nf.format(Aranac.get().get()) + "\t" + parent.nf.format(Branac.get().get()) + "\t" +
									"\n");
							
						}
			
					}
				}
				
			    bw.close();
				fw.close();
				
				 if(Kymos==null || LineKymo == null) {
						
						CurvatureTableDisplay.saveclicked(parent, tablepos);
						Kymos = parent.KymoFileobject.get(ID);
						LineKymo = parent.KymoLineobject.get(ID);
					}
					else
						CurvatureTableDisplay.saveclicked(parent, tablepos);
						
						
			}
			
			
			catch (IOException te) {
			}
			
			
			
		}
		
	}
	
	public void DenseSave() {
		
		parent.saveFile.mkdir();
		for(int tablepos = 0; tablepos< parent.table.getRowCount(); ++tablepos) {
			
			String ID = (String) parent.table.getValueAt(tablepos, 0);
			
			try {
				File fichier = new File(
						parent.saveFile + "//" + "Co-ordinates" + parent.addToName +  parent.inputstring.replaceFirst("[.][^.]+$", "") +  "CellID" +ID + ".txt");

				FileWriter fw = new FileWriter(fichier);
				BufferedWriter bw = new BufferedWriter(fw);
				bw.write("\tTrackID: " + "\t"  + ID+ "\n");
				if(parent.combomethod)
					bw.write("\tX-coordinates\tY-coordinates\tTime\tDeformation\tPerimeter\tIntensity\tIntensitySec\tDistance-Deformation\n");
				else
      				bw.write("\tX-coordinates\tY-coordinates\tTime\tDeformation\tPerimeter\tIntensity\tIntensitySec\n");
				for (Pair<String, Intersectionobject> currentangle : parent.denseTracklist) {
					
					String currentID = currentangle.getA();
					if(currentID.equals(ID)) {
						ArrayList<double[]> linelist = currentangle.getB().linelist;
						for (int index =0; index < linelist.size(); ++index) {
						
						if(parent.combomethod)
							bw.write("\t"+ parent.nf.format(linelist.get(index)[XcordLabel]) +  "\t" +parent.nf.format(linelist.get(index)[YcordLabel])
							+ "\t" +
							 currentangle.getB().z
                              + "\t" + 
							parent.nf.format(linelist.get(index)[CurvatureLabel]) + "\t"  +  parent.nf.format(linelist.get(index)[perimeterLabel]) + "\t"   
							+ parent.nf.format(linelist.get(index)[IntensityALabel]) +
							
							"\t" +parent.nf.format(linelist.get(index)[IntensityBLabel]) + "\t" + parent.nf.format(linelist.get(index)[DistCurvatureLabel]) + 
							"\n");
						else
							
							
						bw.write("\t"+ parent.nf.format(linelist.get(index)[XcordLabel]) +  "\t" +parent.nf.format(linelist.get(index)[YcordLabel])
								+ "\t" +
								 currentangle.getB().z
	                              + "\t" + 
								parent.nf.format(linelist.get(index)[CurvatureLabel]) + "\t"  +  parent.nf.format(linelist.get(index)[perimeterLabel]) + "\t"   
								+ parent.nf.format(linelist.get(index)[IntensityALabel]) +
								
								"\t" +parent.nf.format(linelist.get(index)[IntensityBLabel]) + 
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
		
	}
	public void OldSave() {
		
		parent.saveFile.mkdir();
		for(int tablepos = 0; tablepos< parent.table.getRowCount(); ++tablepos) {
			
			String ID = (String) parent.table.getValueAt(tablepos, 0);
			
		
		IJ.log("All trackes saved in: " + parent.saveFile.getAbsolutePath());
		
		if (!parent.curveautomode && !parent.curvesupermode) {
		try {
			File fichier = new File(
					parent.saveFile + "//" + parent.addToName + "TrackID" +ID + ".txt");

			FileWriter fw = new FileWriter(fichier);
			BufferedWriter bw = new BufferedWriter(fw);
			
			bw.write(
					"\tTime (px)\t AngleT \n");
		for (int index = 0; index< parent.resultAngle.size(); ++index) {
			
			
			if (ID.equals(parent.resultAngle.get(index).getA() )) {
				
				// Save result sin file
			
				int time = (int) parent.resultAngle.get(index).getB()[0];
				double angle = parent.resultAngle.get(index).getB()[1];
				bw.write("\t" + time + "\t" 
						+ angle + 
						
						"\n");
				

				
			}
			
		}
			
		bw.close();
		fw.close();
		}
		catch (IOException te) {
		}
		}
			
			else {
				
				try {
					File fichier = new File(
							parent.saveFile + "//" + parent.addToName + "CellID" +ID + ".txt");

					FileWriter fw = new FileWriter(fichier);
					BufferedWriter bw = new BufferedWriter(fw);
					bw.write("\tTrackID:" + "\t"  + ID+ "\n");
					if(parent.combomethod)
						bw.write("\tX-coordinates\tY-coordinates\tTime\tDeformation\t Perimeter\tIntensity\tIntensitySec\tDistance-Deformation\n");
					else
     					bw.write("\tX-coordinates\tY-coordinates\tTime\tDeformation\t Perimeter\tIntensity\tIntensitySec\n");
					for (Pair<String, Intersectionobject> currentangle : parent.Tracklist) {
						
						String currentID = currentangle.getA();
						if(currentID.equals(ID)) {
							ArrayList<double[]> linelist = currentangle.getB().linelist;
							for (int index =0; index < linelist.size(); ++index) {
							
							if(parent.combomethod)
								
								bw.write("\t"+ parent.nf.format(linelist.get(index)[XcordLabel]) +  "\t" + parent.nf.format(linelist.get(index)[YcordLabel])
								+ "\t" + 
								 currentangle.getB().z
                                  + "\t" + 
								parent.nf.format(linelist.get(index)[CurvatureLabel]) + "\t"  +  parent.nf.format(linelist.get(index)[perimeterLabel]) + "\t"   
								+ parent.nf.format(linelist.get(index)[IntensityALabel]) +
								
								"\t" +  parent.nf.format(linelist.get(index)[IntensityBLabel]) + "\t" + parent.nf.format(linelist.get(index)[DistCurvatureLabel]) + 
								"\n");
								
							else
								
								
								
							bw.write("\t"+ parent.nf.format(linelist.get(index)[XcordLabel]) +  "\t" + parent.nf.format(linelist.get(index)[YcordLabel])
									+ "\t" + 
									 currentangle.getB().z
	                                  + "\t" + 
									parent.nf.format(linelist.get(index)[CurvatureLabel]) + "\t"  +  parent.nf.format(linelist.get(index)[perimeterLabel]) + "\t"   
									+ parent.nf.format(linelist.get(index)[IntensityALabel]) +
									
									"\t" +  parent.nf.format(linelist.get(index)[IntensityBLabel]) + 
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
		
		}
		
		
		
	}
	
	
	public void NewSave() {
		
		parent.saveFile.mkdir();
   for(int tablepos = 0; tablepos< parent.table.getRowCount(); ++tablepos) {
			
			String ID = (String) parent.table.getValueAt(tablepos, 0);
		
		
			try {
				File fichier = new File(
						parent.saveFile + "//" + parent.addToName + "SegmentID" +ID + ".txt");

				FileWriter fw = new FileWriter(fichier);
				BufferedWriter bw = new BufferedWriter(fw);
				bw.write("\tTrackID:" + "\t" + ID+ "\n");
				bw.write("\tX-coordinates\tY-coordinates\tTime\tDeformation\tPerimeter\tIntensity\tIntensitySec\n");
				for (Pair<String, Segmentobject> currentangle : parent.SegmentTracklist) {
					
					String currentID = currentangle.getA();
					if(currentID.equals(ID)) {
						
						
						bw.write("\t"+ parent.nf.format(currentangle.getB().centralpoint.getDoublePosition(0)) +  "\t" +  parent.nf.format(currentangle.getB().centralpoint.getDoublePosition(1))
								+ "\t" +
								 currentangle.getB().z
                                  + "\t" + 
								parent.nf.format(currentangle.getB().Curvature) + "\t"  + parent.nf.format(currentangle.getB().Perimeter) + "\t"   
								+ parent.nf.format(currentangle.getB().IntensityA) +
								
								"\t" +  parent.nf.format(currentangle.getB().IntensityB) + 
								"\n");
						
						
					}
				
				
			}
			
			
	    bw.close();
		fw.close();
		}
		catch (IOException te) {
		}
		
		
		
	}
	}
	
}

package batchMode;

import pluginTools.InteractiveSimpleEllipseFit;

public class CreateINIfile {

	
	final InteractiveSimpleEllipseFit parent;
	
	public CreateINIfile(final InteractiveSimpleEllipseFit parent) {
		
		this.parent = parent;
		
	}
	
	
	public void RecordParent() {
		
		
		LocalPrefs.set("BackgroundLabel.int", parent.background);
		LocalPrefs.set("NumberofSegments.int", parent.minNumInliers);
		LocalPrefs.set("Resolution.int", parent.resolution);
		LocalPrefs.set("IntensityRadius.double", parent.insidedistance);
		LocalPrefs.set("TimeCalibration.double", parent.timecal);
		LocalPrefs.set("SpaceCalibration.double", parent.calibration);
		LocalPrefs.set("CurvatureViaCircle.boolean", parent.pixelcelltrackcirclefits);
		LocalPrefs.set("CurvatureViaDistance.boolean", parent.distancemethod);
		LocalPrefs.set("Box.int", parent.boxsize);
		
		
		
		
		if(parent.saveFile!=null)
		LocalPrefs.setHomeDir(parent.saveFile.getAbsolutePath());
		else
			LocalPrefs.setHomeDir(new java.io.File(".").getAbsolutePath());
        LocalPrefs.savePreferences();
		System.out.println(LocalPrefs.getHomeDir() + " " + parent.saveFile.getAbsolutePath());
		System.exit(1);
	}
	
	
}

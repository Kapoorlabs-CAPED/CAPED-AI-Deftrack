package batchMode;

public class ParamConfig {

	
	public final int background;
	public final int minNumInliers;
	public final int resolution;
	public final double insidedistance;
	public final double timecal;
	public final double calibration;
	public final boolean pixelcelltrackcirclefits;
	public final boolean distancemethod;
	public final int boxsize;
	
	
	
	public ParamConfig(final int background,final int minNumInliers, final int resolution,final double insidedistance, final double timecal,final double calibration,final boolean pixelcelltrackcirclefits,
			final boolean distancemethod,final int boxsize ) {
		
		this.background = background;
		this.minNumInliers = minNumInliers;
		this.resolution = resolution;
		this.insidedistance = insidedistance;
		this.timecal = timecal;
		this.calibration = calibration;
		this.pixelcelltrackcirclefits = pixelcelltrackcirclefits;
		this.distancemethod = distancemethod;
		this.boxsize = boxsize;
		
		
	}
	
	
}

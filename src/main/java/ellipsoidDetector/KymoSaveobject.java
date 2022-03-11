package ellipsoidDetector;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.real.FloatType;

public class KymoSaveobject {
	
	
	
	public final RandomAccessibleInterval<FloatType> CurvatureKymo;
	public final RandomAccessibleInterval<FloatType> DistCurvatureKymo;
	public final RandomAccessibleInterval<FloatType> IntensityAKymo;
	public final RandomAccessibleInterval<FloatType> IntensityBKymo;
	public final RandomAccessibleInterval<FloatType> LineScanAKymo;
	public final RandomAccessibleInterval<FloatType> LineScanBKymo;
	
	
	public KymoSaveobject(RandomAccessibleInterval<FloatType> CurvatureKymo, RandomAccessibleInterval<FloatType> IntensityAKymo,RandomAccessibleInterval<FloatType> IntensityBKymo ) {
		
		this.CurvatureKymo = CurvatureKymo;
		this.IntensityAKymo = IntensityAKymo;
		this.IntensityBKymo = IntensityBKymo;
		this.LineScanAKymo = null;
		this.LineScanBKymo = null;
		this.DistCurvatureKymo = null;
	}

	
	public KymoSaveobject(RandomAccessibleInterval<FloatType> LineScanAKymo, RandomAccessibleInterval<FloatType> LineScanBKymo) {
		
		this.CurvatureKymo = null;
		this.IntensityAKymo = null;
		this.IntensityBKymo = null;
		this.LineScanAKymo = LineScanAKymo;
		this.LineScanBKymo = LineScanBKymo;
		this.DistCurvatureKymo = null;
		
	}
	
public KymoSaveobject(RandomAccessibleInterval<FloatType> CurvatureKymo,RandomAccessibleInterval<FloatType> DistCurvatureKymo, RandomAccessibleInterval<FloatType> IntensityAKymo,RandomAccessibleInterval<FloatType> IntensityBKymo ) {
		
		this.CurvatureKymo = CurvatureKymo;
		this.IntensityAKymo = IntensityAKymo;
		this.IntensityBKymo = IntensityBKymo;
		this.LineScanAKymo = null;
		this.LineScanBKymo = null;
		this.DistCurvatureKymo = DistCurvatureKymo;
	}
	
}

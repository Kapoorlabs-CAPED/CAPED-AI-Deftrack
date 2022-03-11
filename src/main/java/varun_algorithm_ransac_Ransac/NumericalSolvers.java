package varun_algorithm_ransac_Ransac;

public interface NumericalSolvers {

	public double run(final int numComponents, final double[] ellipseCoeff, final double[] sourcePoint,
			final double[] targetPoint);

}

package ransacPoly;

import Jama.Matrix;
import Jama.QRDecomposition;

public class Threepointfit {


	private final int degree;
	private final int Npoints;
	private final Matrix Coefficients;
	 private double SSE;
	    private double SST;
	
	    public Threepointfit(double[] x, double[] y, int degree){
			
			this.degree = degree;
			Npoints = x.length;
			
			// Vandermonde matrix 
			double[][] vandermonde = new double[Npoints][degree+1];
	        for (int i = 0; i < Npoints; i++) {
	            for (int j = 0; j <= degree; j++) {
	                vandermonde[i][j] = Math.pow(x[i], j);
	            }
	        }
	        Matrix X = new Matrix(vandermonde);

	        // create matrix from vector
	        Matrix Y = new Matrix(y, Npoints);
	        
	     // find least squares solution
	        QRDecomposition qr = new QRDecomposition(X);
	        Coefficients = qr.solve(Y);
	        
	        
	        // mean of y[] values
	        double sum = 0.0;
	        for (int i = 0; i < Npoints; i++)
	            sum += y[i];
	        double mean = sum / Npoints;
	        
	        
	        // total variation to be accounted for
	        for (int i = 0; i < Npoints; i++) {
	            double dev = y[i] - mean;
	            SST += dev*dev;
	        }

	        // variation not accounted for
	        Matrix residuals = X.times(Coefficients).minus(Y);
	        SSE = residuals.norm2() * residuals.norm2();
			
		}
		public double GetCoefficients(int j) {
	        return Coefficients.get(j, 0);
	    }

	    public int degree() {
	        return degree;
	    }

	    public double R2() {
	        return 1.0 - SSE/SST;
	    }

	    // Horner's method to get y values correspoing to x
	    public double predict(double x) {
	        // horner's method
	        double y = 0.0;
	        for (int j = degree; j >= 0; j--)
	            y = GetCoefficients(j) + (x * y);
	        return y;
	    }
	    
	    // Horner's method to get y values correspoing to x
	    public double predictderivative(double x) {
	        // horner's method for derivative of a function
	        double y = 0;
			for (int j = 0; j <= degree; j++) {
				 if(j >= 1)    
			        	y += j * GetCoefficients(j) * Math.pow(x, j - 1);
			}
			
			return y;
	    }
	    
	    // Horner's method to get y values correspoing to x
	    public double predictsecderivative(double x) {
	        // horner's method for derivative of a function
	    	double y = 0.0;
			for (int j = 0; j <= degree; j++) {
			 	if(j >= 2)
		        	 y += j * ( j - 1) * GetCoefficients(j) * Math.pow(x, j - 2);
			}
			return y;
	    }
	    
	
		
		
	    
}

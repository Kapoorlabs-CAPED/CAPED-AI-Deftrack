package utility;

public class Convolvers {

	
	public static double[][] movingSum(double[][] paramArrayOfDouble, int paramInt)
	  {
	    int i = paramArrayOfDouble.length;
	    int j = paramArrayOfDouble[0].length;
	    double[][] arrayOfDouble = new double[i + paramInt - 1][j + paramInt - 1];
	    int m;
	    for (int k = 0; k < j; k++)
	    {
	      arrayOfDouble[0][k] = paramArrayOfDouble[0][k];
	      for (m = 1; m < paramInt; m++) {
	        arrayOfDouble[0][k] += paramArrayOfDouble[m][k];
	      }
	      for (m = 1; m < paramInt; m++) {
	        arrayOfDouble[m][k] = (arrayOfDouble[(m - 1)][k] - paramArrayOfDouble[(paramInt - m)][k] + paramArrayOfDouble[m][k]);
	      }
	      for (m = paramInt; m < i; m++) {
	        arrayOfDouble[m][k] = (arrayOfDouble[(m - 1)][k] - paramArrayOfDouble[(m - paramInt)][k] + paramArrayOfDouble[m][k]);
	      }
	      for (m = i; m < i + paramInt; m++) {
	        arrayOfDouble[m][k] = (arrayOfDouble[(m - 1)][k] - paramArrayOfDouble[(m - paramInt)][k] + paramArrayOfDouble[(2 * i - m - 2)][k]);
	      }
	    }
	    for (int k = 0; k < i + paramInt - 1; k++)
	    {
	      arrayOfDouble[k][0] = arrayOfDouble[k][0];
	      for (m = 1; m < paramInt; m++) {
	        arrayOfDouble[k][0] += arrayOfDouble[k][m];
	      }
	      for (m = 1; m < paramInt; m++) {
	        arrayOfDouble[k][m] = (arrayOfDouble[k][(m - 1)] - arrayOfDouble[k][(paramInt - m)] + arrayOfDouble[k][m]);
	      }
	      for (m = paramInt; m < j; m++) {
	        arrayOfDouble[k][m] = (arrayOfDouble[k][(m - 1)] - arrayOfDouble[k][(m - paramInt)] + arrayOfDouble[k][m]);
	      }
	      for (m = j; m < j + paramInt - m; m++) {
	        arrayOfDouble[k][m] = (arrayOfDouble[k][(m - 1)] - arrayOfDouble[k][(m - paramInt)] + arrayOfDouble[k][(2 * j - m - 2)]);
	      }
	    }
	    return arrayOfDouble;
	  }
	  
	  public static double[][] movingSumX(double[][] paramArrayOfDouble, int paramInt)
	  {
	    int i = paramArrayOfDouble.length;
	    int j = paramArrayOfDouble[0].length;
	    double[][] arrayOfDouble = new double[i + paramInt - 1][j + paramInt - 1];
	    for (int k = 0; k < j; k++)
	    {
	      arrayOfDouble[0][k] = paramArrayOfDouble[0][k];
	      for (int m = 1; m < paramInt; m++) {
	        arrayOfDouble[0][k] += paramArrayOfDouble[m][k];
	      }
	      for (int m = 1; m < paramInt; m++) {
	        arrayOfDouble[m][k] = (arrayOfDouble[(m - 1)][k] - paramArrayOfDouble[(paramInt - m)][k] + paramArrayOfDouble[m][k]);
	      }
	      for (int m = paramInt; m < i; m++) {
	        arrayOfDouble[m][k] = (arrayOfDouble[(m - 1)][k] - paramArrayOfDouble[(m - paramInt)][k] + paramArrayOfDouble[m][k]);
	      }
	      for (int m = i; m < i + paramInt; m++) {
	        arrayOfDouble[m][k] = (arrayOfDouble[(m - 1)][k] - paramArrayOfDouble[(m - paramInt)][k] + paramArrayOfDouble[(2 * i - m - 2)][k]);
	      }
	    }
	    return arrayOfDouble;
	  }
	  
	  public static double[][] movingSumY(double[][] paramArrayOfDouble, int paramInt)
	  {
	    int i = paramArrayOfDouble.length;
	    int j = paramArrayOfDouble[0].length;
	    double[][] arrayOfDouble = new double[i + paramInt - 1][j + paramInt - 1];
	    for (int k = 0; k < i + paramInt - 1; k++)
	    {
	      arrayOfDouble[k][0] = arrayOfDouble[k][0];
	      for (int m = 1; m < paramInt; m++) {
	        arrayOfDouble[k][0] += arrayOfDouble[k][m];
	      }
	      for (int m = 1; m < paramInt; m++) {
	        arrayOfDouble[k][m] = (arrayOfDouble[k][(m - 1)] - arrayOfDouble[k][(paramInt - m)] + arrayOfDouble[k][m]);
	      }
	      for (int m = paramInt; m < j; m++) {
	        arrayOfDouble[k][m] = (arrayOfDouble[k][(m - 1)] - arrayOfDouble[k][(m - paramInt)] + arrayOfDouble[k][m]);
	      }
	      for (int m = j; m < j + paramInt - m; m++) {
	        arrayOfDouble[k][m] = (arrayOfDouble[k][(m - 1)] - arrayOfDouble[k][(m - paramInt)] + arrayOfDouble[k][(2 * j - m - 2)]);
	      }
	    }
	    return arrayOfDouble;
	  }
	  
	  public static double[][] convolveEvenX(double[][] paramArrayOfDouble, double[] paramArrayOfDouble1)
	  {
	    int i = paramArrayOfDouble.length;
	    int j = paramArrayOfDouble[0].length;
	    int k = paramArrayOfDouble1.length;
	    int m = k - 1;
	    int n = 2 * i - 2;
	    double[][] arrayOfDouble = new double[i][j];
	    int i1 = 0;
	    for (int i2 = 0; i2 < j; i2++)
	    {
	      int i4;
	      for (int i3 = 0; i3 < m; i3++)
	      {
	        arrayOfDouble[i3][i2] = (paramArrayOfDouble1[0] * paramArrayOfDouble[i3][i2]);
	        for (i4 = 1; i4 <= i3; i4++) {
	          arrayOfDouble[i3][i2] += paramArrayOfDouble1[i4] * (paramArrayOfDouble[(i3 - i4)][i2] + paramArrayOfDouble[(i3 + i4)][i2]);
	        }
	        for (i4 = i3 + 1; i4 < k; i4++) {
	          arrayOfDouble[i3][i2] += paramArrayOfDouble1[i4] * (paramArrayOfDouble[(i4 - i3)][i2] + paramArrayOfDouble[(i3 + i4)][i2]);
	        }
	      }
	      for (int i3 = m; i3 <= i - k; i3++)
	      {
	        arrayOfDouble[i3][i2] = (paramArrayOfDouble1[0] * paramArrayOfDouble[i3][i2]);
	        for (i4 = 1; i4 < k; i4++) {
	          arrayOfDouble[i3][i2] += paramArrayOfDouble1[i4] * (paramArrayOfDouble[(i3 - i4)][i2] + paramArrayOfDouble[(i3 + i4)][i2]);
	        }
	      }
	      for (int i3 = i - m; i3 < i; i3++)
	      {
	        arrayOfDouble[i3][i2] = (paramArrayOfDouble1[0] * paramArrayOfDouble[i3][i2]);
	        for (i4 = 1; i4 < i - i3; i4++) {
	          arrayOfDouble[i3][i2] += paramArrayOfDouble1[i4] * (paramArrayOfDouble[(i3 - i4)][i2] + paramArrayOfDouble[(i3 + i4)][i2]);
	        }
	        for (i4 = i - i3; i4 < k; i4++) {
	          arrayOfDouble[i3][i2] += paramArrayOfDouble1[i4] * (paramArrayOfDouble[(n - i4 - i3)][i2] + paramArrayOfDouble[(i3 - i4)][i2]);
	        }
	      }
	    }
	    return arrayOfDouble;
	  }
	  
	  public static double[] convolveEvenX(double[] paramArrayOfDouble1, double[] paramArrayOfDouble2, int paramInt1, int paramInt2)
	  {
	    int i = paramArrayOfDouble2.length;
	    int j = i - 1;
	    int k = 2 * paramInt1 - 2;
	    double[] arrayOfDouble = new double[paramInt1 * paramInt2];
	    int m = 0;
	    for (int n = 0; n < paramInt2; n++)
	    {
	      int i2;
	      for (int i1 = 0; i1 < j; i1++)
	      {
	        arrayOfDouble[m] = (paramArrayOfDouble2[0] * paramArrayOfDouble1[m]);
	        for (i2 = 1; i2 <= i1; i2++) {
	          arrayOfDouble[m] += paramArrayOfDouble2[i2] * (paramArrayOfDouble1[(m - i2)] + paramArrayOfDouble1[(m + i2)]);
	        }
	        for (i2 = i1 + 1; i2 < i; i2++) {
	          arrayOfDouble[m] += paramArrayOfDouble2[i2] * (paramArrayOfDouble1[(i2 - i1 + n * paramInt1)] + paramArrayOfDouble1[(m + i2)]);
	        }
	        m++;
	      }
	      for (int i1 = j; i1 <= paramInt1 - i; i1++)
	      {
	        arrayOfDouble[m] = (paramArrayOfDouble2[0] * paramArrayOfDouble1[m]);
	        for (i2 = 1; i2 < i; i2++) {
	          arrayOfDouble[m] += paramArrayOfDouble2[i2] * (paramArrayOfDouble1[(m - i2)] + paramArrayOfDouble1[(m + i2)]);
	        }
	        m++;
	      }
	      for (int i1 = paramInt1 - j; i1 < paramInt1; i1++)
	      {
	        arrayOfDouble[m] = (paramArrayOfDouble2[0] * paramArrayOfDouble1[m]);
	        for (i2 = 1; i2 < paramInt1 - i1; i2++) {
	          arrayOfDouble[m] += paramArrayOfDouble2[i2] * (paramArrayOfDouble1[(m - i2)] + paramArrayOfDouble1[(m + i2)]);
	        }
	        for (i2 = paramInt1 - i1; i2 < i; i2++) {
	          arrayOfDouble[m] += paramArrayOfDouble2[i2] * (paramArrayOfDouble1[(k - i2 - i1 + n * paramInt1)] + paramArrayOfDouble1[(m - i2)]);
	        }
	        m++;
	      }
	    }
	    return arrayOfDouble;
	  }
	  
	  public static float[] convolveEvenX(float[] paramArrayOfFloat1, float[] paramArrayOfFloat2, int paramInt1, int paramInt2)
	  {
	    int i = paramArrayOfFloat2.length;
	    int j = i - 1;
	    int k = 2 * paramInt1 - 2;
	    float[] arrayOfFloat = new float[paramInt1 * paramInt2];
	    int m = 0;
	    for (int n = 0; n < paramInt2; n++)
	    {
	      int i2;
	      for (int i1 = 0; i1 < j; i1++)
	      {
	        arrayOfFloat[m] = (paramArrayOfFloat2[0] * paramArrayOfFloat1[m]);
	        for (i2 = 1; i2 <= i1; i2++) {
	          arrayOfFloat[m] += paramArrayOfFloat2[i2] * (paramArrayOfFloat1[(m - i2)] + paramArrayOfFloat1[(m + i2)]);
	        }
	        for (i2 = i1 + 1; i2 < i; i2++) {
	          arrayOfFloat[m] += paramArrayOfFloat2[i2] * (paramArrayOfFloat1[(i2 - i1 + n * paramInt1)] + paramArrayOfFloat1[(m + i2)]);
	        }
	        m++;
	      }
	      for (int i1 = j; i1 <= paramInt1 - i; i1++)
	      {
	        arrayOfFloat[m] = (paramArrayOfFloat2[0] * paramArrayOfFloat1[m]);
	        for (i2 = 1; i2 < i; i2++) {
	          arrayOfFloat[m] += paramArrayOfFloat2[i2] * (paramArrayOfFloat1[(m - i2)] + paramArrayOfFloat1[(m + i2)]);
	        }
	        m++;
	      }
	      for (int i1 = paramInt1 - j; i1 < paramInt1; i1++)
	      {
	        arrayOfFloat[m] = (paramArrayOfFloat2[0] * paramArrayOfFloat1[m]);
	        for (i2 = 1; i2 < paramInt1 - i1; i2++) {
	          arrayOfFloat[m] += paramArrayOfFloat2[i2] * (paramArrayOfFloat1[(m - i2)] + paramArrayOfFloat1[(m + i2)]);
	        }
	        for (i2 = paramInt1 - i1; i2 < i; i2++) {
	          arrayOfFloat[m] += paramArrayOfFloat2[i2] * (paramArrayOfFloat1[(k - i2 - i1 + n * paramInt1)] + paramArrayOfFloat1[(m - i2)]);
	        }
	        m++;
	      }
	    }
	    return arrayOfFloat;
	  }
	  
	  public static double[][] convolveEvenY(double[][] paramArrayOfDouble, double[] paramArrayOfDouble1)
	  {
	    int i = paramArrayOfDouble.length;
	    int j = paramArrayOfDouble[0].length;
	    int k = paramArrayOfDouble1.length;
	    int m = k - 1;
	    int n = 2 * j - 2;
	    double[][] arrayOfDouble = new double[i][j];
	    for (int i1 = 0; i1 < i; i1++)
	    {
	      int i3;
	      for (int i2 = 0; i2 < m; i2++)
	      {
	        arrayOfDouble[i1][i2] = (paramArrayOfDouble1[0] * paramArrayOfDouble[i1][i2]);
	        for (i3 = 1; i3 <= i2; i3++) {
	          arrayOfDouble[i1][i2] += paramArrayOfDouble1[i3] * (paramArrayOfDouble[i1][(i2 - i3)] + paramArrayOfDouble[i1][(i2 + i3)]);
	        }
	        for (i3 = i2 + 1; i3 < k; i3++) {
	          arrayOfDouble[i1][i2] += paramArrayOfDouble1[i3] * (paramArrayOfDouble[i1][(i3 - i2)] + paramArrayOfDouble[i1][(i2 + i3)]);
	        }
	      }
	      for (int i2 = m; i2 <= j - k; i2++)
	      {
	        arrayOfDouble[i1][i2] = (paramArrayOfDouble1[0] * paramArrayOfDouble[i1][i2]);
	        for (i3 = 1; i3 < k; i3++) {
	          arrayOfDouble[i1][i2] += paramArrayOfDouble1[i3] * (paramArrayOfDouble[i1][(i2 - i3)] + paramArrayOfDouble[i1][(i2 + i3)]);
	        }
	      }
	      for (int i2 = j - m; i2 < j; i2++)
	      {
	        arrayOfDouble[i1][i2] = (paramArrayOfDouble1[0] * paramArrayOfDouble[i1][i2]);
	        for (i3 = 1; i3 < j - i2; i3++) {
	          arrayOfDouble[i1][i2] += paramArrayOfDouble1[i3] * (paramArrayOfDouble[i1][(i2 - i3)] + paramArrayOfDouble[i1][(i2 + i3)]);
	        }
	        for (i3 = j - i2; i3 < k; i3++) {
	          arrayOfDouble[i1][i2] += paramArrayOfDouble1[i3] * (paramArrayOfDouble[i1][(n - i3 - i2)] + paramArrayOfDouble[i1][(i2 - i3)]);
	        }
	      }
	    }
	    return arrayOfDouble;
	  }
	  
	  public static double[] convolveEvenY(double[] paramArrayOfDouble1, double[] paramArrayOfDouble2, int paramInt1, int paramInt2)
	  {
	    int i = paramArrayOfDouble2.length;
	    int j = i - 1;
	    int k = 2 * paramInt2 - 2;
	    double[] arrayOfDouble = new double[paramInt1 * paramInt2];
	    for (int i1 = 0; i1 < paramInt1; i1++)
	    {
	      int m;
	      int i3;
	      int n;
	      for (int i2 = 0; i2 < j; i2++)
	      {
	        m = i1 + i2 * paramInt1;
	        arrayOfDouble[m] = (paramArrayOfDouble2[0] * paramArrayOfDouble1[m]);
	        for (i3 = 1; i3 <= i2; i3++)
	        {
	          n = i3 * paramInt1;
	          arrayOfDouble[m] += paramArrayOfDouble2[i3] * (paramArrayOfDouble1[(m - n)] + paramArrayOfDouble1[(m + n)]);
	        }
	        for (i3 = i2 + 1; i3 < i; i3++) {
	          arrayOfDouble[m] += paramArrayOfDouble2[i3] * (paramArrayOfDouble1[((i3 - i2) * paramInt1 + i1)] + paramArrayOfDouble1[(m + i3 * paramInt1)]);
	        }
	      }
	      for (int i2 = j; i2 <= paramInt2 - i; i2++)
	      {
	        m = i1 + i2 * paramInt1;
	        arrayOfDouble[m] = (paramArrayOfDouble2[0] * paramArrayOfDouble1[m]);
	        for (i3 = 1; i3 < i; i3++)
	        {
	          n = i3 * paramInt1;
	          arrayOfDouble[m] += paramArrayOfDouble2[i3] * (paramArrayOfDouble1[(m - n)] + paramArrayOfDouble1[(m + n)]);
	        }
	      }
	      for (int i2 = paramInt2 - j; i2 < paramInt2; i2++)
	      {
	        m = i1 + i2 * paramInt1;
	        arrayOfDouble[m] = (paramArrayOfDouble2[0] * paramArrayOfDouble1[m]);
	        for (i3 = 1; i3 < paramInt2 - i2; i3++)
	        {
	          n = i3 * paramInt1;
	          arrayOfDouble[m] += paramArrayOfDouble2[i3] * (paramArrayOfDouble1[(m - n)] + paramArrayOfDouble1[(m + n)]);
	        }
	        for (i3 = paramInt2 - i2; i3 < i; i3++) {
	          arrayOfDouble[m] += paramArrayOfDouble2[i3] * (paramArrayOfDouble1[((k - i3 - i2) * paramInt1 + i1)] + paramArrayOfDouble1[(m - i3 * paramInt1)]);
	        }
	      }
	    }
	    return arrayOfDouble;
	  }
	  
	  public static float[] convolveEvenY(float[] paramArrayOfFloat1, float[] paramArrayOfFloat2, int paramInt1, int paramInt2)
	  {
	    int i = paramArrayOfFloat2.length;
	    int j = i - 1;
	    int k = 2 * paramInt2 - 2;
	    float[] arrayOfFloat = new float[paramInt1 * paramInt2];
	    for (int i1 = 0; i1 < paramInt1; i1++)
	    {
	      int m;
	      int i3;
	      int n;
	      for (int i2 = 0; i2 < j; i2++)
	      {
	        m = i1 + i2 * paramInt1;
	        arrayOfFloat[m] = (paramArrayOfFloat2[0] * paramArrayOfFloat1[m]);
	        for (i3 = 1; i3 <= i2; i3++)
	        {
	          n = i3 * paramInt1;
	          arrayOfFloat[m] += paramArrayOfFloat2[i3] * (paramArrayOfFloat1[(m - n)] + paramArrayOfFloat1[(m + n)]);
	        }
	        for (i3 = i2 + 1; i3 < i; i3++) {
	          arrayOfFloat[m] += paramArrayOfFloat2[i3] * (paramArrayOfFloat1[((i3 - i2) * paramInt1 + i1)] + paramArrayOfFloat1[(m + i3 * paramInt1)]);
	        }
	      }
	      for (int i2 = j; i2 <= paramInt2 - i; i2++)
	      {
	        m = i1 + i2 * paramInt1;
	        arrayOfFloat[m] = (paramArrayOfFloat2[0] * paramArrayOfFloat1[m]);
	        for (i3 = 1; i3 < i; i3++)
	        {
	          n = i3 * paramInt1;
	          arrayOfFloat[m] += paramArrayOfFloat2[i3] * (paramArrayOfFloat1[(m - n)] + paramArrayOfFloat1[(m + n)]);
	        }
	      }
	      for (int i2 = paramInt2 - j; i2 < paramInt2; i2++)
	      {
	        m = i1 + i2 * paramInt1;
	        arrayOfFloat[m] = (paramArrayOfFloat2[0] * paramArrayOfFloat1[m]);
	        for (i3 = 1; i3 < paramInt2 - i2; i3++)
	        {
	          n = i3 * paramInt1;
	          arrayOfFloat[m] += paramArrayOfFloat2[i3] * (paramArrayOfFloat1[(m - n)] + paramArrayOfFloat1[(m + n)]);
	        }
	        for (i3 = paramInt2 - i2; i3 < i; i3++) {
	          arrayOfFloat[m] += paramArrayOfFloat2[i3] * (paramArrayOfFloat1[((k - i3 - i2) * paramInt1 + i1)] + paramArrayOfFloat1[(m - i3 * paramInt1)]);
	        }
	      }
	    }
	    return arrayOfFloat;
	  }
	  
	  public static double[][] convolveOddX(double[][] paramArrayOfDouble, double[] paramArrayOfDouble1)
	  {
	    int i = paramArrayOfDouble.length;
	    int j = paramArrayOfDouble[0].length;
	    int k = paramArrayOfDouble1.length;
	    int m = k - 1;
	    int n = 2 * i - 2;
	    double[][] arrayOfDouble = new double[i][j];
	    for (int i1 = 0; i1 < j; i1++)
	    {
	      int i3;
	      for (int i2 = 0; i2 < m; i2++)
	      {
	        arrayOfDouble[i2][i1] = 0.0D;
	        for (i3 = 1; i3 <= i2; i3++) {
	          arrayOfDouble[i2][i1] += paramArrayOfDouble1[i3] * (paramArrayOfDouble[(i2 + i3)][i1] - paramArrayOfDouble[(i2 - i3)][i1]);
	        }
	        for (i3 = i2 + 1; i3 < k; i3++) {
	          arrayOfDouble[i2][i1] += paramArrayOfDouble1[i3] * (paramArrayOfDouble[(i2 + i3)][i1] - paramArrayOfDouble[(i3 - i2)][i1]);
	        }
	      }
	      for (int i2 = m; i2 <= i - k; i2++)
	      {
	        arrayOfDouble[i2][i1] = 0.0D;
	        for (i3 = 1; i3 < k; i3++) {
	          arrayOfDouble[i2][i1] += paramArrayOfDouble1[i3] * (paramArrayOfDouble[(i2 + i3)][i1] - paramArrayOfDouble[(i2 - i3)][i1]);
	        }
	      }
	      for (int i2 = i - m; i2 < i; i2++)
	      {
	        arrayOfDouble[i2][i1] = 0.0D;
	        for (i3 = 1; i3 < i - i2; i3++) {
	          arrayOfDouble[i2][i1] += paramArrayOfDouble1[i3] * (paramArrayOfDouble[(i2 + i3)][i1] - paramArrayOfDouble[(i2 - i3)][i1]);
	        }
	        for (i3 = i - i2; i3 < k; i3++) {
	          arrayOfDouble[i2][i1] += paramArrayOfDouble1[i3] * (paramArrayOfDouble[(n - i3 - i2)][i1] - paramArrayOfDouble[(i2 - i3)][i1]);
	        }
	      }
	    }
	    return arrayOfDouble;
	  }
	  
	  public static double[] convolveOddX(double[] paramArrayOfDouble1, double[] paramArrayOfDouble2, int paramInt1, int paramInt2)
	  {
	    int i = paramArrayOfDouble2.length;
	    int j = i - 1;
	    int k = 2 * paramInt1 - 2;
	    double[] arrayOfDouble = new double[paramInt1 * paramInt2];
	    int m = 0;
	    for (int n = 0; n < paramInt2; n++)
	    {
	      int i2;
	      for (int i1 = 0; i1 < j; i1++)
	      {
	        arrayOfDouble[m] = 0.0D;
	        for (i2 = 1; i2 <= i1; i2++) {
	          arrayOfDouble[m] += paramArrayOfDouble2[i2] * (paramArrayOfDouble1[(m + i2)] - paramArrayOfDouble1[(m - i2)]);
	        }
	        for (i2 = i1 + 1; i2 < i; i2++) {
	          arrayOfDouble[m] += paramArrayOfDouble2[i2] * (paramArrayOfDouble1[(m + i2)] - paramArrayOfDouble1[(i2 - i1 + n * paramInt1)]);
	        }
	        m++;
	      }
	      for (int i1 = j; i1 <= paramInt1 - i; i1++)
	      {
	        arrayOfDouble[m] = 0.0D;
	        for (i2 = 1; i2 < i; i2++) {
	          arrayOfDouble[m] += paramArrayOfDouble2[i2] * (paramArrayOfDouble1[(m + i2)] - paramArrayOfDouble1[(m - i2)]);
	        }
	        m++;
	      }
	      for (int i1 = paramInt1 - j; i1 < paramInt1; i1++)
	      {
	        arrayOfDouble[m] = 0.0D;
	        for (i2 = 1; i2 < paramInt1 - i1; i2++) {
	          arrayOfDouble[m] += paramArrayOfDouble2[i2] * (paramArrayOfDouble1[(m + i2)] - paramArrayOfDouble1[(m - i2)]);
	        }
	        for (i2 = paramInt1 - i1; i2 < i; i2++) {
	          arrayOfDouble[m] += paramArrayOfDouble2[i2] * (paramArrayOfDouble1[(k - i2 - i1 + n * paramInt1)] - paramArrayOfDouble1[(m - i2)]);
	        }
	        m++;
	      }
	    }
	    return arrayOfDouble;
	  }
	  
	  public static float[] convolveOddX(float[] paramArrayOfFloat1, float[] paramArrayOfFloat2, int paramInt1, int paramInt2)
	  {
	    int i = paramArrayOfFloat2.length;
	    int j = i - 1;
	    int k = 2 * paramInt1 - 2;
	    float[] arrayOfFloat = new float[paramInt1 * paramInt2];
	    int m = 0;
	    for (int n = 0; n < paramInt2; n++)
	    {
	      int i2;
	      for (int i1 = 0; i1 < j; i1++)
	      {
	        arrayOfFloat[m] = 0.0F;
	        for (i2 = 1; i2 <= i1; i2++) {
	          arrayOfFloat[m] += paramArrayOfFloat2[i2] * (paramArrayOfFloat1[(m + i2)] - paramArrayOfFloat1[(m - i2)]);
	        }
	        for (i2 = i1 + 1; i2 < i; i2++) {
	          arrayOfFloat[m] += paramArrayOfFloat2[i2] * (paramArrayOfFloat1[(m + i2)] - paramArrayOfFloat1[(i2 - i1 + n * paramInt1)]);
	        }
	        m++;
	      }
	      for (int i1 = j; i1 <= paramInt1 - i; i1++)
	      {
	        arrayOfFloat[m] = 0.0F;
	        for (i2 = 1; i2 < i; i2++) {
	          arrayOfFloat[m] += paramArrayOfFloat2[i2] * (paramArrayOfFloat1[(m + i2)] - paramArrayOfFloat1[(m - i2)]);
	        }
	        m++;
	      }
	      for (int i1 = paramInt1 - j; i1 < paramInt1; i1++)
	      {
	        arrayOfFloat[m] = 0.0F;
	        for (i2 = 1; i2 < paramInt1 - i1; i2++) {
	          arrayOfFloat[m] += paramArrayOfFloat2[i2] * (paramArrayOfFloat1[(m + i2)] - paramArrayOfFloat1[(m - i2)]);
	        }
	        for (i2 = paramInt1 - i1; i2 < i; i2++) {
	          arrayOfFloat[m] += paramArrayOfFloat2[i2] * (paramArrayOfFloat1[(k - i2 - i1 + n * paramInt1)] - paramArrayOfFloat1[(m - i2)]);
	        }
	        m++;
	      }
	    }
	    return arrayOfFloat;
	  }
	  
	  public static double[][] convolveOddY(double[][] paramArrayOfDouble, double[] paramArrayOfDouble1)
	  {
	    int i = paramArrayOfDouble.length;
	    int j = paramArrayOfDouble.length;
	    int k = paramArrayOfDouble1.length;
	    int m = k - 1;
	    int n = 2 * j - 2;
	    double[][] arrayOfDouble = new double[i][j];
	    for (int i1 = 0; i1 < i; i1++)
	    {
	      int i3;
	      for (int i2 = 0; i2 < m; i2++)
	      {
	        arrayOfDouble[i1][i2] = 0.0D;
	        for (i3 = 1; i3 <= i2; i3++) {
	          arrayOfDouble[i1][i2] += paramArrayOfDouble1[i3] * (paramArrayOfDouble[i1][(i2 + i3)] - paramArrayOfDouble[i1][(i2 - i3)]);
	        }
	        for (i3 = i2 + 1; i3 < k; i3++) {
	          arrayOfDouble[i1][i2] += paramArrayOfDouble1[i3] * (paramArrayOfDouble[i1][(i2 + i3)] - paramArrayOfDouble[i1][(i3 - i2)]);
	        }
	      }
	      for (int i2 = m; i2 <= j - k; i2++)
	      {
	        arrayOfDouble[i1][i2] = 0.0D;
	        for (i3 = 1; i3 < k; i3++) {
	          arrayOfDouble[i1][i2] += paramArrayOfDouble1[i3] * (paramArrayOfDouble[i1][(i2 + i3)] - paramArrayOfDouble[i1][(i2 - i3)]);
	        }
	      }
	      for (int i2 = j - m; i2 < j; i2++)
	      {
	        arrayOfDouble[i1][i2] = 0.0D;
	        for (i3 = 1; i3 < j - i2; i3++) {
	          arrayOfDouble[i1][i2] += paramArrayOfDouble1[i3] * (paramArrayOfDouble[i1][(i2 + i3)] - paramArrayOfDouble[i1][(i2 - i3)]);
	        }
	        for (i3 = j - i2; i3 < k; i3++) {
	          arrayOfDouble[i1][i2] += paramArrayOfDouble1[i3] * (paramArrayOfDouble[i1][(n - i3 - i2)] - paramArrayOfDouble[i1][(i2 - i3)]);
	        }
	      }
	    }
	    return arrayOfDouble;
	  }
	  
	  public static double[] convolveOddY(double[] paramArrayOfDouble1, double[] paramArrayOfDouble2, int paramInt1, int paramInt2)
	  {
	    int i = paramArrayOfDouble2.length;
	    int j = i - 1;
	    int k = 2 * paramInt2 - 2;
	    double[] arrayOfDouble = new double[paramInt1 * paramInt2];
	    for (int i1 = 0; i1 < paramInt1; i1++)
	    {
	      int m;
	      int i3;
	      int n;
	      for (int i2 = 0; i2 < j; i2++)
	      {
	        m = i1 + i2 * paramInt1;
	        arrayOfDouble[m] = 0.0D;
	        for (i3 = 1; i3 <= i2; i3++)
	        {
	          n = i3 * paramInt1;
	          arrayOfDouble[m] += paramArrayOfDouble2[i3] * (paramArrayOfDouble1[(m + n)] - paramArrayOfDouble1[(m - n)]);
	        }
	        for (i3 = i2 + 1; i3 < i; i3++) {
	          arrayOfDouble[m] += paramArrayOfDouble2[i3] * (paramArrayOfDouble1[(m + i3 * paramInt1)] - paramArrayOfDouble1[((i3 - i2) * paramInt1 + i1)]);
	        }
	      }
	      for (int i2 = j; i2 <= paramInt2 - i; i2++)
	      {
	        m = i1 + i2 * paramInt1;
	        arrayOfDouble[m] = 0.0D;
	        for (i3 = 1; i3 < i; i3++)
	        {
	          n = i3 * paramInt1;
	          arrayOfDouble[m] += paramArrayOfDouble2[i3] * (paramArrayOfDouble1[(m + n)] - paramArrayOfDouble1[(m - n)]);
	        }
	      }
	      for (int i2 = paramInt2 - j; i2 < paramInt2; i2++)
	      {
	        m = i1 + i2 * paramInt1;
	        arrayOfDouble[m] = 0.0D;
	        for (i3 = 1; i3 < paramInt2 - i2; i3++)
	        {
	          n = i3 * paramInt1;
	          arrayOfDouble[m] += paramArrayOfDouble2[i3] * (paramArrayOfDouble1[(m + n)] - paramArrayOfDouble1[(m - n)]);
	        }
	        for (i3 = paramInt2 - i2; i3 < i; i3++) {
	          arrayOfDouble[m] += paramArrayOfDouble2[i3] * (paramArrayOfDouble1[((k - i3 - i2) * paramInt1 + i1)] - paramArrayOfDouble1[(m - i3 * paramInt1)]);
	        }
	      }
	    }
	    return arrayOfDouble;
	  }
	  
	  public static float[] convolveOddY(float[] paramArrayOfFloat1, float[] paramArrayOfFloat2, int paramInt1, int paramInt2)
	  {
	    int i = paramArrayOfFloat2.length;
	    int j = i - 1;
	    int k = 2 * paramInt2 - 2;
	    float[] arrayOfFloat = new float[paramInt1 * paramInt2];
	    for (int i1 = 0; i1 < paramInt1; i1++)
	    {
	      int m;
	      int i3;
	      int n;
	      for (int i2 = 0; i2 < j; i2++)
	      {
	        m = i1 + i2 * paramInt1;
	        arrayOfFloat[m] = 0.0F;
	        for (i3 = 1; i3 <= i2; i3++)
	        {
	          n = i3 * paramInt1;
	          arrayOfFloat[m] += paramArrayOfFloat2[i3] * (paramArrayOfFloat1[(m + n)] - paramArrayOfFloat1[(m - n)]);
	        }
	        for (i3 = i2 + 1; i3 < i; i3++) {
	          arrayOfFloat[m] += paramArrayOfFloat2[i3] * (paramArrayOfFloat1[(m + i3 * paramInt1)] - paramArrayOfFloat1[((i3 - i2) * paramInt1 + i1)]);
	        }
	      }
	      for (int i2 = j; i2 <= paramInt2 - i; i2++)
	      {
	        m = i1 + i2 * paramInt1;
	        arrayOfFloat[m] = 0.0F;
	        for (i3 = 1; i3 < i; i3++)
	        {
	          n = i3 * paramInt1;
	          arrayOfFloat[m] += paramArrayOfFloat2[i3] * (paramArrayOfFloat1[(m + n)] - paramArrayOfFloat1[(m - n)]);
	        }
	      }
	      for (int i2 = paramInt2 - j; i2 < paramInt2; i2++)
	      {
	        m = i1 + i2 * paramInt1;
	        arrayOfFloat[m] = 0.0F;
	        for (i3 = 1; i3 < paramInt2 - i2; i3++)
	        {
	          n = i3 * paramInt1;
	          arrayOfFloat[m] += paramArrayOfFloat2[i3] * (paramArrayOfFloat1[(m + n)] - paramArrayOfFloat1[(m - n)]);
	        }
	        for (i3 = paramInt2 - i2; i3 < i; i3++) {
	          arrayOfFloat[m] += paramArrayOfFloat2[i3] * (paramArrayOfFloat1[((k - i3 - i2) * paramInt1 + i1)] - paramArrayOfFloat1[(m - i3 * paramInt1)]);
	        }
	      }
	    }
	    return arrayOfFloat;
	  }
	  
	  public static double[] convolveEvenX(double[] paramArrayOfDouble1, double[] paramArrayOfDouble2, int[] paramArrayOfInt, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
	  {
	    int i = paramArrayOfInt[0];
	    int j = paramArrayOfInt[1];
	    int k = i * j;
	    int m = paramArrayOfDouble2.length;
	    int n = m - 1;
	    int i1 = 2 * i - 2;
	    double[] arrayOfDouble = new double[(paramInt2 - paramInt1 + 1) * (paramInt4 - paramInt3 + 1)];
	    int i3 = 0;
	    int i4;
	    int i5;
	    int i2;
	    int i6;
	    if ((paramInt1 < n) && (paramInt2 > i - m)) {
	      for (i4 = paramInt3; i4 <= paramInt4; i4++)
	      {
	        for (i5 = paramInt1; i5 < n; i5++)
	        {
	          i2 = i5 + i4 * i;
	          arrayOfDouble[i3] = (paramArrayOfDouble2[0] * paramArrayOfDouble1[i2]);
	          for (i6 = 1; i6 <= i5; i6++) {
	            arrayOfDouble[i3] += paramArrayOfDouble2[i6] * (paramArrayOfDouble1[(i2 - i6)] + paramArrayOfDouble1[(i2 + i6)]);
	          }
	          for (i6 = i5 + 1; i6 < m; i6++) {
	            arrayOfDouble[i3] += paramArrayOfDouble2[i6] * (paramArrayOfDouble1[(i6 - i5 + i4 * i)] + paramArrayOfDouble1[(i2 + i6)]);
	          }
	          i3++;
	        }
	        for (i5 = n; i5 < paramInt2 - n; i5++)
	        {
	          i2 = i5 + i4 * i;
	          arrayOfDouble[i3] = (paramArrayOfDouble2[0] * paramArrayOfDouble1[i2]);
	          for (i6 = 1; i6 < m; i6++) {
	            arrayOfDouble[i3] += paramArrayOfDouble2[i6] * (paramArrayOfDouble1[(i2 - i6)] + paramArrayOfDouble1[(i2 + i6)]);
	          }
	          i3++;
	        }
	        for (i5 = paramInt2 - n; i5 <= paramInt2; i5++)
	        {
	          i2 = i5 + i4 * i;
	          arrayOfDouble[i3] = (paramArrayOfDouble2[0] * paramArrayOfDouble1[i2]);
	          for (i6 = 1; i6 < i - i5; i6++) {
	            arrayOfDouble[i3] += paramArrayOfDouble2[i6] * (paramArrayOfDouble1[(i2 - i6)] + paramArrayOfDouble1[(i2 + i6)]);
	          }
	          for (i6 = i - i5; i6 < m; i6++) {
	            arrayOfDouble[i3] += paramArrayOfDouble2[i6] * (paramArrayOfDouble1[(i1 - i6 - i5 + i4 * i)] + paramArrayOfDouble1[(i2 - i6)]);
	          }
	          i3++;
	        }
	      }
	    } else if ((paramInt1 < n) && (paramInt2 <= i - m)) {
	      for (i4 = paramInt3; i4 <= paramInt4; i4++)
	      {
	        for (i5 = paramInt1; i5 < n; i5++)
	        {
	          i2 = i5 + i4 * i;
	          arrayOfDouble[i3] = (paramArrayOfDouble2[0] * paramArrayOfDouble1[i2]);
	          for (i6 = 1; i6 <= i5; i6++) {
	            arrayOfDouble[i3] += paramArrayOfDouble2[i6] * (paramArrayOfDouble1[(i2 - i6)] + paramArrayOfDouble1[(i2 + i6)]);
	          }
	          for (i6 = i5 + 1; i6 < m; i6++) {
	            arrayOfDouble[i3] += paramArrayOfDouble2[i6] * (paramArrayOfDouble1[(i6 - i5 + i4 * i)] + paramArrayOfDouble1[(i2 + i6)]);
	          }
	          i3++;
	        }
	        for (i5 = n; i5 <= paramInt2; i5++)
	        {
	          i2 = i5 + i4 * i;
	          arrayOfDouble[i3] = (paramArrayOfDouble2[0] * paramArrayOfDouble1[i2]);
	          for (i6 = 1; i6 < m; i6++) {
	            arrayOfDouble[i3] += paramArrayOfDouble2[i6] * (paramArrayOfDouble1[(i2 - i6)] + paramArrayOfDouble1[(i2 + i6)]);
	          }
	          i3++;
	        }
	      }
	    } else if ((paramInt1 >= n) && (paramInt2 > i - m)) {
	      for (i4 = paramInt3; i4 <= paramInt4; i4++)
	      {
	        for (i5 = paramInt1; i5 < paramInt2 - n; i5++)
	        {
	          i2 = i5 + i4 * i;
	          arrayOfDouble[i3] = (paramArrayOfDouble2[0] * paramArrayOfDouble1[i2]);
	          for (i6 = 1; i6 < m; i6++) {
	            arrayOfDouble[i3] += paramArrayOfDouble2[i6] * (paramArrayOfDouble1[(i2 - i6)] + paramArrayOfDouble1[(i2 + i6)]);
	          }
	          i3++;
	        }
	        for (i5 = paramInt2 - n; i5 <= paramInt2; i5++)
	        {
	          i2 = i5 + i4 * i;
	          arrayOfDouble[i3] = (paramArrayOfDouble2[0] * paramArrayOfDouble1[i2]);
	          for (i6 = 1; i6 < i - i5; i6++) {
	            arrayOfDouble[i3] += paramArrayOfDouble2[i6] * (paramArrayOfDouble1[(i2 - i6)] + paramArrayOfDouble1[(i2 + i6)]);
	          }
	          for (i6 = i - i5; i6 < m; i6++) {
	            arrayOfDouble[i3] += paramArrayOfDouble2[i6] * (paramArrayOfDouble1[(i1 - i6 - i5 + i4 * i)] + paramArrayOfDouble1[(i2 - i6)]);
	          }
	          i3++;
	        }
	      }
	    } else {
	      for (i4 = paramInt3; i4 <= paramInt4; i4++) {
	        for (i5 = paramInt1; i5 <= paramInt2; i5++)
	        {
	          i2 = i5 + i4 * i;
	          arrayOfDouble[i3] = (paramArrayOfDouble2[0] * paramArrayOfDouble1[i2]);
	          for (i6 = 1; i6 < m; i6++) {
	            arrayOfDouble[i3] += paramArrayOfDouble2[i6] * (paramArrayOfDouble1[(i2 - i6)] + paramArrayOfDouble1[(i2 + i6)]);
	          }
	          i3++;
	        }
	      }
	    }
	    return arrayOfDouble;
	  }
	  
	  public static double[] convolveOddX(double[] paramArrayOfDouble1, double[] paramArrayOfDouble2, int[] paramArrayOfInt, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
	  {
	    int i = paramArrayOfInt[0];
	    int j = paramArrayOfInt[1];
	    int k = i * j;
	    int m = paramArrayOfDouble2.length;
	    int n = m - 1;
	    int i1 = 2 * i - 2;
	    double[] arrayOfDouble = new double[(paramInt2 - paramInt1 + 1) * (paramInt4 - paramInt3 + 1)];
	    int i3 = 0;
	    int i4;
	    int i5;
	    int i2;
	    int i6;
	    if ((paramInt1 < n) && (paramInt2 > i - m)) {
	      for (i4 = paramInt3; i4 <= paramInt4; i4++)
	      {
	        for (i5 = paramInt1; i5 < n; i5++)
	        {
	          i2 = i5 + i4 * i;
	          arrayOfDouble[i3] = 0.0D;
	          for (i6 = 1; i6 <= i5; i6++) {
	            arrayOfDouble[i3] += paramArrayOfDouble2[i6] * (paramArrayOfDouble1[(i2 + i6)] - paramArrayOfDouble1[(i2 - i6)]);
	          }
	          for (i6 = i5 + 1; i6 < m; i6++) {
	            arrayOfDouble[i3] += paramArrayOfDouble2[i6] * (paramArrayOfDouble1[(i2 + i6)] - paramArrayOfDouble1[(i6 - i5 + i4 * i)]);
	          }
	          i3++;
	        }
	        for (i5 = n; i5 < paramInt2 - n; i5++)
	        {
	          i2 = i5 + i4 * i;
	          arrayOfDouble[i3] = 0.0D;
	          for (i6 = 1; i6 < m; i6++) {
	            arrayOfDouble[i3] += paramArrayOfDouble2[i6] * (paramArrayOfDouble1[(i2 + i6)] - paramArrayOfDouble1[(i2 - i6)]);
	          }
	          i3++;
	        }
	        for (i5 = paramInt2 - n; i5 <= paramInt2; i5++)
	        {
	          i2 = i5 + i4 * i;
	          arrayOfDouble[i3] = 0.0D;
	          for (i6 = 1; i6 < i - i5; i6++) {
	            arrayOfDouble[i3] += paramArrayOfDouble2[i6] * (paramArrayOfDouble1[(i2 + i6)] - paramArrayOfDouble1[(i2 - i6)]);
	          }
	          for (i6 = i - i5; i6 < m; i6++) {
	            arrayOfDouble[i3] += paramArrayOfDouble2[i6] * (paramArrayOfDouble1[(i1 - i6 - i5 + i4 * i)] - paramArrayOfDouble1[(i2 - i6)]);
	          }
	          i3++;
	        }
	      }
	    } else if ((paramInt1 < n) && (paramInt2 <= i - m)) {
	      for (i4 = paramInt3; i4 <= paramInt4; i4++)
	      {
	        for (i5 = paramInt1; i5 < n; i5++)
	        {
	          i2 = i5 + i4 * i;
	          arrayOfDouble[i3] = 0.0D;
	          for (i6 = 1; i6 <= i5; i6++) {
	            arrayOfDouble[i3] += paramArrayOfDouble2[i6] * (paramArrayOfDouble1[(i2 + i6)] - paramArrayOfDouble1[(i2 - i6)]);
	          }
	          for (i6 = i5 + 1; i6 < m; i6++) {
	            arrayOfDouble[i3] += paramArrayOfDouble2[i6] * (paramArrayOfDouble1[(i2 + i6)] - paramArrayOfDouble1[(i6 - i5 + i4 * i)]);
	          }
	          i3++;
	        }
	        for (i5 = n; i5 <= paramInt2; i5++)
	        {
	          i2 = i5 + i4 * i;
	          arrayOfDouble[i3] = 0.0D;
	          for (i6 = 1; i6 < m; i6++) {
	            arrayOfDouble[i3] += paramArrayOfDouble2[i6] * (paramArrayOfDouble1[(i2 + i6)] - paramArrayOfDouble1[(i2 - i6)]);
	          }
	          i3++;
	        }
	      }
	    } else if ((paramInt1 >= n) && (paramInt2 > i - m)) {
	      for (i4 = paramInt3; i4 <= paramInt4; i4++)
	      {
	        for (i5 = paramInt1; i5 < paramInt2 - n; i5++)
	        {
	          i2 = i5 + i4 * i;
	          arrayOfDouble[i3] = 0.0D;
	          for (i6 = 1; i6 < m; i6++) {
	            arrayOfDouble[i3] += paramArrayOfDouble2[i6] * (paramArrayOfDouble1[(i2 + i6)] - paramArrayOfDouble1[(i2 - i6)]);
	          }
	          i3++;
	        }
	        for (i5 = paramInt2 - n; i5 <= paramInt2; i5++)
	        {
	          i2 = i5 + i4 * i;
	          arrayOfDouble[i3] = 0.0D;
	          for (i6 = 1; i6 < i - i5; i6++) {
	            arrayOfDouble[i3] += paramArrayOfDouble2[i6] * (paramArrayOfDouble1[(i2 + i6)] - paramArrayOfDouble1[(i2 - i6)]);
	          }
	          for (i6 = i - i5; i6 < m; i6++) {
	            arrayOfDouble[i3] += paramArrayOfDouble2[i6] * (paramArrayOfDouble1[(i1 - i6 - i5 + i4 * i)] - paramArrayOfDouble1[(i2 - i6)]);
	          }
	          i3++;
	        }
	      }
	    } else {
	      for (i4 = paramInt3; i4 <= paramInt4; i4++) {
	        for (i5 = paramInt1; i5 <= paramInt2; i5++)
	        {
	          i2 = i5 + i4 * i;
	          arrayOfDouble[i3] = 0.0D;
	          for (i6 = 1; i6 < m; i6++) {
	            arrayOfDouble[i3] += paramArrayOfDouble2[i6] * (paramArrayOfDouble1[(i2 + i6)] - paramArrayOfDouble1[(i2 - i6)]);
	          }
	          i3++;
	        }
	      }
	    }
	    return arrayOfDouble;
	  }
	  
	  public static double[] convolveEvenY(double[] paramArrayOfDouble1, double[] paramArrayOfDouble2, int[] paramArrayOfInt, int paramInt1, int paramInt2)
	  {
	    int i = paramArrayOfInt[0];
	    int j = paramArrayOfInt[1];
	    int k = i * j;
	    int m = paramArrayOfDouble2.length;
	    int n = m - 1;
	    int i1 = 2 * j - 2;
	    double[] arrayOfDouble = new double[i * (paramInt2 - paramInt1 + 1)];
	    int i4 = 0;
	    int i5;
	    int i6;
	    int i2;
	    int i7;
	    int i3;
	    if ((paramInt1 < n) && (paramInt2 > j - m)) {
	      for (i5 = 0; i5 < i; i5++)
	      {
	        for (i6 = paramInt1; i6 < n; i6++)
	        {
	          i2 = i5 + i6 * i;
	          i4 = i2 - paramInt1 * i;
	          arrayOfDouble[i4] = (paramArrayOfDouble2[0] * paramArrayOfDouble1[i2]);
	          for (i7 = 1; i7 <= i6; i7++)
	          {
	            i3 = i7 * i;
	            arrayOfDouble[i4] += paramArrayOfDouble2[i7] * (paramArrayOfDouble1[(i2 - i3)] + paramArrayOfDouble1[(i2 + i3)]);
	          }
	          for (i7 = i6 + 1; i7 < m; i7++) {
	            arrayOfDouble[i4] += paramArrayOfDouble2[i7] * (paramArrayOfDouble1[((i7 - i6) * i + i5)] + paramArrayOfDouble1[(i2 + i7 * i)]);
	          }
	        }
	        for (i6 = n; i6 < paramInt2 - n; i6++)
	        {
	          i2 = i5 + i6 * i;
	          i4 = i2 - paramInt1 * i;
	          arrayOfDouble[i4] = (paramArrayOfDouble2[0] * paramArrayOfDouble1[i2]);
	          for (i7 = 1; i7 < m; i7++)
	          {
	            i3 = i7 * i;
	            arrayOfDouble[i4] += paramArrayOfDouble2[i7] * (paramArrayOfDouble1[(i2 - i3)] + paramArrayOfDouble1[(i2 + i3)]);
	          }
	        }
	        for (i6 = paramInt2 - n; i6 <= paramInt2; i6++)
	        {
	          i2 = i5 + i6 * i;
	          i4 = i2 - paramInt1 * i;
	          arrayOfDouble[i4] = (paramArrayOfDouble2[0] * paramArrayOfDouble1[i2]);
	          for (i7 = 1; i7 < j - i6; i7++)
	          {
	            i3 = i7 * i;
	            arrayOfDouble[i4] += paramArrayOfDouble2[i7] * (paramArrayOfDouble1[(i2 - i3)] + paramArrayOfDouble1[(i2 + i3)]);
	          }
	          for (i7 = j - i6; i7 < m; i7++) {
	            arrayOfDouble[i4] += paramArrayOfDouble2[i7] * (paramArrayOfDouble1[((i1 - i7 - i6) * i + i5)] + paramArrayOfDouble1[(i2 - i7 * i)]);
	          }
	        }
	      }
	    } else if ((paramInt1 < n) && (paramInt2 <= j - m)) {
	      for (i5 = 0; i5 < i; i5++)
	      {
	        for (i6 = paramInt1; i6 < n; i6++)
	        {
	          i2 = i5 + i6 * i;
	          i4 = i2 - paramInt1 * i;
	          arrayOfDouble[i4] = (paramArrayOfDouble2[0] * paramArrayOfDouble1[i2]);
	          for (i7 = 1; i7 <= i6; i7++)
	          {
	            i3 = i7 * i;
	            arrayOfDouble[i4] += paramArrayOfDouble2[i7] * (paramArrayOfDouble1[(i2 - i3)] + paramArrayOfDouble1[(i2 + i3)]);
	          }
	          for (i7 = i6 + 1; i7 < m; i7++) {
	            arrayOfDouble[i4] += paramArrayOfDouble2[i7] * (paramArrayOfDouble1[((i7 - i6) * i + i5)] + paramArrayOfDouble1[(i2 + i7 * i)]);
	          }
	        }
	        for (i6 = n; i6 <= paramInt2; i6++)
	        {
	          i2 = i5 + i6 * i;
	          i4 = i2 - paramInt1 * i;
	          arrayOfDouble[i4] = (paramArrayOfDouble2[0] * paramArrayOfDouble1[i2]);
	          for (i7 = 1; i7 < m; i7++)
	          {
	            i3 = i7 * i;
	            arrayOfDouble[i4] += paramArrayOfDouble2[i7] * (paramArrayOfDouble1[(i2 - i3)] + paramArrayOfDouble1[(i2 + i3)]);
	          }
	        }
	      }
	    } else if ((paramInt1 >= n) && (paramInt2 > j - m)) {
	      for (i5 = 0; i5 < i; i5++)
	      {
	        for (i6 = paramInt1; i6 < j - n; i6++)
	        {
	          i2 = i5 + i6 * i;
	          i4 = i2 - paramInt1 * i;
	          arrayOfDouble[i4] = (paramArrayOfDouble2[0] * paramArrayOfDouble1[i2]);
	          for (i7 = 1; i7 < m; i7++)
	          {
	            i3 = i7 * i;
	            arrayOfDouble[i4] += paramArrayOfDouble2[i7] * (paramArrayOfDouble1[(i2 - i3)] + paramArrayOfDouble1[(i2 + i3)]);
	          }
	        }
	        for (i6 = j - n; i6 <= paramInt2; i6++)
	        {
	          i2 = i5 + i6 * i;
	          i4 = i2 - paramInt1 * i;
	          arrayOfDouble[i4] = (paramArrayOfDouble2[0] * paramArrayOfDouble1[i2]);
	          for (i7 = 1; i7 < j - i6; i7++)
	          {
	            i3 = i7 * i;
	            arrayOfDouble[i4] += paramArrayOfDouble2[i7] * (paramArrayOfDouble1[(i2 - i3)] + paramArrayOfDouble1[(i2 + i3)]);
	          }
	          for (i7 = j - i6; i7 < m; i7++) {
	            arrayOfDouble[i4] += paramArrayOfDouble2[i7] * (paramArrayOfDouble1[((i1 - i7 - i6) * i + i5)] + paramArrayOfDouble1[(i2 - i7 * i)]);
	          }
	        }
	      }
	    } else {
	      for (i5 = 0; i5 < i; i5++) {
	        for (i6 = paramInt1; i6 <= paramInt2; i6++)
	        {
	          i2 = i5 + i6 * i;
	          i4 = i2 - paramInt1 * i;
	          arrayOfDouble[i4] = (paramArrayOfDouble2[0] * paramArrayOfDouble1[i2]);
	          for (i7 = 1; i7 < m; i7++)
	          {
	            i3 = i7 * i;
	            arrayOfDouble[i4] += paramArrayOfDouble2[i7] * (paramArrayOfDouble1[(i2 - i3)] + paramArrayOfDouble1[(i2 + i3)]);
	          }
	        }
	      }
	    }
	    return arrayOfDouble;
	  }
	  
	  public static double[] convolveOddY(double[] paramArrayOfDouble1, double[] paramArrayOfDouble2, int[] paramArrayOfInt, int paramInt1, int paramInt2)
	  {
	    int i = paramArrayOfInt[0];
	    int j = paramArrayOfInt[1];
	    int k = i * j;
	    int m = paramArrayOfDouble2.length;
	    int n = m - 1;
	    int i1 = 2 * j - 2;
	    double[] arrayOfDouble = new double[i * (paramInt2 - paramInt1 + 1)];
	    int i4 = 0;
	    int i5;
	    int i6;
	    int i2;
	    int i7;
	    int i3;
	    if ((paramInt1 < n) && (paramInt2 > j - m)) {
	      for (i5 = 0; i5 < i; i5++)
	      {
	        for (i6 = paramInt1; i6 < n; i6++)
	        {
	          i2 = i5 + i6 * i;
	          i4 = i5 + (i6 - paramInt1) * i;
	          arrayOfDouble[i4] = 0.0D;
	          for (i7 = 1; i7 <= i6; i7++)
	          {
	            i3 = i7 * i;
	            arrayOfDouble[i4] += paramArrayOfDouble2[i7] * (paramArrayOfDouble1[(i2 + i3)] - paramArrayOfDouble1[(i2 - i3)]);
	          }
	          for (i7 = i6 + 1; i7 < m; i7++) {
	            arrayOfDouble[i4] += paramArrayOfDouble2[i7] * (paramArrayOfDouble1[(i2 + i7 * i)] - paramArrayOfDouble1[((i7 - i6) * i + i5)]);
	          }
	        }
	        for (i6 = n; i6 <= j - m; i6++)
	        {
	          i2 = i5 + i6 * i;
	          i4 = i5 + (i6 - paramInt1) * i;
	          arrayOfDouble[i4] = 0.0D;
	          for (i7 = 1; i7 < m; i7++)
	          {
	            i3 = i7 * i;
	            arrayOfDouble[i4] += paramArrayOfDouble2[i7] * (paramArrayOfDouble1[(i2 + i3)] - paramArrayOfDouble1[(i2 - i3)]);
	          }
	        }
	        for (i6 = j - n; i6 <= paramInt2; i6++)
	        {
	          i2 = i5 + i6 * i;
	          i4 = i5 + (i6 - paramInt1) * i;
	          arrayOfDouble[i4] = 0.0D;
	          for (i7 = 1; i7 < j - i6; i7++)
	          {
	            i3 = i7 * i;
	            arrayOfDouble[i4] += paramArrayOfDouble2[i7] * (paramArrayOfDouble1[(i2 + i3)] - paramArrayOfDouble1[(i2 - i3)]);
	          }
	          for (i7 = j - i6; i7 < m; i7++) {
	            arrayOfDouble[i4] += paramArrayOfDouble2[i7] * (paramArrayOfDouble1[((i1 - i7 - i6) * i + i5)] - paramArrayOfDouble1[(i2 - i7 * i)]);
	          }
	        }
	      }
	    } else if ((paramInt1 < n) && (paramInt2 <= j - m)) {
	      for (i5 = 0; i5 < i; i5++)
	      {
	        for (i6 = paramInt1; i6 < n; i6++)
	        {
	          i2 = i5 + i6 * i;
	          i4 = i5 + (i6 - paramInt1) * i;
	          arrayOfDouble[i4] = 0.0D;
	          for (i7 = 1; i7 <= i6; i7++)
	          {
	            i3 = i7 * i;
	            arrayOfDouble[i4] += paramArrayOfDouble2[i7] * (paramArrayOfDouble1[(i2 + i3)] - paramArrayOfDouble1[(i2 - i3)]);
	          }
	          for (i7 = i6 + 1; i7 < m; i7++) {
	            arrayOfDouble[i4] += paramArrayOfDouble2[i7] * (paramArrayOfDouble1[(i2 + i7 * i)] - paramArrayOfDouble1[((i7 - i6) * i + i5)]);
	          }
	        }
	        for (i6 = n; i6 <= paramInt2; i6++)
	        {
	          i2 = i5 + i6 * i;
	          i4 = i5 + (i6 - paramInt1) * i;
	          arrayOfDouble[i4] = 0.0D;
	          for (i7 = 1; i7 < m; i7++)
	          {
	            i3 = i7 * i;
	            arrayOfDouble[i4] += paramArrayOfDouble2[i7] * (paramArrayOfDouble1[(i2 + i3)] - paramArrayOfDouble1[(i2 - i3)]);
	          }
	        }
	      }
	    } else if ((paramInt1 >= n) && (paramInt2 > j - m)) {
	      for (i5 = 0; i5 < i; i5++)
	      {
	        for (i6 = paramInt1; i6 <= j - m; i6++)
	        {
	          i2 = i5 + i6 * i;
	          i4 = i5 + (i6 - paramInt1) * i;
	          arrayOfDouble[i4] = 0.0D;
	          for (i7 = 1; i7 < m; i7++)
	          {
	            i3 = i7 * i;
	            arrayOfDouble[i4] += paramArrayOfDouble2[i7] * (paramArrayOfDouble1[(i2 + i3)] - paramArrayOfDouble1[(i2 - i3)]);
	          }
	        }
	        for (i6 = j - n; i6 < paramInt2; i6++)
	        {
	          i2 = i5 + i6 * i;
	          i4 = i5 + (i6 - paramInt1) * i;
	          arrayOfDouble[i4] = 0.0D;
	          for (i7 = 1; i7 < j - i6; i7++)
	          {
	            i3 = i7 * i;
	            arrayOfDouble[i4] += paramArrayOfDouble2[i7] * (paramArrayOfDouble1[(i2 + i3)] - paramArrayOfDouble1[(i2 - i3)]);
	          }
	          for (i7 = j - i6; i7 < m; i7++) {
	            arrayOfDouble[i4] += paramArrayOfDouble2[i7] * (paramArrayOfDouble1[((i1 - i7 - i6) * i + i5)] - paramArrayOfDouble1[(i2 - i7 * i)]);
	          }
	        }
	      }
	    } else {
	      for (i5 = 0; i5 < i; i5++) {
	        for (i6 = paramInt1; i6 <= paramInt2; i6++)
	        {
	          i2 = i5 + i6 * i;
	          i4 = i5 + (i6 - paramInt1) * i;
	          arrayOfDouble[i4] = 0.0D;
	          for (i7 = 1; i7 < m; i7++)
	          {
	            i3 = i7 * i;
	            arrayOfDouble[i4] += paramArrayOfDouble2[i7] * (paramArrayOfDouble1[(i2 + i3)] - paramArrayOfDouble1[(i2 - i3)]);
	          }
	        }
	      }
	    }
	    return arrayOfDouble;
	  }
	
	
}

package utility;

public class Interpolator
{
  private double[] input;
  private double[] c;
  private int nx;
  private int ny;
  private String mode;
  private double a;
  private double c0;
  
  public Interpolator(double[] paramArrayOfDouble, int paramInt1, int paramInt2, String paramString)
  {
    input = paramArrayOfDouble;
    nx = paramInt1;
    ny = paramInt2;
    mode = paramString;
    if (paramString.equals("linear"))
    {
      c = paramArrayOfDouble;
    }
    else if (paramString.equals("quadratic"))
    {
      c0 = 8.0D;
      a = (-3.0D + 2.0D * Math.sqrt(2.0D));
      computeCoefficients();
    }
    else if (paramString.equals("cubic"))
    {
      c0 = 6.0D;
      a = (-2.0D + Math.sqrt(3.0D));
      computeCoefficients();
    }
  }
  
  public double getValue(double paramDouble1, double paramDouble2)
  {
    int i = (int)paramDouble1;
    int j = (int)paramDouble2;
    int m, i1;
    if (mode.equals("linear"))
    {
       double  d1 = paramDouble1 - i;
      double d2 = paramDouble2 - j;
      if (paramDouble1 < 0.0D)
      {
        d1 = -d1;
     m = mirror(i - 1, nx);
      }
      else
      {
       m = mirror(i + 1, nx);
      }
      if (paramDouble2 < 0.0D)
      {
        d2 = -d2;
        i1 = mirror(j - 1, ny);
      }
      else
      {
        i1 = mirror(j + 1, ny);
      }
      int k = mirror(i, nx);
      int n = mirror(j, ny);
      return (1.0D - d2) * (d1 * input[(m + n * nx)] + (1.0D - d1) * input[(k + n * nx)]) + d2 * (d1 * input[(m + i1 * nx)] + (1.0D - d1) * input[(k + i1 * nx)]);
    }
    double d1 = paramDouble1 - i;
    double d2 = paramDouble2 - j;
    if (paramDouble1 < 0.0D)
    {
      i -= 1;
      d1 = 1.0D + d1;
    }
    if (paramDouble2 < 0.0D)
    {
      j -= 1;
      d2 = 1.0D + d2;
    }
    double[] arrayOfDouble1;
    double[] arrayOfDouble2;
    if (mode.equals("quadratic"))
    {
      arrayOfDouble1 = getQuadraticSpline(d1);
      arrayOfDouble2 = getQuadraticSpline(d2);
    }
    else
    {
      arrayOfDouble1 = getCubicSpline(d1);
      arrayOfDouble2 = getCubicSpline(d2);
    }
    int k = i - 1;
     m = i;
    int i2 = i + 1;
    int i3 = i + 2;
    k = mirror(k, nx);
    m = mirror(m, nx);
    i2 = mirror(i2, nx);
    i3 = mirror(i3, nx);
    int n = j - 1;
    i1 = j;
    int i4 = j + 1;
    int i5 = j + 2;
    n = mirror(n, ny);
    i1 = mirror(i1, ny);
    i4 = mirror(i4, ny);
    i5 = mirror(i5, ny);
    n *= nx;
    i1 *= nx;
    i4 *= nx;
    i5 *= nx;
    double d3 = arrayOfDouble1[0] * (arrayOfDouble2[0] * c[(k + n)] + arrayOfDouble2[1] * c[(k + i1)] + arrayOfDouble2[2] * c[(k + i4)] + arrayOfDouble2[3] * c[(k + i5)]) + arrayOfDouble1[1] * (arrayOfDouble2[0] * c[(m + n)] + arrayOfDouble2[1] * c[(m + i1)] + arrayOfDouble2[2] * c[(m + i4)] + arrayOfDouble2[3] * c[(m + i5)]) + arrayOfDouble1[2] * (arrayOfDouble2[0] * c[(i2 + n)] + arrayOfDouble2[1] * c[(i2 + i1)] + arrayOfDouble2[2] * c[(i2 + i4)] + arrayOfDouble2[3] * c[(i2 + i5)]) + arrayOfDouble1[3] * (arrayOfDouble2[0] * c[(i3 + n)] + arrayOfDouble2[1] * c[(i3 + i1)] + arrayOfDouble2[2] * c[(i3 + i4)] + arrayOfDouble2[3] * c[(i3 + i5)]);
    return d3;
  }
  
  private int mirror(int paramInt1, int paramInt2)
  {
    if ((paramInt1 >= 0) && (paramInt1 < paramInt2)) {
      return paramInt1;
    }
    if (paramInt1 < 0) {
      return -paramInt1;
    }
    return 2 * paramInt2 - 2 - paramInt1;
  }
  
  private void computeCoefficients()
  {
    double[] arrayOfDouble = getCausalInitHorizontal(input, a);
    int k;
    for (int j = 0; j < ny; j++) {
    	int i;
      for (k = 1; k < nx; k++)
      {
        i = k + j * nx;
        arrayOfDouble[i] = (input[i] + a * arrayOfDouble[(i - 1)]);
      }
    }
    c = getAntiCausalInitHorizontal(arrayOfDouble, a);
    for (int j = 0; j < ny; j++) {
    	int i;
      for (k = nx - 2; k >= 0; k--)
      {
        i = k + j * nx;
        c[i] = (a * (c[(i + 1)] - arrayOfDouble[i]));
      }
    }
    arrayOfDouble = getCausalInitVertical(c, a);
    for (int j = 0; j < nx; j++) {
    	int i;
      for (k = 1; k < ny; k++)
      {
        i = j + k * nx;
        arrayOfDouble[i] = (c[i] + a * arrayOfDouble[(i - nx)]);
      }
    }
    c = getAntiCausalInitVertical(arrayOfDouble, a);
    for (int j = 0; j < nx; j++) {
    	int i;
      for (k = ny - 2; k >= 0; k--)
      {
        i = j + k * nx;
        c[i] = (a * (c[(i + nx)] - arrayOfDouble[i]));
      }
    }
    double d = c0 * c0;
    for (int i = 0; i < ny * ny; i++) {
      c[i] = (d * c[i]);
    }
  }
  
  private static double[] getQuadraticSpline(double paramDouble)
  {
    if ((paramDouble < 0.0D) || (paramDouble > 1.0D)) {
      throw new ArrayStoreException("Argument t for quadratic B-spline outside of expected range [0, 1]: " + paramDouble);
    }
    double[] arrayOfDouble = new double[4];
    if (paramDouble <= 0.5D)
    {
      arrayOfDouble[0] = ((paramDouble - 0.5D) * (paramDouble - 0.5D) / 2.0D);
      arrayOfDouble[1] = (0.75D - paramDouble * paramDouble);
      arrayOfDouble[2] = (1.0D - arrayOfDouble[1] - arrayOfDouble[0]);
      arrayOfDouble[3] = 0.0D;
    }
    else
    {
      arrayOfDouble[0] = 0.0D;
      arrayOfDouble[1] = ((paramDouble - 1.5D) * (paramDouble - 1.5D) / 2.0D);
      arrayOfDouble[3] = ((paramDouble - 0.5D) * (paramDouble - 0.5D) / 2.0D);
      arrayOfDouble[2] = (1.0D - arrayOfDouble[3] - arrayOfDouble[1]);
    }
    return arrayOfDouble;
  }
  
  private static double[] getCubicSpline(double paramDouble)
  {
    if ((paramDouble < 0.0D) || (paramDouble > 1.0D)) {
      throw new ArrayStoreException("Argument t for cubic B-spline outside of expected range [0, 1]: " + paramDouble);
    }
    double[] arrayOfDouble = new double[4];
    double d1 = 1.0D - paramDouble;
    double d2 = paramDouble * paramDouble;
    arrayOfDouble[0] = (d1 * d1 * d1 / 6.0D);
    arrayOfDouble[1] = (0.6666666666666666D + 0.5D * d2 * (paramDouble - 2.0D));
    arrayOfDouble[3] = (d2 * paramDouble / 6.0D);
    arrayOfDouble[2] = (1.0D - arrayOfDouble[3] - arrayOfDouble[1] - arrayOfDouble[0]);
    return arrayOfDouble;
  }
  
  private double[] getAntiCausalInitVertical(double[] paramArrayOfDouble, double paramDouble)
  {
    double[] arrayOfDouble = new double[nx * ny];
    int i = (ny - 1) * nx;
    double d = paramDouble * paramDouble - 1.0D;
    for (int j = 0; j < nx; j++) {
      arrayOfDouble[(j + i)] = (paramDouble * (paramArrayOfDouble[(j + i)] + paramDouble * paramArrayOfDouble[(j + i - nx)]) / d);
    }
    return arrayOfDouble;
  }
  
  private double[] getAntiCausalInitHorizontal(double[] paramArrayOfDouble, double paramDouble)
  {
    double[] arrayOfDouble = new double[nx * ny];
    double d = paramDouble * paramDouble - 1.0D;
    for (int i = 0; i < ny; i++) {
      arrayOfDouble[(nx - 1 + i * nx)] = (paramDouble * (paramArrayOfDouble[(nx - 1 + i * nx)] + paramDouble * paramArrayOfDouble[(nx - 2 + i * nx)]) / d);
    }
    return arrayOfDouble;
  }
  
  private double[] getCausalInitVertical(double[] paramArrayOfDouble, double paramDouble)
  {
    double[] arrayOfDouble = new double[nx * ny];
    for (int i = 0; i < nx; i++)
    {
      double d1 = Math.pow(paramDouble, ny - 1);
      double d2 = paramArrayOfDouble[i] + paramArrayOfDouble[(i + (ny - 1) * nx)] * d1;
      double d3 = d1 * d1;
      d1 = d3 / paramDouble;
      double d4 = paramDouble;
      for (int j = 1; j < ny - 1; j++)
      {
        d2 += paramArrayOfDouble[(i + j * nx)] * (d4 + d1);
        d4 *= paramDouble;
        d1 /= paramDouble;
      }
      d2 /= (1.0D - d3);
      arrayOfDouble[i] = d2;
    }
    return arrayOfDouble;
  }
  
  private double[] getCausalInitHorizontal(double[] paramArrayOfDouble, double paramDouble)
  {
    double[] arrayOfDouble = new double[nx * ny];
    for (int i = 0; i < ny; i++)
    {
      double d1 = Math.pow(paramDouble, nx - 1);
      double d2 = paramArrayOfDouble[(i * nx)] + paramArrayOfDouble[(nx - 1 + i * nx)] * d1;
      double d3 = d1 * d1;
      d1 = d3 / paramDouble;
      double d4 = paramDouble;
      for (int j = 1; j < nx - 1; j++)
      {
        d2 += paramArrayOfDouble[(j + i * nx)] * (d4 + d1);
        d4 *= paramDouble;
        d1 /= paramDouble;
      }
      d2 /= (1.0D - d3);
      arrayOfDouble[(i * nx)] = d2;
    }
    return arrayOfDouble;
  }
}

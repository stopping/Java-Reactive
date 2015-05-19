package org.auvua.agent.control;

public class Transform {
  
  public static double[] fft (double[] x) {
    return toDoubleArray(fft(toComplexArray(x)));
  }
  
  public static Complex[] fft (Complex[] x) {
    int n = x.length;
    if ((n & n - 1) != 0) return null;
    if (n <= 32) return dftSlow(x);
    
    Complex[] xEven = fft(everyOther(x,0));
    Complex[] xOdd = fft(everyOther(x,1));
    Complex[] factor = buildFactor(n);
    Complex[] factorLeft = range(factor, 0, n / 2);
    Complex[] factorRight = range(factor, n / 2, n);
    Complex[] left = add(xEven, mult(factorLeft, xOdd));
    Complex[] right = add(xEven, mult(factorRight, xOdd));
    
    return concat(left, right);
  }
  
  public static Complex[] dftSlow (Complex[] x) {
    int len = x.length;
    Complex[] out = new Complex[len];
    for (int k = 0; k < len; k++) {
      out[k] = new Complex(0,0);
      for (int n = 0; n < len; n++) {
        double cos = Math.cos(-2.0 * Math.PI * k * n / len);
        double sin = Math.sin(-2.0 * Math.PI * k * n / len);
        out[k].a += x[n].a * cos - x[n].b * sin;
        out[k].b += x[n].b * cos + x[n].a * sin;
      }
    }
    return out;
  }
  
  public static Complex[] everyOther(Complex[] arr, int start) {
    Complex[] out = new Complex[arr.length / 2];
    for (int i = 0; i < out.length; i++) {
      Complex num = arr[start + i * 2];
      out[i] = new Complex(num.a, num.b);
    }
    return out;
  }
  
  public static Complex[] add (Complex[] a, Complex[] b) {
    Complex[] c = new Complex[a.length];
    for (int i = 0; i < a.length; i++) {
      c[i] = new Complex(a[i].a + b[i].a, a[i].b + b[i].b);
    }
    return c;
  }
  
  public static Complex[] sub (Complex[] a, Complex[] b) {
    Complex[] c = new Complex[a.length];
    for (int i = 0; i < a.length; i++) {
      c[i] = new Complex(a[i].a - b[i].a, a[i].b - b[i].b);
    }
    return c;
  }
  
  public static Complex[] mult (Complex[] a, Complex[] b) {
    Complex[] c = new Complex[a.length];
    for (int i = 0; i < a.length; i++) {
      double x = a[i].a * b[i].a - a[i].b * b[i].b;
      double y = a[i].b * b[i].a + a[i].a * b[i].b;
      c[i] = new Complex(x, y);
    }
    return c;
  }
  
  public static Complex[] concat (Complex[] a, Complex[] b) {
    Complex[] c = new Complex[a.length + b.length];
    for (int i = 0; i < a.length; i++) {
      c[i] = a[i];
    }
    for (int i = 0; i < b.length; i++) {
      c[i + a.length] = b[i];
    }
    return c;
  }
  
  public static Complex[] buildFactor (int n) {
    Complex[] out = new Complex[n];
    for (int i = 0; i < n; i++) {
      out[i] = new Complex(Math.cos(-2.0 * Math.PI * i / n), Math.sin(-2.0 * Math.PI * i / n));
    }
    return out;
  }
  
  public static Complex[] range (Complex[] arr, int start, int end) {
    int n = end - start;
    Complex[] out = new Complex[n];
    for (int i = 0; i < n; i++) {
      Complex num = arr[start + i];
      out[i] = new Complex(num.a, num.b);
    }
    return out;
  }
  
  public static double[] toDoubleArray (Complex[] arr) {
    double[] out = new double[arr.length];
    for (int i = 0; i < arr.length; i++) {
      out[i] = Math.hypot(arr[i].a, arr[i].b);
    }
    return out;
  }
  
  
  public static Complex[] toComplexArray (double[] arr) {
    Complex[] out = new Complex[arr.length];
    for (int i = 0; i < arr.length; i++) {
      out[i] = new Complex(arr[i], 0.0);
    }
    return out;
  }
  
}

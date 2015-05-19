package org.auvua.agent.control;

public class Complex {
  
  public double a;
  public double b;
  
  public Complex (double a, double b) {
    this.a = a;
    this.b = b;
  }
  
  public static Complex build (double a, double b) {
    return new Complex(a,b);
  }
  
}

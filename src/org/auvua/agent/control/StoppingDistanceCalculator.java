package org.auvua.agent.control;

public class StoppingDistanceCalculator {
  
  private double t1;
  private double t2;
  private double v0;
  private double a0;
  private double jm;
  private double x;
  private boolean up;
  
  public StoppingDistanceCalculator(double v0, double a0, double jm) {
    this.v0 = v0;
    this.a0 = a0;
    this.jm = jm;
    
    double p = .5 * a0 * Math.abs(a0) / jm;
    double r = Math.abs(a0 / jm);
    if (p + v0 < 0) {
      // use up
      up = true;
      t2 = Math.sqrt(.5 * r * r - v0 / jm);
      if (a0 > 0) {
        t1 = t2 - r;
      } else {
        t1 = t2 + r;
      }
    } else {
      // use down
      up = false;
      t2 = Math.sqrt(.5 * r * r + v0 / jm);
      if (a0 > 0) {
        t1 = t2 + r;
      } else {
        t1 = t2 - r;
      }
    }
    double t = t1 + t2;
    
    if (!up) jm = -jm;
    x = jm / 3 * t1 * t1 * t1
        + (v0 - jm * t1 * t1) * t
        + (a0 / 2 + jm * t1) * t * t
        - jm / 6 * t * t * t;
  }
  
  public double getPosition(double t) {
    double jm = this.jm;
    if (!up) jm = - jm;
    if (t < 0) return 0;
    if (t < t1) {
      return v0 * t
          + .5 * a0 * t * t
          + jm / 6 * t * t * t;
    }
    if (t < t1 + t2) {
      return jm / 3 * t1 * t1 * t1
          + (v0 - jm * t1 * t1) * t
          + (a0 / 2 + jm * t1) * t * t
          - jm / 6 * t * t * t;
    }
    return x;   
  }
  
  public double finalPosition() {
    return x;
  }
  
  public double finalTime() {
    return t1 + t2;
  }
  
}

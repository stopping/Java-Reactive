package org.auvua.agent.control;

import org.auvua.reactive.core.RxVar;

public class StoppingDistance extends RxVar<Double> {
  
  public StoppingDistance(RxVar<Double> v, RxVar<Double> a, double aLimit, double jLimit) {
    /*
    this.setSupplier(() -> {
      double v0 = v.get();
      double a0 = a.get();
      double am = aLimit;
      double jm = jLimit;
      boolean up;
      double t1, t2;
      // The velocity change with linear velocity change from a0 to 0
      double vd = .5 * a0 * Math.abs(a0) / jm;
      double r = Math.abs(a0 / jm);
      if (vd + v0 < 0) {
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
      return jm / 3 * t1 * t1 * t1
          + (v0 - jm * t1 * t1) * t
          + (a0 / 2 + jm * t1) * t * t
          - jm / 6 * t * t * t;
    });
    */
    this.setSupplier(() -> {
      double v0 = v.get();
      double a0 = a.get();
      double am = aLimit;
      double jm = jLimit;
      double t, t1, t2, t3, ta;
      // The velocity change with linear velocity change from a0 to 0
      double vd = .5 * a0 * Math.abs(a0) / jm;
      t3 = am / jm;
      boolean up = vd + v0 < 0;
      if (up) {
        ta = (am - a0) / jm;
        t = (jm * (ta * ta + t3 * t3) / 2 - v0) / (am);
        t2 = t + ta;
      } else {
        ta = (am + a0) / jm;
        t = (-jm * (ta * ta + t3 * t3) / 2 - v0) / (-am);
        t2 = t - ta;
      }
      
      //System.out.printf("v0: %12.4f a0: %12.4f am: %12.4f jm: %12.4f\n", v0, a0, am, jm);
      //System.out.printf("t: %12.4f t2: %12.4f t3: %12.4f ta: %12.4f\n", t, t2, t3, ta);
      
      if (t > t2 && t2 > t3) {
        double f = jm / 6 * (t * t * t - t2 * t2 * t2 - t3 * t3 * t3);
        f = up ? f : -f;
        return v0 * t + a0 * t * t / 2 + f;
      } else {
        double r = Math.abs(a0 / jm);
        if (up) {
          t2 = Math.sqrt(.5 * r * r - v0 / jm);
          if (a0 > 0) {
            t1 = t2 - r;
          } else {
            t1 = t2 + r;
          }
        } else {
          t2 = Math.sqrt(.5 * r * r + v0 / jm);
          if (a0 > 0) {
            t1 = t2 + r;
          } else {
            t1 = t2 - r;
          }
        }
      }
      t = t1 + t2;
      
      if (!up) jm = -jm;
      
      return jm / 3 * t1 * t1 * t1
          + (v0 - jm * t1 * t1) * t
          + (a0 / 2 + jm * t1) * t * t
          - jm / 6 * t * t * t;
    });
  }
  
}

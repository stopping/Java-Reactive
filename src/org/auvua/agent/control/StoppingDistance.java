package org.auvua.agent.control;

import org.auvua.reactive.core.RxVar;

public class StoppingDistance extends RxVar<Double> {
  
  public StoppingDistance(RxVar<Double> v, RxVar<Double> a, double jLimit) {
    this.setSupplier(() -> {
      double v0 = v.get();
      double a0 = a.get();
      double jm = jLimit;
      boolean up;
      double t1, t2;
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
      return jm / 3 * t1 * t1 * t1
          + (v0 - jm * t1 * t1) * t
          + (a0 / 2 + jm * t1) * t * t
          - jm / 6 * t * t * t;
    });
  }
  
}

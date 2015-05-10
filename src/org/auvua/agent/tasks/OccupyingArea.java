package org.auvua.agent.tasks;

import org.auvua.agent.TwoVector;
import org.auvua.agent.control.Timer;
import org.auvua.reactive.RxCondition;

public class OccupyingArea extends RxCondition {
  private boolean inAreaPrev;
  private double entranceTime;

  public OccupyingArea(TwoVector position, TwoVector target, double radius) {
    inAreaPrev = false;
    
    this.setSupplier(() -> {
      double x = position.x.get();
      double y = position.y.get();
      double time = Timer.getInstance().get();
      boolean inArea = Math.hypot(x - target.x.get(), y - target.y.get()) < radius;
      double t = 0;
      if(inArea) {
        if(!inAreaPrev) {
          entranceTime = time;
        }
        t = time - entranceTime;
      }
      inAreaPrev = inArea;
      return t > 1.0;
    });
  }
  
}

package org.auvua.agent.control;

import org.auvua.reactive.core.RxVar;


public class Timer extends RxVar<Double> {

  private double prevTimeReal;
  private double prevTimeAdjusted;
  private double factor = 1.0;
  private static Timer instance;
  
  public static Timer getInstance() {
    if(instance == null) {
      instance = new Timer();
    }
    return instance;
  }

  private Timer() {
    prevTimeReal = System.nanoTime() / 1000000000.0;
    prevTimeAdjusted = 0.0;
    setNoSync(prevTimeAdjusted);
  }

  public void trigger() {
    double currTimeReal = System.nanoTime() / 1000000000.0;
    double diff = currTimeReal - prevTimeReal;
    prevTimeAdjusted += diff * factor;
    set(prevTimeAdjusted);
    prevTimeReal = currTimeReal;
  }
  
  public void scale(double factor) {
    this.factor = factor;
  }

}

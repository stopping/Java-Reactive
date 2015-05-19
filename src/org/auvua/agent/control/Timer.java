package org.auvua.agent.control;

import org.auvua.reactive.core.RxVar;


public class Timer extends RxVar<Double> {

  private double startTime;
  private static Timer instance;
  
  public static Timer getInstance() {
    if(instance == null) {
      instance = new Timer();
    }
    return instance;
  }

  private Timer() {
    startTime = System.nanoTime() / 1000000000.0;
    setNoSync(0.0);
  }

  public void trigger() {
    set(System.nanoTime() / 1000000000.0 - startTime);
  }

}

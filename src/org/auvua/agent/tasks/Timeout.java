package org.auvua.agent.tasks;

import org.auvua.agent.control.Timer;
import org.auvua.reactive.core.RxCondition;

public class Timeout extends RxCondition {

  public Timeout(double time) {
    double startTime = Timer.getInstance().get();
    
    this.setSupplier(() -> {
      return Timer.getInstance().get() >= startTime + time;
    });
  }
  
}

package org.auvua.agent.signal;

import java.util.function.Supplier;

import org.auvua.agent.control.Timer;
import org.auvua.reactive.core.RxVar;

public class Duration extends RxVar<Boolean> {
  
  private double startTime;
  
  public Duration(Supplier<Boolean> supplier, double duration) {
    startTime = Timer.getInstance().get();
    
    this.setSupplier(() -> {
      double time = Timer.getInstance().get();
      if (!supplier.get()) startTime = time;
      return startTime >= duration;
    });
  }
  
}

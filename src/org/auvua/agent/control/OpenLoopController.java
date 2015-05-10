package org.auvua.agent.control;
import org.auvua.reactive.RxVar;

public class OpenLoopController extends RxVar<Double> {
  
  public OpenLoopController(double val) {
    Timer time = Timer.getInstance();
    
    this.setSupplier(() -> {
      time.get();
      return val;
    });
  }
  
}

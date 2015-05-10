package org.auvua.agent.control;

import org.auvua.agent.control.Timer;
import org.auvua.reactive.RxVar;

public class FirstOrderSystem extends RxVar<Double> {
  
  double lastTime;
  double output = 0;
  
  public FirstOrderSystem(RxVar<Double> measuredQuantity, double tau) {
    lastTime = Timer.getInstance().get();
    
    this.setSupplier(() -> {
      double time = Timer.getInstance().get();
      double dT = time - lastTime;
      output *= (1 - dT * tau);
      output += dT * tau * measuredQuantity.get();
      lastTime = time;
      return output;
    });
  }
}
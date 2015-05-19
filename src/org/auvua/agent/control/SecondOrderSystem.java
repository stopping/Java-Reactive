package org.auvua.agent.control;

import java.util.function.Supplier;

import org.auvua.agent.control.Timer;
import org.auvua.reactive.core.RxVar;

public class SecondOrderSystem extends RxVar<Double> {
  
  double lastTime;
  double outputReal = 0;
  double outputIm = 0;
  
  public SecondOrderSystem(Supplier<Double> in, double tauReal, double tauIm) {
    lastTime = Timer.getInstance().get();
    
    this.setSupplier(() -> {
      double time = Timer.getInstance().get();
      double dt = time - lastTime;
      double input = in.get();
      
      double outputRealNew = outputReal * (1 - dt * tauReal);
      outputRealNew += outputIm * dt * tauIm;
      outputRealNew += dt * (tauReal * input);
      
      double outputImNew = outputIm * (1 - dt * tauReal);
      outputImNew -= outputReal * dt * tauIm;
      outputImNew += dt * (tauIm * input);
      
      outputReal = outputRealNew;
      outputIm = outputImNew;
      lastTime = time;
      return outputReal;
    });
  }
}

package org.auvua.agent.simulator;

import java.util.Random;

import org.auvua.agent.control.Timer;
import org.auvua.reactive.core.RxVar;

public class Sensor extends RxVar<Double> {
  
  double lastTime;
  double output = 0;
  double noise = .25;
  
  public Sensor(RxVar<Double> measuredQuantity) {
    Random random = new Random();
    lastTime = Timer.getInstance().get();
    double tau = 20;
    
    this.setSupplier(() -> {
      double time = Timer.getInstance().get();
      double dT = time - lastTime;
      output *= (1 - dT * tau);
      output += dT * tau * measuredQuantity.get();
      lastTime = time;
      return output + random.nextGaussian() * 100;
    });
  }
}

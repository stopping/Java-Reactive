package org.auvua.agent.control;
import org.auvua.reactive.core.RxVar;

public class RateLimiter extends RxVar<Double> {
  
  private Timer timer = Timer.getInstance();
  private double lastTime;
  private double lastVal;
  
  public RateLimiter(RxVar<Double> var, double rateLimit) {
    lastTime = timer.get();
    lastVal = var.get();
    
    this.setSupplier(() -> {
      double time = timer.get();
      double val = var.get();
      if (time != lastTime) {
        double deltaT = time - lastTime;
        double rate = (val - lastVal) / deltaT;
        if (rate > rateLimit) {
          lastVal += deltaT * rateLimit;
        } else if (rate < -rateLimit) {
          lastVal += deltaT * -rateLimit;
        } else {
          lastVal = val;
        }
      } else {
        //System.out.println("Undefined rate");
        lastVal = val;
      }
      lastTime = time;
      return lastVal;
    });
  }

}

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
      double deltaT = time - lastTime;
      double val = var.get();
      double rate = (val - lastVal) / deltaT;
      if (rate > rateLimit) {
        lastVal += deltaT * rateLimit;
      } else if (rate < -rateLimit) {
        lastVal += deltaT * -rateLimit;
      } else {
        lastVal = val;
      }
      lastTime = time;
      var.setNoSync(lastVal);
      return lastVal;
    });
  }

}

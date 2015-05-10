package org.auvua.agent.control;


import java.util.LinkedList;
import java.util.List;

import org.auvua.reactive.RxVar;

public class Limiter extends RxVar<Double> {
  
  private RxVar<Double> var;
  private List<RxVar<Double>> limiters = new LinkedList<RxVar<Double>>();
  
  public Limiter(RxVar<Double> var) {
    this.var = var;
    this.setSupplier(var);
  }
  
  public Limiter hard(double lowerLimit, double upperLimit) {
    limiters.add(new HardLimit(var, lowerLimit, upperLimit));
    
    this.setSupplier(() -> {
      for(RxVar<Double> limiter : limiters) {
        limiter.get();
      }
      return var.get();
    });
    
    return this;
  }
  
  public Limiter rate(double rateLimit) {
    limiters.add(new RateLimiter(var, rateLimit));
    
    this.setSupplier(() -> {
      for(RxVar<Double> limiter : limiters) {
        limiter.get();
      }
      return var.get();
    });
    
    return this;
  }
}

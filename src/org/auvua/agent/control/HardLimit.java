package org.auvua.agent.control;
import org.auvua.reactive.RxVar;

public class HardLimit extends RxVar<Double> {
  
  public HardLimit(RxVar<Double> var, double lowerLimit, double upperLimit) {
    setSupplier(() -> {
      double value = var.get();
      if(value < lowerLimit) value = lowerLimit;
      if(value > upperLimit) value = upperLimit;
      var.setNoSync(value);
      return value;
    });
  }
  
}

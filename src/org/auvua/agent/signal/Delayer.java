package org.auvua.agent.signal;

import java.util.LinkedList;
import java.util.List;

import org.auvua.agent.control.Timer;
import org.auvua.reactive.core.RxVar;

public class Delayer extends RxVar<Double> {

  List<Double> times = new LinkedList<Double>();
  List<Double> values = new LinkedList<Double>();
  
  double preTime;
  double preValue;
  
  public Delayer(RxVar<Double> var, double delay) {
    Timer timer = Timer.getInstance();
    preTime = timer.get();
    preValue = var.get();
    times.add(preTime);
    values.add(preValue);
    
    this.setSupplier(() -> {
      double time = timer.get();
      double value = var.get();
      times.add(time);
      values.add(value);
      prepareLists(time - delay);
      double interpVal = interpolate(time - delay);
      return interpVal;
    });
  }
  
  private double interpolate(double t) {
    if (t < preTime) return preValue;
    double dt = times.get(0) - preTime;
    double dv = values.get(0) - preValue;
    return preValue + (t - preTime) * dv / dt;
  }
  
  private void prepareLists(double t) {
    while (times.get(0) < t) {
      preTime = times.get(0);
      preValue = values.get(0);
      times.remove(0);
      values.remove(0);
    }
  }
}

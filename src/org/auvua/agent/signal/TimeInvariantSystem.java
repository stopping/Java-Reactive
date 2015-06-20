package org.auvua.agent.signal;

import java.util.function.Supplier;

import org.auvua.reactive.core.RxVar;

public class TimeInvariantSystem extends RxVar<Double> {
  
  public TimeInvariantSystem(RxVar<Double> input) {
    this.setSupplier(() -> input.get());
  }
  
  public void addZero(double real, double im) {
    Supplier<Double> oldSystem = this.getSupplier();
    if (im == 0) {
      RxVar<Double> differentiator = new Differentiator(oldSystem);
      this.setSupplier(() -> {
        return differentiator.get() - real * oldSystem.get();
      });
    } else {
      RxVar<Double> diff = new Differentiator(oldSystem);
      RxVar<Double> doubleDiff = new Differentiator(diff);
      double a = 2 * real;
      double b = real * real + im * im;
      this.setSupplier(() -> {
        return doubleDiff.get() - a * diff.get() + b * oldSystem.get();
      });
    }
  }
  
  public void addPole(double real, double im) {
    Supplier<Double> oldSystem = this.getSupplier();
    RxVar<Double> newSystem = new SecondOrderSystem(oldSystem, -real, im);
    this.setSupplier(() -> {
      return newSystem.get();
    });
  }
  
  public void scale(double factor) {
    Supplier<Double> oldSystem = this.getSupplier();
    this.setSupplier(() -> {
      return oldSystem.get() * factor;
    });
  }
  
}

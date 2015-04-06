package org.auvua.reactive;

import java.util.function.Supplier;

public class MovingAverageExponential implements Supplier<Double>  {
  
  private Variable<Double> var;
  private double average = 0;
  private double decay = 0.01;
  
  public MovingAverageExponential(Variable<Double> var, double decay) {
    this.var = var;
    this.decay = decay;
  }

  @Override
  public Double get() {
    double value = var.val();
    average = decay * value + (1 - decay) * average;
    return average;
  }

}

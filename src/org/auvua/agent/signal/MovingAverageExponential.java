package org.auvua.agent.signal;

import java.util.function.Supplier;

public class MovingAverageExponential implements Supplier<Double>  {
  
  private Supplier<Double> var;
  private double average = 0;
  private double decay = 0.01;
  
  public MovingAverageExponential(Supplier<Double> var, double decay) {
    this.var = var;
    this.decay = decay;
  }

  @Override
  public Double get() {
    double value = var.get();
    average = decay * value + (1 - decay) * average;
    return average;
  }

}

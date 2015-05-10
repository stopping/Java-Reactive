package org.auvua.agent.control;

import java.util.function.Supplier;

public class MovingAverageSimple implements Supplier<Double>  {
  
  private Supplier<Double> var;
  
  private int index = 0;
  private int saveNum;
  private double[] lastValues;
  private double average = 0;
  
  public MovingAverageSimple(Supplier<Double> var, int saveNum) {
    this.var = var;
    this.saveNum = saveNum;
    this.lastValues = new double[saveNum];
  }

  @Override
  public Double get() {
    double value = var.get();
    average += (value - lastValues[index]) / saveNum;
    lastValues[index] = value;
    index++;
    index %= saveNum;
    return average;
  }

}

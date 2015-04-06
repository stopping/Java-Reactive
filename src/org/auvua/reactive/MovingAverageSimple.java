package org.auvua.reactive;

import java.util.function.Supplier;

public class MovingAverageSimple implements Supplier<Double>  {
  
  private Variable<Double> var;
  
  private int index = 0;
  private int saveNum;
  private double[] lastValues;
  private double average = 0;
  
  public MovingAverageSimple(Variable<Double> var, int saveNum) {
    this.var = var;
    this.saveNum = saveNum;
    this.lastValues = new double[saveNum];
  }

  @Override
  public Double get() {
    double value = var.val();
    average += (value - lastValues[index]) / saveNum;
    lastValues[index] = value;
    index++;
    index %= saveNum;
    return average;
  }

}

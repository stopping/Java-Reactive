package org.auvua.reactive;


public class Timer implements Variable<Double> {

  double startTime;

  public Timer() {
    startTime = System.nanoTime() / 1000000000.0;
  }

  @Override
  public Double val() {
    return System.nanoTime() / 1000000000.0 - startTime;
  }

  @Override
  public void set(Double value) {
    // Do nothing
  }



}

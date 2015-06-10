package org.auvua.reactive.demo;

import org.auvua.reactive.core.R;
import org.auvua.reactive.core.RxValve;

public class RxValveTest {
  public static void main (String[] args) {
    RxValve<Double> valve = R.valve(0.0);
    valve.setSupplier(() -> {
      return valve.get() + 1;
    });
    
    R.task(() -> {
      System.out.println(valve.get());
    });
    
    for(int i = 0; i < 100; i++) {
      valve.trigger();
    }
  }
}

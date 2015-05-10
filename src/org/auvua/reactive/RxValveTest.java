package org.auvua.reactive;

public class RxValveTest {
  public static void main (String[] args) {
    RxValve<Double> valve = Rx.valve(0.0);
    valve.setSupplier(() -> {
      return valve.get() + 1;
    });
    
    Rx.task(() -> {
      System.out.println(valve.get());
    });
    
    for(int i = 0; i < 100; i++) {
      valve.trigger();
    }
  }
}

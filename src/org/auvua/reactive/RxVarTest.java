package org.auvua.reactive;

import java.util.Scanner;

public class RxVarTest {

  public static void main( String[] args ) {
    RxVar<Double> var1 = Rx.var(0.0);
    RxVar<Double> var2 = Rx.var(0.0);
    RxVar<Double> var3 = Rx.var(0.0);

    RxVar<Double> sum = Rx.var(() -> {
      return var1.get() + var2.get() + var3.get();
    });

    Rx.task(() -> {
      System.out.println("The sum: " + sum.get());
    });

    Rx.task(() -> {
      if(sum.get() > 5.0) {
        System.out.println("The sum of these numbers is greater than 5!");
      }
    });

    Scanner input = new Scanner(System.in);

    for (int i = 0; i < 10; i++) {
      Rx.doSync(() -> {
        var1.set(input.nextDouble());
        var2.set(input.nextDouble());
        var3.set(input.nextDouble());
      });
    }

    input.close();
  }

}

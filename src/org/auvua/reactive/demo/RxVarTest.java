package org.auvua.reactive.demo;

import java.util.Scanner;

import org.auvua.reactive.core.R;
import org.auvua.reactive.core.RxVar;

public class RxVarTest {

  public static void main( String[] args ) {
    RxVar<Double> var1 = R.var(0.0);
    RxVar<Double> var2 = R.var(0.0);
    RxVar<Double> var3 = R.var(0.0);

    RxVar<Double> sum = R.var(() -> {
      return var1.get() + var2.get() + var3.get();
    });

    R.task(() -> {
      System.out.println("The sum: " + sum.get());
    });

    R.task(() -> {
      if(sum.get() > 5.0) {
        System.out.println("The sum of these numbers is greater than 5!");
      }
    });

    Scanner input = new Scanner(System.in);

    for (int i = 0; i < 10; i++) {
      R.doSync(() -> {
        var1.set(input.nextDouble());
        var2.set(input.nextDouble());
        var3.set(input.nextDouble());
      });
    }

    input.close();
  }

}

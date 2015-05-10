package org.auvua.reactive;

import java.util.Scanner;

public class RxThreadTest {

  public static void main( String[] args ) {
    Rx.initialize(2);
    
    Var<Double> x = new Var<Double>(0.0);
    int count = 1000000;
    RxVar<Double> var1 = Rx.var(1.0);
    RxVar<Double> var2 = Rx.var(2.0);
    RxVar<Double> var3 = Rx.var(3.0);
    RxVar<Double> var4 = Rx.var(4.0);
    RxVar<Double> var5 = Rx.var(5.0);
    RxVar<Double> var6 = Rx.var(6.0);
    RxVar<Double> var7 = Rx.var(7.0);
    RxVar<Double> var8 = Rx.var(8.0);
    
    RxVar<Double> dep1 = Rx.var(() -> {
      double a = 1;
      for (int i = 0; i < count; i++) {
        a *= 1 + 1.0 / count;
      }
      return var1.get() + var2.get() + var3.get() + var4.get() + var5.get() + var6.get() + var7.get() + var8.get() + a;
    });
    
    RxVar<Double> dep2 = Rx.var(() -> {
      double a = 1;
      for (int i = 0; i < count; i++) {
        a *= 1 + 1.0 / count;
      }
      return var1.get() + var2.get() + var3.get() + var4.get() + var5.get() + var6.get() + var7.get() + var8.get() - a;
    });
    
    RxVar<Double> dep3 = Rx.var(() -> {
      double a = 1;
      for (int i = 0; i < count; i++) {
        a *= 1 + 1.0 / count;
      }
      return var1.get() + var2.get() + var3.get() + var4.get() + var5.get() + var6.get() + var7.get() + var8.get() * a;
    });
    
    RxVar<Double> dep4 = Rx.var(() -> {
      double a = 1;
      for (int i = 0; i < count; i++) {
        a *= 1 + 1.0 / count;
      }
      return var1.get() + var2.get() + var3.get() + var4.get() + var5.get() + var6.get() + var7.get() + var8.get() / a;
    });
    
    RxVar<Double> dep5 = Rx.var(() -> {
      double a = 1;
      for (int i = 0; i < count; i++) {
        a *= 1 + 1.0 / count;
      }
      return var1.get() + var2.get() + var3.get() + var4.get() + var5.get() + var6.get() + var7.get() + var8.get() + a;
    });
    
    RxVar<Double> dep6 = Rx.var(() -> {
      double a = 1;
      for (int i = 0; i < count; i++) {
        a *= 1 + 1.0 / count;
      }
      return var1.get() + var2.get() + var3.get() + var4.get() + var5.get() + var6.get() + var7.get() + var8.get() - a;
    });
    
    RxVar<Double> dep7 = Rx.var(() -> {
      double a = 1;
      for (int i = 0; i < count; i++) {
        a *= 1 + 1.0 / count;
      }
      return var1.get() + var2.get() + var3.get() + var4.get() + var5.get() + var6.get() + var7.get() + var8.get() * a;
    });
    
    RxVar<Double> dep8 = Rx.var(() -> {
      double a = 1;
      for (int i = 0; i < count; i++) {
        a *= 1 + 1.0 / count;
      }
      return var1.get() + var2.get() + var3.get() + var4.get() + var5.get() + var6.get() + var7.get() + var8.get() / a;
    });

    Rx.task(() -> {
      System.out.println(dep1.get());
      System.out.println(dep2.get());
      System.out.println(dep3.get());
      System.out.println(dep4.get());
      System.out.println(dep5.get());
      System.out.println(dep6.get());
      System.out.println(dep7.get());
      System.out.println(dep8.get());
    });

    Scanner input = new Scanner(System.in);
    
    long startTime = System.currentTimeMillis();
    
    for (int i = 0; i < 1000; i++) {
      Rx.doSync(() -> {
        var1.set(increment(x));
        var2.set(increment(x));
        var3.set(increment(x));
        var4.set(increment(x));
        var5.set(increment(x));
        var6.set(increment(x));
        var7.set(increment(x));
        var8.set(increment(x));
      });
      System.out.println("    " + i);
    }
    
    System.out.println("Total time: " + (System.currentTimeMillis() - startTime) / 1000.0);

    input.close();
  }
  
  private static Double increment(Var<Double> var) {
    var.set(var.get() + 1);
    return var.get();
  }

}

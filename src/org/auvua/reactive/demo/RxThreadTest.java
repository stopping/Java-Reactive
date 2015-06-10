package org.auvua.reactive.demo;

import java.util.Scanner;

import org.auvua.reactive.core.R;
import org.auvua.reactive.core.RxVar;
import org.auvua.reactive.core.Var;

public class RxThreadTest {

  public static void main( String[] args ) {
    R.initialize(4);
    
    Var<Double> x = new Var<Double>(0.0);
    int count = 1000;
    int iter = 1000000000 / count;
    RxVar<Double> var1 = R.var(1.0);
    RxVar<Double> var2 = R.var(2.0);
    RxVar<Double> var3 = R.var(3.0);
    RxVar<Double> var4 = R.var(4.0);
    RxVar<Double> var5 = R.var(5.0);
    RxVar<Double> var6 = R.var(6.0);
    RxVar<Double> var7 = R.var(7.0);
    RxVar<Double> var8 = R.var(8.0);
    
    RxVar<Double> dep1 = R.var(() -> {
      double a = 1;
      for (int i = 0; i < count; i++) {
        a *= 1 + 1.0 / count;
      }
      return var1.get() + var2.get() + var3.get() + var4.get() + var5.get() + var6.get() + var7.get() + var8.get() + a;
    });
    
    RxVar<Double> dep2 = R.var(() -> {
      double a = 1;
      for (int i = 0; i < count; i++) {
        a *= 1 + 1.0 / count;
      }
      return var1.get() + var2.get() + var3.get() + var4.get() + var5.get() + var6.get() + var7.get() + var8.get() - a;
    });
    
    RxVar<Double> dep3 = R.var(() -> {
      double a = 1;
      for (int i = 0; i < count; i++) {
        a *= 1 + 1.0 / count;
      }
      return var1.get() + var2.get() + var3.get() + var4.get() + var5.get() + var6.get() + var7.get() + var8.get() * a;
    });
    
    RxVar<Double> dep4 = R.var(() -> {
      double a = 1;
      for (int i = 0; i < count; i++) {
        a *= 1 + 1.0 / count;
      }
      return var1.get() + var2.get() + var3.get() + var4.get() + var5.get() + var6.get() + var7.get() + var8.get() / a;
    });
    
    RxVar<Double> dep5 = R.var(() -> {
      double a = 1;
      for (int i = 0; i < count; i++) {
        a *= 1 + 1.0 / count;
      }
      return var1.get() + var2.get() + var3.get() + var4.get() + var5.get() + var6.get() + var7.get() + var8.get() + a;
    });
    
    RxVar<Double> dep6 = R.var(() -> {
      double a = 1;
      for (int i = 0; i < count; i++) {
        a *= 1 + 1.0 / count;
      }
      return var1.get() + var2.get() + var3.get() + var4.get() + var5.get() + var6.get() + var7.get() + var8.get() - a;
    });
    
    RxVar<Double> dep7 = R.var(() -> {
      double a = 1;
      for (int i = 0; i < count; i++) {
        a *= 1 + 1.0 / count;
      }
      return var1.get() + var2.get() + var3.get() + var4.get() + var5.get() + var6.get() + var7.get() + var8.get() * a;
    });
    
    RxVar<Double> dep8 = R.var(() -> {
      double a = 1;
      for (int i = 0; i < count; i++) {
        a *= 1 + 1.0 / count;
      }
      return var1.get() + var2.get() + var3.get() + var4.get() + var5.get() + var6.get() + var7.get() + var8.get() / a;
    });
    
    System.out.println("" + var1 + var2 + var3 + var4);
    System.out.println("" + var5 + var6 + var7 + var8);
    System.out.println("" + dep1 + dep2 + dep3 + dep4);
    System.out.println("" + dep5 + dep6 + dep7 + dep8);
    

    /*
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
    */
    
    Scanner input = new Scanner(System.in);
    
    long startTime = System.currentTimeMillis();
    
    for (int i = 0; i < iter; i++) {
      R.doSync(() -> {
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

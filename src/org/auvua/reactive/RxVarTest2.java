package org.auvua.reactive;

import java.util.ArrayList;
import java.util.Scanner;

public class RxVarTest2 {

  public static void main(String[] args) {
    RxVar<ArrayList<Double>> rxList = Rx.var(new ArrayList<Double>());
    
    Rx.task(() -> {
      System.out.println(rxList.get().toString());
    });
    
    Scanner input = new Scanner(System.in);
    
    while(input.hasNextDouble()) {
      rxList.mod((list) -> {
        list.add(input.nextDouble());
        list.replaceAll((val) -> {
          return val - 1;
        });
      });
    }
    
    input.close();
  }

}

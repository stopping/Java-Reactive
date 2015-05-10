package org.auvua.reactive;

import java.util.function.Supplier;

public class RxCondition extends RxVar<Boolean> {
  public RxCondition() {
    super(false);
  }
  
  public RxCondition(boolean b) {
    super(b);
  }
  
  public RxCondition(Supplier<Boolean> supplier) {
    super(supplier);
  }
  
  public void triggers(Runnable ... tasks) {
    new RxTaskBuilder().when(this).then(tasks);
  }
}

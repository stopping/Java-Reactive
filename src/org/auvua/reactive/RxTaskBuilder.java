package org.auvua.reactive;

import java.util.function.Supplier;

import org.auvua.reactive.Rx;

public class RxTaskBuilder {
  
  public RxTaskTrigger when(Supplier<Boolean> var) {
    return new RxTaskTrigger(var);
  }
  
  public class RxTaskTrigger {
    private Supplier<Boolean> predicate;
    
    public RxTaskTrigger(Supplier<Boolean> predicate) {
      this.predicate = predicate;
    }
    
    public RxTaskTrigger then(Runnable ... tasks) {
      Rx.task(() -> {
        if(predicate.get()) {
          for (Runnable task : tasks) {
            task.run();
          }
        }
      });
      return this;
    }
  }
  
}

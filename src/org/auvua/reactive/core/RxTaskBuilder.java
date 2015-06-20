package org.auvua.reactive.core;

import java.util.function.Supplier;

public class RxTaskBuilder {
  
  public RxTaskTrigger when(Supplier<Boolean> var) {
    return new RxTaskTrigger(var);
  }
  
  public class RxTaskTrigger {
    private Supplier<Boolean> predicate;
    
    public RxTaskTrigger(Supplier<Boolean> predicate) {
      this.predicate = predicate;
    }
    
    public RxTaskTrigger then(Runnable ... runnables) {
      RxTask task = R.task();
      task.setRunnable(() -> {
        if(predicate.get()) {
          for (Runnable runnable : runnables) {
            runnable.run();
          }
          task.setRunnable(() -> {});
        }
      });
      return this;
    }
  }
  
}

package org.auvua.reactive.core;

import java.util.Set;

public class RxTask extends StandardDependency implements ReactiveDependency, Runnable {

  private Runnable runnable;

  public RxTask(Runnable runnable) {
    this.clear();
    this.runnable = runnable;
    
    if(Rx.isDetectingNewDependencies()) {
      Rx.addNewDependency(this);
    }
    
    Set<ReactiveDependency> previousDependencies = Rx.getGetDependenciesAndClear();

    Rx.startDetectingGets();
    update();
    Rx.stopDetectingGets();

    for(ReactiveDependency dep : Rx.getGetDependenciesAndClear()) {
      this.add(dep);
    }
    for(ReactiveDependency dep : previousDependencies) {
      Rx.addThreadLocalGetDependency(dep);
    }
  }

  @Override
  public void update() {
    synchronized(this) {
      runnable.run();
      finishUpdate();
      this.notifyAll();
    }
  }

  @Override
  public void run() {
    Rx.doSync(this.runnable);
  }

  @Override
  public void awaitUpdate() {
    synchronized(this) {
      while(isUpdating()) {
        try {
          this.wait();
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }
  }
}

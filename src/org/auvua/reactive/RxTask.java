package org.auvua.reactive;

import java.util.Collection;

public class RxTask implements ReactiveDependency, Runnable {

  private StandardDependency dependency = new StandardDependency(this);
  private Runnable runnable;

  public RxTask() {}

  public RxTask(Runnable runnable) {
    dependency.clear();
    this.runnable = runnable;

    Rx.startDetectingGets();
    update();
    Rx.stopDetectingGets();

    for(ReactiveDependency dep : Rx.getThreadLocalGetDependenciesAndClear()) {
      dependency.add(dep);
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
  public Collection<ReactiveDependency> getParents() {
    return dependency.getParents();
  }

  @Override
  public Collection<ReactiveDependency> getChildren() {
    return dependency.getChildren();
  }

  @Override
  public void run() {
    Rx.doSync(this.runnable);
  }

  @Override
  public Runnable getUpdateRunner() {
    return dependency.getUpdateRunner();
  }
  
  @Override
  public void prepareUpdate() {
    dependency.prepareUpdate();
  }

  @Override
  public void finishUpdate() {
    dependency.finishUpdate();
  }

  @Override
  public boolean isUpdating() {
    return dependency.isUpdating();
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

package org.auvua.reactive.core;

public class RxTask extends StandardDependency implements ReactiveDependency, Runnable {

  private Runnable runnable;

  public RxTask(Runnable runnable) {
    this.clear();
    this.runnable = runnable;
    
    determineDependencies();
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
    R.doSync(this.runnable);
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

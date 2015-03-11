package org.auvua.reactive;

import java.util.List;

public class RxTask implements ReactiveDependency {

  private StandardDependency dependency = new StandardDependency(this);
  private Runnable runnable;

  public RxTask() {}

  public RxTask(Runnable runnable) {
    dependency.clear();
    this.runnable = runnable;

    Rx.startDetectingGetDependencies();
    update();
    Rx.stopDetectingGetDependencies();

    for(ReactiveDependency dep : Rx.getThreadLocalGetDependenciesAndClear()) {
      dependency.add(dep);
    }
  }

  @Override
  public void update() {
    runnable.run();
  }

  @Override
  public List<ReactiveDependency> getParents() {
    return dependency.getParents();
  }

  @Override
  public List<ReactiveDependency> getChildren() {
    return dependency.getChildren();
  }
}

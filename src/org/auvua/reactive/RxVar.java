package org.auvua.reactive;

import java.util.Collection;
import java.util.function.Supplier;

public class RxVar<E> implements ReactiveVariable<E> {

  private Supplier<E> function;
  private StandardDependency dependency = new StandardDependency(this);
  private Var<E> var = new Var<E>();

  public RxVar() {
    var.set(null);
  }

  public RxVar(E value) {
    var.set(value);
  }

  public RxVar(Supplier<E> function) {
    setFunction(function);
  }

  @Override
  public void update() {
    synchronized(this) {
      setNoSync(function.get());
      finishUpdate();
      this.notifyAll();
    }
  }

  /**
   * Returns the value contained within this variable. If the reactive
   * controller is detecting dependencies via use of the val() method, then add
   * this variable to the thread-local pool of get-dependencies.
   * 
   * @return the value of the object held within this variable
   */
  @Override
  public E val() {
    if(Rx.isDetectingGets()) {
      Rx.addThreadLocalGetDependency(this);
    }
    awaitUpdate();
    return var.val();
  }

  /**
   * Similar to val(), but does not add this object to the get-dependency pool
   * (mainly for purposes where updates to this object should not spawn
   * cascading updates).
   * 
   * @return the value of the object held within this variable
   */
  public E peek() {
    awaitUpdate();
    return var.val();
  }

  /**
   * Sets the supplier function for this variable. This function will be used
   * to calculate the value of this variable when one or more of its children
   * are updated. Executes the supplier function once in order to automatically
   * detect dependencies and calculate the initial value of this variable.
   * 
   * @param function the supplier function for this variable.
   */
  public void setFunction(Supplier<E> function) {
    dependency.clear();
    this.function = function;

    Rx.startDetectingGets();
    update();
    Rx.stopDetectingGets();

    for(ReactiveDependency dep : Rx.getThreadLocalGetDependenciesAndClear()) {
      dependency.add(dep);
    }
  }

  public void setNoSync(E value) {
    var.set(value);
  }

  @Override
  public void set(E value) {
    if(Rx.isDetectingSets()) {
      Rx.addThreadLocalSetDependency(this);
      setNoSync(value);
    } else if(Rx.isDetectingGets()) {
      setNoSync(value);
    } else {
      Rx.doSync(() -> { this.set(value); });
    }
  }

  /**
   * A simple synchronization barrier for threads awaiting updates on this
   * object. Threads will wait until this object receives an update before
   * proceeding.
   */
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

  @Override
  public Collection<ReactiveDependency> getParents() {
    return dependency.getParents();
  }

  @Override
  public Collection<ReactiveDependency> getChildren() {
    return dependency.getChildren();
  }

  @Override
  public Runnable getUpdateRunner() {
    return dependency.getUpdateRunner();
  }

  @Override
  public void prepareUpdate() {
    awaitUpdate();
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

}

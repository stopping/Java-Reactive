package org.auvua.reactive;

import java.util.List;
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
    setNoSync(function.get());
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
    if(Rx.isDetectingGetDependencies()) {
      Rx.addThreadLocalGetDependency(this);
    }
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

    Rx.startDetectingGetDependencies();
    update();
    Rx.stopDetectingGetDependencies();

    for(ReactiveDependency dep : Rx.getThreadLocalGetDependenciesAndClear()) {
      dependency.add(dep);
    }
  }

  public void setNoSync(E value) {
    var.set(value);
    synchronized(this) {
      notifyAll();
    }
  }

  @Override
  public void set(E value) {
    if(Rx.isDetectingSetDependencies()) {
      Rx.addThreadLocalSetDependency(this);
      setNoSync(value);
    } else {
      Rx.doSync(() -> { this.set(value); });
    }
  }

  public void await(E value) {
    synchronized(this) {
      while(val() != value) {
        try {
          this.wait();
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }
  }

  /**
   * A simple synchronization barrier for threads awaiting updates on this
   * object. Threads will wait until this object receives an update before
   * proceeding.
   */
  public void awaitUpdate() {
    synchronized(this) {
      try {
        this.wait();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
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

package org.auvua.reactive.core;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class RxVar<E> extends StandardDependency implements ReactiveVariable<E> {

  private Supplier<E> supplier;
  private Var<E> var = new Var<E>();

  public RxVar() {
    if(R.isDetectingNewDependencies()) {
      R.addNewDependency(this);
    } 
    var.set(null);
  }

  public RxVar(E value) {
    if(R.isDetectingNewDependencies()) {
      R.addNewDependency(this);
    }
    var.set(value);
  }

  public RxVar(Supplier<E> supplier) {
    if(R.isDetectingNewDependencies()) {
      R.addNewDependency(this);
    }
    setSupplier(supplier);
  }

  @Override
  public void update() {
    synchronized(this) {
      setNoSync(supplier.get());
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
  public E get() {
    if(R.isDetectingGets()) {
      R.addThreadLocalGetDependency(this);
    }
    awaitUpdate();
    return var.get();
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
    return var.get();
  }

  /**
   * Sets the supplier function for this variable. This function will be used
   * to calculate the value of this variable when one or more of its children
   * are updated. Executes the supplier function once in order to automatically
   * detect dependencies and calculate the initial value of this variable.
   * 
   * @param supplier the supplier function for this variable.
   */
  public void setSupplier(Supplier<E> supplier) {
    this.clear();
    this.supplier = supplier;
    
    determineDependencies();
  }
  
  public Supplier<E> getSupplier() {
    return supplier;
  }

  public void setNoSync(E value) {
    var.set(value);
  }

  @Override
  public void set(E value) {
    if(R.isDetectingSets()) {
      R.addThreadLocalSetDependency(this);
      setNoSync(value);
    } else if(R.isDetectingGets()) {
      setNoSync(value);
    } else {
      R.doSync(() -> { this.set(value); });
    }
  }
  
  /**
   * Perform an operation on the value of this variable and propagate updates
   * upstream. Typically used if the value stored by this variable is an object
   * which needs to be interacted with without necessarily re-assigning a new
   * object to this reactive variable.
   * 
   * @param consumer - The consumer function to apply
   */
  public void mod(Consumer<E> consumer) {
    consumer.accept(get());
    set(get());
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

}

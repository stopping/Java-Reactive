package org.auvua.reactive.core;

import java.util.function.Supplier;

public class RxValve<E> extends RxVar<E> implements Triggerable {
  
  private RxVar<E> var;
  
  public RxValve(E value) {
    var = R.var(value);
    setThis(var.get());
  }
  
  public RxValve(Supplier<E> supplier) {
    var = R.var(supplier);
    setThis(var.get());
  }
  
  @Override
  public void setSupplier(Supplier<E> supplier) {
    var.setSupplier(supplier);
    setThis(var.get());
  }
  
  @Override
  public Supplier<E> getSupplier() {
    return var.getSupplier();
  }

  @Override
  public void trigger() {
    setThis(var.get());
  }
  
  public void setThis(E value) {
    if(R.isDetectingSets()) {
      R.addThreadLocalSetDependency(this);
      setNoSync(value);
    } else if(R.isDetectingGets()) {
      setNoSync(value);
    } else {
      R.doSync(() -> { this.setThis(value); });
    }
  }
  
  @Override
  public void set(E value) {
    var.set(value);
  }
  
}

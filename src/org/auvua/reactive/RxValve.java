package org.auvua.reactive;

import java.util.function.Supplier;

public class RxValve<E> extends RxVar<E> implements Triggerable {
  
  private RxVar<E> var;
  
  public RxValve(E value) {
    var = Rx.var(value);
    set(var.get());
  }
  
  public RxValve(Supplier<E> supplier) {
    var = Rx.var(supplier);
    set(var.get());
  }
  
  @Override
  public void setSupplier(Supplier<E> supplier) {
    var.setSupplier(supplier);
    set(var.get());
  }
  
  @Override
  public Supplier<E> getSupplier() {
    return var.getSupplier();
  }

  @Override
  public void trigger() {
    set(var.get());
  }
  
}

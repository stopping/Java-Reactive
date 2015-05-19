package org.auvua.reactive.core;

public class Var<E> implements Variable<E> {

  private E value;

  public Var() {
    this(null);
  }

  public Var(E value) {
    this.value = value;
  }

  @Override
  public E get() {
    return value;
  }

  @Override
  public void set(E value) {
    this.value = value;
  }

}

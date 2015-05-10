package org.auvua.reactive;

import java.util.function.Supplier;

public interface Variable<E> extends Supplier<E> {
  public void set(E value);
}

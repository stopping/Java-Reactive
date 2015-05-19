package org.auvua.model;

import org.auvua.reactive.core.Variable;

public interface Component {
  public Variable<? extends Object> getAttribute(String name);
  public void setAttribute(String name, Variable<? extends Object> value);
}

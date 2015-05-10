package org.auvua.agent.control;

import org.auvua.reactive.RxVar;

public interface Controllable {
  @SuppressWarnings("rawtypes")
  public RxVar getInput(String name);
  @SuppressWarnings("rawtypes")
  public RxVar getOutput(String name);
}

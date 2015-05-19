package org.auvua.reactive.core;

public interface Reactive {
  public void update();
  public void prepareUpdate();
  public void finishUpdate();
  public void awaitUpdate();
  public boolean isUpdating();
  public Runnable getUpdateRunner();
}

package org.auvua.reactive;

public interface Reactive {
  public void update();
  public void prepareUpdate();
  public void finishUpdate();
  public void awaitUpdate();
  public boolean isUpdating();
  public Runnable getUpdateRunner();
}

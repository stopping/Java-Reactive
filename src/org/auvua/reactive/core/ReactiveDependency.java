package org.auvua.reactive.core;

import java.util.Collection;

public interface ReactiveDependency extends Reactive {
  /**
   * @return The list of upward dependencies (i.e. dependencies which rely on this one)
   */
  public Collection<ReactiveDependency> getParents();
  /**
   * @return The list of downward dependencies (i.e. dependencies which this one uses directly)
   */
  public Collection<ReactiveDependency> getChildren();
  
  public void calculateDependencyRank();
  
  public int getDependencyRank();
  
  public void clear();
}

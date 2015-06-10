package org.auvua.reactive.core;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author sean
 *
 * A standard implementation of a reactive dependency object which delegates
 * update behavior to the dependency that it wraps.
 */
public abstract class StandardDependency implements ReactiveDependency {

  private Collection<ReactiveDependency> parents = new HashSet<ReactiveDependency>();
  private Collection<ReactiveDependency> children = new HashSet<ReactiveDependency>();

  private Runnable updateRunner = () -> update();
  private boolean updating = false;
  private int dependencyRank = 0;
  private boolean marked = false;

  @Override
  public Collection<ReactiveDependency> getParents() {
    return parents;
  }

  @Override
  public Collection<ReactiveDependency> getChildren() {
    return children;
  }

  public void add(ReactiveDependency ... dependencies) {
    for(ReactiveDependency dep : dependencies) {
      this.children.add(dep);
      dep.getParents().add(this);
    }
  }

  public void remove(ReactiveDependency ... dependencies) {
    for(ReactiveDependency dep : dependencies) {
      this.children.remove(dep);
      dep.getParents().remove(this);
    }
  }
  
  public void determineDependencies() {
    if(R.isDetectingNewDependencies()) {
      R.addNewDependency(this);
    }
    
    Set<ReactiveDependency> previousDependencies = R.getGetDependenciesAndClear();

    R.startDetectingGets();
    update();
    R.stopDetectingGets();

    for(ReactiveDependency dep : R.getGetDependenciesAndClear()) {
      this.add(dep);
    }
    
    calculateDependencyRank();
    
    for(ReactiveDependency dep : previousDependencies) {
      R.addThreadLocalGetDependency(dep);
    }
  }
  
  public void calculateDependencyRank() {
    if (marked) {
      throw new IllegalStateException("Circular reactive dependency detected.");
    }
    
    int tempRank = 0;
    for (ReactiveDependency dep : children) {
      int r = dep.getDependencyRank() + 1;
      tempRank = r > tempRank ? r : tempRank;
    }
    dependencyRank = tempRank;
    
    marked = true;
    for (ReactiveDependency dep : parents) {
      dep.calculateDependencyRank();
    }
    marked = false;
  }
  
  public int getDependencyRank() {
    return dependencyRank;
  }
  
  @Override
  public void clear() {
    for(ReactiveDependency dep : children) {
      dep.getParents().remove(this);
    }
    children.clear();
  }

  @Override
  public Runnable getUpdateRunner() {
    return updateRunner;
  }

  @Override
  public void prepareUpdate() {
    updating = true;
  }

  @Override
  public void finishUpdate() {
    updating = false;
  }

  @Override
  public boolean isUpdating() {
    return updating;
  }

}

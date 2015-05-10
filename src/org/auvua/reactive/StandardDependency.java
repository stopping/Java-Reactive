package org.auvua.reactive;

import java.util.Collection;
import java.util.HashSet;

/**
 * @author sean
 *
 * A standard implementation of a reactive dependency object which delegates
 * update behavior to the dependency that it wraps.
 */
public abstract class StandardDependency implements ReactiveDependency {

  private Collection<ReactiveDependency> parents = new HashSet<ReactiveDependency>();
  private Collection<ReactiveDependency> children = new HashSet<ReactiveDependency>();

  private Runnable updateRunner;
  private boolean updating = false;

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

package org.auvua.reactive;

import java.util.Collection;
import java.util.HashSet;

/**
 * @author sean
 *
 */
public class StandardDependency implements ReactiveDependency {

  private Collection<ReactiveDependency> parents = new HashSet<ReactiveDependency>();
  private Collection<ReactiveDependency> children = new HashSet<ReactiveDependency>();

  private ReactiveDependency updateableDependency;
  private Runnable updateRunner;
  private boolean updating = false;

  public StandardDependency(ReactiveDependency dep) {
    updateableDependency = dep;
    updateRunner = () -> {
      update();
    };
  }

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

  public void clear() {
    for(ReactiveDependency dep : children) {
      dep.getParents().remove(this);
    }
    children.clear();
  }

  @Override
  public void update() {
    updateableDependency.update();
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

  @Override
  public void awaitUpdate() {
    updateableDependency.awaitUpdate();
  }

}

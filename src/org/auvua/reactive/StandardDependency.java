package org.auvua.reactive;

import java.util.LinkedList;
import java.util.List;

/**
 * @author sean
 *
 */
public class StandardDependency implements ReactiveDependency {

  private List<ReactiveDependency> parents = new LinkedList<ReactiveDependency>();
  private List<ReactiveDependency> children = new LinkedList<ReactiveDependency>();
  
  private ReactiveDependency updateableDependency;
  
  public StandardDependency(ReactiveDependency dep) {
    updateableDependency = dep;
  }

  @Override
  public List<ReactiveDependency> getParents() {
    return parents;
  }

  @Override
  public List<ReactiveDependency> getChildren() {
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

}

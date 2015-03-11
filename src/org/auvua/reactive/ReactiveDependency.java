package org.auvua.reactive;

import java.util.List;

public interface ReactiveDependency extends Reactive {
	/**
	 * @return The list of upward dependencies (i.e. dependencies which rely on this one)
	 */
	public List<ReactiveDependency> getParents();
	/**
	 * @return The list of downward dependencies (i.e. dependencies which this one uses directly)
	 */
	public List<ReactiveDependency> getChildren();
}

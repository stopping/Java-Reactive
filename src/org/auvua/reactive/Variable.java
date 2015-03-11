package org.auvua.reactive;

public interface Variable<E> {
	public E val();
	public void set(E value);
}

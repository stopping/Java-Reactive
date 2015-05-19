package org.auvua.agent.control;
import org.auvua.reactive.core.RxVar;

public class ChangeDetector<E extends Comparable<E>> extends RxVar<Integer> {
  private Comparable<E> lastVal;
  
  @SuppressWarnings("unchecked")
  public ChangeDetector(RxVar<Comparable<E>> val) {
    lastVal = val.get();
    
    this.setSupplier(() -> {
      Comparable<E> value = val.get();
      int comparison = value.compareTo((E) lastVal);
      lastVal = value;
      return comparison;
    });
  }
  
}

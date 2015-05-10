package org.auvua.agent.tasks;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import org.auvua.model.BaseComponent;
import org.auvua.reactive.ReactiveDependency;
import org.auvua.reactive.Rx;
import org.auvua.reactive.RxCondition;

public abstract class AbstractTask extends BaseComponent implements Task {
  
  private Map<String, RxCondition>conditionDictionary
    = new HashMap<String, RxCondition>();
  private Collection<ReactiveDependency> newReactiveDependencies;
  
  public abstract void initialize();
  
  public void start() {
    Rx.startDetectingNewDependencies();
    initialize();
    Rx.stopDetectingNewDependencies();
    newReactiveDependencies = Rx.getNewDependenciesAndClear();
  }
  
  public void stop() {
    for(ReactiveDependency dep : newReactiveDependencies) {
      dep.clear();
    }
    for(String condName : conditionDictionary.keySet()) {
      boolean lastVal = conditionDictionary.get(condName).get();
      conditionDictionary.get(condName).setSupplier(() -> lastVal);
    }
  }
  
  public void declareCondition(String name) {
    setCondition(name, Rx.cond(false));
  }
  
  public void initializeCondition(String name, Supplier<Boolean> supplier) {
    RxCondition condition = getCondition(name);
    if(condition == null) {
      throw new IllegalStateException("Condition \"" + name + "\" is undeclared for this task.");
    }
    getCondition(name).setSupplier(supplier);
  }
  
  public RxCondition getCondition(String name) {
    RxCondition condition = conditionDictionary.get(name);
    if(condition == null) {
      throw new IllegalStateException("Condition \"" + name + "\" is undeclared for this task.");
    }
    return condition;
  }
  
  public void setCondition(String name, RxCondition condition) {
    conditionDictionary.put(name, condition);
  }
  
  public Runnable begin() {
    return () -> this.start();
  }
  
  public Runnable end() {
    return () -> this.stop();
  }
  
  public Map<String, RxCondition> getConditionDictionary() {
    return conditionDictionary;
  }
  
}
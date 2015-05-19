package org.auvua.model;

import java.util.HashMap;
import java.util.Map;

import org.auvua.reactive.core.Variable;

public class BaseComponent implements Component {
  
  private Map<String, Variable<? extends Object>> attributeDictionary;
  
  public BaseComponent() {
    this.attributeDictionary = new HashMap<String, Variable<? extends Object>>();
  }

  @Override
  public Variable<? extends Object> getAttribute(String name) {
    return attributeDictionary.get(name);
  }

  @Override
  public void setAttribute(String name, Variable<? extends Object> value) {
    attributeDictionary.put(name, value);
  }

}

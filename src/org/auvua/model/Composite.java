package org.auvua.model;

import java.util.HashMap;
import java.util.Map;

public class Composite extends BaseComponent {
  
  private Map<String, Component> componentDictionary;
  
  public Composite() {
    this.componentDictionary = new HashMap<String, Component>();
  }
  
  public Component getComponent(String name) {
    return componentDictionary.get(name);
  }
  
  public void setComponent(String name, Component component) {
    componentDictionary.put(name, component);
  }

}

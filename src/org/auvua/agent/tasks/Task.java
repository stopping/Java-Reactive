package org.auvua.agent.tasks;

import java.util.Map;

import org.auvua.model.Component;
import org.auvua.reactive.core.RxCondition;

public interface Task extends Component {
  public void start();
  public void stop();
  
  public Runnable begin();
  public Runnable end();
  
  public RxCondition getCondition(String name);
  public void setCondition(String name, RxCondition condition);
  
  public Map<String, RxCondition> getConditionDictionary();
  
  public static Runnable start(Task task) {
    return () -> task.start();
  }
  
  public static Runnable stop(Task task) {
    return () -> task.stop();
  }
}

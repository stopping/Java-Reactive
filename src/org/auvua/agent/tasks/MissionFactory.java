package org.auvua.agent.tasks;

import org.auvua.agent.TwoVector;
import org.auvua.model.RobotModel2;
import org.auvua.reactive.Rx;

public class MissionFactory {
  
  private RobotModel2 robot = RobotModel2.getInstance();

  public Task build(MissionType type) {
    switch(type) {
      case RANDOM_WALK:
        GoToArea task = new GoToArea(robot, new TwoVector(Rx.var(Math.random() * 400 - 200), Rx.var(Math.random() * 400 - 200)), 10);
        Task doNothing = new DoNothing(robot);
        
        Rx.when(task.getCondition("timeout"))
          .then(() -> {
            task.stop();
            doNothing.start();
          });
        
        Rx.when(task.getCondition("success"))
          .then(() -> {
            task.stop();
            task.target.x.set(Math.random() * 400 - 200);
            task.target.y.set(Math.random() * 400 - 200);
            task.start();
          });
        
        return task;
      default:
        return new DoNothing(robot);
    }
  }
  
  public enum MissionType {
    RANDOM_WALK;
  }
}

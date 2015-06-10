package org.auvua.agent.tasks;

import org.auvua.agent.TwoVector;
import org.auvua.model.RobotModel;
import org.auvua.reactive.core.R;

public class MissionFactory {
  
  private RobotModel robot = RobotModel.getInstance();

  public Task build(MissionType type) {
    switch(type) {
      case RANDOM_WALK:
        GoToArea task = new GoToArea(robot, new TwoVector(R.var(Math.random() * 400 - 200), R.var(Math.random() * 400 - 200)), 10);
        Task doNothing = new DoNothing(robot);
        
        R.when(task.getCondition("timeout"))
          .then(() -> {
            task.stop();
            doNothing.start();
          });
        
        R.when(task.getCondition("success"))
          .then(() -> {
            task.target.x.set(Math.random() * 400 - 200);
            task.target.y.set(Math.random() * 400 - 200);
          });
        
        return task;
      case POSITION_CONTROL:
        return new GoToArea(robot, new TwoVector(), 10);
      default:
        return new DoNothing(robot);
    }
  }
  
  public enum MissionType {
    RANDOM_WALK,
    POSITION_CONTROL
  }
}

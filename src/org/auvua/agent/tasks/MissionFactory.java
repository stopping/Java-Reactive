package org.auvua.agent.tasks;

import org.auvua.agent.TwoVector;
import org.auvua.model.RobotModel;

public class MissionFactory {
  
  private RobotModel robot = RobotModel.getInstance();

  public Task build(MissionType type) {
    switch(type) {
      //case RANDOM_WALK:
        //return new RandomWalkMission().getStartTask();
      case SQUARE_WALK:
        return new DrivingMission().getStartTask();
      case POSITION_CONTROL:
        return new GoToArea(robot, new TwoVector(), 10);
      default:
        return new DoNothing(robot);
    }
  }
  
  public enum MissionType {
    RANDOM_WALK,
    SQUARE_WALK,
    POSITION_CONTROL
  }
}

package org.auvua.agent.tasks;

import org.auvua.agent.control.OpenLoopController;
import org.auvua.model.RobotModel;

public class DoNothing extends AbstractTask {
  
  private RobotModel robot;

  public DoNothing(RobotModel robot) {
    this.robot = robot;
  }
  @Override
  public void initialize() {
    robot.thrustInput.x.setSupplier(new OpenLoopController(0.0));
    robot.thrustInput.y.setSupplier(new OpenLoopController(0.0));
  }

}

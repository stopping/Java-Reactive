package org.auvua.agent.tasks;

import org.auvua.agent.control.PidController;
import org.auvua.model.RobotModel;
import org.auvua.reactive.core.RxVar;

public class MaintainDepth extends AbstractTask {
  
  private RxVar<Double> desiredDepth;
  private RobotModel robot;
  private double error;

  public MaintainDepth(RobotModel robot, RxVar<Double> desiredDepth, double error) {
    this.desiredDepth = desiredDepth;
    this.robot = robot;
    this.error = error;
    declareCondition("atDepth");
  }
  
  @Override
  public void initialize() {
    initializeCondition("atDepth", () -> {
      return Math.abs(desiredDepth.get() - robot.motion.z.pos.get()) < error;
    });
    
    PidController controller = new PidController(robot.motion.z.pos, desiredDepth, 1, 0, 2.5);
    controller.setSaturationLimits(-200, 200);
    robot.thrustInputZ.setSupplier(controller);
  }

}

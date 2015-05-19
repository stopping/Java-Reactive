package org.auvua.agent.tasks;

import org.auvua.agent.TwoVector;
import org.auvua.agent.control.PidController;
import org.auvua.model.RobotModel2;
import org.auvua.reactive.core.Rx;

public class GoToArea extends AbstractTask {
  
  public TwoVector target;
  public TwoVector output;
  private TwoVector position;
  private RobotModel2 robot;
  private double radius;
  
  public GoToArea(RobotModel2 robot, TwoVector target, double radius) {
    this.target = target;
    this.robot = robot;
    this.radius = radius;
    declareCondition("success");
    declareCondition("timeout");
  }
  
  @Override
  public void initialize() {
    this.position = new TwoVector(robot.motion.x.pos, robot.motion.y.pos);
    
    initializeCondition("success", new OccupyingArea(position, target, radius));
    initializeCondition("timeout", new Timeout(60.0));
    
    Rx.when(getCondition("success"))
      .then(() -> System.out.println("Made it to coordinate " + target.x.get() + " " + target.y.get()));
    
    PidController xControl = new PidController(robot.positionSensor.x, target.x, .25, 0, 0);
    PidController yControl = new PidController(robot.positionSensor.y, target.y, .25, 0, 0);
    
    xControl.setSaturationLimits(-100, 100);
    yControl.setSaturationLimits(-100, 100);
    
    robot.thrustInput.x.setSupplier(xControl);
    robot.thrustInput.y.setSupplier(yControl);
  }
  
}

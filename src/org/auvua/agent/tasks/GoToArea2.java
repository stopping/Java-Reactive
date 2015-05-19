package org.auvua.agent.tasks;

import org.auvua.agent.TwoVector;
import org.auvua.agent.control.Differentiator;
import org.auvua.agent.control.Integrator;
import org.auvua.agent.control.PidController;
import org.auvua.agent.control.StoppingDistance;
import org.auvua.agent.control.TimeInvariantSystem;
import org.auvua.model.RobotModel2;
import org.auvua.reactive.core.Rx;
import org.auvua.reactive.core.RxVar;

public class GoToArea2 extends AbstractTask {
  
  public TwoVector target;
  public TwoVector output;
  private TwoVector position;
  private RobotModel2 robot;
  private double radius;
  
  public GoToArea2(RobotModel2 robot, TwoVector target, double radius) {
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
    initializeCondition("timeout", new Timeout(600.0));
    
    Rx.when(getCondition("success"))
      .then(() -> System.out.println("Made it to coordinate " + target.x.get() + " " + target.y.get()));
    
    RxVar<Double> xSetPoint = this.target.x;
    RxVar<Double> xPos = new Integrator(robot.velocitySensor.x);
    RxVar<Double> xAccel = new Differentiator(robot.velocitySensor.x);
    RxVar<Double> xStopPos = new StoppingDistance(robot.motion.x.vel, robot.motion.x.accel, 60);
    PidController xController = new PidController(Rx.var(() -> robot.motion.x.pos.get() + xStopPos.get()), xSetPoint, 100, 0, 0);
    xController.setSaturationLimits(-100, 100);
    robot.thrustInput.x.setSupplier(xController);
    
    RxVar<Double> ySetPoint = this.target.y;
    RxVar<Double> yPos = new Integrator(robot.velocitySensor.y);
    RxVar<Double> yAccel = new Differentiator(robot.velocitySensor.y);
    RxVar<Double> yStopPos = new StoppingDistance(robot.motion.y.vel, robot.motion.y.accel, 60);
    PidController yController = new PidController(Rx.var(() -> robot.motion.y.pos.get() + yStopPos.get()), ySetPoint, 100, 0, 0);
    yController.setSaturationLimits(-100, 100);
    robot.thrustInput.y.setSupplier(yController);
  }
  
}

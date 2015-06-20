package org.auvua.agent.tasks;

import org.auvua.agent.TwoVector;
import org.auvua.agent.control.PidController;
import org.auvua.agent.control.StoppingDistance;
import org.auvua.model.RobotModel;
import org.auvua.reactive.core.R;
import org.auvua.reactive.core.RxVar;

public class GoToArea extends AbstractTask {
  
  public TwoVector target;
  public TwoVector output;
  private TwoVector position;
  private RobotModel robot;
  private double radius;
  
  public GoToArea(RobotModel robot, TwoVector target, double radius) {
    this.target = target;
    this.robot = robot;
    this.radius = radius;
    declareCondition("inArea");
    declareCondition("timeout");
  }
  
  @Override
  public void initialize() {
    this.position = new TwoVector(robot.motion.x.pos, robot.motion.y.pos);
    
    initializeCondition("inArea", new OccupyingArea(position, target, radius));
    initializeCondition("timeout", new Timeout(600.0));
    
    RxVar<Double> xSetPoint = this.target.x;
    RxVar<Double> xPos = robot.positionSensor.x;
    RxVar<Double> xStopPos = new StoppingDistance(robot.motion.x.vel, robot.controlledAccelX, 200, 200);
    PidController xController = new PidController(R.var(() -> xPos.get() + xStopPos.get()), xSetPoint, 10, 0, 0);
    xController.setSaturationLimits(-200, 200);
    robot.thrustInputX.setSupplier(xController);
    
    RxVar<Double> ySetPoint = this.target.y;
    RxVar<Double> yPos = robot.positionSensor.y;
    RxVar<Double> yStopPos = new StoppingDistance(robot.motion.y.vel, robot.controlledAccelY, 200, 200);
    PidController yController = new PidController(R.var(() -> yPos.get() + yStopPos.get()), ySetPoint, 10, 0, 0);
    yController.setSaturationLimits(-200, 200);
    robot.thrustInputY.setSupplier(yController);
  }
  
}

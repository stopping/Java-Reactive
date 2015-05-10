package org.auvua.model;
import org.auvua.agent.ThreeKinematics;
import org.auvua.agent.TwoVector;
import org.auvua.agent.control.Integrator;
import org.auvua.agent.control.Limiter;
import org.auvua.agent.control.Timer;
import org.auvua.agent.simulator.Sensor;
import org.auvua.reactive.Rx;

public class RobotModel {
  
  private static RobotModel model;
  
  private double mass = 1.0;
  private double mu = 20;
  
  public final ThreeKinematics motion = new ThreeKinematics();
  public final TwoVector vel = new TwoVector(motion.x.vel, motion.y.vel);
  
  public final TwoVector thrustTarget = new TwoVector(Rx.var(0.0), Rx.var(0.0));
  
  public final TwoVector thrustInput = new TwoVector();
  
  public final TwoVector thrust = new TwoVector(
      new Limiter(thrustInput.x).hard(-100, 100).rate(100),
      new Limiter(thrustInput.y).hard(-100, 100).rate(100));
      
  private TwoVector friction = new TwoVector(
      Rx.var(() -> vel.r.get() != 0 ? mu * mass * - motion.x.vel.get() / vel.r.get() : 0),
      Rx.var(() -> vel.r.get() != 0 ? mu * mass * - motion.y.vel.get() / vel.r.get() : 0));
  
  private TwoVector force = new TwoVector(
      Rx.var(() -> thrust.x.get() + friction.x.peek()), 
      Rx.var(() -> thrust.y.get() + friction.y.peek()));
  
  public TwoVector velocitySensor = new TwoVector(
      new Sensor(vel.x),
      new Sensor(vel.y));
  
  public TwoVector positionSensor = new TwoVector(
      new Integrator(velocitySensor.x, Timer.getInstance()),
      new Integrator(velocitySensor.y, Timer.getInstance()));
  
  public RobotModel() {
    this.motion.x.accel.setSupplier(() -> force.x.get() / mass);
    this.motion.y.accel.setSupplier(() -> force.y.get() / mass);
  }
  
  public static RobotModel getInstance() {
    if (model == null) model = new RobotModel();
    return model;
  }
  
}
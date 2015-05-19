package org.auvua.model;
import java.util.HashMap;
import java.util.Map;

import org.auvua.agent.ThreeKinematics;
import org.auvua.agent.TwoVector;
import org.auvua.agent.control.Controllable;
import org.auvua.agent.control.FirstOrderSystem;
import org.auvua.agent.control.Integrator;
import org.auvua.agent.control.Limiter;
import org.auvua.agent.control.Timer;
import org.auvua.reactive.core.Rx;
import org.auvua.reactive.core.RxValve;
import org.auvua.reactive.core.RxVar;
import org.auvua.reactive.core.Triggerable;

public class RobotModel2 implements Controllable, Triggerable {
  
  private static RobotModel2 model;
  private Map<String, RxVar<?>> inputs = new HashMap<String, RxVar<?>>();
  private Map<String, RxVar<?>> outputs = new HashMap<String, RxVar<?>>();
  
  private double mass = 1.0;
  private double mu = 20;
  private double cD = .47;
  
  public final ThreeKinematics motion = new ThreeKinematics();
  public final TwoVector vel = new TwoVector(motion.x.vel, motion.y.vel);
  
  private final RxValve<Double> thrustX = Rx.valve(0.0);
  private final RxValve<Double> thrustY = Rx.valve(0.0);
  public final TwoVector thrustInput = new TwoVector(thrustX, thrustY);
  
  public final TwoVector thrust = new TwoVector(
      new Limiter(thrustInput.x).hard(-100, 100).rate(100),
      new Limiter(thrustInput.y).hard(-100, 100).rate(100));
      
  private TwoVector friction = new TwoVector(
      Rx.var(() -> vel.r.get() != 0 ? mu * mass * - motion.x.vel.get() / vel.r.get() : 0),
      Rx.var(() -> vel.r.get() != 0 ? mu * mass * - motion.y.vel.get() / vel.r.get() : 0));
  
  private TwoVector drag = new TwoVector(
      Rx.var(() -> - vel.x.get() * Math.abs(vel.x.get()) * cD * .5 * 1000 * .01 * .01),
      Rx.var(() -> - vel.y.get() * Math.abs(vel.y.get()) * cD * .5 * 1000 * .01 * .01));
  
  private TwoVector force = new TwoVector(
      Rx.var(() -> thrust.x.get() + friction.x.peek() * 0 + drag.x.peek()), 
      Rx.var(() -> thrust.y.get() + friction.y.peek() * 0 + drag.y.peek()));
  
  public TwoVector velocitySensor = new TwoVector(
      new FirstOrderSystem(vel.x, 100),
      new FirstOrderSystem(vel.y, 100));
  
  public TwoVector positionSensor = new TwoVector(
      new Integrator(velocitySensor.x, Timer.getInstance()),
      new Integrator(velocitySensor.y, Timer.getInstance()));
  
  public RobotModel2() {
    this.motion.x.accel.setSupplier(() -> force.x.get() / mass);
    this.motion.y.accel.setSupplier(() -> force.y.get() / mass);
    
    inputs.put("xThrust", thrustInput.x);
    inputs.put("yThrust", thrustInput.y);
    
    outputs.put("xPos", positionSensor.x);
    outputs.put("yPos", positionSensor.y);
    outputs.put("xThrust", thrust.x);
    outputs.put("yThrust", thrust.y);
  }
  
  public static RobotModel2 getInstance() {
    if (model == null) model = new RobotModel2();
    return model;
  }

  @Override
  @SuppressWarnings("rawtypes")
  public RxVar getInput(String name) {
    return inputs.get(name);
  }

  @Override
  @SuppressWarnings("rawtypes")
  public RxVar getOutput(String name) {
    return outputs.get(name);
  }

  @Override
  public void trigger() {
    Rx.doSync(() -> {
      Timer.getInstance().trigger();
      thrustX.trigger();
      thrustY.trigger();
    });
  }
  
}
package org.auvua.model;
import java.util.HashMap;
import java.util.Map;

import org.auvua.agent.ThreeKinematics;
import org.auvua.agent.TwoVector;
import org.auvua.agent.control.Controllable;
import org.auvua.agent.control.FirstOrderSystem;
import org.auvua.agent.control.HardLimit;
import org.auvua.agent.control.Integrator;
import org.auvua.agent.control.RateLimiter;
import org.auvua.agent.control.Timer;
import org.auvua.reactive.core.R;
import org.auvua.reactive.core.RxValve;
import org.auvua.reactive.core.RxVar;
import org.auvua.reactive.core.Triggerable;

public class RobotModel implements Controllable, Triggerable {
  
  private static RobotModel model;
  private Map<String, RxVar<?>> inputs = new HashMap<String, RxVar<?>>();
  private Map<String, RxVar<?>> outputs = new HashMap<String, RxVar<?>>();
  
  private double mass = 1.0;
  private double mu = 20;
  private double cD = .47;
  
  public final ThreeKinematics motion = new ThreeKinematics();
  public final TwoVector vel = new TwoVector(motion.x.vel, motion.y.vel);
  
  private final RxValve<Double> thrustInputX = R.valve(0.0);
  private final RxValve<Double> thrustInputY = R.valve(0.0);
  public final TwoVector thrustInput = new TwoVector(thrustInputX, thrustInputY);
  
  public final TwoVector thrust = new TwoVector(
      new RateLimiter(new HardLimit(thrustInput.x, -200, 200), 200),
      new RateLimiter(new HardLimit(thrustInput.y, -200, 200), 200));
  
  public final TwoVector controlledAccel = new TwoVector(
      R.var(() -> thrust.x.get() / mass),
      R.var(() -> thrust.y.get() / mass));
      
  private TwoVector friction = new TwoVector(
      R.var(() -> vel.r.get() != 0 ? mu * mass * - motion.x.vel.get() / vel.r.get() : 0),
      R.var(() -> vel.r.get() != 0 ? mu * mass * - motion.y.vel.get() / vel.r.get() : 0));
  
  private TwoVector drag = new TwoVector(
      R.var(() -> - vel.x.get() * Math.abs(vel.x.get()) * cD * .5 * 1000 * .01 * .01),
      R.var(() -> - vel.y.get() * Math.abs(vel.y.get()) * cD * .5 * 1000 * .01 * .01));
  
  private TwoVector force = new TwoVector(
      R.var(() -> thrust.x.get() + friction.x.peek() * 0 + drag.x.peek() * 1), 
      R.var(() -> thrust.y.get() + friction.y.peek() * 0 + drag.y.peek() * 1));
  
  public TwoVector velocitySensor = new TwoVector(
      new FirstOrderSystem(vel.x, 5),
      new FirstOrderSystem(vel.y, 5));
  
  public TwoVector positionSensor = new TwoVector(
      new Integrator(velocitySensor.x, Timer.getInstance()),
      new Integrator(velocitySensor.y, Timer.getInstance()));
  
  public RobotModel() {
    this.motion.x.accel.setSupplier(() -> force.x.get() / mass);
    this.motion.y.accel.setSupplier(() -> force.y.get() / mass);
    
    inputs.put("xThrust", thrustInput.x);
    inputs.put("yThrust", thrustInput.y);
    
    outputs.put("xPos", positionSensor.x);
    outputs.put("yPos", positionSensor.y);
    outputs.put("xThrust", thrust.x);
    outputs.put("yThrust", thrust.y);
  }
  
  public static RobotModel getInstance() {
    if (model == null) model = new RobotModel();
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
    R.doSync(() -> {
      Timer.getInstance().trigger();
      thrustInputX.trigger();
      thrustInputY.trigger();
    });
  }
  
}
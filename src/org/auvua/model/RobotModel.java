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
  private double cD = .47;
  
  public final ThreeKinematics motion = new ThreeKinematics();
  
  public final RxValve<Double> thrustInputX = R.valve(0.0);
  public final RxValve<Double> thrustInputY = R.valve(0.0);
  public final RxValve<Double> thrustInputZ = R.valve(0.0);
  
  public final RxVar<Double> thrustX = new RateLimiter(new HardLimit(thrustInputX, -200, 200), 200);
  public final RxVar<Double> thrustY = new RateLimiter(new HardLimit(thrustInputY, -200, 200), 200);
  public final RxVar<Double> thrustZ = new RateLimiter(new HardLimit(thrustInputZ, -200, 200), 200);
  
  public final RxVar<Double> controlledAccelX = R.var(() -> thrustX.get() / mass);
  public final RxVar<Double> controlledAccelY = R.var(() -> thrustY.get() / mass);
  public final RxVar<Double> controlledAccelZ = R.var(() -> thrustZ.get() / mass);
  
  private RxVar<Double> dragX = R.var(() -> - motion.x.vel.get() * Math.abs(motion.x.vel.get()) * cD * .5 * 1000 * .01 * .01);
  private RxVar<Double> dragY = R.var(() -> - motion.y.vel.get() * Math.abs(motion.y.vel.get()) * cD * .5 * 1000 * .01 * .01);
  private RxVar<Double> dragZ = R.var(() -> - motion.z.vel.get() * Math.abs(motion.z.vel.get()) * cD * .5 * 1000 * .01 * .01);
  
  private RxVar<Double> forceX = R.var(() -> thrustX.get() + dragX.peek() * 1);
  private RxVar<Double> forceY = R.var(() -> thrustY.get() + dragY.peek() * 1);
  private RxVar<Double> forceZ = R.var(() -> thrustZ.get() + dragZ.peek() * 1);
  
  public TwoVector velocitySensor = new TwoVector(
      new FirstOrderSystem(motion.x.vel, 5),
      new FirstOrderSystem(motion.y.vel, 5));
  
  public TwoVector positionSensor = new TwoVector(
      new Integrator(velocitySensor.x, Timer.getInstance()),
      new Integrator(velocitySensor.y, Timer.getInstance()));
  
  public RxVar<Double> depthSensor = new FirstOrderSystem(motion.z.pos, 5);
  
  public RobotModel() {
    this.motion.x.accel.setSupplier(() -> forceX.get() / mass);
    this.motion.y.accel.setSupplier(() -> forceY.get() / mass);
    this.motion.z.accel.setSupplier(() -> forceZ.get() / mass);
    
    inputs.put("xThrust", thrustInputX);
    inputs.put("yThrust", thrustInputY);
    inputs.put("zThrust", thrustInputZ);
    
    outputs.put("xPos", positionSensor.x);
    outputs.put("yPos", positionSensor.y);
    outputs.put("depth", depthSensor);
    
    outputs.put("xThrust", thrustX);
    outputs.put("yThrust", thrustY);
    outputs.put("zThrust", thrustZ);
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
      thrustInputZ.trigger();
    });
  }
  
}
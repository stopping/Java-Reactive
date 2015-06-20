package org.auvua.model;
import java.util.HashMap;
import java.util.Map;

import org.auvua.agent.TwoVector;
import org.auvua.agent.control.Controllable;
import org.auvua.agent.control.HardLimit;
import org.auvua.agent.control.RateLimiter;
import org.auvua.agent.control.Timer;
import org.auvua.agent.signal.FirstOrderSystem;
import org.auvua.agent.signal.Integrator;
import org.auvua.model.motion.ThreeKinematics;
import org.auvua.reactive.core.R;
import org.auvua.reactive.core.RxValve;
import org.auvua.reactive.core.RxVar;
import org.auvua.reactive.core.Triggerable;

public class RobotModel2 implements Controllable, Triggerable {
  
  private static RobotModel2 model;
  private Map<String, RxVar<?>> inputs = new HashMap<String, RxVar<?>>();
  private Map<String, RxVar<?>> outputs = new HashMap<String, RxVar<?>>();
  
  private double mass = 1.0;
  private double cdSurge = .47  * .5 * 1000 * .01 * .01 * 2;
  private double cdSway = .47  * .5 * 1000 * .01 * .01 * 6;
  private double cdHeave = .47  * .5 * 1000 * .01 * .01 * 6;
  private double momentOfInertia = 500.0;
  
  public final ThreeKinematics motion = new ThreeKinematics();
  
  public final RxValve<Double> surgeLeftInput = R.valve(0.0);
  public final RxValve<Double> surgeRightInput = R.valve(0.0);
  public final RxValve<Double> swayFrontInput = R.valve(0.0);
  public final RxValve<Double> swayBackInput = R.valve(0.0);
  public final RxValve<Double> heaveInput = R.valve(0.0);
  
  public final RxVar<Double> surgeLeft = new RateLimiter(new HardLimit(surgeLeftInput, -200, 200), 1000);
  public final RxVar<Double> surgeRight = new RateLimiter(new HardLimit(surgeRightInput, -200, 200), 1000);
  public final RxVar<Double> swayFront = new RateLimiter(new HardLimit(swayFrontInput, -200, 200), 1000);
  public final RxVar<Double> swayBack = new RateLimiter(new HardLimit(swayBackInput, -200, 200), 1000);
  public final RxVar<Double> heave = new RateLimiter(new HardLimit(heaveInput, -200, 200), 1000);
  
  private final RxVar<Double> thrustSurge = R.var(() -> (surgeLeft.get() + surgeRight.get()) / mass);
  private final RxVar<Double> thrustSway = R.var(() -> (swayFront.get() + swayBack.get()) / mass);
  private final RxVar<Double> thrustHeave = R.var(() -> heave.get() / mass);
  
  private final RxVar<Double> angularAccel = R.var(0.0);
  private final RxVar<Double> angularVel = new Integrator(angularAccel);
  public final RxVar<Double> angle = new Integrator(angularVel);
  
  private final RxVar<Double> torque = R.var(() -> {
		double rotationalDrag = -angularVel.peek() * Math.abs(angularVel.peek()) * 600;
		return -surgeLeft.get() * 10 + surgeRight.get() * 10 - swayFront.get() * 15 + swayBack.get() * 15 + rotationalDrag;
	  });
  
  public RxVar<Double> velSurge = R.var(() -> {
	  double vAngle = Math.atan2(motion.y.vel.get(), motion.x.vel.get());
	  double vMag = Math.hypot(motion.x.vel.get(), motion.y.vel.get());
	  return vMag * Math.cos(vAngle - angle.get());
  });
  public RxVar<Double> velSway = R.var(() -> {
	  double vAngle = Math.atan2(motion.y.vel.get(), motion.x.vel.get());
	  double vMag = Math.hypot(motion.x.vel.get(), motion.y.vel.get());
	  return vMag * Math.cos(vAngle - (angle.get() - Math.PI / 2));
  });
  private RxVar<Double> velHeave = R.var(() -> motion.z.vel.get());
  
  public RxVar<Double> dragSurge = R.var(() -> -velSurge.get() * Math.abs(velSurge.get()) * cdSurge);
  public RxVar<Double> dragSway = R.var(() -> -velSway.get() * Math.abs(velSway.get()) * cdSway);
  private RxVar<Double> dragHeave = R.var(() -> -velHeave.get() * Math.abs(velHeave.get()) * cdHeave);
  
  private RxVar<Double> forceSurge = R.var(() -> thrustSurge.get() + dragSurge.peek() * 1);
  private RxVar<Double> forceSway = R.var(() -> thrustSway.get() + dragSway.peek() * 1);
  private RxVar<Double> forceHeave = R.var(() -> thrustHeave.get() + dragHeave.peek() * 1);
  
  public TwoVector velocitySensor = new TwoVector(
      new FirstOrderSystem(motion.x.vel, 5),
      new FirstOrderSystem(motion.y.vel, 5));
  
  public TwoVector positionSensor = new TwoVector(
      new Integrator(velocitySensor.x, Timer.getInstance()),
      new Integrator(velocitySensor.y, Timer.getInstance()));
  
  public RxVar<Double> depthSensor = new FirstOrderSystem(motion.z.pos, 5);
  
  public RobotModel2() {
	this.angularAccel.setSupplier(() -> torque.get() / momentOfInertia);
    this.motion.x.accel.setSupplier(() -> (forceSway.get() * Math.sin(angle.get()) + forceSurge.get() * Math.cos(angle.get())) / mass);
    this.motion.y.accel.setSupplier(() -> (-forceSway.get() * Math.cos(angle.get()) + forceSurge.get() * Math.sin(angle.get())) / mass);
    this.motion.z.accel.setSupplier(() -> forceHeave.get() / mass);
    
    outputs.put("xPos", positionSensor.x);
    outputs.put("yPos", positionSensor.y);
    outputs.put("depth", depthSensor);
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
    R.doSync(() -> {
      Timer.getInstance().trigger();
      surgeLeftInput.trigger();
      surgeRightInput.trigger();
      swayFrontInput.trigger();
      swayBackInput.trigger();
      heaveInput.trigger();
    });
  }
  
}
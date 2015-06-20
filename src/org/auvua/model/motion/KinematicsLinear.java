package org.auvua.model.motion;
import org.auvua.agent.control.Timer;
import org.auvua.agent.signal.Integrator;
import org.auvua.reactive.core.R;
import org.auvua.reactive.core.RxVar;
import org.auvua.reactive.core.Variable;

public class KinematicsLinear {
  
  public final RxVar<Double> pos;
  public final RxVar<Double> vel;
  public final RxVar<Double> accel;
  
  public KinematicsLinear(double pos, double vel, double accel) {
    Variable<Double> time = Timer.getInstance();
    this.accel = R.var(accel);
    this.vel = new Integrator(this.accel, time, vel);
    this.pos = new Integrator(this.vel, time, pos);
  }
  
  
  
}

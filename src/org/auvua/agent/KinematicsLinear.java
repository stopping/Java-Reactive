package org.auvua.agent;
import org.auvua.agent.control.Integrator;
import org.auvua.agent.control.Timer;
import org.auvua.reactive.Rx;
import org.auvua.reactive.RxVar;
import org.auvua.reactive.Variable;

public class KinematicsLinear {
  
  public final RxVar<Double> pos;
  public final RxVar<Double> vel;
  public final RxVar<Double> accel;
  
  public KinematicsLinear(double pos, double vel, double accel) {
    Variable<Double> time = Timer.getInstance();
    this.accel = Rx.var(accel);
    this.vel = new Integrator(this.accel, time, vel);
    this.pos = new Integrator(this.vel, time, pos);
  }
  
  
  
}

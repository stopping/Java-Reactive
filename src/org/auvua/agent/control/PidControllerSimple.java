package org.auvua.agent.control;
import org.auvua.reactive.Rx;
import org.auvua.reactive.RxVar;

public class PidControllerSimple extends RxVar<Double> {
  
  public PidControllerSimple(RxVar<Double> processVar, RxVar<Double> targetVar, double kp, double ki, double kd) {
    Timer time = Timer.getInstance();
    
    RxVar<Double> error = Rx.var(() -> targetVar.get() - processVar.peek());
    RxVar<Double> integral = Rx.var(new Integrator(error, time));
    RxVar<Double> derivative = Rx.var(new Differentiator(error, time));
    
    this.setSupplier(() -> kp * error.get() + ki * integral.get() + kd * derivative.get());
  }
}
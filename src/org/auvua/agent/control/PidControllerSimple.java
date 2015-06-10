package org.auvua.agent.control;
import org.auvua.reactive.core.R;
import org.auvua.reactive.core.RxVar;

public class PidControllerSimple extends RxVar<Double> {
  
  public PidControllerSimple(RxVar<Double> processVar, RxVar<Double> targetVar, double kp, double ki, double kd) {
    Timer time = Timer.getInstance();
    
    RxVar<Double> error = R.var(() -> targetVar.get() - processVar.peek());
    RxVar<Double> integral = R.var(new Integrator(error, time));
    RxVar<Double> derivative = R.var(new Differentiator(error, time));
    
    this.setSupplier(() -> kp * error.get() + ki * integral.get() + kd * derivative.get());
  }
}

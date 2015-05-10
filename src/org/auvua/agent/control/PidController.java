package org.auvua.agent.control;
import org.auvua.reactive.Rx;
import org.auvua.reactive.RxVar;

public class PidController extends RxVar<Double> {
  
  private double feedForward = 0;
  private double lastIntegral = 0;
  public final RxVar<Double> outputMax = Rx.var(Double.MAX_VALUE);
  public final RxVar<Double> outputMin = Rx.var(-Double.MAX_VALUE);
  
  public PidController(RxVar<Double> processVar, RxVar<Double> targetVar, double kp, double ki, double kd) {
    Timer time = Timer.getInstance();
    this.setNoSync(0.0);
    
    RxVar<Double> error = Rx.var(() -> targetVar.get() - processVar.get());
    RxVar<Double> proportional = Rx.var(() -> kp * error.get());
    RxVar<Double> integral = new Integrator(() -> ki * error.get(), time);
    RxVar<Double> derivative = Rx.var(new Differentiator(() -> kd * error.get(), time));
    
    this.setSupplier(() -> {
      double p = proportional.get();
      double i = integral.get();
      double d = derivative.get();
      double f = feedForward * targetVar.get();
      return p + i + d + f;
    });
    
    Rx.task(() -> {
      double output = this.get();
      double min = outputMin.get();
      double max = outputMax.get();
      if(output < min) {
        integral.setNoSync(lastIntegral);
      }
      if(output > max) {
        integral.setNoSync(lastIntegral);
      }
      lastIntegral = integral.get();
    });
  }
  
  public void setSaturationLimits(double min, double max) {
    outputMax.setNoSync(max);
    outputMin.setNoSync(min);
  }
}

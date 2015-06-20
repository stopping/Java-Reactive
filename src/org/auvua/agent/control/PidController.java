package org.auvua.agent.control;
import org.auvua.agent.signal.Differentiator;
import org.auvua.agent.signal.Integrator;
import org.auvua.reactive.core.R;
import org.auvua.reactive.core.RxVar;

public class PidController extends RxVar<Double> {
  
  private double feedForward = 0;
  private double lastIntegral = 0;
  public final RxVar<Double> outputMin = R.var(-Double.MAX_VALUE);
  public final RxVar<Double> outputMax = R.var(Double.MAX_VALUE);
  public final RxVar<Double> error;
  public final RxVar<Double> proportional;
  public final RxVar<Double> integral;
  public final RxVar<Double> derivative;
  
  public PidController(RxVar<Double> processVar, RxVar<Double> targetVar, double kp, double ki, double kd) {
    Timer time = Timer.getInstance();
    this.setNoSync(0.0);
    
    error = R.var(() -> targetVar.get() - processVar.get());
    proportional = error;
    integral = new Integrator(error, time);
    derivative = new Differentiator(error, time);
    
    this.setSupplier(() -> {
      double p = kp * proportional.get();
      double i = ki * integral.get();
      double d = kd * derivative.get();
      double f = feedForward * targetVar.get();
      return p + i + d + f;
    });
    
    /*
    R.task(() -> {
      double output = this.get();
      double min = outputMin.get();
      double max = outputMax.get();
      if(output < min) {
        integral.setNoSync(min - output);
        System.out.println("Below min");
      }
      if(output > max) {
        integral.setNoSync(max - output);
        System.out.println("Above max");
      }
      lastIntegral = integral.get();
    });
    */
  }
  
  public void setSaturationLimits(double min, double max) {
    outputMin.setNoSync(min);
    outputMax.setNoSync(max);
  }
}

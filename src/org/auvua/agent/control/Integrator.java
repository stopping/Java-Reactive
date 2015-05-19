package org.auvua.agent.control;

import java.util.function.Supplier;

import org.auvua.reactive.core.RxVar;

public class Integrator extends RxVar<Double> {

  private double integral;
  private double lastVariableValue;
  
  public Integrator(Supplier<Double> integrand) {
    this(integrand, Timer.getInstance(), 0.0);
  }

  public Integrator(Supplier<Double> integrand, Supplier<Double> variable) {
    this(integrand, variable, 0.0);
  }

  public Integrator(Supplier<Double> integrand, Supplier<Double> variable, double init) {
    this.lastVariableValue = variable.get();
    this.setNoSync(init);
    
    this.setSupplier(() -> {
      double currVariableValue = variable.get();
      integral = this.peek();
      integral += integrand.get() * (currVariableValue - lastVariableValue);
      lastVariableValue = currVariableValue;
      return integral;
    });
  }

}

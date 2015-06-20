package org.auvua.agent.signal;

import java.util.function.Supplier;

import org.auvua.agent.control.Timer;
import org.auvua.reactive.core.RxVar;

public class Differentiator extends RxVar<Double> {
	
	private double lastDepValue;
	private double lastIndepValue;
	private double derivative = 0;
	
	public Differentiator(Supplier<Double> dependent) {
	  this(dependent, Timer.getInstance());
	}
	
	public Differentiator(Supplier<Double> dependent, Supplier<Double> independent) {
	  this.lastDepValue = dependent.get();
	  this.lastIndepValue = independent.get();

	  this.setSupplier(() -> {
	    double currDepValue = dependent.get();
	    double currIndepValue = independent.get();
	    if (currIndepValue == lastIndepValue) return 0.0;
	    double derivative = (currDepValue - lastDepValue) / (currIndepValue - lastIndepValue);
	    this.derivative = derivative;
	    this.lastDepValue = currDepValue;
	    this.lastIndepValue = currIndepValue;
	    return this.derivative;
	  });
	}
}

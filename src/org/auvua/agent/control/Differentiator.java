package org.auvua.agent.control;

import java.util.function.Supplier;

import org.auvua.reactive.RxVar;

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
	    double derivative = (currDepValue - lastDepValue) / (currIndepValue - lastIndepValue);
	    if(!Double.isNaN(derivative)) {
	       this.derivative = derivative;
	       this.lastDepValue = currDepValue;
	       this.lastIndepValue = currIndepValue;
	    } else {
	      System.out.println("ERROR: NaN");
	    }
	    return this.derivative;
		});
	}
}

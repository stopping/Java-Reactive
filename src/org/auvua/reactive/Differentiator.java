package org.auvua.reactive;

import java.util.function.Supplier;

public class Differentiator implements Supplier<Double> {
	
	private double lastDepValue;
	private double lastIndepValue;
	private Variable<Double> dependent;
	private Variable<Double> independent;
	private double derivative = 0;
	
	public Differentiator(Variable<Double> dependent, Variable<Double> independent) {
		this.dependent = dependent;
		this.independent = independent;
		this.lastDepValue = dependent.val();
		this.lastIndepValue = independent.val();
	}

	@Override
	public Double get() {
	  double currDepValue = dependent.val();
		double currIndepValue = independent.val();
		double derivative = (currDepValue - lastDepValue) / (currIndepValue - lastIndepValue);
		if(!Double.isNaN(derivative)) {
		   this.derivative = derivative;
		   this.lastDepValue = currDepValue;
		   this.lastIndepValue = currIndepValue;
		} else {
		  System.out.println("ERROR: NaN");
		}
		return this.derivative;
	}
}

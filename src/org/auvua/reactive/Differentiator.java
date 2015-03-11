package org.auvua.reactive;

import java.util.function.Supplier;

public class Differentiator implements Supplier<Double> {
	
	private double derivative = 0.0;
	private double lastDepValue;
	private double lastIndepValue;
	private Variable<Double> dependent;
	private Variable<Double> independent;
	
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
		lastDepValue = currDepValue;
		lastIndepValue = currIndepValue;
		if(!Double.isNaN(derivative)) {
			this.derivative = this.derivative * 0 + derivative * 1;
		}
		return this.derivative;
	}
}

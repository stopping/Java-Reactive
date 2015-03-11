package org.auvua.reactive;

import java.util.function.Supplier;

public class Integrator implements Supplier<Double> {
	
	private double integral;
	private double lastVariableValue;
	private Variable<Double> integrand;
	private Variable<Double> variable;
	
	public Integrator(Variable<Double> integrand, Variable<Double> variable) {
		this.integrand = integrand;
		this.variable = variable;
		this.integral = 0.0;
		this.lastVariableValue = variable.val();
	}
	
	public Integrator(Variable<Double> integrand, Variable<Double> variable, double init) {
		this.integrand = integrand;
		this.variable = variable;
		this.integral = init;
		this.lastVariableValue = variable.val();
	}
	
	@Override
	public Double get() {
		double currVariableValue = variable.val();
		integral += integrand.val() * (currVariableValue - lastVariableValue);
		lastVariableValue = currVariableValue;
		return integral;
	}
	
	public void setIntegral(Double value) {
		integral = value;
	}

}

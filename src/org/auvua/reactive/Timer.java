package org.auvua.reactive;


public class Timer implements Variable<Double> {
	
	double startTime;
	
	public Timer() {
		startTime = System.currentTimeMillis() / 1000.0;
	}

	@Override
	public Double val() {
		return System.currentTimeMillis() / 1000.0 - startTime;
	}

	@Override
	public void set(Double value) {
		// Do nothing
	}
	
	
	
}

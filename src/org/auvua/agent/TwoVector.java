package org.auvua.agent;
import org.auvua.reactive.Rx;
import org.auvua.reactive.RxVar;

public class TwoVector {
  
  public final RxVar<Double> x;
  public final RxVar<Double> y;
  public final RxVar<Double> r;
  public final RxVar<Double> theta;
  
  public TwoVector(RxVar<Double> x, RxVar<Double> y) {
    this.x = x;
    this.y = y;
    this.r = Rx.var(() -> Math.hypot(x.get(), y.get()));
    this.theta = Rx.var(() -> Math.atan(y.get() / x.get()));
  }
  
  public TwoVector() {
    this(Rx.var(0.0), Rx.var(0.0));
  }
}

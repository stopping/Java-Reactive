package org.auvua.agent;
import org.auvua.reactive.core.R;
import org.auvua.reactive.core.RxVar;

public class TwoVector {
  
  public final RxVar<Double> x;
  public final RxVar<Double> y;
  public final RxVar<Double> r;
  public final RxVar<Double> theta;
  
  public TwoVector(RxVar<Double> x, RxVar<Double> y) {
    this.x = x;
    this.y = y;
    this.r = R.var(() -> Math.hypot(x.get(), y.get()));
    this.theta = R.var(() -> Math.atan(y.get() / x.get()));
  }
  
  public TwoVector() {
    this(R.var(0.0), R.var(0.0));
  }
}

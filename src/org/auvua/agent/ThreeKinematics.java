package org.auvua.agent;
public class ThreeKinematics {
  
  public final KinematicsLinear x;
  public final KinematicsLinear y;
  public final KinematicsLinear z;
  
  public ThreeKinematics() {
    this.x = new KinematicsLinear(0,0,0);
    this.y = new KinematicsLinear(0,0,0);
    this.z = new KinematicsLinear(0,0,0);
  }

}

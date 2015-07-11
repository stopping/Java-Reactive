package org.auvua.view;

import javax.media.j3d.BranchGroup;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.AxisAngle4f;
import javax.vecmath.Vector3f;

import org.auvua.model.RobotModel2;

import com.sun.j3d.utils.geometry.ColorCube;
import com.sun.j3d.utils.universe.SimpleUniverse;

public class RobotRenderer {
  
  private TransformGroup objTrans;
  private Transform3D trans;
  private SimpleUniverse universe;
  
  public RobotRenderer() {
    universe = new SimpleUniverse();

    BranchGroup group = new BranchGroup();
    
    objTrans = new TransformGroup();
    objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
    trans = new Transform3D();
    trans.setTranslation(new Vector3f(0.0f,0.0f,0.0f));
    objTrans.setTransform(trans);
    objTrans.addChild(new ColorCube(20.0));

    group.addChild(objTrans);
    
    //universe.getViewingPlatform().setNominalViewingTransform();

    Transform3D viewTrans = new Transform3D();
    viewTrans.setTranslation(new Vector3f(0.0f, -1000.0f, 400.0f));
    viewTrans.setRotation(new AxisAngle4f(1.0f, 0.0f, 0.0f, (float) (Math.PI / 3)));
    universe.getViewingPlatform().getViewPlatformTransform().setTransform(viewTrans);
    universe.getViewer().getView().setBackClipDistance(10000.0f);

    universe.addBranchGraph(group);
  }
  
  public void update() {
    RobotModel2 robot = RobotModel2.getInstance();
    float x = robot.motion.x.pos.get().floatValue();
    float y = robot.motion.y.pos.get().floatValue();
    float z = robot.motion.z.pos.get().floatValue();
    trans.setTranslation(new Vector3f(x, y, z));
    trans.setRotation(new AxisAngle4f(0.0f, 0.0f, 1.0f, robot.angle.get().floatValue()));
    objTrans.setTransform(trans);
  }
  
}

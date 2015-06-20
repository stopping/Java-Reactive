package org.auvua.agent.simulator;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JLabel;

import org.auvua.agent.control.DataRecorder;
import org.auvua.agent.control.StoppingDistance;
import org.auvua.agent.control.Timer;
import org.auvua.agent.oi.OperatorInterface;
import org.auvua.agent.oi.OperatorInterface2;
import org.auvua.agent.tasks.MissionFactory;
import org.auvua.agent.tasks.MissionFactory.MissionType;
import org.auvua.agent.tasks.Task;
import org.auvua.model.RobotModel;
import org.auvua.model.RobotModel2;
import org.auvua.reactive.core.RxVar;
import org.auvua.view.RChart;
import org.auvua.view.RPlane;

public class RobotSim2 {
  
  public static Task command;
  public static Map<Character,Integer> keyMap = new HashMap<Character,Integer>();
  public static RobotModel2 robot = RobotModel2.getInstance();

  public static void main( String[] args ) throws SecurityException, IOException {
    
    buildFrames();

    new Thread(() -> {
      while(true) {
        robot.trigger();
  
        try {
          Thread.sleep(30);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }).start();
    
    new Thread(() -> {
      @SuppressWarnings("resource")
      Scanner input = new Scanner(System.in);
      while(true) {
        Timer.getInstance().scale(input.nextDouble());
      }
    }).start();
  }

  private static void buildFrames() {
    JFrame frame = new JFrame();
    
    OperatorInterface2 oi = new OperatorInterface2();
    
    robot.surgeLeftInput.setSupplier(() -> oi.forward.get() - oi.rotation.get());
    robot.surgeRightInput.setSupplier(() -> oi.forward.get() + oi.rotation.get());
    robot.swayFrontInput.setSupplier(() -> oi.strafe.get() - oi.rotation.get());
    robot.swayBackInput.setSupplier(() -> oi.strafe.get() + oi.rotation.get());
    robot.heaveInput.setSupplier(() -> -oi.elevation.get());
    
    frame.addKeyListener(oi.getKeyListener());

    Container pane = frame.getContentPane();

    JLabel xVelLabel = new JLabel();
    JLabel yVelLabel = new JLabel();

    pane.add(xVelLabel);
    pane.add(yVelLabel);

    frame.setSize(new Dimension(800, 600));
    frame.setVisible(true);
    
    RPlane drawPlane = new RPlane(800, 600);
    
    drawPlane.addPainter((g) -> {
      int x = robot.motion.x.pos.get().intValue() + 400;
      int y = -robot.motion.y.pos.get().intValue() + 300;
      
      g.setColor(Color.WHITE);
      g.drawOval(x - 10, y - 10, 20, 20);
      int x2 = (int) (x + 10 * Math.cos(-robot.angle.get()));
      int y2 = (int) (y + 10 * Math.sin(-robot.angle.get()));
      g.setColor(Color.RED);
      g.drawLine(x, y, x2, y2);
      g.setColor(Color.BLACK);
    });
    
    frame.add(drawPlane.getPanel());
    
    RChart chart = new RChart(800, 600);
    
    /*
    chart.observe(robot.positionSensor.y, "Position (Measured)");
    chart.observe(robot.velocitySensor.y, "Velocity (Measured)");
    chart.observe(((GoToArea) command).target.y, "Target Position");
    chart.observe(stopPosY, "Stopping Position");
    */
    /*
    chart.observe(robot.thrustY, "Thrust");
    chart.observe(robot.thrustInputY, "Thrust Input");
    chart.observe(robot.velocitySensor.y, "Y Velocity");
    */
    chart.observe(robot.depthSensor, "Depth");
    chart.observe(oi.elevation, "Elevation Control");
    chart.observe(robot.velSurge, "Surge velocity");
    chart.observe(robot.velSway, "Sway Velocity");
    chart.observe(robot.dragSurge, "Surge drag");
    chart.observe(robot.dragSway, "Sway drag");
    
    JFrame frame2 = new JFrame();
    frame2.setSize(new Dimension(800, 600));
    frame2.setVisible(true);
    frame2.add(chart.getPanel());
  }

}

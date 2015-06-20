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
import org.auvua.agent.tasks.MissionFactory;
import org.auvua.agent.tasks.MissionFactory.MissionType;
import org.auvua.agent.tasks.Task;
import org.auvua.model.RobotModel;
import org.auvua.reactive.core.RxVar;
import org.auvua.view.RChart;
import org.auvua.view.RPlane;

public class RobotSim {
  
  public static Task command;
  public static Map<Character,Integer> keyMap = new HashMap<Character,Integer>();
  public static RobotModel robot = RobotModel.getInstance();

  public static void main( String[] args ) throws SecurityException, IOException {
    command =  new MissionFactory().build(MissionType.SQUARE_WALK);
    command.start();
    
    buildFrames();
    
    DataRecorder recorder = new DataRecorder("data.txt");
    recorder.record(robot.positionSensor.x, "xPos");
    recorder.record(robot.positionSensor.y, "yPos");
    recorder.record(robot.velocitySensor.x, "yVel");
    recorder.start();

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
    
    OperatorInterface oi = new OperatorInterface();
    
    frame.addKeyListener(oi.getKeyListener());

    Container pane = frame.getContentPane();

    JLabel xVelLabel = new JLabel();
    JLabel yVelLabel = new JLabel();

    pane.add(xVelLabel);
    pane.add(yVelLabel);

    frame.setSize(new Dimension(800, 600));
    frame.setVisible(true);
    
    RxVar<Double> stopPosX = new StoppingDistance(robot.motion.x.vel, robot.controlledAccelX, 200, 200);
    RxVar<Double> stopPosY = new StoppingDistance(robot.motion.y.vel, robot.controlledAccelY, 200, 200);
    
    RPlane drawPlane = new RPlane(800, 600);
    
    drawPlane.addPainter((g) -> {
      int x = robot.motion.x.pos.get().intValue() + 400;
      int y = robot.motion.y.pos.get().intValue() + 300;
      
      int stopX = stopPosX.get().intValue() + x;
      int stopY = stopPosY.get().intValue() + y;
      
      g.drawOval(x - 10, y - 10, 20, 20);
      
      int x2 = (int) (x + robot.thrustX.get());
      int y2 = (int) (y + robot.thrustY.get());
      g.setColor(Color.RED);
      g.drawLine(x, y, x2, y2);
      g.setColor(Color.YELLOW);
      g.drawOval(stopX - 5, stopY - 5, 10, 10);
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
    
    JFrame frame2 = new JFrame();
    frame2.setSize(new Dimension(800, 600));
    frame2.setVisible(true);
    frame2.add(chart.getPanel());
  }

}

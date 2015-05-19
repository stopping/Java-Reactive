package org.auvua.agent.simulator;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.auvua.agent.control.StoppingDistance;
import org.auvua.agent.tasks.GoToArea2;
import org.auvua.agent.tasks.MissionFactory;
import org.auvua.agent.tasks.MissionFactory.MissionType;
import org.auvua.agent.tasks.Task;
import org.auvua.model.RobotModel2;
import org.auvua.reactive.core.RxVar;

public class RobotSim2 {
  
  public static Task command;
  public static RobotModel2 robot = RobotModel2.getInstance();

  public static void main( String[] args ) {
    command =  new MissionFactory().build(MissionType.RANDOM_WALK);
    command.start();
    
    JFrame frame = buildFrame();

    while(true) {
      robot.trigger();
      frame.repaint();
      
      try {
        Thread.sleep(1);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      
    }
  }

  private static JFrame buildFrame() {
    JFrame frame = new JFrame();

    Container pane = frame.getContentPane();

    JLabel xVelLabel = new JLabel();
    JLabel yVelLabel = new JLabel();

    pane.add(xVelLabel);
    pane.add(yVelLabel);

    frame.setSize(new Dimension(800,600));
    frame.setVisible(true);
    
    RxVar<Double> stopPosX = new StoppingDistance(robot.motion.x.vel, robot.motion.x.accel, 100);
    RxVar<Double> stopPosY = new StoppingDistance(robot.motion.y.vel, robot.motion.y.accel, 100);

    frame.add(new JPanel() {
      private static final long serialVersionUID = -3805700651446212348L;

      protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int x = robot.motion.x.pos.get().intValue() + 400;
        int y = robot.motion.y.pos.get().intValue() + 300;
        int xTarget = ((GoToArea2) command).target.x.get().intValue() + 400;
        int yTarget = ((GoToArea2) command).target.y.get().intValue() + 300;
        
        int stopX = stopPosX.get().intValue() + x;
        int stopY = stopPosY.get().intValue() + y;
        
        g.drawOval(x - 10, y - 10, 20, 20);
        
        int x2 = (int) (x + robot.thrust.x.get());
        int y2 = (int) (y + robot.thrust.y.get());
        g.setColor(Color.RED);
        g.drawLine(x, y, x2, y2);
        g.setColor(Color.BLUE);
        g.drawOval(xTarget - 5, yTarget - 5, 10, 10);
        g.drawOval(stopX - 5, stopY - 5, 10, 10);
        g.setColor(Color.BLACK);
      }
    });
    
    return frame;
  }

}

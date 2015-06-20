package org.auvua.agent.simulator;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.auvua.agent.control.StoppingDistanceCalculator;
import org.auvua.agent.control.Timer;
import org.auvua.agent.signal.Differentiator;
import org.auvua.reactive.core.R;
import org.auvua.reactive.core.RxVar;

public class StoppingAnalysis {
  
  static double prevTime;
  static double prevValue;
  static double prevVel;
  static double prevAccel;

  public static void main( String[] args ) {
    
    double v0 = 100;
    double a0 = 100;
    double jm = 100;
    
    StoppingDistanceCalculator d = new StoppingDistanceCalculator(v0, a0, jm);
    
    RxVar<Double> value = R.var(() -> {
      return d.getPosition(Timer.getInstance().get()) - d.finalPosition();
    });
    RxVar<Double> vel = new Differentiator(value);
    RxVar<Double> accel = new Differentiator(vel);

    JFrame frame = new JFrame();

    frame.setSize(new Dimension(1200, 900));
    frame.setVisible(true);
    
    prevTime = Timer.getInstance().get();
    prevValue = value.get();
    prevVel = vel.get();
    prevAccel = accel.get();
    
    BufferedImage image = new BufferedImage(1200, 900, BufferedImage.TYPE_INT_RGB);
    
    frame.add(new JPanel() {
      private static final long serialVersionUID = -3805700651446212348L;

      protected void paintComponent(Graphics g) {
        g.drawImage(image, 0, 0, null);
      }
    });
    
    R.task(() -> {
      double fx = 1;
      double fv = 1;
      double fa = 1;
      
      double t = Timer.getInstance().get();
      double x = value.get();
      double v = vel.get();
      double a = accel.get();
      
      int timeInt = (int) (t * 100) % 1200;
      int valueInt = (int) (-x * fx) + 450;
      int velInt = (int) (-v * fv) + 450;
      int accelInt = (int) (-a * fa) + 450;
      
      int prevTimeInt = (int) (prevTime * 100) % 1200;
      int prevValueInt = (int) (-prevValue * fx) + 450;
      int prevVelInt = (int) (-prevVel * fv) + 450;
      int prevAccelInt = (int) (-prevAccel * fa) + 450;
      
      Graphics g = image.getGraphics();
      if (timeInt >= prevTimeInt) { 
        g.setColor(Color.WHITE);
        g.drawLine(prevTimeInt, prevValueInt, timeInt, valueInt);
        g.setColor(Color.GREEN);
        g.drawLine(prevTimeInt, prevVelInt, timeInt, velInt);
        g.setColor(Color.RED);
        g.drawLine(prevTimeInt, prevAccelInt, timeInt, accelInt);
      } else {
        g.clearRect(0, 0, 2000, 2000);
      }
      
      prevTime = t;
      prevValue = x;
      prevVel = v;
      prevAccel = a;
    });

    while(Timer.getInstance().get() < 10) {
      R.doSync(() -> {
        Timer.getInstance().trigger();
      });
      frame.repaint();
      try {
        Thread.sleep(10);
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }

}

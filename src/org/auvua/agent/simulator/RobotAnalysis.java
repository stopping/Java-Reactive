package org.auvua.agent.simulator;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.auvua.agent.TwoVector;
import org.auvua.agent.control.Delayer;
import org.auvua.agent.control.Differentiator;
import org.auvua.agent.control.FirstOrderSystem;
import org.auvua.agent.control.Integrator;
import org.auvua.agent.control.Limiter;
import org.auvua.agent.control.RateLimiter;
import org.auvua.agent.control.SecondOrderSystem;
import org.auvua.agent.control.TimeInvariantSystem;
import org.auvua.agent.control.Timer;
import org.auvua.model.RobotModel2;
import org.auvua.reactive.core.Rx;
import org.auvua.reactive.core.RxVar;

public class RobotAnalysis {
  
  static double prevTime;
  static double prevValue;
  static double prevSensor;
  static double prevDeriv;

  public static void main( String[] args ) {
    
    RxVar<Double> setPoint = Rx.var(0.0);
    RxVar<Double> output = new Integrator(RobotModel2.getInstance().velocitySensor.x);
    RxVar<Double> diff = Rx.var(() -> setPoint.get() - output.get());
    TimeInvariantSystem controller = new TimeInvariantSystem(diff);
    controller.addPole(-20, 0);
    controller.scale(1);
    RobotModel2.getInstance().thrustInput.x.setSupplier(() -> controller.get());
    
    RxVar<Double> outputDerivative = new Differentiator(output);

    JFrame frame = new JFrame();

    frame.setSize(new Dimension(1200, 900));
    frame.setVisible(true);
    
    prevTime = Timer.getInstance().get();
    prevValue = setPoint.get();
    prevSensor = output.get();
    prevDeriv = outputDerivative.get();
    
    BufferedImage image = new BufferedImage(1200, 900, BufferedImage.TYPE_INT_RGB);
    
    frame.add(new JPanel() {
      private static final long serialVersionUID = -3805700651446212348L;

      protected void paintComponent(Graphics g) {
        g.drawImage(image, 0, 0, null);
      }
    });
    
    Rx.task(() -> {
      double f = 1;
      
      double t = Timer.getInstance().get();
      double v = setPoint.get();
      double s = output.get();
      double d = outputDerivative.get();
      
      int timeInt = (int) (t * 100) % 1200;
      int valueInt = (int) (-v * f) + 450;
      int sensorInt = (int) (-s * f) + 450;
      int derivInt = (int) (-d * f) + 450;
      
      int prevTimeInt = (int) (prevTime * 100) % 1200;
      int prevValueInt = (int) (-prevValue * f) + 450;
      int prevSensorInt = (int) (-prevSensor * f) + 450;
      int prevDerivInt = (int) (-prevDeriv * f) + 450;
      
      Graphics g = image.getGraphics();
      if (timeInt >= prevTimeInt) { 
        g.setColor(Color.WHITE);
        g.drawLine(prevTimeInt, prevValueInt, timeInt, valueInt);
        g.setColor(Color.RED);
        g.drawLine(prevTimeInt, prevSensorInt, timeInt, sensorInt);
        g.setColor(Color.GREEN);
        g.drawLine(prevTimeInt, prevDerivInt, timeInt, derivInt);
      } else {
        g.clearRect(0, 0, 2000, 2000);
      }
      
      prevTime = t;
      prevValue = v;
      prevSensor = s;
      prevDeriv = d;
    });
    
    new Thread(() -> {
      Scanner in = new Scanner(System.in);
      while(true) {
        double d = in.nextDouble();
        Rx.doSync(() -> {
          setPoint.set(d);
          Timer.getInstance().trigger();
        });
      }
    }).start();

    while(true) {
      Rx.doSync(() -> {
        Timer.getInstance().trigger();
        RobotModel2.getInstance().trigger();
      });
      frame.repaint();
      try {
        Thread.sleep(1);
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }

}

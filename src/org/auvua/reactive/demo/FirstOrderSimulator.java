package org.auvua.reactive.demo;

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
import org.auvua.agent.control.Timer;
import org.auvua.agent.signal.Delayer;
import org.auvua.agent.signal.Differentiator;
import org.auvua.agent.simulator.Sensor;
import org.auvua.reactive.core.R;
import org.auvua.reactive.core.RxVar;

public class FirstOrderSimulator {
  
  static double prevTime;
  static double prevValue;
  static double prevSensor;

  public static void main( String[] args ) {
    
    RxVar<Double> mousePosX = R.var(0.0);
    RxVar<Double> mousePosY = R.var(0.0);
    
    RxVar<Double> mouseVelX = R.var(new Differentiator(mousePosX, Timer.getInstance()));
    RxVar<Double> mouseVelY = R.var(new Differentiator(mousePosY, Timer.getInstance()));
    
    RxVar<Double> value = new TwoVector(mouseVelX, mouseVelY).r;
    RxVar<Double> preSine = new Delayer(new Sensor(value), .5);
    RxVar<Double> sensor = R.var(() -> {
      return Math.sin(Timer.getInstance().get() * 6.28 * 60) * 100 + preSine.get();
    });

    JFrame frame = new JFrame();

    frame.setSize(new Dimension(800,600));
    frame.setVisible(true);
    
    prevTime = Timer.getInstance().get();
    prevValue = value.get();
    prevSensor = sensor.get();
    
    BufferedImage image = new BufferedImage(800, 600, BufferedImage.TYPE_INT_RGB);
    
    frame.add(new JPanel() {
      private static final long serialVersionUID = -3805700651446212348L;

      protected void paintComponent(Graphics g) {
        g.drawImage(image, 0, 0, null);
      }
    });
    
    R.task(() -> {
      double f = .05;
      
      double t = Timer.getInstance().get();
      double v = value.get();
      double s = sensor.get();
      
      int timeInt = (int) (t * 100) % 800;
      int valueInt = (int) (-v * f) + 300;
      int sensorInt = (int) (-s * f) + 500;
      
      int prevTimeInt = (int) (prevTime * 100) % 800;
      int prevValueInt = (int) (-prevValue * f) + 300;
      int prevSensorInt = (int) (-prevSensor * f) + 500;
      
      Graphics g = image.getGraphics();
      if (timeInt >= prevTimeInt) { 
        g.setColor(Color.WHITE);
        g.drawLine(prevTimeInt, prevValueInt, timeInt, valueInt);
        g.setColor(Color.RED);
        g.drawLine(prevTimeInt, prevSensorInt, timeInt, sensorInt);
//        g.setColor(new Color(0, 0, 0, 5));
//        g.fillRect(0, 0, 1000, 1000);
      } else {
        g.clearRect(0, 0, 1000, 1000);
      }
      
      prevTime = t;
      prevValue = v;
      prevSensor = s;
    });
    
    new Thread(() -> {
      Scanner input = new Scanner(System.in);
      while(true) {
        double d = input.nextDouble();
        R.doSync(() -> {
          value.set(d);
          Timer.getInstance().trigger();
        });
      }
    }).start();

    while(true) {
      Point mousePos = MouseInfo.getPointerInfo().getLocation();
      R.doSync(() -> {
        mousePosX.set(mousePos.x + 0.0);
        mousePosY.set(mousePos.y + 0.0);
        Timer.getInstance().trigger();
      });
      frame.repaint();
      //System.out.println(Timer.getInstance().get());
      try {
        Thread.sleep(5);
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }

}

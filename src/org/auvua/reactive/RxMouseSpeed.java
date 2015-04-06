package org.auvua.reactive;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.MouseInfo;
import java.awt.Point;

import javax.swing.JFrame;
import javax.swing.JLabel;

public class RxMouseSpeed {

  public static void main( String[] args ) {
    RxVar<Double> mousePosX = Rx.var(0.0);
    RxVar<Double> mousePosY = Rx.var(0.0);
    Variable<Double> time = new Timer();
    
    RxVar<Double> mousePosXAvg = Rx.var(new MovingAverageExponential(mousePosX, .5));
    RxVar<Double> mousePosYAvg = Rx.var(new MovingAverageExponential(mousePosY, .5));
    
    RxVar<Double> mouseVelX = Rx.var(new Differentiator(mousePosXAvg, time));
    RxVar<Double> mouseVelY = Rx.var(new Differentiator(mousePosYAvg, time));

    Point prevDrawPoint = new Point(0,0);
    Point currDrawPoint = new Point(0,0);

    JFrame frame = new JFrame();
    Container pane = frame.getContentPane();
    pane.setLayout(new FlowLayout());

    JLabel xVelLabel = new JLabel();
    JLabel yVelLabel = new JLabel();

    pane.add(xVelLabel);
    pane.add(yVelLabel);

    frame.setSize(new Dimension(800,600));
    frame.setVisible(true);

    Rx.task(() -> {
      xVelLabel.setText(String.format("X Velocity = %5.3f pixels/sec", mouseVelX.val()));
      yVelLabel.setText(String.format("Y Velocity = %5.3f pixels/sec", mouseVelY.val()));
    });
    
    RxVar<Double> t = Rx.var(0.0);

    Rx.task(() -> {
      currDrawPoint.x = t.val().intValue() % 800;
      double vel = Math.hypot(mouseVelX.val(), mouseVelY.val());
      currDrawPoint.y = (int) (-vel / 100 + 300);
      if (currDrawPoint.x > prevDrawPoint.x) { 
        pane.getGraphics().drawLine(prevDrawPoint.x, prevDrawPoint.y, currDrawPoint.x, currDrawPoint.y);
      }
      prevDrawPoint.x = currDrawPoint.x;
      prevDrawPoint.y = currDrawPoint.y;
      
      int velXScaled = (int) (mouseVelX.val() / 50.0);
      int velYScaled = (int) (mouseVelY.val() / 50.0);
      
      pane.getGraphics().clearRect(0, 301, 1000, 1000);
      pane.getGraphics().drawLine(400, 450, 400 + velXScaled, 450 + velYScaled);
    });

    Rx.task(new Runnable() {
      private int i = 800;
      @Override
      public void run() {
        if(t.val() > i) {
          pane.getGraphics().clearRect(0, 0, 1000, 1000);
          i += 800;
        }
      }
    });

    RxTask task = Rx.task(() -> {
      Point mouseLoc = MouseInfo.getPointerInfo().getLocation();
      mousePosX.set(mouseLoc.x + 0.0);
      mousePosY.set(mouseLoc.y + 0.0);
      t.set(time.val() * 100);
      
      try {
        Thread.sleep(10);
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    });

    while(true) {
      task.run();
    }
  }

}

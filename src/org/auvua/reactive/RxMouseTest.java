package org.auvua.reactive;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;
import javax.swing.JLabel;

public class RxMouseTest {

  public static void main( String[] args ) {
    RxVar<Double> mousePosX = Rx.var(0.0);
    RxVar<Double> mousePosY = Rx.var(0.0);
    Variable<Double> time = new Timer();

    RxVar<Double> mouseVelX = Rx.var(new Differentiator(mousePosX, time));
    RxVar<Double> mouseVelY = Rx.var(new Differentiator(mousePosY, time));

    Integrator xIntegrator = new Integrator(mouseVelX, time);
    Integrator yIntegrator = new Integrator(mouseVelY, time);

    RxVar<Double> drawPosX = Rx.var(xIntegrator);
    RxVar<Double> drawPosY = Rx.var(yIntegrator);

    Point prevDrawPoint = new Point(0,0);
    Point currDrawPoint = new Point(0,0);

    Object drawLock = new Object();

    JFrame frame = new JFrame();
    Container pane = frame.getContentPane();
    pane.setLayout(new FlowLayout());

    JLabel xVelLabel = new JLabel();
    JLabel yVelLabel = new JLabel();

    pane.add(xVelLabel);
    pane.add(yVelLabel);

    pane.addMouseListener(new MouseListener() {

      @Override
      public void mouseClicked(MouseEvent arg0) {}

      @Override
      public void mouseEntered(MouseEvent arg0) {
        // TODO Auto-generated method stub
        synchronized(drawLock) {
          Point entrance = arg0.getPoint();
          prevDrawPoint.x = entrance.x;
          prevDrawPoint.y = entrance.y;
          xIntegrator.setIntegral(entrance.x + 0.0);
          yIntegrator.setIntegral(entrance.y + 0.0);
        }
      }

      @Override
      public void mouseExited(MouseEvent arg0) {}
      @Override
      public void mousePressed(MouseEvent arg0) {}
      @Override
      public void mouseReleased(MouseEvent arg0) {}

    });

    frame.setSize(new Dimension(800,600));
    frame.setVisible(true);

    Rx.task(() -> {
      xVelLabel.setText(String.format("X Velocity = %5.3f pixels/sec", mouseVelX.val()));
      yVelLabel.setText(String.format("Y Velocity = %5.3f pixels/sec", mouseVelY.val()));
    });

    Rx.task(() -> {
      synchronized(drawLock) {
        currDrawPoint.x = drawPosX.val().intValue();
        currDrawPoint.y = drawPosY.val().intValue();
        pane.getGraphics().drawLine(prevDrawPoint.x, prevDrawPoint.y, currDrawPoint.x, currDrawPoint.y);
        prevDrawPoint.x = currDrawPoint.x;
        prevDrawPoint.y = currDrawPoint.y;
      }
    });

    while(true) {
      Point mouseLoc = MouseInfo.getPointerInfo().getLocation();
      Rx.doSync(() -> {
        mousePosX.set(mouseLoc.x + 0.0);
        mousePosY.set(mouseLoc.y + 0.0);
      });

      try {
        Thread.sleep(5);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

}

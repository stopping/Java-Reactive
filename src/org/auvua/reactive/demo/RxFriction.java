package org.auvua.reactive.demo;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.auvua.agent.control.Timer;
import org.auvua.agent.signal.Integrator;
import org.auvua.reactive.core.R;
import org.auvua.reactive.core.RxVar;

public class RxFriction {

  public static void main( String[] args ) {

    Map<Character,Double> keyStates = new HashMap<Character,Double>();

    keyStates.put('w', 0.0);
    keyStates.put('a', 0.0);
    keyStates.put('s', 0.0);
    keyStates.put('d', 0.0);

    JFrame frame = new JFrame();

    frame.addKeyListener(new KeyListener() {

      @Override
      public void keyPressed(KeyEvent arg0) {
        keyStates.put(arg0.getKeyChar(), 1.0);
      }

      @Override
      public void keyReleased(KeyEvent arg0) {
        keyStates.put(arg0.getKeyChar(), 0.0);
      }

      @Override
      public void keyTyped(KeyEvent arg0) {}

    });

    RxVar<Double> time = Timer.getInstance();
    RxVar<Double> force = R.var(0.0);
    RxVar<Double> omega = R.var(0.0);
    RxVar<Double> theta = R.var(new Integrator(omega, time));
    
    RxVar<Double> xVelPrev = R.var(0.0);
    RxVar<Double> yVelPrev = R.var(0.0);

    double cd = 1.0;

    RxVar<Double> xVel = new Integrator(R.var(() -> {
      double xV = xVelPrev.peek();
      return force.get() * Math.cos(theta.get()) - xV * cd;
    }), time);

    RxVar<Double> yVel = new Integrator(R.var(() -> {
      double yV = yVelPrev.peek();
      return force.get() * Math.sin(theta.get()) - yV * cd;
    }), time);
    
    xVelPrev.setSupplier(xVel);
    yVelPrev.setSupplier(yVel);

    RxVar<Double> xPos = new Integrator(xVel, time, 100);
    RxVar<Double> yPos = new Integrator(yVel, time, 100);

    Container pane = frame.getContentPane();

    JLabel xVelLabel = new JLabel();
    JLabel yVelLabel = new JLabel();

    pane.add(xVelLabel);
    pane.add(yVelLabel);

    frame.setSize(new Dimension(800,600));
    frame.setVisible(true);

    // Now let's set up 

    R.task(() -> {
      double x = xPos.get();
      if(x <= 0.0 || x >= pane.getSize().width - 20)
        xVel.setNoSync(-xVel.peek());
    });

    R.task(() -> {
      double y = yPos.get();
      if(y <= 0.0 || y >= pane.getSize().height - 20)
        yVel.setNoSync(-yVel.peek());
    });

    frame.add(new JPanel() {
      private static final long serialVersionUID = -3805700651446212348L;

      protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        //g.clearRect(0, 0, 10000, 10000);
        int x = xPos.get().intValue();
        int y = yPos.get().intValue();
        g.drawOval(x, y, 20, 20);
        int x2 = (int) (x + 10 * Math.cos(theta.get()));
        int y2 = (int) (y + 10 * Math.sin(theta.get()));
        g.setColor(Color.RED);
        g.drawLine(x + 10, y + 10, x2 + 10, y2 + 10);
        g.setColor(Color.BLACK);
      }
    });

    while(true) {
      double w = keyStates.get('w');
      double a = keyStates.get('a');
      double s = keyStates.get('s');
      double d = keyStates.get('d');

      R.doSync(() -> {
        force.set(1000 * (w - s));
        omega.set(5 * (d - a));
        Timer.getInstance().trigger();
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

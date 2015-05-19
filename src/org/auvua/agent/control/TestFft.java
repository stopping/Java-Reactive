package org.auvua.agent.control;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class TestFft {

  public static void main(String[] args) {
    double[] x = new double[1024];
    for (int i = 0; i < x.length; i++) {
      double angle = 2.0 * Math.PI * i / x.length;
      x[i] = 10 * Math.sin(angle * 16);
      x[i] += 20 * Math.sin(angle * 32);
//      x[i] = 10 * Math.random();
//      x[i] = i % 128 < 64 ? i % 128 : 128 - i % 128;
//      x[i] = i % 128 < 64 ? 100 : - 100;
//      x[i] = 0;
    }
    double[] w = Transform.fft(x);
    for (int i = 0; i < w.length; i++) {
      System.out.println(w[i]);
    }
    
    JFrame frame = new JFrame();

    frame.setSize(new Dimension(1200,900));
    frame.setVisible(true);
    
    BufferedImage image = new BufferedImage(1200, 900, BufferedImage.TYPE_INT_RGB);
    
    frame.add(new JPanel() {
      private static final long serialVersionUID = -3805700651446212348L;

      protected void paintComponent(Graphics g) {
        g.drawImage(image, 0, 0, null);
      }
    });
    

    // x = Transform.toDoubleArray(Transform.concat(Transform.everyOther(Transform.toComplexArray(x), 0), Transform.everyOther(Transform.toComplexArray(x), 1)));
    Graphics g = image.getGraphics();
    g.setColor(Color.WHITE);
    for (int i = 0; i < x.length; i++) {
      g.fillOval(i, (int) -x[i] + 200, 4, 4);
    }
    g.setColor(Color.RED);
    double max = 0;
    for (int i = 0; i < w.length; i++) {
      if (Math.abs(w[i]) > max) {
        max = Math.abs(w[i]);
      }
      g.fillOval(i, (int) -w[i] / 100 + 400, 4, 4);
    }
    System.out.println("\n" + max);
  }

}

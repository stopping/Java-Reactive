package org.auvua.view;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.JPanel;

import org.auvua.reactive.core.StandardDependency;

public class RPlane extends StandardDependency {
  
  private Image image;
  private List<Consumer<Graphics>> painters = new LinkedList<Consumer<Graphics>>();
  private final JPanel panel;
  
  public RPlane(int width, int height) {
    image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    
    panel = new JPanel() {
      private static final long serialVersionUID = -3805700651446212348L;

      protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, null);
      }
    };
  }

  @Override
  public void update() {
    image.getGraphics().clearRect(0, 0, 1200, 900);
    for (Consumer<Graphics> painter : painters) {
      painter.accept(image.getGraphics());
    }
    panel.repaint();
  }

  @Override
  public void awaitUpdate() {
    // TODO Auto-generated method stub
    
  }
  
  public void addPainter(Consumer<Graphics> painter) {
    painters.add(painter);
    
    determineDependencies();
  }
  
  public JPanel getPanel() {
    return panel;
  }
  
}

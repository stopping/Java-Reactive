package org.auvua.reactive.demo;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.MouseInfo;
import java.awt.Point;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import org.auvua.agent.control.Differentiator;
import org.auvua.agent.control.MovingAverageExponential;
import org.auvua.agent.control.Timer;
import org.auvua.reactive.core.R;
import org.auvua.reactive.core.RxTask;
import org.auvua.reactive.core.RxVar;

public class RxMouseSpeed {

  public static void main( String[] args ) throws LineUnavailableException {
    R.initialize(1);
    
    RxVar<Double> mousePosX = R.var(0.0);
    RxVar<Double> mousePosY = R.var(0.0);
    Timer time = Timer.getInstance();
    
    RxVar<Double> mousePosXAvg = R.var(new MovingAverageExponential(mousePosX, .5));
    RxVar<Double> mousePosYAvg = R.var(new MovingAverageExponential(mousePosY, .5));
    
    RxVar<Double> mouseVelX = R.var(new Differentiator(mousePosXAvg, time));
    RxVar<Double> mouseVelY = R.var(new Differentiator(mousePosYAvg, time));

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

    R.task(() -> {
      xVelLabel.setText(String.format("X Velocity = %5.3f pixels/sec", mouseVelX.get()));
      yVelLabel.setText(String.format("Y Velocity = %5.3f pixels/sec", mouseVelY.get()));
    });
    
    RxVar<Double> t = R.var(0.0);

    R.task(() -> {
      currDrawPoint.x = t.get().intValue() % 800;
      double vel = Math.hypot(mouseVelX.get(), mouseVelY.get());
      currDrawPoint.y = (int) (-vel / 100 + 300);
      if (currDrawPoint.x > prevDrawPoint.x) { 
        pane.getGraphics().drawLine(prevDrawPoint.x, prevDrawPoint.y, currDrawPoint.x, currDrawPoint.y);
      }
      prevDrawPoint.x = currDrawPoint.x;
      prevDrawPoint.y = currDrawPoint.y;
      
      int velXScaled = (int) (mouseVelX.get() / 50.0);
      int velYScaled = (int) (mouseVelY.get() / 50.0);
      
      pane.getGraphics().clearRect(0, 301, 1000, 1000);
      pane.getGraphics().drawLine(400, 450, 400 + velXScaled, 450 + velYScaled);
    });


    
    final AudioFormat af = new AudioFormat(SAMPLE_RATE, 8, 1, true, true);
    SourceDataLine line;
    line = AudioSystem.getSourceDataLine(af);
    line.open(af, SAMPLE_RATE);
    line.start();
    
    boolean sound = false;
    
    R.task(new Runnable() {
      private int i = 800;
      @Override
      public void run() {
        if(t.get() > i) {
          pane.getGraphics().clearRect(0, 0, 1000, 1000);
          i += 800;
        }
        
        if(sound) {
          double vel = Math.hypot(mouseVelX.get(), mouseVelY.get());
          double freq = vel / 5;
          if( freq > 100 ) {
            double periodMs = 1 / freq * 1000;
            double time = 50 - (50 % periodMs);
            byte [] toneBuffer = createSinWaveBuffer(freq, time);
            line.write(toneBuffer, 0, toneBuffer.length);
          }
        }
      }
    });

    RxTask task = R.task(() -> {
      Point mouseLoc = MouseInfo.getPointerInfo().getLocation();
      mousePosX.set(mouseLoc.x + 0.0);
      mousePosY.set(mouseLoc.y + 0.0);
      time.trigger();
      t.set(time.get() * 100);
      
      try {
        Thread.sleep(20);
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    });

    long startTime = System.currentTimeMillis();
    while(System.currentTimeMillis() < startTime + 120 * 1000) {
      task.run();
    }
    
    line.drain();
    line.close();
  }
  
  protected static final int SAMPLE_RATE = 16 * 1024;
  
  public static byte[] createSinWaveBuffer(double freq, double ms) {
    int samples = (int)((ms * SAMPLE_RATE) / 1000);
    byte[] output = new byte[samples];
        //
    double period = (double) (SAMPLE_RATE / freq);
    for (int i = 0; i < output.length; i++) {
        double angle = 2.0 * Math.PI * i / period;
        output[i] = (byte)(Math.sin(angle) * 127f);  }

    return output;
}

}

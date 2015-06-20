package org.auvua.agent.oi;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Map;

import org.auvua.agent.TwoVector;
import org.auvua.agent.control.Timer;
import org.auvua.agent.signal.Integrator;
import org.auvua.reactive.core.R;
import org.auvua.reactive.core.RxVar;

public class OperatorInterface2 {
  
  private Map<Character,Double> keyStates = new HashMap<Character,Double>();
  private KeyListener listener;
  
  public RxVar<Double> forward;
  public RxVar<Double> strafe;
  public RxVar<Double> rotation;
  public RxVar<Double> elevation;
  
  public OperatorInterface2() {
    keyStates.put('w', 0.0);
    keyStates.put('a', 0.0);
    keyStates.put('s', 0.0);
    keyStates.put('d', 0.0);
    keyStates.put('q', 0.0);
    keyStates.put('e', 0.0);
    keyStates.put('r', 0.0);
    keyStates.put('f', 0.0);
    
    forward = R.var(() -> keyStates.get('w') - keyStates.get('s') + 0 * Timer.getInstance().get());
    strafe = R.var(() -> keyStates.get('d') - keyStates.get('a') + 0 * Timer.getInstance().get());
    rotation = R.var(() -> keyStates.get('q') - keyStates.get('e') + 0 * Timer.getInstance().get());
    elevation = R.var(() -> keyStates.get('f') - keyStates.get('r') + 0 * Timer.getInstance().get());
    
    listener = new KeyListener() {

      @Override
      public void keyPressed(KeyEvent e) {
        keyStates.put(e.getKeyChar(), 200.0);
      }

      @Override
      public void keyReleased(KeyEvent e) {
        keyStates.put(e.getKeyChar(), 0.0);
      }

      @Override
      public void keyTyped(KeyEvent e) {}
      
    };
  }
  
  public KeyListener getKeyListener() {
    return listener;
  }
  
}

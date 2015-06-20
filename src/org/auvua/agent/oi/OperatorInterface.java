package org.auvua.agent.oi;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Map;

import org.auvua.agent.TwoVector;
import org.auvua.agent.signal.Integrator;

public class OperatorInterface {
  
  private Map<Character,Double> keyStates = new HashMap<Character,Double>();
  private KeyListener listener;
  
  public TwoVector target;
  
  public OperatorInterface() {
    keyStates.put('w', 0.0);
    keyStates.put('a', 0.0);
    keyStates.put('s', 0.0);
    keyStates.put('d', 0.0);
    
    target = new TwoVector(
        new Integrator(() -> keyStates.get('d') - keyStates.get('a')),
        new Integrator(() -> keyStates.get('s') - keyStates.get('w')));
    
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

package org.auvua.agent.control;


import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.TimerTask;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.auvua.reactive.core.Variable;

public class DataRecorder {
  
  private List<String> labels = new LinkedList<String>();
  private List<Variable<? extends Number>> variables = new LinkedList<Variable<? extends Number>>();
  
  private boolean started = false;
  
  private Logger logger = Logger.getLogger("DataLog");
  
  private java.util.Timer repeatTimer = new java.util.Timer();
  private TimerTask writeTask = new TimerTask() {
    @Override
    public void run() {
      for (Variable<? extends Number> var : variables) {
        logger.log(Level.INFO, String.format("%16.4f", var.get()));
      }
      logger.log(Level.INFO, "\n");
    }
  };
  
  public DataRecorder(String filename) throws SecurityException, IOException {
    FileHandler fh;
    fh = new FileHandler(filename);
    
    fh.setFormatter(new Formatter() {
      public String format(LogRecord record) {
        return record.getMessage();
      }
    });
    
    logger.addHandler(fh);
    logger.setUseParentHandlers(false);
    
    labels.add("time");
    variables.add(Timer.getInstance());
  }
  
  public void record(Variable<? extends Number> var, String label) {
    if (started) return;
    labels.add(label);
    variables.add(var);
  }
  
  public void start() {
    started = true;
    for (String label : labels) {
      logger.log(Level.INFO, String.format("%16s", label));
    }
    logger.log(Level.INFO, "\n");

    repeatTimer.schedule(writeTask, 0, 100);
  }

  public void stop() {
    repeatTimer.cancel();
    started = false;
  }
  
}

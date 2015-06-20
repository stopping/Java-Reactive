package org.auvua.agent.tasks;

import org.auvua.agent.TwoVector;
import org.auvua.model.RobotModel;
import org.auvua.reactive.core.R;

public class DrivingMission implements Mission {
  
  private Task startTask;
  private RobotModel robot = RobotModel.getInstance();
  
  public DrivingMission() {
    GoToArea task1 = new GoToArea(robot, new TwoVector(R.var(-200.0), R.var(-200.0)), 10);
    GoToArea task2 = new GoToArea(robot, new TwoVector(R.var(-200.0), R.var(200.0)), 10);
    GoToArea task3 = new GoToArea(robot, new TwoVector(R.var(200.0), R.var(200.0)), 10);
    GoToArea task4 = new GoToArea(robot, new TwoVector(R.var(200.0), R.var(-200.0)), 10);
    
    MaintainDepth depth1 = new MaintainDepth(robot, R.var(50.0), 5);
    MaintainDepth depth2 = new MaintainDepth(robot, R.var(150.0), 5);
    MaintainDepth surface = new MaintainDepth(robot, R.var(0.0), 5);
    
    depth1.when("atDepth").triggers(() -> {
      task1.start();
    });
    
    R.cond(() -> { 
      return depth1.when("atDepth").get() & task1.when("inArea").get();
    }).triggers(() -> {
      depth1.stop();
      depth2.start();
    });
    
    R.cond(() -> { 
      return depth2.when("atDepth").get() & task1.when("inArea").get();
    }).triggers(() -> {
      task1.stop();
      task2.start();
    });
    
    task2.when("inArea").triggers(() -> {
      task2.stop();
      task3.start();
    });
    
    task3.when("inArea").triggers(() -> {
      task3.stop();
      task4.start();
    });
    
    task4.when("inArea").triggers(() -> {
      depth2.stop();
      surface.start();
    });
    
    startTask = depth1;
  }

  @Override
  public Task getStartTask() {
    return startTask;
  }
  
}

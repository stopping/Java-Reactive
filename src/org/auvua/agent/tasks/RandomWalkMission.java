package org.auvua.agent.tasks;

import org.auvua.agent.TwoVector;
import org.auvua.model.RobotModel;
import org.auvua.reactive.core.R;

public class RandomWalkMission implements Mission {
	
	private RobotModel robot = RobotModel.getInstance();
	private Task startTask;
	
	public RandomWalkMission() {
		buildMission();
	}

	private void buildMission() {
		GoToArea task = new GoToArea(robot, new TwoVector(R.var(Math.random() * 400 - 200), R.var(Math.random() * 400 - 200)), 10);
        Task doNothing = new DoNothing(robot);
        
        R.when(task.getCondition("timeout"))
          .then(() -> {
            task.stop();
            doNothing.start();
          });
        
        R.when(task.getCondition("success"))
          .then(() -> {
            task.target.x.set(Math.random() * 400 - 200);
            task.target.y.set(Math.random() * 400 - 200);
          });
        
        startTask = task;
	}

	@Override
	public Task getStartTask() {
		return startTask;
	}
	
}

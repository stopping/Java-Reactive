package org.auvua.agent.tasks.motion;

import org.auvua.agent.TwoVector;
import org.auvua.agent.control.PidController;
import org.auvua.agent.tasks.AbstractTask;
import org.auvua.model.RobotModel;
import org.auvua.reactive.core.R;
import org.auvua.reactive.core.RxVar;

public class MaintainDepth extends AbstractTask {
	

	public RxVar<Double> desiredDepth;
	public TwoVector output;
	private RobotModel robot;
	private double threshold;

	public MaintainDepth(RobotModel robot, RxVar<Double> desiredDepth, double threshold) {
		this.desiredDepth = desiredDepth;
		this.robot = robot;
		this.threshold = threshold;
		declareCondition("success");
	}

	@Override
	public void initialize() {
		initializeCondition("success", () -> {
			return Math.abs(robot.depthSensor.get() - desiredDepth.get()) < threshold;
		});
		
	    PidController controller = new PidController(robot.depthSensor, desiredDepth, 1, 0, 2.5);
	    robot.thrustInputZ.setSupplier(controller);
	}

}

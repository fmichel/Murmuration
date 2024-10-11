package turtlekit.murmuration;

import jcuda.utils.Timer;
import turtlekit.kernel.TKScheduler;

public class SchedulerFlocking  extends TKScheduler {
	
	protected boolean writeFile = false;
	protected boolean logFile = false;
	
	@Override
	protected void activate() {
		super.activate();
		setSimulationDuration(20000);
	}

	
	@Override
	public void doSimulationStep() {
			
			Timer.startTimer(getTurtleActivator());
			getTurtleActivator().execute();
			Timer.stopTimer(getTurtleActivator());

			Timer.startTimer(getEnvironmentUpdateActivator());
			getEnvironmentUpdateActivator().execute();
			Timer.stopTimer(getEnvironmentUpdateActivator());
			
			getViewerActivator().execute();
			setGVT(getGVT() + 1);
	}
}

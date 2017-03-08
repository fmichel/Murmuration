package turtlekit.murmuration;

import javax.swing.BoundedRangeModel;
import javax.swing.DefaultBoundedRangeModel;

import madkit.gui.AgentFrame;
import turtlekit.kernel.TKModel;

public class FlockingModel extends TKModel{


	/**
	 * The Field of View
	 */
	final static public BoundedRangeModel BIRD_FOV = new DefaultBoundedRangeModel(10,1,1,15){
		public void setValue(int n) {
			super.setValue(n);
			System.err.println("BIRD_FOV = " + n);
		}
	};

	final static public BoundedRangeModel CUDA_FOV = new DefaultBoundedRangeModel(5,1,1,15){
		public void setValue(int n) {
			super.setValue(n);
			System.err.println("CUDA_FOV = " + n);
		}
	};

	/**
	 * The Maximum Speed
	 * @see BirdFlockingCPU#adaptSpeed()
	 */
	final static public BoundedRangeModel MAX_SPEED = new DefaultBoundedRangeModel(2,1,1,10){
		public void setValue(int n) {
			super.setValue(n);
			System.err.println("MAX_SPEED = " + n);
		}
	};

	/**
	 * have to test that for more coherence: less blue birds
	 */
	final static public BoundedRangeModel MAX_DISTANCE = new DefaultBoundedRangeModel(5,1,0,10){
		public void setValue(int n) {
			super.setValue(n);
			System.err.println("MAX_DISTANCE = " + n);
		}
	};

	/**
	 * The minimal distance between agents
	 * @see BirdFlockingCPU#flock()
	 */
	final static public BoundedRangeModel MIN_SEPARATION = new DefaultBoundedRangeModel(1,1,0,10){
		public void setValue(int n) {
			super.setValue(n);
			System.err.println("MIN_SEPARATION = " + n);
		}
	};

	/**
	 * The maximum rotation angle for the separate behavior
	 * @see BirdFlockingCPU#separate()
	 */
	final static public BoundedRangeModel MAX_SEPARATE_TURN = new DefaultBoundedRangeModel(60,1,0,360){
		public void setValue(int n) {
			super.setValue(n);
			System.err.println("MAX_SEPARATE_TURN = " + n);
		}
	};

	/**
	 * The maximum rotation angle for the cohesion behavior
	 * @see BirdFlockingCPU#cohere()
	 */
	final static public BoundedRangeModel MAX_COHERE_TURN = new DefaultBoundedRangeModel(20,1,0,360){
		public void setValue(int n) {
			super.setValue(n);
			System.err.println("MAX_COHERE_TURN = " + n);
		}
	};

	/**
	 * The maximum rotation angle for the align behavior
	 * @see BirdFlockingCPU#align()
	 */
	final static public BoundedRangeModel MAX_ALIGN_TURN = new DefaultBoundedRangeModel(20,1,0,360){
		public void setValue(int n) {
			super.setValue(n);
			System.err.println("MAX_ALIGN_TURN = " + n);
		}
	};

	@Override
	protected void activate() {
		createGUIOnStartUp();
		super.activate();
	}
	
	@Override
	public void setupFrame(AgentFrame frame) {
		frame.add(getParametersView(getName()));
	}
	
	/**
	 * The Maximum Speed
	 * @see BirdFlockingCPU#adaptSpeed()
	 */
	static float maxSpeed = 2.0f;
	/**
	 * The Minimum Speed
	 * @see BirdFlockingCPU#adaptSpeed()
	 */
	static float minSpeed = 0.5f;

}

package turtlekit.murmuration;

import java.awt.Color;
import java.util.List;

import turtlekit.kernel.Turtle;

public class AbstractStarling extends Turtle {

	/**
	 * The Speed
	 * @see BirdFlockingCPU#activate()
	 * @see BirdFlockingCPU#move()
	 */
	protected float speed = 1.0f;
	/**
	 * An adaptative speed value
	 * @see BirdFlockingCPU#align()
	 * @see BirdFlockingCPU#cohere()
	 */
	protected float adapatativeSpeed = 0.1f;
	/**
	 * The nearest neighbor
	 * @see BirdFlockingCPU#flock()
	 * @see BirdFlockingCPU#separate()
	 * @see BirdFlockingCPU#align()
	 */
	protected AbstractStarling nearestBird;
	/**
	 * The neighbor list
	 * @see BirdFlockingCPU#flock()
	 * @see BirdFlockingCPU#cohere()
	 */
	protected List<AbstractStarling> neighborBirds;

	/**
	 * Return the Field of View
	 * @return vision
	 */
	public static int getVision() {
	    return FlockingModel.BIRD_FOV.getValue();
	}

	public AbstractStarling(String initMethod) {
		super(initMethod);
	}

	public AbstractStarling() {
		super();
	}

	/**
	 * Return the Speed
	 * @return speed
	 */
	public float getSpeed() {
	    return speed;
	}

	/**
	 * Set the Speed of the agent
	 * @param speed
	 *            The new Speed
	 * 
	 * @see BirdFlockingCPU#align()
	 * @see BirdFlockingCPU#cohere()
	 */
	public void setSpeed(float speed) {
	    this.speed = speed;
	}

	/**
	 * Activate the agent
	 * @see Turtle#activate()
	 */
	protected void activate() {
	    super.activate();
	    setColor(Color.YELLOW);
	    home();
	    randomHeading();
	    speed = generator.nextFloat() + 0.5f;
	    adapatativeSpeed = speed / 5.0f;
	    setNextAction("flock");
	}

	/**
	 * Agent behavior : move
	 */
	public void move() {
	    fd(speed);
	}

	/**
	 * Random heading for the agent
	 */
	public void headingChange() {
	    randomHeading();
	}

	/**
	 * Fill its heading value in the environment
	 */
	public void fillHeadingEnvironment(double heading) {
	    ((MurmurationEnvironment) getEnvironment()).setCudaHeadingValue(this.xcor(), this.ycor(), heading);
	}

	/**
	 * Random heading for the agent
	 */
	public double changeHeading(double heading, int turn) {
		if (generator.nextBoolean()) {
			return heading = heading + generator.nextInt(turn);
		} else {
			return heading = heading - generator.nextInt(turn);
		}
	}

	/**
	 * Test pour connaitre si je suis dans l'intervalle
	 */
	public boolean amIInTheInterval(double myHeading, double otherHeading, int interval) {
		double diffTwoAngle = differenceTwoAngle(myHeading, otherHeading);
		return (diffTwoAngle <= interval);
	}

	/**
	 * Connaitre la différence entre deux angles (sans prise en compte des signes)
	 */
	public double differenceTwoAngle(double targetA, double targetB) {
		double d = Math.abs(targetA - targetB) % 360;
		return d > 180 ? 360 - d : d;
	}

	/**
	 * Adapter la direction en fonction de la différence d'angle entre l'agent et la cible
	 */
	public double changeHeadingReduceInterval(double myHeading, double otherHeading, int turn) {
	//		//GOOD for GPU
			double differenceAngle = differenceTwoAngle(myHeading, otherHeading);
			int turnAngle = generator.nextInt(turn);
			double temp = differenceTwoAngle((myHeading + turnAngle), otherHeading);
			
			if(temp > differenceAngle){
				return myHeading = myHeading - turnAngle;
			}
			else{
				return myHeading = myHeading + turnAngle;
			}
			
	//		Good for CPU
	//		double differenceAngle = differenceTwoAngle(myHeading, otherHeading);
	//		int turnAngle = generator.nextInt(turn);
	//		while (turnAngle > differenceAngle){
	//			turnAngle = generator.nextInt(turn);
	//		}
	//		double tempP = differenceTwoAngle((myHeading + turnAngle), otherHeading);
	//		double tempM = differenceTwoAngle((myHeading - turnAngle), otherHeading);
	//
	//		if(differenceAngle == 0){
	//			return myHeading;
	//		}
	//		else {
	//			if(tempP > tempM){
	//				return myHeading = myHeading - turnAngle;
	//			}
	//			else{
	//				return myHeading = myHeading + turnAngle;
	//			}
	//		}
		}

	/**
	 * Connaitre la différence entre deux angles (avec prise en compte des signes)
	 */
	public double differenceTwoAngleV2(double myHeading, double otherHeading) {
		double a = (otherHeading - myHeading) % 360;
		if(a < -180) a += 360;
		if(a >  180) a -= 360;
		return a;
	}

	/**
	 * Adapter la direction en fonction de la différence d'angle entre l'agent et la cible (en fonction du signe de la diff d'angle)
	 */
	public double changeHeadingReduceIntervalV2(double myHeading, double otherHeading, int turn) {
		double differenceAngle = differenceTwoAngleV2(myHeading, otherHeading);
		int turnAngle = generator.nextInt(turn);
		
		if(differenceAngle > 0){
			return myHeading = myHeading - turnAngle;
		}
		else{
			return myHeading = myHeading + turnAngle;
		}
	}

	/**
	 * Adapt the speed
	 */
	public void adaptSpeed(float comparativeSpeed) {
	    float currentSpeed = this.getSpeed();
	    if (currentSpeed > FlockingModel.maxSpeed) {
	        currentSpeed = currentSpeed - adapatativeSpeed;
	    } else if (currentSpeed < FlockingModel.minSpeed) {
	        currentSpeed = currentSpeed + adapatativeSpeed;
	    } else {
	        if (currentSpeed > comparativeSpeed) {
	            currentSpeed = currentSpeed - adapatativeSpeed;
	        } else {
	            currentSpeed = currentSpeed + adapatativeSpeed;
	        }
	    }
	    this.setSpeed(currentSpeed);
	}

}
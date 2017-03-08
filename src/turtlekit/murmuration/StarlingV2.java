package turtlekit.murmuration;

import java.awt.Color;
import java.util.List;

import turtlekit.kernel.Turtle;
import turtlekit.kernel.TurtleKit.Option;
import turtlekit.viewer.TKDefaultViewer;

/**
 * BirdFlocking represents the "Bird" of the simulation
 * <p>
 * A "Bird" agent is characterized by:
 * <ul>
 * <li>A Field of View</li>
 * <li>A Speed</li>
 * <li>Rotation Angles</li>
 * <li>A neighbor list</li>
 * </ul>
 * </p>
 * 
 * @author Emmanuel Hermellin
 * @author Fabien Michel
 * 
 * @version 0.1
 * 
 * @see turtlekit.kernel.Turtle
 * 
 */

public class StarlingV2 extends AbstractStarling {


	/**
	 * Agent behavior : global behavior
	 * According to the distance between the agents, the different Reynolds's rules will be activated
	 */
	public String flock() {
		
//		neighborBirds = getOtherTurtles(FlockingModel.vision, true);
	
	    neighborBirds = getPatch().getTurtles(FlockingModel.BIRD_FOV.getValue(), true, AbstractStarling.class);
	    neighborBirds.remove(this);
	    
	    if(neighborBirds.isEmpty()){
	        move();
//	        fillHeadingEnvironment(this.getHeading());//useless: nobody around
	        return "flock";
	    }
	    nearestBird = neighborBirds.get(0);
	    final double distance = distance(nearestBird);
		if (distance < FlockingModel.MIN_SEPARATION.getValue()) {
	        return "separate";
	    } 
//		else if(distance > FlockingModel.MAX_DISTANCE.getValue()){//I am too far away from my peers
//	    	StarlingV2 flockingBird = neighborBirds.parallelStream().filter(b -> {return b.getColor() == Color.GREEN;}).findAny().orElse(nearestBird);
//			setHeadingTowards(flockingBird);
//			adaptSpeed(.1f);
//			move();
//	        return "flock";
//		}
	    
	    if(neighborBirds.size() >5)
            return "cohere";
        return "align";
	}

	/**
	 * Agent behavior : separate
	 * If agents are too close, they separate
	 */
	public String separate() {
	    this.setColor(Color.RED);
	
	    double headingInterseptionNearestBird = getHeading();
		try {
			headingInterseptionNearestBird = this.towards(nearestBird);
		} catch (ArithmeticException e) {
			randomHeading();
		}
	
	    if (amIInTheInterval(this.getHeading(), headingInterseptionNearestBird, FlockingModel.MAX_SEPARATE_TURN.getValue()*2)) {
			this.setHeading(changeHeading(this.getHeading(), FlockingModel.MAX_SEPARATE_TURN.getValue()));
		}
	
	    adaptSpeed(nearestBird.getSpeed() + generator.nextFloat());
	    move();
	    fillHeadingEnvironment(this.getHeading());// Avant double myHeading = this.getHeading()
	    return "flock";
	}

	/**
	 * Agent behavior : align
	 * The agent searches for align its movement to its neighbor
	 */
	public String align() {
	    this.setColor(Color.BLUE);
	
	    double otherHeading = nearestBird.getHeading();
	    
		if (!amIInTheInterval(this.getHeading(), otherHeading, 1)) {
			this.setHeading(changeHeadingReduceInterval(this.getHeading(),otherHeading,FlockingModel.MAX_ALIGN_TURN.getValue()));
		}
		
	    adaptSpeed(nearestBird.getSpeed());
	    move();
	    fillHeadingEnvironment(this.getHeading());// Avant double myHeading = this.getHeading()
	    return "flock";
	}

	/**
	     * Agent behavior : cohesion
	     * The agents try to make a group during their movement
	     */
	    public String cohere() {
	        
	        float globalHeading = 0;
	                 
	        int size = getOtherTurtles(0, true).size();
			if(size > 0){
				this.setColor(Color.WHITE);
	        }
	        else{
	        	this.setColor(Color.GREEN);
	        }
	        
	        if(!MurmurationEnvironment.isCUDA()){
	        	float globalSpeed = 0;
	        	
	             for(AbstractStarling bird : neighborBirds){
	             globalHeading += bird.getHeading();
	             globalSpeed += bird.getSpeed();
	             }
	             
	             globalHeading = globalHeading / neighborBirds.size();
	             globalSpeed = globalSpeed / neighborBirds.size();
	             
	             adaptSpeed(globalSpeed);
	        }
	        else{
	            globalHeading = ((MurmurationEnvironment) getEnvironment()).getCudaHeadingValue(this.xcor(), this.ycor());
	        }
	 
	//        if (myHeading > globalHeading) {
	//            myHeading = myHeading - generator.nextInt(maxCohereTurn);
	//        } else if (myHeading < globalHeading) {
	//            myHeading = myHeading + generator.nextInt(maxCohereTurn);
	//        } else {
	//            myHeading = globalHeading;
	//        }
	// 
	//        this.setHeading(myHeading);
	        
			if (!amIInTheInterval(this.getHeading(), globalHeading, 1)) {
				this.setHeading(changeHeadingReduceInterval(this.getHeading(),globalHeading,FlockingModel.MAX_COHERE_TURN.getValue()));
			}
	         
	        move();
	        fillHeadingEnvironment(this.getHeading());
	 
	        int currentBehaviorCount = getCurrentBehaviorCount();//why ?
	        if (currentBehaviorCount > 10) {
	            return "flock";
	        }
	        
	        return "cohere";
	    }

	/**
	 * Agent behavior : move
	 */
	public void move() {
	    fd(speed);
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
     * Random heading for the agent
     */
    public void headingChange() {
        randomHeading();
    }
     
    /**
     * Fill its heading value in the environment
     */
    public void fillHeadingEnvironment(double heading){
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
	public boolean amIInTheInterval(double myHeading, double otherHeading, int interval){
		double diffTwoAngle = differenceTwoAngle(myHeading, otherHeading);
		return (diffTwoAngle <= interval);
	}
			
	/**
	 * Connaitre la différence entre deux angles (sans prise en compte des signes)
	 */
	public double differenceTwoAngle(double targetA, double targetB){
		double d = Math.abs(targetA - targetB) % 360;
		return d > 180 ? 360 - d : d;
	}
	
	/**
	 * Adapter la direction en fonction de la différence d'angle entre l'agent et la cible
	 */
	public double changeHeadingReduceInterval(double myHeading, double otherHeading, int turn){
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
	public double differenceTwoAngleV2(double myHeading, double otherHeading){
		double a = (otherHeading - myHeading) % 360;
		if(a < -180) a += 360;
		if(a >  180) a -= 360;
		return a;
	}
	
	/**
	 * Adapter la direction en fonction de la différence d'angle entre l'agent et la cible (en fonction du signe de la diff d'angle)
	 */
	public double changeHeadingReduceIntervalV2(double myHeading, double otherHeading, int turn){
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
        if (currentSpeed > FlockingModel.MAX_SPEED.getValue()) {
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
    
    public static void main(String[] args) {
		executeThisTurtle(4000
				,Option.environment.toString(),MurmurationEnvironment.class.getName()
				,Option.viewers.toString(),FlockingModel.class.getName()+";"+
				TKDefaultViewer.class.getName()
				,Option.envWidth.toString(),"512,512"
				,Option.startSimu.toString()
				,Option.cuda.toString()
				);
	}
}
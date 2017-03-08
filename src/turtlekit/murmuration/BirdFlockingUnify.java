package turtlekit.murmuration;

import java.awt.Color;

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
 * 
 * @version 0.1
 * 
 * @see turtlekit.kernel.Turtle
 * 
 */

public class BirdFlockingUnify extends AbstractStarling {

    /**
     * Agent behavior : global behavior
     * According to the distance between the agents, the different Reynolds's rules will be activated
     */
    public String flock() {
 
        neighborBirds = getPatch().getTurtles(FlockingModel.BIRD_FOV.getValue(), false, AbstractStarling.class);
        nearestBird = null;
         
        if (! neighborBirds.isEmpty()) {
            nearestBird = neighborBirds.get(0);
        }
        if (nearestBird == null || neighborBirds.isEmpty()) {
            move();
            fillHeadingEnvironment(this.getHeading());
            return "flock";
        }
        if (this.distance(nearestBird) < FlockingModel.MIN_SEPARATION.getValue()) {
            return "separate";
        }
        else {
            if (neighborBirds.size() > 5) {
                return "cohere";
            } else {
                return "align";
            }
        }
    }
 
    /**
     * Agent behavior : separate
     * If agents are too close, they separate
     */
    public String separate() {
        this.setColor(Color.RED);

        double headingInterseptionNearestBird = this.towards(nearestBird);
 
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
        
        double globalHeading = 0;
                 
        int size = getOtherTurtles(0, true).size();
		if(size > 0){
			this.setColor(Color.WHITE);
        }
        else{
        	this.setColor(Color.GREEN);
        }
        
        if(!MurmurationEnvironment.isCUDA()){
        	float globalSpeed = 0;
        	
//        	 globalHeading = neighborBirds.parallelStream().mapToDouble(b-> b.getHeading()).average().orElse(globalHeading);//Fab if not using speed

        	for(AbstractStarling bird : neighborBirds){
             globalHeading += bird.getHeading();
             globalSpeed += bird.getSpeed();
             }
             
             globalHeading = globalHeading / neighborBirds.size();
             globalSpeed = globalSpeed / neighborBirds.size();
             
             adaptSpeed(globalSpeed); //TODO and with Cuda ??
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
 
        int currentBehaviorCount = getCurrentBehaviorCount();
        if (currentBehaviorCount > 10) {
            return "flock";
        }
        
        return "cohere";
    }
}
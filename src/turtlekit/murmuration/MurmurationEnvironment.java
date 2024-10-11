package turtlekit.murmuration;

import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import turtlekit.cuda.CudaAverageField;
import turtlekit.cuda.CudaEngine;
import turtlekit.kernel.Patch;
import turtlekit.kernel.TKEnvironment;

/**
 * The MurmurationEnvironment in this flocking simulation
 * <p>
 * The environment is characterized by:
 * <ul>
 * <li>Its size</li>
 * </ul>
 * </p>
 * 
 * @author Emmanuel Hermellin
 * 
 * @version 0.1
 * 
 * @see turtlekit.kernel.TKEnvironment
 * 
 */

public class MurmurationEnvironment extends TKEnvironment {
    
    /**
     * The GPU Module
     */
    private CudaAverageField cudaHeadingGrid;
     
//    /**
//     * The size of the environment
//     */
//    private static int envDimension = 512;//use super class
     
    /**
     * Do you want to use CUDA ?
     */
//    private static boolean CUDA = true; //TODO move that option in the launchers
    private static boolean CUDA = false;
     
    /**
     * The array containing the heading of all the agents
     */
    private static float headingSheet[];
     
    /**
     * Random number generator
     */
    protected static Random generator = new Random(); //TODO ne peut on pas réutiliser celui des Turtles ?????
 
    /**
     * Return the GPU module
     * @return cudaHeadingGrid
     */
    public CudaAverageField getCudaHeadingGrid() {
        return cudaHeadingGrid;
    }
     
    /**
     * Return if CUDA is used or not
     * @return CUDA
     */
    public static boolean isCUDA() {
        return CUDA;
    }
     
    /**
     * Activate of the MurmurationEnvironment
     * @see TKEnvironment#activate()
     */
    protected void activate(){
        super.activate();
        headingSheet = new float[getWidth()*getHeight()];
//        makeTheCache();
        cudaHeadingGrid = new CudaAverageField("Average",getWidth(),getHeight(),headingSheet);
        cleanHeadingSheet();
    }
     
    /**
     * Forcing caching of the Patch
     * @see MurmurationEnvironment#activate()
     */
    protected void makeTheCache(){
        for (Patch p : getPatchGrid()) {
            p.getNeighbors(10, true);
        }
    }
     
    /**
     * Update the environment
     * @see TKEnvironment#update()
     */
    @Override
    protected void update() {
    	
        if(isCudaOn()){
 
//        	updateSpeedAndHeadingSheetV2();
            cudaHeadingGrid.computeAverage(FlockingModel.CUDA_FOV.getValue());
 
            if(isSynchronizeGPU()){
                CudaEngine.cuCtxSynchronizeAll();   
            }

            cleanHeadingSheet();
        }
    }
     
    /**
     * Accessing data compute by the GPU module
     * @see CudaAverageField
     */
    public float getCudaHeadingValue(int xcor, int ycor){
        return cudaHeadingGrid.getResult(get1DIndex(xcor, ycor));
    }
     
    /**
     * Set data which will be compute by the GPU module
     * @see CudaAverageField
     */
    public void setCudaHeadingValue(int xcor, int ycor, double heading){
        cudaHeadingGrid.set(get1DIndex(xcor, ycor),((float)heading));
    }
     
    /**
     * Update the array containing heading of the agents
     */
    protected void updateSpeedAndHeadingSheet(){
            for(int i = 0 ; i < getWidth() * getHeight() ; i++){
                List<BirdFlockingUnify> turtleList = getPatchGrid()[i].getTurtles(BirdFlockingUnify.class);
                if(turtleList.isEmpty()){
                    cudaHeadingGrid.set(i,-1);
                }
                else{
                    for(AbstractStarling b : turtleList){
                        cudaHeadingGrid.set(i,(float)b.getHeading());
                    }
                }
            }
    }
     
    /**
     * Update the array containing heading of the agents (V2)
     */
    protected void updateSpeedAndHeadingSheetV2(){
    	IntStream.range(0, getWidth() * getHeight()).parallel()
    		.forEach(i -> {if(getPatchGrid()[i].isEmpty()) cudaHeadingGrid.set(i, -1);});
//        int j = envDimension * envDimension;
//        for(int i = j - 1 ; i > 0 ; i--){
//            if(patchSheet[i].isEmpty()){
//                cudaHeadingGrid.set(i,-1);
//            }
//        }
    }

    /**
     * Update the array containing heading of the agents (V2)
     */
    protected void cleanHeadingSheet(){
    	IntStream.range(0, getWidth() * getHeight()).parallel()
    		.forEach(i -> {cudaHeadingGrid.set(i, -1);});
//        int j = envDimension * envDimension;
//        for(int i = j - 1 ; i > 0 ; i--){
//            if(patchSheet[i].isEmpty()){
//                cudaHeadingGrid.set(i,-1);
//            }
//        }
    }
}


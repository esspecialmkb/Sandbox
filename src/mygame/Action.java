/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.math.FastMath;
import java.util.ArrayList;

/**
 *
 * @author Maestro
 */
public class Action {
    protected ArrayList<KeyFrame> frames;
    private float currentTime;
    private float totalTime;
    private float currentFrame;
    private int currentKeyFrame;
    private KeyFrame interpolatedFrame;
    protected int id, flag;
    protected String name;
    private int nextAction;
    protected boolean active;
    
    public Action(String name){
        this.frames = new ArrayList<KeyFrame>();
        this.flag = 0;
        this.name = name;
        this.active = false;
        this.interpolatedFrame = new KeyFrame(-1,-1);
    }
    
    public void init(){
        //Divide last frame# by 30 (30 fps)
        this.totalTime = this.frames.get((this.frames.size() - 1)).frameNumber / 30;
    }
    
    public void start(){
        this.currentTime = 0;
        this.currentFrame = 0;
        this.currentKeyFrame = 0;
        this.active = true;
    }
    
    public void end(){
        this.active = false;
    }
    
    public boolean update(float tpf){
        //Update the time
        //this.currentTime = this.currentTime + (tpf);
        //this.currentFrame = this.currentTime * 30;
        //System.out.println("Time: " + currentTime + ", TPF: " + tpf + ", Frame: " + currentFrame);
        if(this.frames.size() == 1){
            //Pose Action
            interpolatedFrame.LHip_x = frames.get(0).LHip_x;
            interpolatedFrame.LHip_y = frames.get(0).LHip_y;
            interpolatedFrame.LHip_z = frames.get(0).LHip_z;
            interpolatedFrame.RHip_x = frames.get(0).RHip_x;
            interpolatedFrame.RHip_y = frames.get(0).RHip_y;
            interpolatedFrame.RHip_z = frames.get(0).RHip_z;
            interpolatedFrame.LShoulder_x = frames.get(0).LShoulder_x;
            interpolatedFrame.LShoulder_y = frames.get(0).LShoulder_y;
            interpolatedFrame.LShoulder_z = frames.get(0).LShoulder_z;
            interpolatedFrame.RShoulder_x = frames.get(0).RShoulder_x;
            interpolatedFrame.RShoulder_y = frames.get(0).RShoulder_y;
            interpolatedFrame.RShoulder_z = frames.get(0).RShoulder_z;
            interpolatedFrame.handle_x = frames.get(0).handle_x;
            interpolatedFrame.handle_y = frames.get(0).handle_y;
            interpolatedFrame.handle_z = frames.get(0).handle_z;
            interpolatedFrame.waist_x = frames.get(0).waist_x;
            interpolatedFrame.waist_y = frames.get(0).waist_y;
            interpolatedFrame.waist_z = frames.get(0).waist_z;
            return true;
        }else if(this.frames.size() > 1){
            this.currentTime = this.currentTime + (tpf);
            this.currentFrame = this.currentTime * 30;
            if(currentFrame > frames.get(frames.size() - 1).frameNumber ){
                //The animation has passed the last frame...
                if(this.flag == 0){
                    this.end();
                    return false;
                }
                //If we are looping, reset
                if(this.flag == 1){
                    this.currentTime = this.currentTime % this.totalTime;
                    this.currentFrame = this.currentTime * 30;
                }
                //System.out.println("Action Update : Looping Frame" + currentFrame);
            }
            for(int i = 0;i<(frames.size() - 1);i++){
                //System.out.println("Logic Test: currentFrame( " + currentTime + " ) > startFrame( " + frames.get(i).frameNumber + " ) AND currentFrame < endFrame( " + frames.get(i + 1).frameNumber + " )");
                if((currentFrame > frames.get(i).frameNumber) && (currentFrame < frames.get(i + 1).frameNumber) ){
                    //Calculate an interpolated frame
                    
                    float start = frames.get(i).frameNumber;
                    float end = frames.get(i + 1).frameNumber;
                    float delta = ((currentFrame - start)/(end - start));
                    
                    //System.out.println("Interpolate : Delta " + delta);
                    
                    interpolatedFrame.LHip_x = FastMath.interpolateLinear(delta, frames.get(i).LHip_x, frames.get(i + 1).LHip_x);
                    interpolatedFrame.LHip_y = FastMath.interpolateLinear(delta, frames.get(i).LHip_y, frames.get(i + 1).LHip_y);
                    interpolatedFrame.LHip_z = FastMath.interpolateLinear(delta, frames.get(i).LHip_z, frames.get(i + 1).LHip_z);
                    interpolatedFrame.RHip_x = FastMath.interpolateLinear(delta, frames.get(i).RHip_x, frames.get(i + 1).RHip_x);
                    interpolatedFrame.RHip_y = FastMath.interpolateLinear(delta, frames.get(i).RHip_y, frames.get(i + 1).RHip_y);
                    interpolatedFrame.RHip_z = FastMath.interpolateLinear(delta, frames.get(i).RHip_z, frames.get(i + 1).RHip_z);
                    interpolatedFrame.LShoulder_x = FastMath.interpolateLinear(delta, frames.get(i).LShoulder_x, frames.get(i + 1).LShoulder_x);
                    interpolatedFrame.LShoulder_y = FastMath.interpolateLinear(delta, frames.get(i).LShoulder_y, frames.get(i + 1).LShoulder_y);
                    interpolatedFrame.LShoulder_z = FastMath.interpolateLinear(delta, frames.get(i).LShoulder_z, frames.get(i + 1).LShoulder_z);
                    interpolatedFrame.RShoulder_x = FastMath.interpolateLinear(delta, frames.get(i).RShoulder_x, frames.get(i + 1).RShoulder_x);
                    interpolatedFrame.RShoulder_y = FastMath.interpolateLinear(delta, frames.get(i).RShoulder_y, frames.get(i + 1).RShoulder_y);
                    interpolatedFrame.RShoulder_z = FastMath.interpolateLinear(delta, frames.get(i).RShoulder_z, frames.get(i + 1).RShoulder_z);
                    interpolatedFrame.handle_x = FastMath.interpolateLinear(delta, frames.get(i).handle_x, frames.get(i + 1).handle_x);
                    interpolatedFrame.handle_y = FastMath.interpolateLinear(delta, frames.get(i).handle_y, frames.get(i + 1).handle_y);
                    interpolatedFrame.handle_z = FastMath.interpolateLinear(delta, frames.get(i).handle_z, frames.get(i + 1).handle_z);
                    interpolatedFrame.waist_x = FastMath.interpolateLinear(delta, frames.get(i).waist_x, frames.get(i + 1).waist_x);
                    interpolatedFrame.waist_y = FastMath.interpolateLinear(delta, frames.get(i).waist_y, frames.get(i + 1).waist_y);
                    interpolatedFrame.waist_z = FastMath.interpolateLinear(delta, frames.get(i).waist_z, frames.get(i + 1).waist_z);
                    
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @return the interpolatedFrame
     */
    public KeyFrame getInterpolatedFrame() {
        return interpolatedFrame;
    }

    /**
     * @return the currentTime
     */
    public float getCurrentTime() {
        return currentTime;
    }

    /**
     * @param currentTime the currentTime to set
     */
    public void setCurrentTime(float currentTime) {
        this.currentTime = currentTime;
    }

    /**
     * @return the nextAction
     */
    public int getNextAction() {
        return nextAction;
    }

    /**
     * @param nextAction the nextAction to set
     */
    public void setNextAction(int nextAction) {
        this.nextAction = nextAction;
    }

    /**
     * @return the currentFrame
     */
    public float getCurrentFrame() {
        return currentFrame;
    }

    /**
     * @param currentFrame the currentFrame to set
     */
    public void setCurrentFrame(float currentFrame) {
        this.currentFrame = currentFrame;
    }
}

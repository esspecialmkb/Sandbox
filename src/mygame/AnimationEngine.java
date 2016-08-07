/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import java.util.ArrayList;

/**
 *
 * @author Maestro
 */
public class AnimationEngine {
    protected ArrayList<Action> actions;
    protected KeyFrame frame;
    private int currentAction;
    private int prevAction;
    
    /**
     *  This constructor creates default actions stand and walk
     * flag 0 - single-frame/pose
     * flag 1 - loop animation
     */
    public AnimationEngine(){
        this.currentAction = 0;
        this.prevAction = 0;
        this.frame = new KeyFrame(-1,-1);
        //Create stand action
        Action stand = new Action("Stand");
        KeyFrame frame = new KeyFrame(1,0);
        frame.frameNumber = 0;
        stand.frames.add(frame);
        
        //Create walk action
        Action walk = new Action("Walk");
        KeyFrame w_frame0 = new KeyFrame(2,0);
        w_frame0.frameNumber = 0;
        w_frame0.LHip_x = 50 * FastMath.DEG_TO_RAD;
        w_frame0.RHip_x = -50 * FastMath.DEG_TO_RAD;
        
        KeyFrame w_frame1 = new KeyFrame(3,0);
        w_frame1.frameNumber = 7;
        w_frame1.LHip_x = -50 * FastMath.DEG_TO_RAD;
        w_frame1.RHip_x = 50 * FastMath.DEG_TO_RAD;
        
        KeyFrame w_frame2 = new KeyFrame(4,0);
        w_frame2.frameNumber = 14;
        w_frame2.LHip_x = 50 * FastMath.DEG_TO_RAD;
        w_frame2.RHip_x = -50 * FastMath.DEG_TO_RAD;
        
        walk.frames.add(w_frame0);
        walk.frames.add(w_frame1);
        walk.frames.add(w_frame2);
        walk.flag = 1;
        walk.init();
        //Add all actions to engine
        actions = new ArrayList<Action>();
        actions.add(stand);
        actions.add(walk);
        
    }
    
    /**
     *  Call AnimationEngine.update to calculate the next frame
     * @param tpf - timePerFrame
     */
    public void update(float tpf){
        if(this.prevAction == this.currentAction){
            if(actions.get(this.currentAction).active == true){
                //Calculate the next interpolated frame
                if(actions.get(this.currentAction).update(tpf) == true){
                    //If the update returns true, continue the animation
                    this.frame = actions.get(this.currentAction).getInterpolatedFrame();
                }
            }else if(actions.get(this.currentAction).active == false){
                //The animation needs to be started
                actions.get(this.currentAction).start();
            }
        }else{
            //The current action does not match the prev action
            //We can either transistion between actions or immediately start the next
            actions.get(this.prevAction).end();
            actions.get(this.currentAction).start();
            this.prevAction = this.currentAction;
        }
    }
    
    public Quaternion getLHip(){return new Quaternion().fromAngles(this.frame.LHip_x, this.frame.LHip_y, this.frame.LHip_z);}
    public Quaternion getRHip(){return new Quaternion().fromAngles(this.frame.RHip_x, this.frame.RHip_y, this.frame.RHip_z);}
    public Quaternion getLShoulder(){return new Quaternion().fromAngles(this.frame.LShoulder_x, this.frame.LShoulder_y, this.frame.LShoulder_z);}
    public Quaternion getRShoulder(){return new Quaternion().fromAngles(this.frame.RShoulder_x, this.frame.RShoulder_y, this.frame.RShoulder_z);}
    public Quaternion getHandle(){return new Quaternion().fromAngles(this.frame.handle_x, this.frame.handle_y, this.frame.handle_z);}
    public Quaternion getWaist(){return new Quaternion().fromAngles(this.frame.waist_x, this.frame.waist_y, this.frame.waist_z);}

    /**
     * @return the currentAction
     */
    public int getCurrentAction() {
        return currentAction;
    }

    /**
     * @param currentAction the currentAction to set
     */
    public void setCurrentAction(int currentAction) {
        this.currentAction = currentAction;
    }
    
    
}

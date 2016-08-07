/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.math.Vector3f;

/**
 *
 * @author Maestro
 */
public class KeyFrame {
    protected float frameNumber;
    protected int id;
    protected int flag;
    
    public float handle_x, handle_y, handle_z;
    public float waist_x, waist_y, waist_z;
    public float LHip_x, LHip_y, LHip_z;
    public float RHip_x, RHip_y, RHip_z;
    public float LShoulder_x, LShoulder_y, LShoulder_z;
    public float RShoulder_x, RShoulder_y, RShoulder_z;
    
    public KeyFrame(int id, int flag){
        this.id = id;
        this.flag = flag;
        
        //Set default joint values
        this.handle_x = 0;
        this.handle_y = 0;
        this.handle_z = 0;
        
        this.waist_x = 0;
        this.waist_y = 0;
        this.waist_z = 0;
        
        this.LHip_x = 0;
        this.LHip_y = 0;
        this.LHip_z = 0;
        
        this.RHip_x = 0;
        this.RHip_y = 0;
        this.RHip_z = 0;
        
        this.LShoulder_x = 0;
        this.LShoulder_y = 0;
        this.LShoulder_z = 0;
        
        this.RShoulder_x = 0;
        this.RShoulder_y = 0;
        this.RShoulder_z = 0;
    }
}

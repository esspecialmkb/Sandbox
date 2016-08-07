/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import java.io.IOException;

/**
 *
 * @author Maestro
 */
public class CharacterController extends AbstractControl {
    //Any local variables should be encapsulated by getters/setters so they
    //appear in the SDK properties window and can be edited.
    //Right-click a local variable to encapsulate it with getters and setters.
    
    /**
     * The inputMask allows controls to change during run-time by assigning an
     * index to action listener
     * 0 - KEY_A
     * 1 - KEY_S
     * 2 - KEY_D
     * 3 - KEY_W
     */
    public boolean inputMask[];
    protected boolean lastMoveCheck = false;
    protected float viewAngle = 90 * FastMath.DEG_TO_RAD;  //In Radians
    protected Vector3f forwardVector;
    protected Vector3f sideVector;
    
    protected CharacterControl phy_control;
    
    protected Node root;
    protected Node handle;
    protected Node LHip;
    protected Node RHip;
    protected Node waist;
    protected Node Neck;
    protected Node LShoulder;
    protected Node RShoulder;
    
    protected float time = 0;
    
    protected AnimationEngine anim;
    
    public void initController(PhysicsSpace phySpace){
        phy_control = new CharacterControl(new CapsuleCollisionShape(1.5f, 5f), .1f);
        phy_control.setPhysicsLocation(new Vector3f(0,4,0));
        
        phy_control.setJumpSpeed(30);
        phy_control.setFallSpeed(40);
        phy_control.setGravity(70);
        phySpace.add(phy_control);
        this.getSpatial().addControl(phy_control);
        root.move(0,-4,0);
        phy_control.setPhysicsLocation(new Vector3f(0,10,0));
        //control.setJoints();
    }
    
    public void setJoints(){
        Node model = (Node)this.getSpatial();
        root = (Node)model.getChild(0);
        handle = (Node)root.getChild(0);
        waist = (Node)handle.getChild(0);
        LHip = (Node)handle.getChild(1);
        RHip = (Node)handle.getChild(2);
        Neck = (Node)waist.getChild(1);
        LShoulder = (Node)waist.getChild(2);
        RShoulder = (Node)waist.getChild(3);
        
        anim = new AnimationEngine();
        inputMask = new boolean[8];
    }
    
    public boolean isMoving() {return (this.inputMask[0] || this.inputMask[1] || this.inputMask[2] || this.inputMask[3]); }
    
    public void updateAnim(float tpf){
        boolean check = isMoving();
        if(check != this.lastMoveCheck){
            if(anim.getCurrentAction() == 0){
                anim.setCurrentAction(1);
            }else if(anim.getCurrentAction() == 1){
                anim.setCurrentAction(0);
            }
            this.lastMoveCheck = check;
        }
        anim.update(tpf);
        //Quaternion q =  new Quaternion();
        this.LHip.setLocalRotation(anim.getLHip());
        this.RHip.setLocalRotation(anim.getRHip());
        this.LShoulder.setLocalRotation(anim.getLShoulder());
        this.RShoulder.setLocalRotation(anim.getRShoulder());
        this.handle.setLocalRotation(anim.getHandle());
        this.waist.setLocalRotation(anim.getWaist());
    }
    
    public void updateMove(float tpf){
        //Movement update
        if(inputMask[4] == true){
            viewAngle = viewAngle - (0.03f);
        }if(inputMask[5] == true){
            viewAngle = viewAngle + (0.03f);
        }
        
        float temp = viewAngle * FastMath.RAD_TO_DEG;
        
        float forward_x = FastMath.cos((viewAngle) - (90 * FastMath.DEG_TO_RAD));
        float forward_z = FastMath.sin((viewAngle) - (90 * FastMath.DEG_TO_RAD));
        Vector3f f = new Vector3f(forward_x,0,forward_z);
        f.normalizeLocal();
        
        float left_x = FastMath.cos(viewAngle);
        float left_z = FastMath.sin(viewAngle);
        Vector3f l = new Vector3f(left_x,0,left_z);
        l.normalizeLocal();
        
        this.getSpatial().setLocalRotation(new Quaternion().fromAngles(0, -viewAngle, 0));
        Vector3f dir = new Vector3f(Vector3f.ZERO);
        
        if(inputMask[1] == true){
            dir.addLocal(l);
        }if(inputMask[0] == true){
            dir.addLocal(f.negate());
        }if(inputMask[3] == true){
            dir.addLocal(l.negate());
        }if(inputMask[2] == true){
            dir.addLocal(f);
        }
        
        dir.normalizeLocal();
        //System.out.println("View Angle :" + viewAngle + ", " + temp + ", " + f);
        //this.getSpatial().move(dir.mult(tpf*20));
        this.phy_control.setWalkDirection(dir.mult(0.333f));
        this.phy_control.setViewDirection(l);
    }
    
    @Override
    protected void controlUpdate(float tpf) {
        //TODO: add code that controls AnimationEngine
        //e.g. spatial.rotate(tpf,tpf,tpf);
        //System.out.println("Control Update " + tpf);
        this.updateAnim(tpf);
        this.updateMove(tpf);
    }
    
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        //Only needed for rendering-related operations,
        //not called when spatial is culled.
        /** Animation updates could be moved here */
    }
    
    public Control cloneForSpatial(Spatial spatial) {
        CharacterController control = new CharacterController();
        //TODO: copy parameters to new Control
        return control;
    }
    
    @Override
    public void read(JmeImporter im) throws IOException {
        super.read(im);
        InputCapsule in = im.getCapsule(this);
        //TODO: load properties of this Control, e.g.
        //this.value = in.readFloat("name", defaultValue);
    }
    
    @Override
    public void write(JmeExporter ex) throws IOException {
        super.write(ex);
        OutputCapsule out = ex.getCapsule(this);
        //TODO: save properties of this Control, e.g.
        //out.write(this.value, "name", defaultValue);
    }
}

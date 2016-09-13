/*
 * KartControl.java v0.2
 */
package Vehicle;

import com.jme3.asset.AssetManager;
import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.control.VehicleControl;
import com.jme3.bullet.objects.VehicleWheel;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import java.io.IOException;

/**
 *
 * @author Maestro
 */
public class KartControl extends AbstractControl {
    //Any local variables should be encapsulated by getters/setters so they
    //appear in the SDK properties window and can be edited.
    //Right-click a local variable to encapsulate it with getters and setters.
    //private BulletAppState bulletAppState;
    //private VehicleControl player;
    //private VehicleWheel fr, fl, br, bl;
    private Node node_fr, node_fl, node_br, node_bl;
    private float wheelRadius;
    private float steeringValue = 0;
    private float accelerationValue = 0;
    //private Node carNode;
    private float forwardAngle = 90;
    public Vector3f forward = Vector3f.UNIT_Z.negate();
    
    private float mass = 100;
    private float force = 60;
    private float speed = 0;
    private Vector3f velocity = Vector3f.ZERO;
    public Node kart;
    private Node body;
    private Ray spring;
    
    public void createPhysicsKartControl(Node rootNode, AssetManager assetManager, PhysicsSpace space){
        kart = (Node)assetManager.loadModel("Models/kart2.j3o");
        kart.setShadowMode(RenderQueue.ShadowMode.Cast);
        kart.addControl(new RigidBodyControl(0));
        kart.addControl(this);
        
        body = (Node)kart.getChild("Body");
        node_fr = (Node)kart.getChild("WheelFR");
        node_fl = (Node)kart.getChild("WheelFL");
        node_br = (Node)kart.getChild("WheelRR");
        node_bl = (Node)kart.getChild("WheelRL");
        rootNode.attachChild(kart);
        space.add(kart);
    }
    
    public void accelerate(float accel){
        this.accelerationValue = accel;
    }
    
    public void steering(float steer){
        this.steeringValue = steer;
    }
    
    public void input(float accel, float steer){
        this.accelerationValue = accel;
        this.steeringValue = steer;
    }
    
    public void calcMovement(float tpf){
        float accel = (this.force * this.accelerationValue) / this.mass;
        float friction = (-1 * this.speed * 0.8f) * tpf;
        float steerAngle = this.forwardAngle + ((this.steeringValue * FastMath.RAD_TO_DEG)*tpf);
        
        //this.speed = this.speed * 0.9f;
        this.speed = this.speed + ((accel) * tpf);
        this.forwardAngle = steerAngle ;
        
        float vel_x = FastMath.cos((forwardAngle-90));// * FastMath.DEG_TO_RAD);// * this.speed;
        float vel_z = FastMath.sin((forwardAngle-90));// * FastMath.DEG_TO_RAD);// * this.speed;
        
        System.out.println("ForwardAngle: " + forwardAngle);
        System.out.println("Vel: " + vel_x + ", " + vel_z);
        System.out.println("Speed: " + this.speed);
        
        this.forward.set(vel_x,0,vel_z);
        //this.forward.normalizeLocal();
    }
    
    public void updatePhysics(float tpf){
        
        this.calcMovement(tpf);
        //Calculate the positions of center, front, and rear nodes
        Vector3f pos = kart.getWorldTranslation();
        //Vector3f front = pos.add(this.forward.mult(5));
        //Vector3f rear = pos.subtract(this.forward.mult(4));
        
        //this.kart.move(this.forward.mult(this.speed));
        
        Quaternion rot = new Quaternion();
        rot.lookAt(forward, Vector3f.UNIT_Y);
        //this.kart.setLocalRotation(rot);
    }

    @Override
    protected void controlUpdate(float tpf) {
        //TODO: add code that controls Spatial,
        //e.g. spatial.rotate(tpf,tpf,tpf);
        //kart.getControl(0);
        this.updatePhysics(tpf);
        this.kart.move(forward.mult(this.speed / tpf));
        Quaternion rot =new Quaternion().fromAngleAxis(steeringValue * FastMath.DEG_TO_RAD, Vector3f.UNIT_Y);
        this.kart.rotate(0,this.steeringValue * tpf,0);
    }
    
    public void reset(){
        this.speed = 0;
        this.forwardAngle = 90;
        this.forward = Vector3f.UNIT_Z.negate();
        this.kart.setLocalTranslation(0, 0, 0);
        this.kart.setLocalRotation(new Quaternion().fromAngleAxis(forwardAngle * FastMath.DEG_TO_RAD, Vector3f.UNIT_Y));
    }
    
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        //Only needed for rendering-related operations,
        //not called when spatial is culled.
    }
    
    public Control cloneForSpatial(Spatial spatial) {
        KartControl control = new KartControl();
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

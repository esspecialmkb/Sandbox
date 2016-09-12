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
    private Vector3f forward = Vector3f.UNIT_Z.negate();
    
    private float mass = 100;
    private float force = 20;
    private float speed = 0;
    private Vector3f velocity = Vector3f.ZERO;
    private Node kart;
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
    
    public void updatePhysics(float tpf){
        //Calculate accel forces
        float f_accel = this.force * this.accelerationValue;
        //Calculate friction
        float f_friction = this.speed * 0.8f;
        
        //Calculate the positions of center, front, and rear nodes
        Vector3f pos = kart.getLocalTranslation();
        Vector3f front = pos.add(this.forward.mult(5));
        Vector3f rear = pos.subtract(this.forward.mult(4));
        
        //Move the nodes:
        //The rear node always moves forward * speed
        rear.addLocal(this.forward.mult(speed));
        //The front node always moves (forwardAngle + steeringValue) * speed
        Vector3f temp = new Vector3f(FastMath.cos(steeringValue + (forwardAngle * FastMath.DEG_TO_RAD)),0,FastMath.sin(steeringValue + (forwardAngle * FastMath.DEG_TO_RAD)));
        front.addLocal(temp.mult(speed));
        //After the front and rear nodes have been moved, calculate new center
        temp = front.subtract(rear);
        pos = front.subtract(temp.mult(0.5f));
        kart.setLocalTranslation(pos);
        //Cheap implemtation to update view
        kart.lookAt(front, Vector3f.UNIT_Y);
        float angles[] = new float[3];
        kart.getLocalRotation().toAngles(angles);
        forwardAngle = angles[1] * FastMath.RAD_TO_DEG;
        forward.set(FastMath.cos(forwardAngle * FastMath.DEG_TO_RAD),0,FastMath.sin(forwardAngle * FastMath.DEG_TO_RAD));
    }

    @Override
    protected void controlUpdate(float tpf) {
        //TODO: add code that controls Spatial,
        //e.g. spatial.rotate(tpf,tpf,tpf);
        //kart.getControl(0);
        this.updatePhysics(tpf);
    }
    
    public void reset(){
        this.speed = 0;
        this.forwardAngle = 90;
        this.forward = Vector3f.UNIT_Z.negate();
        this.kart.setLocalTranslation(0, 0, 0);
        this.kart.setLocalRotation(new Quaternion().fromAngleAxis(force, Vector3f.UNIT_Y));
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

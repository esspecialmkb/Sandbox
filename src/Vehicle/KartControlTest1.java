/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Matrix3f;
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
import com.jme3.scene.shape.Box;
import java.io.IOException;

/**
 *
 * @author Maestro
 */
public class KartControlTest1 extends AbstractControl {
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
    private Vector3f forward = new Vector3f(FastMath.cos(forwardAngle * FastMath.DEG_TO_RAD),0,FastMath.sin(forwardAngle * FastMath.DEG_TO_RAD));
    
    private float mass = 100;
    private float force = 10;
    private float speed = 0;
    private Vector3f velocity = Vector3f.ZERO;
    private Node kart;
    private Geometry body;
    private Ray spring;
    
    public void createPhysicsKartControl(Node rootNode, AssetManager assetManager, PhysicsSpace space){
        //kart = (Node)assetManager.loadModel("Models/kart2.j3o");
        Material mat = new Material();/* A colored lit cube. Needs light source! */ 
        Box boxMesh = new Box(3f,1f,5f); 
        Geometry boxGeo = new Geometry("Colored Box", boxMesh);
        
        Box tireBox = new Box(0.25f,0.75f,0.75f);
        Geometry fr = new Geometry("FR", tireBox);
        Geometry fl = new Geometry("FL", tireBox);
        Geometry br = new Geometry("BR", tireBox);
        Geometry bl = new Geometry("BL", tireBox);
        
        Material boxMat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md"); 
        boxMat.setBoolean("UseMaterialColors", true); 
        boxMat.setColor("Ambient", ColorRGBA.Blue); 
        boxMat.setColor("Diffuse", ColorRGBA.Blue); 
        boxGeo.setMaterial(boxMat); 
        Material tireMat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md"); 
        tireMat.setBoolean("UseMaterialColors", true); 
        tireMat.setColor("Ambient", ColorRGBA.Red); 
        tireMat.setColor("Diffuse", ColorRGBA.Red);
         //rootNode.attachChild(boxGeo);
        fr.setMaterial(tireMat);
        fl.setMaterial(tireMat);
        br.setMaterial(tireMat);
        bl.setMaterial(tireMat);
        boxMat.getAdditionalRenderState().setWireframe(true);
        tireMat.getAdditionalRenderState().setWireframe(true);

        kart = new Node("Kart");
        kart.attachChild(boxGeo);
        //kart.setShadowMode(RenderQueue.ShadowMode.Cast);
        kart.addControl(new RigidBodyControl(0));
        kart.addControl(this);
        
        //body = (Node)kart.getChild("Body");
        node_fr = new Node("WheelFR");
        node_fl = new Node("WheelFL");
        node_br = new Node("WheelRR");
        node_bl = new Node("WheelRL");
        
        node_fr.attachChild(fr);
        node_fl.attachChild(fl);
        node_br.attachChild(br);
        node_bl.attachChild(bl);
        
        kart.attachChild(node_fr);
        kart.attachChild(node_fl);
        kart.attachChild(node_br);
        kart.attachChild(node_bl);
        
        node_fr.move(3,0,-6);
        node_fl.move(-3,0,-6);
        node_br.move(3,0,6);
        node_bl.move(-3,0,6);
        Quaternion temp = new Quaternion();
        kart.setLocalRotation(temp.fromAngleAxis(forwardAngle * FastMath.DEG_TO_RAD, Vector3f.UNIT_Y));
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
    
    public void updateAcceleration(float tpf){
        float accel = (this.force * this.accelerationValue) / this.mass;
        this.speed = (accel * tpf) + speed;
        
        float x = FastMath.cos(this.forwardAngle * FastMath.DEG_TO_RAD);
        float z = FastMath.sin(this.forwardAngle * FastMath.DEG_TO_RAD);
        this.forward.set(x,0,z);
        this.forward.normalizeLocal();
        //this.forward.mult(tpf);
    }
    private float brakeValue = 0f;
    public void brake(float brake){
        this.brakeValue = brake;
    }
    @Override
    protected void controlUpdate(float tpf) {
        //TODO: add code that controls Spatial,
        //e.g. spatial.rotate(tpf,tpf,tpf);
        //kart.getControl(0);
        updateAcceleration(tpf);
        spatial.move(this.forward.mult(this.speed));
        this.forwardAngle = this.forwardAngle - (this.steeringValue * FastMath.RAD_TO_DEG * tpf);
        
        spatial.rotate(0,steeringValue * tpf,0);
        
        speed = speed -(speed * 0.8f * tpf);
        speed = speed - (this.brakeValue * -tpf);
        if(speed > -0.012f){
            if(this.accelerationValue == 0){
               speed = 0f; 
            }
        }
        if(speed > 0){
            speed = 0;
        }
    }
    
    public Vector3f getForwardVector(){ return this.forward;}
    public Vector3f getPosition(){ return this.kart.getWorldTranslation();}
    
    public void reset(){
        this.speed = 0;
        this.forwardAngle = 90;
        this.forward = new Vector3f(FastMath.cos(forwardAngle * FastMath.DEG_TO_RAD),0,FastMath.sin(forwardAngle * FastMath.DEG_TO_RAD));
        this.spatial.setLocalTranslation(0, 0, 0);
        Quaternion temp = new Quaternion();
        kart.setLocalRotation(temp.fromAngleAxis(forwardAngle * FastMath.DEG_TO_RAD, Vector3f.UNIT_Y));
    }
    
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        //Only needed for rendering-related operations,
        //not called when spatial is culled.
    }
    
    public Control cloneForSpatial(Spatial spatial) {
        KartControlTest1 control = new KartControlTest1();
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

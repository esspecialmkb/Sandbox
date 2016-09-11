/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Vehicle;

import com.jme3.asset.AssetManager;
import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.control.VehicleControl;
import com.jme3.bullet.objects.VehicleWheel;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
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
    private BulletAppState bulletAppState;
    private VehicleControl player;
    private VehicleWheel fr, fl, br, bl;
    private Node node_fr, node_fl, node_br, node_bl;
    private float wheelRadius;
    private float steeringValue = 0;
    private float accelerationValue = 0;
    private Node carNode;
    
    public void setupKart(Node rootNode, AssetManager assetManager, PhysicsSpace space){
        float stiffness = 190.0f;//200=f1 car
        float compValue = 0.2f; //(lower than damp!)
        float dampValue = 0.5f;
        final float mass = 250;

        //Load model and get chassis Geometry
        Spatial kart = assetManager.loadModel("Models/kart2.j3o");
        carNode = (Node)assetManager.loadModel("Models/kart2.j3o");
        carNode.setShadowMode(RenderQueue.ShadowMode.Cast);
        
        Node body = (Node)carNode.getChild("Body");
        Geometry chasis = (Geometry)body.getChild(0);
        BoundingBox box = (BoundingBox) chasis.getModelBound();


        //Create four wheels and add them at their locations
        //note that our fancy car actually goes backwards..
        Vector3f wheelDirection = new Vector3f(0, -1, 0);
        Vector3f wheelAxle = new Vector3f(-1, 0, 0);

        Node wheelfr = (Node)carNode.getChild("WheelFR");
        Node frNode = (Node)wheelfr.getChild(0);
        
        Node wheelfl = (Node)carNode.getChild("WheelFL");
        Node flNode = (Node)wheelfl.getChild(0);
        
        Node wheelrl = (Node)carNode.getChild("WheelRL");
        Node rlNode = (Node)wheelrl.getChild(0);
        
        Node wheelrr = (Node)carNode.getChild("WheelRR");
        Node rrNode = (Node)wheelrr.getChild(0);

        rootNode.attachChild(carNode);
        space.add(player);
    }

    @Override
    protected void controlUpdate(float tpf) {
        //TODO: add code that controls Spatial,
        //e.g. spatial.rotate(tpf,tpf,tpf);
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

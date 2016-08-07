/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.Arrow;

/**
 *
 * @author Maestro
 */
public class Character implements ActionListener,AnalogListener{
    protected Node model;
    protected CharacterController control;
    
    
    public boolean inputMask[];
    
    public Geometry putShape(AssetManager assetManager, Mesh shape, ColorRGBA color){
        Geometry g = new Geometry("shape", shape);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.getAdditionalRenderState().setWireframe(true);
        mat.setColor("Color", color);
        g.setMaterial(mat);
        control.handle.attachChild(g);
        return g;
    }

    public void putDebugArrow(AssetManager assetManager, Vector3f pos, Vector3f dir, ColorRGBA color){
        Arrow arrow = new Arrow(dir);
        arrow.setLineWidth(4); // make arrow thicker
        putShape(assetManager, arrow, color).setLocalTranslation(pos);
    }
    
    public void initController(PhysicsSpace phySpace){
        control = new CharacterController();
        model.addControl(control);
        control.setJoints();
        control.initController(phySpace);
    }
    
    public void loadCharacterModel(AssetManager assetManager){
        /** Load a model. Uses model and texture from jme3-test-data library! */ 
        model = (Node)assetManager.loadModel("Models/character.j3o");
        Material defaultMat = new Material( assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        defaultMat.setColor("Color", ColorRGBA.Blue);   // set color of material to blue
        model.setMaterial(defaultMat);
        //rootNode.attachChild(teapot); 
    }
    
    public void setRootNode(Node root){
        root.attachChild(model);
    }
    
    public void initControls(InputManager inputManager, boolean debug){
        /** You can map one or several inputs to one named mapping. */
        if(debug == false){
            
            inputManager.addMapping("Key_A",  new KeyTrigger(KeyInput.KEY_A));
            inputManager.addMapping("Key_S",   new KeyTrigger(KeyInput.KEY_S));
            inputManager.addMapping("Key_D",  new KeyTrigger(KeyInput.KEY_D));
            inputManager.addMapping("Key_W",  new KeyTrigger(KeyInput.KEY_W));
            inputManager.addMapping("Space", new KeyTrigger(KeyInput.KEY_SPACE));
            inputManager.addMapping("leftMouseButton", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
            inputManager.addMapping("rightMouseButton", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));

            inputManager.addListener(this, "Key_A");
            inputManager.addListener(this, "Key_S");
            inputManager.addListener(this, "Key_D");
            inputManager.addListener(this, "Key_W");
            inputManager.addListener(this, "Space");
            inputManager.addListener(this, "leftMouseButton");
            inputManager.addListener(this, "rightMouseButton");
        }else if(debug == true){
            inputManager.addMapping("Key_A",  new KeyTrigger(KeyInput.KEY_J));
            inputManager.addMapping("Key_S",   new KeyTrigger(KeyInput.KEY_K));
            inputManager.addMapping("Key_D",  new KeyTrigger(KeyInput.KEY_L));
            inputManager.addMapping("Key_W",  new KeyTrigger(KeyInput.KEY_I));
            inputManager.addMapping("Space", new KeyTrigger(KeyInput.KEY_SPACE));
            inputManager.addMapping("lookLeft", new KeyTrigger(KeyInput.KEY_U));
            inputManager.addMapping("lookRight", new KeyTrigger(KeyInput.KEY_O));
            inputManager.addMapping("mouseLookLeft", new MouseAxisTrigger(MouseInput.AXIS_X, false));
            inputManager.addMapping("mouseLookRight", new MouseAxisTrigger(MouseInput.AXIS_X, true));
            inputManager.addMapping("leftMouseButton", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
            inputManager.addMapping("rightMouseButton", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));

            inputManager.addListener(this, "Key_A");
            inputManager.addListener(this, "Key_S");
            inputManager.addListener(this, "Key_D");
            inputManager.addListener(this, "Key_W");
            inputManager.addListener(this, "Space");
            inputManager.addListener(this, "lookLeft");
            inputManager.addListener(this, "lookRight");
            inputManager.addListener(analogListener, "mouseLookLeft");
            inputManager.addListener(analogListener, "mouseLookRight");
            inputManager.addListener(this, "leftMouseButton");
            inputManager.addListener(this, "rightMouseButton");
            //inputManager.
        }
        
    }
    
    /**
     * The character class implements ActionListener that will be used for player input and
     * possibly used for AI command messages
     * @param name
     * @param isPressed
     * @param tpf 
     */
    public void onAction(String name, boolean isPressed, float tpf) {
        if (name.equals("Key_A"))
        { 
            this.control.inputMask[0] = isPressed;
            //Move left
        }else if (name.equals("Key_S"))
        { 
            this.control.inputMask[1] = isPressed;
            //Move backward
        }else if (name.equals("Key_D"))
        { 
            this.control.inputMask[2] = isPressed;
            //Move right
        }else if (name.equals("Key_W"))
        { 
            this.control.inputMask[3] = isPressed;
            //Move forward
        }else if (name.equals("Space") && isPressed)
        { 
            //isRunning = !isRunning;
            control.phy_control.jump();
        }else if (name.equals("lookLeft"))
        { 
           this.control.inputMask[4] = isPressed;
        }else if (name.equals("lookRight"))
        { 
            this.control.inputMask[5] = isPressed;
        }else if (name.equals("leftMouseButton") && !isPressed)
        { 
            //isRunning = !isRunning;
        }else if (name.equals("rightMouseButton") && !isPressed)
        { 
            //isRunning = !isRunning;
        }
    }
    public AnalogListener analogListener = new AnalogListener() {
        public void onAnalog(String name, float value, float tpf) {
            System.out.println(name + " = " + value);
            if(name.equals("mouseLookLeft")){
                control.viewAngle += value;
            }if(name.equals("mouseLookRight")){
                control.viewAngle -= value;
            }
        }
    };
    
    public void onAnalog(String name, float value, float tpf) {
        if (name.equals("lookLeft"))
        { 
           System.out.println(value);
           this.control.viewAngle -= 0.3f;
        }else if (name.equals("lookRight"))
        { 
            this.control.viewAngle += 0.3f;
        }
    }
}

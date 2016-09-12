/*
 * Copyright (c) 2009-2012 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package Vehicle;

import com.jme3.app.SimpleApplication;
import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.VehicleControl;
import com.jme3.bullet.objects.VehicleWheel;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.math.FastMath;
import com.jme3.math.Matrix3f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.shadow.BasicShadowRenderer;

public class KartControlDev extends SimpleApplication implements ActionListener {

    private BulletAppState bulletAppState;
    private KartControl player;
    private VehicleWheel fr, fl, br, bl;
    private Node node_fr, node_fl, node_br, node_bl;
    private float wheelRadius;
    private float steeringValue = 0;
    private float accelerationValue = 0;
    private Node carNode;

    public static void main(String[] args) {
        KartControlDev app = new KartControlDev();
        app.start();
    }

    private void setupKeys() {
        inputManager.addMapping("Lefts", new KeyTrigger(KeyInput.KEY_H));
        inputManager.addMapping("Rights", new KeyTrigger(KeyInput.KEY_K));
        inputManager.addMapping("Ups", new KeyTrigger(KeyInput.KEY_U));
        inputManager.addMapping("Downs", new KeyTrigger(KeyInput.KEY_J));
        inputManager.addMapping("Space", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping("Reset", new KeyTrigger(KeyInput.KEY_RETURN));
        inputManager.addListener(this, "Lefts");
        inputManager.addListener(this, "Rights");
        inputManager.addListener(this, "Ups");
        inputManager.addListener(this, "Downs");
        inputManager.addListener(this, "Space");
        inputManager.addListener(this, "Reset");
    }

    @Override
    public void simpleInitApp() {
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
//        bulletAppState.getPhysicsSpace().enableDebug(assetManager);
        if (settings.getRenderer().startsWith("LWJGL")) {
            BasicShadowRenderer bsr = new BasicShadowRenderer(assetManager, 512);
            bsr.setDirection(new Vector3f(-0.5f, -0.3f, -0.3f).normalizeLocal());
            viewPort.addProcessor(bsr);
        }
        //cam.setFrustumFar(150f);
        flyCam.setMoveSpeed(10);

        setupKeys();
        //PhysicsTestHelper.createPhysicsTestWorld(rootNode, assetManager, bulletAppState.getPhysicsSpace());
        SceneBuider.createPhysicsKartTrack(rootNode, assetManager, bulletAppState.getPhysicsSpace());
        //SceneBuider.createPhysicsKartControl(rootNode, assetManager, bulletAppState.getPhysicsSpace());
        player = new KartControl();
        player.createPhysicsKartControl(rootNode, assetManager, bulletAppState.getPhysicsSpace());
        //setupFloor();
        //buildPlayer();

        DirectionalLight dl = new DirectionalLight();
        dl.setDirection(new Vector3f(-0.5f, -1f, -0.3f).normalizeLocal());
        rootNode.addLight(dl);

        dl = new DirectionalLight();
        dl.setDirection(new Vector3f(0.5f, -0.1f, 0.3f).normalizeLocal());
        rootNode.addLight(dl);
    }

    private PhysicsSpace getPhysicsSpace() {
        return bulletAppState.getPhysicsSpace();
    }

//    public void setupFloor() {
//        Material mat = assetManager.loadMaterial("Textures/Terrain/BrickWall/BrickWall.j3m");
//        mat.getTextureParam("DiffuseMap").getTextureValue().setWrap(WrapMode.Repeat);
////        mat.getTextureParam("NormalMap").getTextureValue().setWrap(WrapMode.Repeat);
////        mat.getTextureParam("ParallaxMap").getTextureValue().setWrap(WrapMode.Repeat);
//
//        Box floor = new Box(Vector3f.ZERO, 140, 1f, 140);
//        floor.scaleTextureCoordinates(new Vector2f(112.0f, 112.0f));
//        Geometry floorGeom = new Geometry("Floor", floor);
//        floorGeom.setShadowMode(ShadowMode.Receive);
//        floorGeom.setMaterial(mat);
//
//        PhysicsNode tb = new PhysicsNode(floorGeom, new MeshCollisionShape(floorGeom.getMesh()), 0);
//        tb.setLocalTranslation(new Vector3f(0f, -6, 0f));
////        tb.attachDebugShape(assetManager);
//        rootNode.attachChild(tb);
//        getPhysicsSpace().add(tb);
//    }

    private Geometry findGeom(Spatial spatial, String name) {
        if (spatial instanceof Node) {
            Node node = (Node) spatial;
            for (int i = 0; i < node.getQuantity(); i++) {
                Spatial child = node.getChild(i);
                Geometry result = findGeom(child, name);
                if (result != null) {
                    return result;
                }
            }
        } else if (spatial instanceof Geometry) {
            if (spatial.getName().startsWith(name)) {
                return (Geometry) spatial;
            }
        }
        return null;
    }

    private void buildPlayer() {
        float stiffness = 190.0f;//200=f1 car
        float compValue = 0.2f; //(lower than damp!)
        float dampValue = 0.5f;
        final float mass = 250;

        //Load model and get chassis Geometry
        Spatial kart = assetManager.loadModel("Models/kart2.j3o");
        carNode = (Node)assetManager.loadModel("Models/kart2.j3o");
        carNode.setShadowMode(ShadowMode.Cast);
        
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
        getPhysicsSpace().add(player);
    }

    public void onAction(String binding, boolean value, float tpf) {
        if (binding.equals("Lefts")) {
            if (value) {
                steeringValue += .125f;
            } else {
                steeringValue += -.125f;
            }
            player.steering(steeringValue);
            //player.steer(steeringValue);
        } else if (binding.equals("Rights")) {
            if (value) {
                steeringValue += -.125f;
            } else {
                steeringValue += .125f;
            }
            player.steering(steeringValue);
            //player.steer(steeringValue);
        } //note that our fancy car actually goes backwards..
        else if (binding.equals("Ups")) {
            if (value) {
                accelerationValue -= 800;
            } else {
                accelerationValue += 800;
            }
            player.accelerate(accelerationValue);
            //player.accelerate(accelerationValue);
            //player.setCollisionShape(CollisionShapeFactory.createDynamicMeshShape((Node)carNode.getChild("Body")));
        } else if (binding.equals("Downs")) {
            if (value) {
                //player.brake(40f);
            } else {
                //player.brake(0f);
            }
        } else if (binding.equals("Reset")) {
            if (value) {
                System.out.println("Reset");
                //player.setPhysicsLocation(Vector3f.ZERO);
                //player.setPhysicsRotation(new Matrix3f());
                //player.setLinearVelocity(Vector3f.ZERO);
                //player.setAngularVelocity(Vector3f.ZERO);
                //player.resetSuspension();
                player.reset();
            } else {
            }
        }
    }

    @Override
    public void simpleUpdate(float tpf) {
        //cam.setLocation(carNode.getWorldTranslation().add(player.getForwardVector(null).mult(20).add(0,8,0)));
        //cam.lookAt(carNode.getWorldTranslation().subtract(player.getForwardVector(null).mult(20).add(0,-5,0)), Vector3f.UNIT_Y);
    }
}

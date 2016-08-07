/**
 *
 * @author Maestro
 * This prototype is derived from the TestTutorial provided with the cubes library
 */

package com.cubes.test;

import java.util.logging.Level;
import java.util.logging.Logger;
import com.jme3.app.SimpleApplication;
import com.jme3.math.Vector3f;
import com.jme3.system.AppSettings;
import com.cubes.*;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.MeshCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.scene.Node;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;

public class CubesPrototype extends SimpleApplication
    implements ActionListener{

    public static void main(String[] args){
        Logger.getLogger("").setLevel(Level.SEVERE);
        CubesPrototype app = new CubesPrototype();
        app.start();
    }
    
    //The BulletAppState gives the app access to bullet physics engine
    private BulletAppState bulletAppState;
    //The mineSteve Spatial loads the character model
    private Spatial mineSteve;
    //The rigidBodyControl is used to make the block-world solid
    private RigidBodyControl landscape;
    //BlockTerrainControl is helper class for Cubes engine
    private BlockTerrainControl blockTerrain;
    //Used for first person representation
    private CharacterControl player;
    //Walk direction and bool values are used for physics-controlled navigation
    private Vector3f walkDirection = new Vector3f();
    private boolean left=false, right=false, up=false, down=false;
    
    private Vector3f camDir = new Vector3f();
    private Vector3f camLeft = new Vector3f();

    public CubesPrototype(){
        settings = new AppSettings(true);
        settings.setWidth(1280);
        settings.setHeight(720);
        settings.setTitle("Cubes Demo - Tutorial");
    }

    @Override
    public void simpleInitApp(){
        //The scene is initialized here
        CubesTestAssets.registerBlocks();
        
        //Create the BulletAppState object
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        
        //This is your terrain, it contains the whole
        //block world and offers methods to modify it
        //The vector defines the number of chunks in the world
        CubesSettings blockSettings = CubesTestAssets.getSettings(this);
        blockSettings.setBlockSize((float) 2.5);
        blockTerrain = new BlockTerrainControl(blockSettings, new Vector3Int(4, 1, 4));
        
        /*To set a block, just specify the location and the block object
        //(Existing blocks will be replaced)
        blockTerrain.setBlock(new Vector3Int(0, 0, 0), CubesTestAssets.BLOCK_WOOD);
        blockTerrain.setBlock(0, 0, 0, CubesTestAssets.BLOCK_GRASS); //For the lazy users :P */

        //You can place whole areas of blocks too: setBlockArea(location, size, block)
        //(The specified block will be cloned each time)
        //The following line will set 3 blocks on top of each other
        //({1,1,1}, {1,2,3} and {1,3,1})
        blockTerrain.setBlockArea(new Vector3Int(2, 0, 2), new Vector3Int(14, 1, 14), CubesTestAssets.BLOCK_STONE);
        blockTerrain.setBlockArea(new Vector3Int(1, 0, 1), new Vector3Int(16, 3, 1), CubesTestAssets.BLOCK_WOOD );
        blockTerrain.setBlockArea(new Vector3Int(1, 0, 15), new Vector3Int(16, 3, 1), CubesTestAssets.BLOCK_WOOD );
        blockTerrain.setBlockArea(new Vector3Int(1, 0, 1), new Vector3Int(1, 3, 15), CubesTestAssets.BLOCK_WOOD );
        blockTerrain.setBlockArea(new Vector3Int(16, 0, 1), new Vector3Int(1, 3, 15), CubesTestAssets.BLOCK_WOOD );

        //Removing a block works in a similar way
        //blockTerrain.removeBlock(new Vector3Int(1, 2, 1));
        //blockTerrain.removeBlock(new Vector3Int(1, 3, 1));
        
        //Here,we want to add blocks to the physics engine
        blockTerrain.addChunkListener(new BlockChunkListener(){

            @Override
            public void onSpatialUpdated(BlockChunkControl blockChunk){
                Geometry optimizedGeometry = blockChunk.getOptimizedGeometry_Opaque();
                landscape = optimizedGeometry.getControl(RigidBodyControl.class);
                if(landscape == null){
                    landscape = new RigidBodyControl(0);
                    optimizedGeometry.addControl(landscape);
                    bulletAppState.getPhysicsSpace().add(landscape);
                }
                landscape.setCollisionShape(new MeshCollisionShape(optimizedGeometry.getMesh()));
            }
        });

        //The terrain is a jME-Control, you can add it
        //to a node of the scenegraph to display it
        Node terrainNode = new Node();
        terrainNode.addControl(blockTerrain);
        rootNode.attachChild(terrainNode);
        
        //First-person collisions. Create capsule shape and apply to player for collisions
        CapsuleCollisionShape capsuleShape = new CapsuleCollisionShape(1.5f,6f,1);
        player = new CharacterControl(capsuleShape, 0.05f);//Step size is set in last argument
        player.setJumpSpeed(20);
        player.setFallSpeed(30);
        player.setGravity(30);
        
        //Place player in starting position
        player.setPhysicsLocation(new Vector3f(7,10,7));
        
        //bulletAppState.getPhysicsSpace().add(landscape);
        bulletAppState.getPhysicsSpace().add(player);
        
        
        //Load the minecraft character model from blender
        mineSteve = assetManager.loadModel("Models/MinecraftSteve/MinecraftSteve.j3o");
        rootNode.attachChild(mineSteve);
        mineSteve.setLocalScale(2f);
        mineSteve.move(20f, 2.5f, 20f);
        
        
        //cam.setLocation(new Vector3f(5, 10, 5));
        //cam.lookAt(mineSteve.getWorldTranslation(), Vector3f.UNIT_Y);
        cam.lookAtDirection(new Vector3f(1, 0, 1), Vector3f.UNIT_Y);
        flyCam.setMoveSpeed(15);
        setUpKeys();
    }
    
    //Add input mappings to inputManager
    private void setUpKeys() {
        inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("Up", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("Down", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("Jump", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addListener(this, "Left");
        inputManager.addListener(this, "Right");
        inputManager.addListener(this, "Up");
        inputManager.addListener(this, "Down");
        inputManager.addListener(this, "Jump");
    }
    
    public void onAction(String binding, boolean value, float tpf) {
        if (binding.equals("Left")) {
            if (value) { left = true; } else { left = false; }
        } else if (binding.equals("Right")) {
            if (value) { right = true; } else { right = false; }
        } else if (binding.equals("Up")) {
            if (value) { up = true; } else { up = false; }
        } else if (binding.equals("Down")) {
            if (value) { down = true; } else { down = false; }
        } else if (binding.equals("Jump")) {
            player.jump();
        }
    }
    
    @Override
    public void simpleUpdate(float tpf) {
        
        camDir.set(cam.getDirection()).multLocal(0.3f);
        camLeft.set(cam.getLeft()).multLocal(0.1f);
        
        //initialize the walkDirection value so it can be recalculated
        walkDirection.set(0,0,0);
        if (left) {
            walkDirection.addLocal(camLeft);
        }
        if (right) {
            walkDirection.addLocal(camLeft.negate());
        }
        if (up) {
            walkDirection.addLocal(camDir);
        }
        if (down) {
            walkDirection.addLocal(camDir.negate());
        }
        player.setWalkDirection(walkDirection);
        cam.setLocation(player.getPhysicsLocation());
    }
}


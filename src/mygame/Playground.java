/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.cubes.BlockChunkControl;
import com.cubes.BlockChunkListener;
import com.cubes.BlockNavigator;
import com.cubes.BlockTerrainControl;
import com.cubes.Vector3Int;
import com.cubes.test.CubesTestAssets;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.MeshCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapText;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;

/**
 *
 * @author Maestro
 */
public class Playground extends SimpleApplication implements ActionListener {
    private Node terrainNode;
    private BlockTerrainControl blockTerrain;
    
    private Character player;
    private BulletAppState bulletAppState;
    public Playground(){
        settings = new AppSettings(true);
        settings.setWidth(1280);
        settings.setHeight(720);
        settings.setTitle("Sandbox Demo - Playground");
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        Playground app = new Playground();
        app.start();
    }

    @Override
    public void simpleInitApp(){
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        //bulletAppState.getPhysicsSpace().enableDebug(assetManager);
        CubesTestAssets.registerBlocks();
        initControls();
        initBlockTerrainPhy();
        initGUI();
        cam.setLocation(new Vector3f(-16.6f, 46, 97.6f));
        cam.lookAtDirection(new Vector3f(0.68f, -0.47f, -0.56f), Vector3f.UNIT_Y);
        flyCam.setMoveSpeed(250);
        
        player = new Character();
        player.loadCharacterModel(this.assetManager);
        player.setRootNode(rootNode);
        player.model.setLocalTranslation(2.1f, 4.2f, 2.1f);
        player.initController(bulletAppState.getPhysicsSpace());
        player.initControls(inputManager,true);
        player.putDebugArrow(assetManager, Vector3f.ZERO, new Vector3f(0,0,-2), ColorRGBA.Red);
    }
    
    private void initControls(){
        inputManager.addMapping("set_block", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addListener(this, "set_block");
        inputManager.addMapping("remove_block", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
        inputManager.addListener(this, "remove_block");
    }
    
    private void initBlockTerrainRand(){
        blockTerrain = new BlockTerrainControl(CubesTestAssets.getSettings(this), new Vector3Int(2, 1, 2));
        blockTerrain.setBlockArea(new Vector3Int(0, 0, 0), new Vector3Int(32, 1, 32), CubesTestAssets.BLOCK_STONE);
        blockTerrain.setBlocksFromNoise(new Vector3Int(0, 1, 0), new Vector3Int(32, 5, 32), 0.5f, CubesTestAssets.BLOCK_GRASS);
        terrainNode = new Node();
        terrainNode.addControl(blockTerrain);
        rootNode.attachChild(terrainNode);
    }
    
    private void initBlockTerrain(){
        blockTerrain = new BlockTerrainControl(CubesTestAssets.getSettings(this), new Vector3Int(2, 1, 2));
        blockTerrain.setBlockArea(new Vector3Int(0, 0, 0), new Vector3Int(32, 1, 32), CubesTestAssets.BLOCK_WOOD);
        terrainNode = new Node();
        terrainNode.addControl(blockTerrain);
        rootNode.attachChild(terrainNode);
    }
    
    private void initBlockTerrainPhy(){
        CubesTestAssets.registerBlocks();
        terrainNode = new Node();
        //CubesTestAssets.initializeEnvironment(this);
        
        blockTerrain = new BlockTerrainControl(CubesTestAssets.getSettings(this), new Vector3Int(2, 1, 2));
        blockTerrain.setBlockArea(new Vector3Int(0, 0, 0), new Vector3Int(32, 1, 32), CubesTestAssets.BLOCK_WOOD);
        blockTerrain.addChunkListener(new BlockChunkListener(){

            @Override
            public void onSpatialUpdated(BlockChunkControl blockChunk){
                Geometry optimizedGeometry = blockChunk.getOptimizedGeometry_Opaque();
                RigidBodyControl rigidBodyControl = optimizedGeometry.getControl(RigidBodyControl.class);
                if(rigidBodyControl == null){
                    rigidBodyControl = new RigidBodyControl(0);
                    optimizedGeometry.addControl(rigidBodyControl);
                    bulletAppState.getPhysicsSpace().add(rigidBodyControl);
                }
                rigidBodyControl.setCollisionShape(new MeshCollisionShape(optimizedGeometry.getMesh()));
            }
        });
        terrainNode.addControl(blockTerrain);
        terrainNode.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        rootNode.attachChild(terrainNode);
    }
    
    private void initGUI(){
        //Crosshair
        BitmapText crosshair = new BitmapText(guiFont);
        crosshair.setText("+");
        crosshair.setSize(guiFont.getCharSet().getRenderedSize() * 2);
        crosshair.setLocalTranslation(
                (settings.getWidth() / 2) - (guiFont.getCharSet().getRenderedSize() / 3 * 2),
                (settings.getHeight() / 2) + (crosshair.getLineHeight() / 2), 0);
        guiNode.attachChild(crosshair);
        //Instructions
        BitmapText instructionsText1 = new BitmapText(guiFont);
        instructionsText1.setText("Left Click: Set");
        instructionsText1.setLocalTranslation(0, settings.getHeight(), 0);
        guiNode.attachChild(instructionsText1);
        BitmapText instructionsText2 = new BitmapText(guiFont);
        instructionsText2.setText("Right Click: Remove");
        instructionsText2.setLocalTranslation(0, settings.getHeight() - instructionsText2.getLineHeight(), 0);
        guiNode.attachChild(instructionsText2);
        BitmapText instructionsText3 = new BitmapText(guiFont);
        instructionsText3.setText("(Bottom layer is marked as indestructible)");
        instructionsText3.setLocalTranslation(0, settings.getHeight() - (2 * instructionsText3.getLineHeight()), 0);
        guiNode.attachChild(instructionsText3);
   }

    @Override
    public void onAction(String action, boolean value, float lastTimePerFrame){
        if(action.equals("set_block") && value){
            Vector3Int blockLocation = getCurrentPointedBlockLocation(true);
            if(blockLocation != null){
                blockTerrain.setBlock(blockLocation, CubesTestAssets.BLOCK_WOOD);
            }
        }
        else if(action.equals("remove_block") && value){
            Vector3Int blockLocation = getCurrentPointedBlockLocation(false);
            //This conditional test ensures the bottom row is not removed.
            if((blockLocation != null) && (blockLocation.getY() > 0)){
                blockTerrain.removeBlock(blockLocation);
            }
        }
    }
    
    private Vector3Int getCurrentPointedBlockLocation(boolean getNeighborLocation){
        CollisionResults results = getRayCastingResults(terrainNode);
        if(results.size() > 0){
            Vector3f collisionContactPoint = results.getClosestCollision().getContactPoint();
            return BlockNavigator.getPointedBlockLocation(blockTerrain, collisionContactPoint, getNeighborLocation);
        }
        return null;
    }
    
    private CollisionResults getRayCastingResults(Node node){
        Vector3f origin = cam.getWorldCoordinates(new Vector2f((settings.getWidth() / 2), (settings.getHeight() / 2)), 0.0f);
        Vector3f direction = cam.getWorldCoordinates(new Vector2f((settings.getWidth() / 2), (settings.getHeight() / 2)), 0.3f);
        direction.subtractLocal(origin).normalizeLocal();
        Ray ray = new Ray(origin, direction);
        CollisionResults results = new CollisionResults();
        node.collideWith(ray, results);
        return results;
    }
    
    @Override
    public void simpleUpdate(float tpf){
        Vector3f pos = player.control.phy_control.getPhysicsLocation().add(0,5,0);
        cam.setLocation(pos);
        cam.lookAtDirection(player.control.phy_control.getViewDirection().negate(), Vector3f.UNIT_Y);
    }
    
    public void camUpdate3rdPerson(){
        Vector3f pos = player.control.phy_control.getPhysicsLocation().subtract(player.control.phy_control.getViewDirection().negate().mult(10));
        pos.addLocal(0,5,0);
        Vector3f dif = pos.subtract(cam.getLocation());
        cam.setLocation(cam.getLocation().add(dif.mult(0.1f)));
        cam.lookAtDirection(player.control.phy_control.getViewDirection().negate(), Vector3f.UNIT_Y);
    }
}

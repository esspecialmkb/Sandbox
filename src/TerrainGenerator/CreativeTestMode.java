/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package TerrainGenerator;

import com.cubes.BlockNavigator;
import com.cubes.BlockTerrainControl;
import com.cubes.Vector3Int;
import com.cubes.test.CubesAssets;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.collision.CollisionResults;
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
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.debug.WireBox;

/**
 *      AppState for creative mode editing - no gui yet!!!
 * @author Maestro
 */
public class CreativeTestMode extends AbstractAppState {
    
    public boolean rotation = false;
    public int currentBlock = 0;
    public boolean moveUp = false, moveDown = false, moveLeft = false, moveRight = false, moveForward = false, moveBackward = false;
    
    public Node rootNode;
    public Node terrainNode;
    public BlockTerrainControl blockTerrain;
    public Node cursor = new Node("Cursor");
    public Application app;
    public InputManager inputManager;
    public AssetManager assetManager;
    
    public float setBlockTimer = 0.0f;
    public float removeBlockTimer = 0.0f;
    public Vector3f camAngle = new Vector3f();
    public Camera cam;
    
    public void selectBlock(int value){
        currentBlock = value;
    }
    
    
    /**
     *      Constructor
     * @param root - ref to rootNode from scene
     */
    public CreativeTestMode(Node root){
        this.rootNode = root;
    }
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        //TODO: initialize your AppState, e.g. attach spatials to rootNode
        //this is called on the OpenGL thread after the AppState has been attached
        this.app = app;
        this.cam = app.getCamera();
        this.inputManager = app.getInputManager();
        this.assetManager = app.getAssetManager();
        
        CubesAssets.registerBlocks();
        initControls();
        cam.setLocation(new Vector3f(-16.6f, 46, 97.6f));
        cam.lookAtDirection(new Vector3f(0.68f, -0.47f, -0.56f), Vector3f.UNIT_Y);
        this.initBlockTerrain();
        createCursorBox(1.51f, ColorRGBA.Black);
    }
    
    private void initBlockTerrain(){
        blockTerrain = new BlockTerrainControl(CubesAssets.getSettings((SimpleApplication)this.app), new Vector3Int(8, 1, 8));
        blockTerrain.setBlockArea(new Vector3Int(0, 0, 0), new Vector3Int(128, 8, 128), CubesAssets.BLOCK_STONE);
        blockTerrain.setBlockArea(new Vector3Int(0, 8, 0), new Vector3Int(128, 4, 128), CubesAssets.BLOCK_GRASS);
        //blockTerrain.setBlocksFromNoise(new Vector3Int(0, 1, 0), new Vector3Int(32, 5, 32), 0.5f, CubesAssets.BLOCK_GRASS);
        terrainNode = new Node();
        terrainNode.addControl(blockTerrain);
        rootNode.attachChild(terrainNode);
    }
    
    public Geometry putShape(Mesh shape, ColorRGBA color){
        shape.setLineWidth(2);
        Geometry g = new Geometry("shape", shape);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.getAdditionalRenderState().setWireframe(true);
        mat.setColor("Color", color);
        g.setMaterial(mat);
        cursor.attachChild(g);
        rootNode.attachChild(cursor);
        return g;
    }
    
    public void createCursorBox(float size, ColorRGBA color){
        putShape(new WireBox(size, size, size), color);
    }
    
    @Override
    public void update(float tpf) {
        //TODO: implement behavior during runtime
        Quaternion view = new Quaternion();
        view.fromAngles(camAngle.x,camAngle.y,0);
        cam.setRotation(view);
        Vector3f dir = cam.getDirection();
        Vector3f left = cam.getLeft();
        Vector3f camMove = new Vector3f();
        if(moveUp == true){
            camMove = camMove.add(0, 1, 0);
        }if(moveDown ==true){
            camMove = camMove.add(0, -1, 0);
        }if(moveLeft == true){
            camMove = camMove.add(left);
        }if(moveRight == true){
            camMove = camMove.add(left.negate());
        }if(moveForward == true){
            camMove = camMove.add(dir);
        }if(moveBackward == true){
            camMove = camMove.add(dir.negate());
        }
        camMove = camMove.normalize();
        camMove = camMove.mult(0.25f);
        cam.setLocation(cam.getLocation().add(camMove));
        Vector3Int pos = getCurrentPointedBlockLocation(false);
        if(pos != null){
            cursor.setLocalTranslation((pos.getX() * 3) + 1.5f, (pos.getY() * 3) + 1.5f, (pos.getZ() * 3) + 1.5f);
        } else {
            cursor.setLocalTranslation(0, -100, 0);
        }
    }
    
    @Override
    public void cleanup() {
        super.cleanup();
        //TODO: clean up what you initialized in the initialize method,
        //e.g. remove all spatials from rootNode
        //this is called on the OpenGL thread after the AppState has been detached
        this.terrainNode.removeControl(blockTerrain);
        this.rootNode.detachChild(terrainNode);
        this.cam = null;
        this.inputManager.removeListener(this.analogListener);
        this.inputManager.removeListener(this.actionListener);
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
        Vector2f mPos = app.getInputManager().getCursorPosition();
        Vector3f origin = app.getCamera().getWorldCoordinates(mPos, 0.0f);
        Vector3f direction = app.getCamera().getWorldCoordinates(mPos, 0.3f);
        direction.subtractLocal(origin).normalizeLocal();
        Ray ray = new Ray(origin, direction);
        CollisionResults results = new CollisionResults();
        node.collideWith(ray, results);
        return results;
    }
    
    private void initControls(){
        inputManager.addMapping("moveForward", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("moveBackward", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("moveUp", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping("moveDown", new KeyTrigger(KeyInput.KEY_LSHIFT));
        inputManager.addMapping("moveLeft", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("moveRight", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addListener(actionListener,"moveForward","moveBackward","moveUp","moveDown","moveLeft","moveRight");
        
        inputManager.addMapping("set_block", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addListener(actionListener, "set_block");
        inputManager.addListener(analogListener, "set_block");
        inputManager.addMapping("remove_block", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
        inputManager.addListener(actionListener, "remove_block");
        inputManager.addListener(analogListener, "remove_block");
        inputManager.addMapping("mouseLookLeft", new MouseAxisTrigger(MouseInput.AXIS_X,true));
        inputManager.addMapping("mouseLookRight", new MouseAxisTrigger(MouseInput.AXIS_X,false));
        inputManager.addMapping("mouseLookUp", new MouseAxisTrigger(MouseInput.AXIS_Y,false));
        inputManager.addMapping("mouseLookDown", new MouseAxisTrigger(MouseInput.AXIS_Y,true));
        inputManager.addListener(analogListener, "mouseLookLeft","mouseLookRight","mouseLookUp","mouseLookDown");
        inputManager.addMapping("l_shift",new MouseButtonTrigger(MouseInput.BUTTON_MIDDLE));
        inputManager.addListener(actionListener, "l_shift");
    }
    
    public ActionListener actionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean value, float lastTimePerFrame) {
            if(name.equals("set_block") && value){
                Vector3Int blockLocation = getCurrentPointedBlockLocation(true);
                if(blockLocation != null){
                    blockTerrain.setBlock(blockLocation, CubesAssets.getBlockFromId(currentBlock + 1));
                }              
            }else if(name.equals("remove_block") && value){
                
                Vector3Int blockLocation = getCurrentPointedBlockLocation(false);
                //This conditional test ensures the bottom row is not removed.
                if((blockLocation != null) && (blockLocation.getY() > 0)){
                    blockTerrain.removeBlock(blockLocation);
                }
                
            }if(name.equals("set_block") && !value){
                setBlockTimer = 0.0f;              
            }else if(name.equals("remove_block") && !value){
                removeBlockTimer = 0.0f;
            }if(name.equals("l_shift")){
                rotation = value;
                //flyCam.setDragToRotate(!value);
            }if(name.equals("moveUp")){
                moveUp = value;
            }if(name.equals("moveDown")){
                moveDown = value;
            }if(name.equals("moveLeft")){
                moveLeft = value;
            }if(name.equals("moveRight")){
                moveRight = value;
            }if(name.equals("moveForward")){
                moveForward = value;
            }if(name.equals("moveBackward")){
                moveBackward = value;
            }
        }
    };
    
    public AnalogListener analogListener = new AnalogListener() {
        
        @Override
        public void onAnalog(String name, float value, float tpf) {
            if(name.equals("mouseLookLeft")){
                if(rotation == true){
                    camAngle.y = camAngle.y + value;
                }
                //System.out.println("mouseLookLeft");
                //control.viewAngle += value;
            }if(name.equals("mouseLookRight")){
                if(rotation == true){
                    camAngle.y = camAngle.y - value;
                }
                //System.out.println("mouseLookRight");
                //control.viewAngle -= value;
            }if(name.equals("mouseLookUp")){
                if(rotation == true){
                    camAngle.x = camAngle.x - value;
                }
                //System.out.println("mouseLookUp");
                //control.viewAngle += value;
            }if(name.equals("mouseLookDown")){
                if(rotation == true){
                    camAngle.x = camAngle.x + value;
                }
                //System.out.println("mouseLookDown");
                //control.viewAngle -= value;
            }if(name.equals("set_block")){
                setBlockTimer = setBlockTimer + value;
                System.out.println("set_block: " + setBlockTimer);
                if(setBlockTimer > 0.33f){
                    Vector3Int blockLocation = getCurrentPointedBlockLocation(true);
                    if(blockLocation != null){
                        blockTerrain.setBlock(blockLocation, CubesAssets.getBlockFromId(currentBlock + 1));
                    }
                    setBlockTimer = 0.0f;
                }
            }
            if(name.equals("remove_block")){
                removeBlockTimer = removeBlockTimer + value;
                System.out.println("remove_block: " + removeBlockTimer);
                if(removeBlockTimer > 0.33f){
                    Vector3Int blockLocation = getCurrentPointedBlockLocation(false);
                    //This conditional test ensures the bottom row is not removed.
                    if((blockLocation != null) && (blockLocation.getY() > 0)){
                        blockTerrain.removeBlock(blockLocation);
                    }
                    removeBlockTimer = 0.0f;
                }
            }
        }
    };
}

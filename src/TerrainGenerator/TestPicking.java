package TerrainGenerator;

import com.cubes.test.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.jme3.app.SimpleApplication;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapText;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.Vector3f;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.system.AppSettings;
import com.jme3.scene.Node;
import com.cubes.*;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;

public class TestPicking extends SimpleApplication implements ActionListener{
    public boolean rotation = false;
    public int currentBlock = 0;

    public static void main(String[] args){
        Logger.getLogger("").setLevel(Level.SEVERE);
        TestPicking app = new TestPicking();
        app.start();
    }

    public TestPicking(){
        settings = new AppSettings(true);
        settings.setWidth(1280);
        settings.setHeight(720);
        settings.setTitle("Cubes Demo - Picking");
    }
    public Node terrainNode;
    public BlockTerrainControl blockTerrain;
    
    public void selectBlock(int value){
        currentBlock = value;
    }
    
    public void compressBlockTerrain(){
        CubesSettings blocks = blockTerrain.getSettings();
        int chunk_x = blocks.getChunkSizeX();
        int chunk_y = blocks.getChunkSizeY();
        int chunk_z = blocks.getChunkSizeZ();
        //blocks.
    }

    @Override
    public void simpleInitApp(){
        CubesAssets.registerBlocks();
        initControls();
        initBlockTerrain();
        //initGUI();
        cam.setLocation(new Vector3f(-16.6f, 46, 97.6f));
        cam.lookAtDirection(new Vector3f(0.68f, -0.47f, -0.56f), Vector3f.UNIT_Y);
        flyCam.setMoveSpeed(250);
        flyCam.setDragToRotate(true);
    }
    
    private void initControls(){
        inputManager.addMapping("set_block", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addListener(actionListener, "set_block");
        inputManager.addMapping("remove_block", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
        inputManager.addListener(actionListener, "remove_block");
        inputManager.addMapping("l_shift", new KeyTrigger(KeyInput.KEY_LSHIFT),new MouseButtonTrigger(MouseInput.BUTTON_MIDDLE));
        inputManager.addListener(actionListener, "l_shift");
    }
    
    private void initBlockTerrain(){
        blockTerrain = new BlockTerrainControl(CubesAssets.getSettings(this), new Vector3Int(4, 1, 4));
        blockTerrain.setBlockArea(new Vector3Int(0, 0, 0), new Vector3Int(64, 8, 64), CubesAssets.BLOCK_STONE);
        blockTerrain.setBlockArea(new Vector3Int(0, 8, 0), new Vector3Int(64, 4, 64), CubesAssets.BLOCK_GRASS);
        //blockTerrain.setBlocksFromNoise(new Vector3Int(0, 1, 0), new Vector3Int(32, 5, 32), 0.5f, CubesAssets.BLOCK_GRASS);
        terrainNode = new Node();
        terrainNode.addControl(blockTerrain);
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
                switch(currentBlock){
                    case 0:
                        blockTerrain.setBlock(blockLocation, CubesAssets.BLOCK_GRASS);
                        break;
                    case 1:
                        blockTerrain.setBlock(blockLocation, CubesAssets.BLOCK_WOOD);
                        break;
                    case 2:
                        blockTerrain.setBlock(blockLocation, CubesAssets.BLOCK_STONE);
                        break;
                    case 3:
                        blockTerrain.setBlock(blockLocation, CubesAssets.BLOCK_BRICK);
                        break;
                }
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
        Vector2f mPos = inputManager.getCursorPosition();
        Vector2f scrnCenter = new Vector2f((settings.getWidth() / 2), (settings.getHeight() / 2));
        Vector3f origin = cam.getWorldCoordinates(mPos, 0.0f);
        Vector3f direction = cam.getWorldCoordinates(mPos, 0.3f);
        direction.subtractLocal(origin).normalizeLocal();
        Ray ray = new Ray(origin, direction);
        CollisionResults results = new CollisionResults();
        node.collideWith(ray, results);
        return results;
    }
    
    public ActionListener actionListener = new ActionListener() {
        public void onAction(String name, boolean value, float lastTimePerFrame) {
            if(name.equals("set_block") && value){
                Vector3Int blockLocation = getCurrentPointedBlockLocation(true);
                if(blockLocation != null){
                    switch(currentBlock){
                        case 0:
                            blockTerrain.setBlock(blockLocation, CubesAssets.BLOCK_GRASS);
                            break;
                        case 1:
                            blockTerrain.setBlock(blockLocation, CubesAssets.BLOCK_WOOD);
                            break;
                        case 2:
                            blockTerrain.setBlock(blockLocation, CubesAssets.BLOCK_STONE);
                            break;
                        case 3:
                            blockTerrain.setBlock(blockLocation, CubesAssets.BLOCK_BRICK);
                            break;
                    }
                }
                
            }
            else if(name.equals("remove_block") && value){
                
                Vector3Int blockLocation = getCurrentPointedBlockLocation(false);
                //This conditional test ensures the bottom row is not removed.
                if((blockLocation != null) && (blockLocation.getY() > 0)){
                    blockTerrain.removeBlock(blockLocation);
                }
                
            }if(name.equals("l_shift")){
                rotation = value;
                flyCam.setDragToRotate(!value);
            }
        }
    };
    
    public AnalogListener analogListener = new AnalogListener() {
        public void onAnalog(String name, float value, float tpf) {
            if(name.equals("mouseLookLeft")){
                if(rotation == true){
                    
                }
                //control.viewAngle += value;
            }if(name.equals("mouseLookRight")){
                //control.viewAngle -= value;
            }
        }
    };
}

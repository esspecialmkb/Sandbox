/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package TerrainGenerator;

import com.cubes.BlockTerrainControl;
import com.cubes.Vector3Int;
import com.cubes.test.CubesTestAssets;
import com.cubes.test.TestTutorial;
import com.jme3.app.SimpleApplication;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Maestro
 */
public class VillageGen extends SimpleApplication{
    public BlockTerrainControl blockTerrain;
    public Node terrainNode;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Logger.getLogger("").setLevel(Level.SEVERE);
        VillageGen app = new VillageGen();
        app.start();
    }
    
    public VillageGen(){
        settings = new AppSettings(true);
        settings.setWidth(1280);
        settings.setHeight(720);
        settings.setTitle("Cubes Demo - Tutorial");
    }
    @Override
    public void simpleInitApp() {
        CubesTestAssets.registerBlocks();
        
        //This is your terrain, it contains the whole
        //block world and offers methods to modify it
        blockTerrain = new BlockTerrainControl(CubesTestAssets.getSettings(this), new Vector3Int(4, 1, 4));

        //To set a block, just specify the location and the block object
        //(Existing blocks will be replaced)
        //blockTerrain.setBlock(new Vector3Int(0, 0, 0), CubesTestAssets.BLOCK_WOOD);
        //blockTerrain.setBlock(new Vector3Int(0, 0, 1), CubesTestAssets.BLOCK_WOOD);
        //blockTerrain.setBlock(new Vector3Int(1, 0, 0), CubesTestAssets.BLOCK_WOOD);
        //blockTerrain.setBlock(new Vector3Int(1, 0, 1), CubesTestAssets.BLOCK_STONE);
        //blockTerrain.setBlock(0, 0, 0, CubesTestAssets.BLOCK_GRASS); //For the lazy users :P

        //You can place whole areas of blocks too: setBlockArea(location, size, block)
        //(The specified block will be cloned each time)
        //The following line will set 3 blocks on top of each other
        //({1,1,1}, {1,2,3} and {1,3,1})
        blockTerrain.setBlockArea(new Vector3Int(0, 0, 0), new Vector3Int(64, 1, 64), CubesTestAssets.BLOCK_GRASS);
        blockTerrain.setBlockArea(new Vector3Int(30, 0, 2), new Vector3Int(3,1,60), CubesTestAssets.BLOCK_STONE);
        
        int roadLength = 60;
        int x = 34;
        int z = 3;
        
        while(roadLength > 0){
            int length = 7;
            blockTerrain.setBlockArea(new Vector3Int(x,1,z), new Vector3Int(5,4,length), CubesTestAssets.BLOCK_WOOD);
            z = z + (length + 5);
            roadLength = roadLength - (length + 5);
        }
        
        //blockTerrain.setBlockArea(new Vector3Int(34,1,3), new Vector3Int(5,4,7), CubesTestAssets.BLOCK_WOOD);
        //blockTerrain.setBlockArea(new Vector3Int(34,1,3 + (7 + 5)), new Vector3Int(5,4,7), CubesTestAssets.BLOCK_WOOD);
        
        //Removing a block works in a similar way
        //blockTerrain.removeBlock(new Vector3Int(1, 2, 1));
        //blockTerrain.removeBlock(new Vector3Int(1, 3, 1));

        //The terrain is a jME-Control, you can add it
        //to a node of the scenegraph to display it
        terrainNode = new Node();
        terrainNode.addControl(blockTerrain);
        rootNode.attachChild(terrainNode);
        terrainNode.move(-32 * 4.2f,0,-32 * 4.2f);
        
        cam.setLocation(new Vector3f(0, 10, 0));
        cam.lookAtDirection(new Vector3f(0, 0, 1), Vector3f.UNIT_Y);
        flyCam.setMoveSpeed(50);
    }
}

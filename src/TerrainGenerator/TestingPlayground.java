package TerrainGenerator;

import SimplexNoise.OpenSimplexNoise;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.jme3.app.SimpleApplication;
import com.jme3.font.BitmapText;
import com.jme3.system.AppSettings;
import com.jme3.scene.Node;
import com.cubes.*;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.debug.WireBox;
import com.jme3.scene.shape.Box;
import java.util.ArrayList;

/**
 *      Starting phase 2 of engine development
 *    - Creative mode editing is complete
 *    - Need to start standardizing map and player data
 * @author Maestro
 */
public class TestingPlayground extends SimpleApplication{
    public boolean rotation = false;
    public int currentBlock = 0;
    public boolean moveUp = false, moveDown = false, moveLeft = false, moveRight = false, moveForward = false, moveBackward = false;
    
    public ArrayList<GUIRequest> requestQueue;
    public Node cursor = new Node("Cursor");
    
    private static final int WIDTH = 256;
    private static final int HEIGHT = 256;
    private static final double FEATURE_SIZE = 64;
    public double [][] mapRaw = new double[WIDTH][HEIGHT];
    public int [][] map = new int[WIDTH][HEIGHT];
    public double min = 0;
    public double max = 0;
    
    public void prepareTerrain(int height){
        //FastMath.nextRandomInt();
        double range = max - min;
        System.out.println("Prep Terrain");
        for(int y = 0; y < HEIGHT; y++) {
            for(int x = 0; x < WIDTH; x++) {
                map[x][y] = (int)FastMath.interpolateLinear((float) ((mapRaw[x][y] - min) / range), 1, height);
            }
        }
    }
    
    public void genSimplexFlatten(float power){
        OpenSimplexNoise noise = new OpenSimplexNoise(FastMath.nextRandomInt(1,999999));
        
        min = 1;
        max = 0;
        for (int y = 0; y < HEIGHT; y++)
        {
            for (int x = 0; x < WIDTH; x++)
            {
                double value = noise.eval((x/FEATURE_SIZE) , (y/FEATURE_SIZE) , 0.0) + (noise.eval((x/FEATURE_SIZE)*2 , (y/FEATURE_SIZE)*2 , 0.0)* 0.5 ) + (noise.eval((x/FEATURE_SIZE)*4 , (y/FEATURE_SIZE)*4 , 0.0)* 0.25 );
                float elevation = (float)((value + 1) * 127.5)/255.0f;
                elevation = (float) Math.pow(elevation, power);
                mapRaw[x][y] = elevation;
                if(elevation < min){
                    min = elevation;
                }
                if(elevation > max){
                    max = elevation;
                }
            }
        }
        System.out.println("Min:" + min);
        System.out.println("Max:" + max);
        prepareTerrain(50);
    }
    
    public void genSimplexFlatten(float power, int seed){
        OpenSimplexNoise noise = new OpenSimplexNoise(seed);
        
        min = 1;
        max = 0;
        for (int y = 0; y < HEIGHT; y++)
        {
            for (int x = 0; x < WIDTH; x++)
            {
                double value = noise.eval((x/FEATURE_SIZE) , (y/FEATURE_SIZE) , 0.0) + (noise.eval((x/FEATURE_SIZE)*2 , (y/FEATURE_SIZE)*2 , 0.0)* 0.5 ) + (noise.eval((x/FEATURE_SIZE)*4 , (y/FEATURE_SIZE)*4 , 0.0)* 0.25 );
                float elevation = (float)((value + 1) * 127.5)/255.0f;
                elevation = (float) Math.pow(elevation, power);
                mapRaw[x][y] = elevation;
                if(elevation < min){
                    min = elevation;
                }
                if(elevation > max){
                    max = elevation;
                }
            }
        }
        System.out.println("Min:" + min);
        System.out.println("Max:" + max);
        prepareTerrain(50);
    }
    
    public class GUIRequest{
        public String name;
        public int op_Code;
        public GUIRequest(int op, String name){
            this.name = name;
            this.op_Code = op;
        }
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
    
    public void createGUIRequest(int op, String name){
        GUIRequest temp = new GUIRequest(op,name);
        requestQueue.add(temp); 
    }

    public static void main(String[] args){
        Logger.getLogger("").setLevel(Level.SEVERE);
        TestingPlayground app = new TestingPlayground();
        app.start();
    }

    public TestingPlayground(){
        settings = new AppSettings(true);
        //settings.
        settings.setWidth(1280);
        settings.setHeight(720);
        settings.setTitle("Cubes Demo - Picking");
    }
    public Node terrainNode;
    public BlockTerrainControl blockTerrain;
    
    public void selectBlock(int value){
        currentBlock = value;
    }
    
    Geometry geom;
    Node guiHand;
    
    //Copied to  CreativeTestMode
    @Override
    public void simpleInitApp(){
        //initGUI();
        setDisplayFps(false);
        setDisplayStatView(false);
        CreativeTestMode appState = new CreativeTestMode(rootNode);
        GUIAppState guiState = new GUIAppState(guiNode);
        stateManager.attach(appState);
        stateManager.attach(guiState);
        
        /*
        cam.setLocation(new Vector3f(-16.6f, 46, 97.6f));
        cam.lookAtDirection(new Vector3f(0.68f, -0.47f, -0.56f), Vector3f.UNIT_Y);
        */
        
        flyCam.setEnabled(false);
        guiState.initHUD(guiFont);
    }
    
    //Copied to  CreativeTestMode
    @Override
    public void simpleUpdate(float tpf){
        //guiHand.rotate(0, 0, tpf);
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
}

package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.debug.Grid;
import com.jme3.scene.debug.WireBox;
import com.jme3.scene.shape.Box;
import java.util.ArrayList;

/**
 * test
 * @author normenhansen
 */
public class Main extends SimpleApplication {
    static Main app;
    public class Cell{
        public float posX, posZ;
        public float dimX, dimZ;
        public float movX, movZ;
        
        public float getMinX(){return this.posX - this.dimX;}
        public float getMaxX(){return this.posX + this.dimX;}
        public float getMinZ(){return this.posZ - this.dimZ;}
        public float getMaxZ(){return this.posZ + this.dimZ;}
        
        public int id;
        public int flag;
        public ArrayList<Integer> overlap;
        public Cell(int id){
            this.posX = 0;
            this.posZ = 0;
            this.dimX = 0;
            this.dimZ = 0;
            this.movX = 0;
            this.movZ = 0;
            this.flag = 0;
            this.id = id;
            overlap = new ArrayList<Integer>();
        }
    }
    
    public ArrayList<Cell> cells;
    
    public static void main(String[] args) {
        app = new Main();
        app.start();
    }
    
    public Geometry putShape(Mesh shape, ColorRGBA color){
        Geometry g = new Geometry("shape", shape);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.getAdditionalRenderState().setWireframe(true);
        mat.setColor("Color", color);
        g.setMaterial(mat);
        rootNode.attachChild(g);
        return g;
    }
    
    public void putBox(Vector3f pos, float size, ColorRGBA color){
        putShape(new WireBox(size, size, size), color).setLocalTranslation(pos);
    }
    
    public void putBox(Vector3f pos, float sizeX, float sizeZ, ColorRGBA color){
        putShape(new Box(sizeX, 0.5f, sizeZ), color).setLocalTranslation(pos);
    }
    
    public boolean Intersect2d(int A,int B) {
        // Get info from Cells
        
        if(cells.get(A).getMaxX() < cells.get(B).getMinX()) return false;
        if(cells.get(A).getMinX() > cells.get(B).getMaxX()) return false;
        if(cells.get(A).getMaxZ() < cells.get(B).getMinZ()) return false;
        if(cells.get(A).getMinZ() > cells.get(B).getMaxZ()) return false;
        cells.get(A).movX = cells.get(A).posX - cells.get(B).posX;
        cells.get(A).movZ = cells.get(A).posZ - cells.get(B).posZ;
        /*
        if((cells.get(A).getMaxX() > cells.get(B).getMinX()) && (cells.get(A).getMaxX() < cells.get(B).getMaxX())){
            cells.get(A).movX = -1;
        }
        
        if((cells.get(A).getMinX() < cells.get(B).getMaxX()) && (cells.get(A).getMinX() > cells.get(B).getMinX())){
            cells.get(A).movX = 1;
        }
        
        if((cells.get(A).getMaxZ() > cells.get(B).getMinZ()) && (cells.get(A).getMaxZ() < cells.get(B).getMaxZ())){
            cells.get(A).movZ = -1;
        }
        
        if((cells.get(A).getMinZ() < cells.get(B).getMaxZ()) && (cells.get(A).getMinZ() > cells.get(B).getMinZ())){
            cells.get(A).movZ = 1;
        }
        
        cells.get(A).posX = cells.get(A).posX + cells.get(A).movX;
        cells.get(A).posZ = cells.get(A).posZ + cells.get(A).movZ;
        //System.out.println("Move = " + listCells.get(A).movX + " , " + listCells.get(A).movZ);
        cells.get(A).movX = 0;
        cells.get(A).movZ = 0;
        */
        return true;
    }
    
    /** Custom Keybinding: Map named actions to inputs. */
  private void initKeys() {
    /** You can map one or several inputs to one named mapping. */
    inputManager.addMapping("Reset",  new KeyTrigger(KeyInput.KEY_F1));
    /** Add the named mappings to the action listeners. */
    inputManager.addListener(actionListener,"Reset");
  }

  /** Use this listener for KeyDown/KeyUp events */
  private ActionListener actionListener = new ActionListener() {
      public float t = 0.0f;
    public void onAction(String name, boolean keyPressed, float tpf) {
      if (name.equals("Reset") && !keyPressed) {
        app.restart();
      }
    }
  };

    @Override
    public void simpleInitApp() {
        initKeys();
        // 1.)  Set the number of cells(ex. 150)
        cells = new ArrayList<Cell>();
        int numCellsA = 10;
        for(int i_List = 0;i_List < numCellsA;i_List++){
            cells.add(new Cell(i_List));
        }
        
        // 2.)  Spawn a Rect of random width and length within a certain radius
        for(int i_Spawn = 0; i_Spawn < cells.size();i_Spawn++){
            float width = 0;
            float length = 0;
            
            float average = (width + length) * 2;
            average = average / 2;
            do{
                width = FastMath.nextRandomInt(5, 11);
                length = FastMath.nextRandomInt(5, 11);
            }while(FastMath.abs(width - length) > 3);
            
            float angle = FastMath.nextRandomFloat() * 360;
            float radius = (FastMath.nextRandomFloat() * 8) + 2;
            
            cells.get(i_Spawn).posX = FastMath.cos(angle) * radius;
            cells.get(i_Spawn).posZ = FastMath.sin(angle) * radius;
            cells.get(i_Spawn).dimX = width;
            cells.get(i_Spawn).dimZ = length;
        }
        
        // 3.)  Separate the cells(separation steering behavior
        int overlap = 0;
        int thisTest = 0;
        int loopCount = 0;
        do{
            overlap = 0;
            loopCount++;
            for(int i_Separate = 0; i_Separate < cells.size();i_Separate++){
                //Check each cell for collisions
                thisTest = 0;
                for(int i_Check = 0; i_Check < cells.size();i_Check++){
                    if(i_Separate != i_Check){
                        if(Intersect2d(i_Separate,i_Check)){
                            overlap++;
                            thisTest++;
                        }
                    }
                }
                if(thisTest == 0){
                    float p_length = (cells.get(i_Separate).posX * cells.get(i_Separate).posX) + (cells.get(i_Separate).posZ + cells.get(i_Separate).posZ);
                    p_length = FastMath.sqrt(p_length);
                    //cells.get(i_Separate).movX = (cells.get(i_Separate).movX) - ((cells.get(i_Separate).posX / FastMath.abs(p_length))*0.75f);
                    //cells.get(i_Separate).movZ = (cells.get(i_Separate).movZ) - ((cells.get(i_Separate).posZ / FastMath.abs(p_length))*0.75f);
                }
            }
            if(overlap > 0){
                for(int i_Move = 0;i_Move < cells.size();i_Move++){
                    //Manual normilization
                    float v_length = (cells.get(i_Move).movX * cells.get(i_Move).movX) + (cells.get(i_Move).movZ * cells.get(i_Move).movZ);
                    v_length = FastMath.sqrt(v_length);
                    
                    cells.get(i_Move).movX = cells.get(i_Move).movX / FastMath.abs(v_length);
                    cells.get(i_Move).movZ = cells.get(i_Move).movZ / FastMath.abs(v_length);
                    
                    //Move the cell
                    cells.get(i_Move).posX = cells.get(i_Move).posX + (cells.get(i_Move).movX * 0.25f);
                    cells.get(i_Move).posZ = cells.get(i_Move).posZ + (cells.get(i_Move).movZ * 0.25f);
                }
            }
            System.out.println("Num overlaps = " + overlap + ". Num loops = " + loopCount);
        }while((overlap != 0) && (loopCount < 1000));
        loopCount = 0;
        
        // 4.)  Fill remainning gaps with 1x1 cells
        
        
        // 5.)  Convert each cell with width and length above a certain threshold
        
        // 6.)  Delaunay Triangulation
        
        // 7.)  Minimum Spanning tree
        
        // 8.)  Reconnect a few edges from triangulation
        
        // 9.)  Connect edges via straight lines(L-shapes)
        //      Convert intersecting cells into corridoors
        
        
        
        // End of algorythm 
        // convert cells into spatials [DEBUG]
        
        for(int i_Convert = 0; i_Convert < cells.size();i_Convert++) {
            if(cells.get(i_Convert).flag == 10){
                putBox(new Vector3f(cells.get(i_Convert).posX, 0, cells.get(i_Convert).posZ), cells.get(i_Convert).dimX, cells.get(i_Convert).dimZ, ColorRGBA.Blue);
            }else{
                putBox(new Vector3f(cells.get(i_Convert).posX, 0, cells.get(i_Convert).posZ), cells.get(i_Convert).dimX, cells.get(i_Convert).dimZ, ColorRGBA.Yellow);
            }
            
        }
        
        cam.setLocation(new Vector3f(0,250,0));
        cam.lookAtDirection(new Vector3f(0,-1,0), Vector3f.UNIT_Z);
        flyCam.setDragToRotate(true);
        flyCam.setMoveSpeed(15);
        //rootNode.attachChild(geom);
    }

    @Override
    public void simpleUpdate(float tpf) {
        //TODO: add update code
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
}

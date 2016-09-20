/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package TerrainGenerator;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.input.InputManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Image;
import com.jme3.texture.Image.Format;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import com.jme3.texture.image.ImageRaster;
import com.jme3.ui.Picture;
import com.jme3.util.BufferUtils;
import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 *  The GUIAppState is a container for GUI objects such as:
 *      - The player's hand/current item in 1st person view
 *      - The HUD (9 Hotbar slots)
 * @author Maestro
 */
public class GUIAppState extends AbstractAppState {
    
    public Application app;
    public Geometry geom;
    public Node guiHand;
    public Node guiNode;
    public Node hotbarNode;
    public Material itemSlotText;
    public Material itemSlotTextHighlight;
    public InputManager inputManager;
    public AssetManager assetManager;
    
    Picture hotbar;
    
    public GUIAppState(Node guiRoot){
        this.guiNode = guiRoot;
    }
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        //TODO: initialize your AppState, e.g. attach spatials to rootNode
        //this is called on the OpenGL thread after the AppState has been attached
        this.app = app;
        this.assetManager = app.getAssetManager();
        initHand();
        
        this.hotbarNode = new Node("Hotbar");
        
        Texture tex = this.assetManager.loadTexture("Interface/ItemSlot.png");
        tex.setMagFilter(Texture.MagFilter.Nearest);
        tex.setMinFilter(Texture.MinFilter.NearestNoMipMaps);
        tex.setAnisotropicFilter(16);
        itemSlotText = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        itemSlotText.setTexture("ColorMap", tex);
        itemSlotText.getAdditionalRenderState().setBlendMode(BlendMode.Alpha); 
        
        Texture tex2 = this.assetManager.loadTexture("Interface/ItemSlotHighlight.png");
        tex2.setMagFilter(Texture.MagFilter.Nearest);
        tex2.setMinFilter(Texture.MinFilter.NearestNoMipMaps);
        tex2.setAnisotropicFilter(16);
        itemSlotTextHighlight = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        itemSlotTextHighlight.setTexture("ColorMap", tex2);
        itemSlotTextHighlight.getAdditionalRenderState().setBlendMode(BlendMode.Alpha); 
        
        Quad q = new Quad(64,64);
        Geometry g = new Geometry("quad", q);
        g.setLocalTranslation(5.5f * 64, 64, -0.0001f);
        g.setMaterial(itemSlotTextHighlight);
        hotbarNode.attachChild(g); 
        Geometry g1 = new Geometry("quad", q);
        g1.setLocalTranslation(6.5f * 64, 64, -0.0001f);
        g1.setMaterial(itemSlotText);
        hotbarNode.attachChild(g1);
        Geometry g2 = new Geometry("quad", q);
        g2.setLocalTranslation(7.5f * 64, 64, -0.0001f);
        g2.setMaterial(itemSlotText);
        hotbarNode.attachChild(g2);
        Geometry g3 = new Geometry("quad", q);
        g3.setLocalTranslation(8.5f * 64, 64, -0.0001f);
        g3.setMaterial(itemSlotText);
        hotbarNode.attachChild(g3);
        Geometry g4 = new Geometry("quad", q);
        g4.setLocalTranslation(9.5f * 64, 64, -0.0001f);
        g4.setMaterial(itemSlotText);
        hotbarNode.attachChild(g4);
        Geometry g5 = new Geometry("quad", q);
        g5.setLocalTranslation(10.5f * 64, 64, -0.0001f);
        g5.setMaterial(itemSlotText);
        hotbarNode.attachChild(g5);
        Geometry g6 = new Geometry("quad", q);
        g6.setLocalTranslation(11.5f * 64, 64, -0.0001f);
        g6.setMaterial(itemSlotText);
        hotbarNode.attachChild(g6);
        Geometry g7 = new Geometry("quad", q);
        g7.setLocalTranslation(12.5f * 64, 64, -0.0001f);
        g7.setMaterial(itemSlotText);
        hotbarNode.attachChild(g7);
        Geometry g8 = new Geometry("quad", q);
        g8.setLocalTranslation(13.5f * 64, 64, -0.0001f);
        g8.setMaterial(itemSlotText);
        hotbarNode.attachChild(g8);
        
        guiNode.attachChild(hotbarNode);
    }
    
    public void initHUD(BitmapFont font){
        //Create Hotbar image
                
        BitmapText text = new BitmapText(font);
        text.setText("1 - Slot");
        text.setLocalTranslation(5.5f * 64, 18 + 64, 0);
        guiNode.attachChild(text);
        BitmapText text1 = new BitmapText(font);
        text1.setText("2 - Slot");
        text1.setLocalTranslation(6.5f * 64, 18 + 64, 0);
        guiNode.attachChild(text1);
        BitmapText text2 = new BitmapText(font);
        text2.setText("3 - Slot");
        text2.setLocalTranslation(7.5f * 64, 18 + 64, 0);
        guiNode.attachChild(text2);
        BitmapText text3 = new BitmapText(font);
        text3.setText("4 - Slot");
        text3.setLocalTranslation(8.5f * 64, 18 + 64, 0);
        guiNode.attachChild(text3);
        BitmapText text4 = new BitmapText(font);
        text4.setText("5 - Slot");
        text4.setLocalTranslation(9.5f * 64, 18 + 64, 0);
        guiNode.attachChild(text4);
        BitmapText text5 = new BitmapText(font);
        text5.setText("6 - Slot");
        text5.setLocalTranslation(10.5f * 64, 18 + 64, 0);
        guiNode.attachChild(text5);
        BitmapText text6 = new BitmapText(font);
        text6.setText("7 - Slot");
        text6.setLocalTranslation(11.5f * 64, 18 + 64, 0);
        guiNode.attachChild(text6);
        BitmapText text7 = new BitmapText(font);
        text7.setText("8 - Slot");
        text7.setLocalTranslation(12.5f * 64, 18 + 64, 0);
        guiNode.attachChild(text7);
        BitmapText text8 = new BitmapText(font);
        text8.setText("9 - Slot");
        text8.setLocalTranslation(13.5f * 64, 18 + 64, 0);
        guiNode.attachChild(text8);
                
        
    }
    
    public void initHand(){
        // create a cube-shaped mesh
        Box b = new Box(Vector3f.ZERO, 100, 300, 100);
        // create an object from the mesh
        geom = new Geometry("Box", b);
        guiHand = new Node("Hand");
        // create a simple blue material
        Material mat = new Material(assetManager,
        "Common/MatDefs/Misc/Unshaded.j3md");
        //mat.getAdditionalRenderState().setWireframe(true);

        mat.setColor("Color", ColorRGBA.Blue);
        // give the object the blue material
        geom.setMaterial(mat);
        guiHand.attachChild(geom);
        geom.move(0, 300, 0);
        geom.rotate(-30 * FastMath.DEG_TO_RAD, 30 * FastMath.DEG_TO_RAD, 0);
        // make the object appear in the scene
        guiNode.attachChild(guiHand);
        guiHand.setLocalTranslation(app.getContext().getSettings().getWidth() / 1.125f,-1 * app.getContext().getSettings().getHeight()/ 1.75f, 15);
    }
    
    @Override
    public void update(float tpf) {
        //TODO: implement behavior during runtime
        //hotbar.rotate(0, 0, tpf);
    }
    
    @Override
    public void cleanup() {
        super.cleanup();
        //TODO: clean up what you initialized in the initialize method,
        //e.g. remove all spatials from rootNode
        //this is called on the OpenGL thread after the AppState has been detached
    }
}

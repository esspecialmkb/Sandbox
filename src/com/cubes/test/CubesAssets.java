/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cubes.test;

import java.util.List;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.SceneProcessor;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.util.SkyFactory;
import com.jme3.water.WaterFilter;
import com.cubes.*;
import com.cubes.shapes.*;

/**
 *
 * @author Carl
 */
public class CubesAssets{
    
    public static CubesSettings getSettings(Application application){
        CubesSettings settings = new CubesSettings(application);
        settings.setDefaultBlockMaterial("Textures/cubes/newTerrainAlpha.png");
        return settings;
    }
    
    public static final Block BLOCK_GRASS = new Block(1,new BlockSkin[]{
            new BlockSkin(new BlockSkin_TextureLocation(0, 0), false),
            new BlockSkin(new BlockSkin_TextureLocation(3, 0), false),
            new BlockSkin(new BlockSkin_TextureLocation(2, 0), false)
        }){

        @Override
        protected int getSkinIndex(BlockChunkControl chunk, Vector3Int location, Block.Face face){
            if(chunk.isBlockOnSurface(location)){
                switch(face){
                    case Top:
                        return 0;

                    case Bottom:
                        return 2;
                }
                return 1;
            }
            return 2;
        }
    };
    private static final BlockSkin[] SKINS_WOOD = new BlockSkin[]{
        new BlockSkin(new BlockSkin_TextureLocation(5, 1), false),
        new BlockSkin(new BlockSkin_TextureLocation(5, 1), false),
        new BlockSkin(new BlockSkin_TextureLocation(4, 1), false),
        new BlockSkin(new BlockSkin_TextureLocation(4, 1), false),
        new BlockSkin(new BlockSkin_TextureLocation(4, 1), false),
        new BlockSkin(new BlockSkin_TextureLocation(4, 1), false)
    };
    public static Block BLOCK_WOOD = new Block(2,SKINS_WOOD);
    public static Block BLOCK_WOOD_FLAT = new Block(3,SKINS_WOOD){{
        setShapes(new BlockShape_Cuboid(new float[]{0, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f}));
    }};
    public static Block BLOCK_BRICK = new Block(4,new BlockSkin(new BlockSkin_TextureLocation(7, 0), false));
    public static Block BLOCK_CONNECTOR_ROD = new Block(5,new BlockSkin(new BlockSkin_TextureLocation(7, 0), false)){{
            setShapes(
                new BlockShape_Cuboid(new float[]{0.2f, 0.2f, 0.2f, 0.2f, 0.2f, 0.2f}),
                new BlockShape_Cuboid(new float[]{0.5f, 0.5f, 0.2f, 0.2f, 0.2f, 0.2f}),
                new BlockShape_Cuboid(new float[]{0.2f, 0.2f, 0.5f, 0.5f, 0.2f, 0.2f}),
                new BlockShape_Cuboid(new float[]{0.2f, 0.2f, 0.2f, 0.2f, 0.5f, 0.5f})
            );
        }

        @Override
        protected int getShapeIndex(BlockChunkControl chunk, Vector3Int location){
            if((chunk.getNeighborBlock_Global(location, Block.Face.Top) !=  null) && (chunk.getNeighborBlock_Global(location, Block.Face.Bottom) !=  null)){
                return 1;
            }
            else if((chunk.getNeighborBlock_Global(location, Block.Face.Left) !=  null) && (chunk.getNeighborBlock_Global(location, Block.Face.Right) !=  null)){
                return 2;
            }
            else if((chunk.getNeighborBlock_Global(location, Block.Face.Front) !=  null) && (chunk.getNeighborBlock_Global(location, Block.Face.Back) !=  null)){
                return 3;
            }
            return 0;
    }};
    public static Block BLOCK_GLASS = new Block(6,new BlockSkin(new BlockSkin_TextureLocation(1, 3), true));
    public static Block BLOCK_STONE = new Block(7,new BlockSkin(new BlockSkin_TextureLocation(0, 1), false));
    public static Block BLOCK_STONE_PILLAR = new Block(8,new BlockSkin(new BlockSkin_TextureLocation(0, 1), false)){{
            setShapes(new BlockShape_Cube(), new BlockShape_Pyramid());
        }

        @Override
        protected int getShapeIndex(BlockChunkControl chunk, Vector3Int location){
            return (chunk.isBlockOnSurface(location)?1:0);
        }
    };
    public static Block BLOCK_WATER = new Block(9,new BlockSkin(new BlockSkin_TextureLocation(14, 0), true));
    public static Block DECO_STONE = new Block(10,new BlockSkin(new BlockSkin_TextureLocation(5, 13), false));
    private static final BlockSkin[] SKINS_SAND = new BlockSkin[]{
        new BlockSkin(new BlockSkin_TextureLocation(0, 11), false),
        new BlockSkin(new BlockSkin_TextureLocation(0, 13), false),
        new BlockSkin(new BlockSkin_TextureLocation(0, 12), false),
        new BlockSkin(new BlockSkin_TextureLocation(0, 12), false),
        new BlockSkin(new BlockSkin_TextureLocation(0, 12), false),
        new BlockSkin(new BlockSkin_TextureLocation(0, 12), false)
    };
    public static Block SAND_STONE = new Block(11,SKINS_SAND);
    public static Block WOOL_BLACK = new Block(12,new BlockSkin(new BlockSkin_TextureLocation(1, 7), false));
    public static Block WOOL_RED = new Block(13,new BlockSkin(new BlockSkin_TextureLocation(1, 8), false));
    public static Block WOOL_GREEN = new Block(14,new BlockSkin(new BlockSkin_TextureLocation(1, 9), false));
    public static Block WOOL_BROWN = new Block(15,new BlockSkin(new BlockSkin_TextureLocation(1, 10), false));
    public static Block WOOL_BLUE = new Block(16,new BlockSkin(new BlockSkin_TextureLocation(1, 11), false));
    public static Block WOOL_PURPLE = new Block(17,new BlockSkin(new BlockSkin_TextureLocation(1, 12), false));
    public static Block WOOL_CYAN = new Block(18,new BlockSkin(new BlockSkin_TextureLocation(1, 13), false));
    public static Block SPAWNER = new Block(19,new BlockSkin(new BlockSkin_TextureLocation(1, 4), true));
    
    private static final BlockSkin[] SKINS_DATA = new BlockSkin[]{
        new BlockSkin(new BlockSkin_TextureLocation(9, 10), false),
        new BlockSkin(new BlockSkin_TextureLocation(9, 10), false),
        new BlockSkin(new BlockSkin_TextureLocation(8, 10), false),
        new BlockSkin(new BlockSkin_TextureLocation(8, 10), false),
        new BlockSkin(new BlockSkin_TextureLocation(8, 10), false),
        new BlockSkin(new BlockSkin_TextureLocation(8, 10), false)
    };
    
    public static Block DATA_BLOCK = new Block(20,SKINS_DATA);
    
    private static final BlockSkin[] SKINS_WAYPOINT = new BlockSkin[]{
        new BlockSkin(new BlockSkin_TextureLocation(11, 10), false),
        new BlockSkin(new BlockSkin_TextureLocation(11, 10), false),
        new BlockSkin(new BlockSkin_TextureLocation(10, 10), false),
        new BlockSkin(new BlockSkin_TextureLocation(10, 10), false),
        new BlockSkin(new BlockSkin_TextureLocation(10, 10), false),
        new BlockSkin(new BlockSkin_TextureLocation(10, 10), false)
    };
    
    public static Block WAYPOINT_BLOCK = new Block(21,SKINS_WAYPOINT);
    private static final BlockSkin[] SKINS_EVENT = new BlockSkin[]{
        new BlockSkin(new BlockSkin_TextureLocation(13, 10), false),
        new BlockSkin(new BlockSkin_TextureLocation(13, 10), false),
        new BlockSkin(new BlockSkin_TextureLocation(12, 10), false),
        new BlockSkin(new BlockSkin_TextureLocation(12, 10), false),
        new BlockSkin(new BlockSkin_TextureLocation(12, 10), false),
        new BlockSkin(new BlockSkin_TextureLocation(12, 10), false)
    };
    
    public static Block EVENT_BLOCK = new Block(22,SKINS_EVENT);
    public static Block BLOCK_BEDROCK = new Block(23,new BlockSkin(new BlockSkin_TextureLocation(1, 1), false));
    
    public static int registerBlocks(){
        BlockManager.register(BLOCK_GRASS);             //1
        BlockManager.register(BLOCK_WOOD);              //2
        BlockManager.register(BLOCK_WOOD_FLAT);         //3
        BlockManager.register(BLOCK_BRICK);             //4
        BlockManager.register(BLOCK_CONNECTOR_ROD);     //5
        BlockManager.register(BLOCK_GLASS);             //6
        BlockManager.register(BLOCK_STONE);             //7
        BlockManager.register(BLOCK_STONE_PILLAR);      //8
        BlockManager.register(BLOCK_WATER);             //9
        BlockManager.register(DECO_STONE);              //10   
        BlockManager.register(SAND_STONE);              //11
        BlockManager.register(WOOL_BLACK);              //12
        BlockManager.register(WOOL_RED);                //13
        BlockManager.register(WOOL_GREEN);              //14   
        BlockManager.register(WOOL_BROWN);              //15
        BlockManager.register(WOOL_BLUE);               //16
        BlockManager.register(WOOL_PURPLE);             //17
        BlockManager.register(WOOL_CYAN);               //18
        BlockManager.register(SPAWNER);                 //19
        BlockManager.register(DATA_BLOCK);              //20
        BlockManager.register(WAYPOINT_BLOCK);          //21
        BlockManager.register(EVENT_BLOCK);             //22
        BlockManager.register(BLOCK_BEDROCK);           //23
        return 23;      //RETURN THE TOTAL NUMBER OF BLOCKS
    }
    
    private static final Vector3f lightDirection = new Vector3f(-0.8f, -1, -0.8f).normalizeLocal();
    
    public static void initializeEnvironment(SimpleApplication simpleApplication){
        DirectionalLight directionalLight = new DirectionalLight();
        directionalLight.setDirection(lightDirection);
        directionalLight.setColor(new ColorRGBA(1f, 1f, 1f, 1.0f));
        simpleApplication.getRootNode().addLight(directionalLight);
        simpleApplication.getRootNode().attachChild(SkyFactory.createSky(simpleApplication.getAssetManager(), "Textures/cubes/sky.jpg", true));
        
        DirectionalLightShadowRenderer directionalLightShadowRenderer = new DirectionalLightShadowRenderer(simpleApplication.getAssetManager(), 2048, 3);
        directionalLightShadowRenderer.setLight(directionalLight);
        directionalLightShadowRenderer.setShadowIntensity(0.3f);
        simpleApplication.getViewPort().addProcessor(directionalLightShadowRenderer);
    }
    
    public static void initializeWater(SimpleApplication simpleApplication){
        WaterFilter waterFilter = new WaterFilter(simpleApplication.getRootNode(), lightDirection);
        getFilterPostProcessor(simpleApplication).addFilter(waterFilter);
    }
    
    private static FilterPostProcessor getFilterPostProcessor(SimpleApplication simpleApplication){
        List<SceneProcessor> sceneProcessors = simpleApplication.getViewPort().getProcessors();
        for(int i=0;i<sceneProcessors.size();i++){
            SceneProcessor sceneProcessor = sceneProcessors.get(i);
            if(sceneProcessor instanceof FilterPostProcessor){
                return (FilterPostProcessor) sceneProcessor;
            }
        }
        FilterPostProcessor filterPostProcessor = new FilterPostProcessor(simpleApplication.getAssetManager());
        simpleApplication.getViewPort().addProcessor(filterPostProcessor);
        return filterPostProcessor;
    }
    
    //Update the max Id EVERYTIME WE ADD NEW BLOCKS
    public static int maxId() {return 23;}
    
    public static Block getBlockFromId(int id) {
        switch(id) {
            case 1:
                return BLOCK_GRASS;             //1
            case 2:
                return BLOCK_WOOD;             //2
            case 3:
                return BLOCK_WOOD_FLAT;         //3
            case 4:
                return BLOCK_BRICK;             //4
            case 5:
                return BLOCK_CONNECTOR_ROD;     //5
            case 6:
                return BLOCK_GLASS;             //6
            case 7:
                return BLOCK_STONE;             //7
            case 8:
                return BLOCK_STONE_PILLAR;      //8
            case 9:
                return BLOCK_WATER;             //9
            case 10:
                return DECO_STONE;              //10   
            case 11:
                return SAND_STONE;              //11
            case 12:
                return WOOL_BLACK;              //12
            case 13:
                return WOOL_RED;                //13
            case 14:
                return WOOL_GREEN;              //14   
            case 15:
                return WOOL_BROWN;              //15
            case 16:
                return WOOL_BLUE;               //16
            case 17:
                return WOOL_PURPLE;             //17
            case 18:
                return WOOL_CYAN;               //18
            case 19:
                return SPAWNER;                 //19
            case 20:
                return DATA_BLOCK;              //20
            case 21:
                return WAYPOINT_BLOCK;          //21
            case 22:
                return EVENT_BLOCK;             //22
            case 23:
                return BLOCK_BEDROCK;           //23
        }
        return null;
    }
    
}

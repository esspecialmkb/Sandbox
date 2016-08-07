/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.app.SimpleApplication;

/**
 *
 * @author Maestro
 */
public class Animator extends SimpleApplication{
    public Character player;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        Animator app = new Animator();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        player = new Character();
        player.loadCharacterModel(this.assetManager);
        player.setRootNode(rootNode);
        //player.initController();
    }
    
    @Override
    public void simpleUpdate(float tpf){
        
    }
}

package Renderer;

import Components.SpriteRenderer;
import Motor.GameObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Renderer {

    private final int MAX_BATCH_SIZE = 1000;
    private List<RenderBatch> batchList;

    public Renderer(){
        this.batchList = new ArrayList<>();
    }
    public void add(GameObject obj){
        SpriteRenderer spriteRenderer = obj.getComponent(SpriteRenderer.class);
        if(spriteRenderer != null){
            add(spriteRenderer);
        }

    }
    private void add(SpriteRenderer spr){
        boolean added= false;
        for (RenderBatch batch : batchList){
            if(batch.hasRoom()){
                Texture tex = spr.getTexture();
                if(batch.hasTexture(tex) || batch.hasTextureRoom() || tex == null){
                    batch.addSprite(spr);
                    added = true;
                    break;
                }

            }
        }
        if(!added){
            RenderBatch newBatch = new RenderBatch(MAX_BATCH_SIZE,spr.gameObject.getzIndex());
            newBatch.start();
            batchList.add(newBatch);
            newBatch.addSprite(spr);
            Collections.sort(batchList);
        }
    }
    public void render(){
        for (RenderBatch batch : batchList){
            batch.render();
        }
    }

}

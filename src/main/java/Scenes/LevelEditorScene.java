package Scenes;

import Components.*;
import Motor.Camera;
import Motor.GameObject;
import Motor.Prefabs;
import Motor.Transform;
import Renderer.DebugDraw;
import Util.AssetPool;
import com.google.gson.Gson;
import imgui.ImGui;
import imgui.ImVec2;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class  LevelEditorScene extends Scene {

    private Spritesheet spritesheet;

    GameObject levelEditorComponents = new GameObject("LevelEditor",new Transform(new Vector2f()),0);

    public LevelEditorScene(){
    }

    @Override
    public void init(){
        levelEditorComponents.addComponent(new MouseControls());
        levelEditorComponents.addComponent(new GridLines());

        loadResources();
        this.camera = new Camera(new Vector2f(0,0));
        spritesheet = AssetPool.getSpritesheet("assets/images/spritesheets/decorationsAndBlocks.png");
    }

    private void loadResources() {
        AssetPool.getShader("assets/shaders/default.glsl");
//        AssetPool.getShader("assets/shaders/pickingShader.glsl");
        AssetPool.addSpritesheet("assets/images/spritesheets/decorationsAndBlocks.png",
                new Spritesheet(AssetPool.getTexture("assets/images/spritesheets/decorationsAndBlocks.png"),
                        16,16,81,0));
        AssetPool.getTexture("assets/images/blendImage1.png");

        for (GameObject g: gameObjects){
            if(g.getComponent(SpriteRenderer.class) != null){
                SpriteRenderer spr = g.getComponent(SpriteRenderer.class);
                if(spr.getTexture() != null){
                    spr.setTexture(AssetPool.getTexture(spr.getTexture().getFilepath()));
                }
            }
        }
    }
    float x,y;
    float angle = 0;
    @Override
    public void update(float dt) {

        angle += 1.5f * dt;
        levelEditorComponents.update(dt);

        x += 50f *dt;
        y += 50f *dt;
        DebugDraw.addCircle(new Vector2f(300,200),50,new Vector3f(1,1,1),1);
        DebugDraw.addBox2D(new Vector2f(200,200),
                new Vector2f(64,32),angle ,new Vector3f(0,1,1),1);

        for (GameObject go : gameObjects){
            go.update(dt);
        }

    }
    public void render(){
        this.renderer.render();
    }
    @Override
    public void imgui(){
        ImGui.begin("World Builder");
        ImVec2 windowsPos = new ImVec2();
        ImGui.getWindowPos(windowsPos);
        ImVec2 windowSize = new ImVec2();
        ImGui.getWindowSize(windowSize);
        ImVec2 itemSpacing = new ImVec2();
        ImGui.getStyle().getItemSpacing(itemSpacing);

        float windowX2 = windowsPos.x + windowSize.x;
        for (int i = 0; i < spritesheet.size(); i++) {
            Sprite sprite = spritesheet.getSprite(i);
            float spriteWidth = sprite.getWidth() * 2;
            float spriteHeight = sprite.getHeight() * 2;
            int id = sprite.getTexId();
            Vector2f[] texCoords = sprite.getTexCoords();

            ImGui.pushID(i);
            if(ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[2].x,texCoords[0].y,texCoords[0].x,texCoords[2].y)){
                GameObject object = Prefabs.generateSpriteObject(sprite,32,32);
                //Attach object to mouse cursor
                levelEditorComponents.getComponent(MouseControls.class).pickupObject(object);
                //System.out.println("button " + (i+1) + " clicked");
            }
            ImGui.popID();
            ImVec2 lastButtonPos = new ImVec2();
            ImGui.getItemRectMax(lastButtonPos);
            float lastButtonX2 = lastButtonPos.x;
            float nextButtonX2 = lastButtonX2 + itemSpacing.x + spriteWidth ;
            if(i + 1 < spritesheet.size() && nextButtonX2 < windowX2){
                ImGui.sameLine();
            }
        }

        ImGui.end();
    }

}

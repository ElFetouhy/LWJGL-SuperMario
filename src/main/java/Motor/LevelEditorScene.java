package Motor;

import Components.Rigidbody;
import Components.Sprite;
import Components.SpriteRenderer;
import Components.Spritesheet;
import Util.AssetPool;
import com.google.gson.Gson;
import imgui.ImGui;
import imgui.ImVec2;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class  LevelEditorScene extends Scene {
    private GameObject obj1;
    private Spritesheet spritesheet;
    private SpriteRenderer obj1SpriteRenderer;
    Gson gson;

    public LevelEditorScene(){
    }

    @Override
    public void init(){
        loadResources();

        this.camera = new Camera(new Vector2f());
        spritesheet = AssetPool.getSpritesheet("assets/images/spritesheets/decorationsAndBlocks.png");

        if(loadedLevel){
            this.activeGameObject = gameObjects.getFirst();
            return;
        }

        // Game object 1
        obj1 = new GameObject("Object 1",
                new Transform( new Vector2f(100,100),
                                new Vector2f(256,256)),-3);
         obj1SpriteRenderer = new SpriteRenderer();
        obj1SpriteRenderer.setColor(new Vector4f(1,1,0,1));
        obj1.addComponent(obj1SpriteRenderer);
        obj1.addComponent(new Rigidbody());
        this.addGameObjectToScene(obj1);
        this.activeGameObject = obj1;

        // Game object 2
        GameObject obj2 = new GameObject("Object 2",
                new Transform( new Vector2f(300,100),
                                new Vector2f(256,256)),-3);
        SpriteRenderer obj2SpriteRender = new SpriteRenderer();
        Sprite obj2Sprite = new Sprite();
        obj2Sprite.setTexture(AssetPool.getTexture("assets/images/blendImage1.png"));
        obj2SpriteRender.setSprite(obj2Sprite);

        obj2.addComponent(obj2SpriteRender);
        this.addGameObjectToScene(obj2);

//        int xOffset = 10;
//        int yOffset = 10;

//        float totalWidth = (float) (600- xOffset * 2);
//        float totalHeight = (float) (300 - yOffset * 2);
//        float sizeX = totalWidth / 100.0f;
//        float sizeY = totalHeight / 100.0f;
//        GameObject gameObject= null;
//        for (int x = 0; x < 100; x++) {
//            for (int y = 0; y < 100; y++) {
//                float xPos = xOffset + (x * sizeX);
//                float yPos = yOffset + (y * sizeY);
//
//                gameObject = new GameObject("Obj" + x +""+ y, new Transform(new Vector2f(xPos,yPos),new Vector2f(sizeX,sizeY)),0);
//                gameObject.addComponent(new SpriteRenderer(new Vector4f(xPos / totalWidth, yPos/ totalHeight,1,1)));
//                this.addGameObjectToScene(gameObject);
//                this.activeGameObject = gameObject;
//            }
//        }

    }

    private void loadResources() {
        AssetPool.getShader("assets/shaders/default.glsl");
        AssetPool.addSpritesheet("assets/images/spritesheets/decorationsAndBlocks.png",
                new Spritesheet(AssetPool.getTexture("assets/images/spritesheets/decorationsAndBlocks.png"),
                        16,16,81,0));
        AssetPool.getTexture("assets/images/blendImage1.png");
    }
    private int spriteIndex = 0;
    private float spriteFlipTime = 0.2f;
    private float spriteFlipTimeLeft = 0.0f;

    @Override
    public void update(float dt) {
        //System.out.println( "@FPS " + (1.0f / dt) +".");
//        spriteFlipTimeLeft -=dt;
//        if(spriteFlipTimeLeft <= 0){
//            spriteFlipTimeLeft = spriteFlipTime;
//            spriteIndex++;
//            if(spriteIndex > 4){
//                spriteIndex =0;
//            }
//            obj1.getComponent(SpriteRenderer.class).setSprite(spritesheet.getSprite(spriteIndex));
//        }
//
//        obj1.transform.position.x += 10 * dt;
        System.out.println(MouseListener.getOrthoX());;
        for (GameObject go : gameObjects){
            go.update(dt);
        }
        this.renderer.render();
    }
    @Override
    public void imgui(){
        ImGui.begin("Test window");
        ImVec2 windowsPos = new ImVec2();
        ImGui.getWindowPos(windowsPos);
        ImVec2 windowSize = new ImVec2();
        ImGui.getWindowSize(windowSize);
        ImVec2 itemSpacing = new ImVec2();
        ImGui.getStyle().getItemSpacing(itemSpacing);

        float windowX2 = windowsPos.x + windowSize.x;
        for (int i = 0; i < spritesheet.size(); i++) {
            Sprite sprite = spritesheet.getSprite(i);
            float spriteWidth = sprite.getWidth() * 4;
            float spriteHeight = sprite.getHeight() * 4;
            int id = sprite.getTexId();
            Vector2f[] texCoords = sprite.getTexCoords();

            ImGui.pushID(i);
            if(ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[0].x,texCoords[0].y,texCoords[2].x,texCoords[2].y)){
                System.out.println("button " + (i+1) + " clicked");
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

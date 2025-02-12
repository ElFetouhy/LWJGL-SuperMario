package Motor;

import Components.Sprite;
import Components.SpriteRenderer;
import Components.Spritesheet;
import Util.AssetPool;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import imgui.ImGui;
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

        spritesheet = AssetPool.getSpritesheet("assets/images/spritesheet.png");

        // Game object 1
        obj1 = new GameObject("Object 1",
                new Transform( new Vector2f(100,100),
                                new Vector2f(256,256)),-3);
         obj1SpriteRenderer = new SpriteRenderer();
        obj1SpriteRenderer.setColor(new Vector4f(1,1,0,1));

        obj1.addComponent(obj1SpriteRenderer);
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


        gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();

        String serialized = gson.toJson(obj1);
        System.out.println(serialized);
        GameObject obj = gson.fromJson(serialized,GameObject.class);
        System.out.println(obj);


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
        AssetPool.addSpritesheet("assets/images/spritesheet.png",
                new Spritesheet(AssetPool.getTexture("assets/images/spritesheet.png"),
                        16,16,26,0));
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

        for (GameObject go : gameObjects){
            go.update(dt);
        }
        this.renderer.render();
    }
    @Override
    public void imgui(){
        ImGui.begin("Test window");
        ImGui.text("Some text");
        ImGui.end();
    }

}

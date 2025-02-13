package Motor;

import Components.Sprite;
import Components.SpriteRenderer;
import org.joml.Vector2f;

public class Prefabs {
    public static GameObject generateSpriteObject(Sprite sprite, float sizeX, float sizeY){

        GameObject block = new GameObject("Sprite_Object_Gen",
                new Transform(new Vector2f(),new Vector2f(sizeX,sizeY)),0);
        SpriteRenderer spriteRenderer = new SpriteRenderer();
        spriteRenderer.setSprite(sprite);
        block.addComponent(spriteRenderer);

        return block;
    }
}

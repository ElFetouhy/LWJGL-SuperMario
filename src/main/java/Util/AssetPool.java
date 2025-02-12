package Util;

import Components.Sprite;
import Components.Spritesheet;
import Renderer.Shader;
import Renderer.Texture;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class AssetPool {
    private static Map<String, Shader> shaders = new HashMap<>();
    private static Map<String, Texture> textures = new HashMap<>();
    private static Map<String, Spritesheet> spritesheets = new HashMap<>();


    public static Shader getShader(String srcName){
        File file = new File(srcName);
        if(AssetPool.shaders.containsKey(file.getAbsolutePath())){
            return AssetPool.shaders.get(file.getAbsolutePath());
        }else{
            Shader shader = new Shader(srcName);
            shader.compile();
            AssetPool.shaders.put(file.getAbsolutePath(), shader);
            return shader;
        }
    }
    public static Texture getTexture(String srcName){
        File file = new File(srcName);
        if(AssetPool.textures.containsKey(file.getAbsolutePath())){
            return AssetPool.textures.get(file.getAbsolutePath());
        }else{
            Texture texture = new Texture();
            texture.init(srcName);
            AssetPool.textures.put(file.getAbsolutePath(),texture);
            return texture;
        }
    }

    public static void addSpritesheet(String srcName, Spritesheet spritesheet){
        File file = new File(srcName);
        if(!AssetPool.spritesheets.containsKey(file.getAbsolutePath())){
             AssetPool.spritesheets.put(file.getAbsolutePath(),spritesheet);
        }
    }
    public static Spritesheet getSpritesheet(String srcName){
        File file = new File(srcName);
        if(!AssetPool.spritesheets.containsKey(file.getAbsolutePath())){
            assert false: "Error: Tried to access spritesheet not in pool";
        }
        return AssetPool.spritesheets.getOrDefault(file.getAbsolutePath(),null);
    }
}

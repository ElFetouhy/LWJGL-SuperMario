package Renderer;

import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Objects;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBImage.*;

public class Texture {
    private String filepath;
    private transient int textureID;
    private int height, width;

    public Texture(){
        textureID = -1;
        height = -1;
        width = -1;
    }
    public Texture(int width, int height){
        this.filepath = "Generated";

        //Gen Texture to GPU
        textureID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D,textureID);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER,GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER,GL_LINEAR);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width, height, 0, GL_RGB, GL_UNSIGNED_BYTE, 0);


    }
    public void init(String filepath){
        this.filepath = filepath;

        //Gen Texture to GPU
        textureID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D,textureID);
        //Set texture parameters
        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_WRAP_S,GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_WRAP_T,GL_REPEAT);
        //When stetching/shrinking the image, pixelate
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER,GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MAG_FILTER,GL_NEAREST);

        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);

        IntBuffer channels = BufferUtils.createIntBuffer(1);
        stbi_set_flip_vertically_on_load(true);
        ByteBuffer image = stbi_load(filepath,width,height,channels,0);

        if(image != null){
            this.width = width.get(0);
            this.height = height.get(0);

            if(channels.get(0) == 3) {
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, this.width, this.height, 0, GL_RGB, GL_UNSIGNED_BYTE, image);
            }else if (channels.get(0) == 4) {
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, this.width, this.height, 0, GL_RGBA, GL_UNSIGNED_BYTE, image);
            }else{
                assert false: "Error: unknown number of channels";
            }
        } else{

            assert false : "Error: (Texture) could not load image";
        }
        stbi_image_free(image);
    }
    public void bind(){
        glBindTexture(GL_TEXTURE_2D,textureID);
    }
    public void unbind(){

        glBindTexture(GL_TEXTURE_2D,0);
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public int getId() {
        return this.textureID;
    }

    public String getFilepath() {
        return filepath;
    }

    @Override
    public boolean equals(Object o) {
        if(o == null) return false;
        if (!(o instanceof Texture texture)) return false;
        return textureID == texture.getId()
                && height == texture.getHeight()
                && width == texture.getWidth()
                && Objects.equals(filepath, texture.getFilepath());
    }


}

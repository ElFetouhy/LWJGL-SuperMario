package Renderer;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL14.GL_DEPTH_COMPONENT32;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;

public class PickingTexture {
    private int pickingTextureId;
    private int fboID;
    private int depthTexture; //3D stuff
    int h,w;
    public PickingTexture(int width,int height){
        this.w = width;
        this.h = height;
        if (!init(width, height)){
            assert false : "Error initializing picking texture";
        }

    }
    public boolean init(int w, int h){
        //Gen
        fboID = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER,fboID);

        //
        pickingTextureId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D,pickingTextureId);

        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_WRAP_S,GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_WRAP_T,GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MAG_FILTER,GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MIN_FILTER,GL_NEAREST);

        glTexImage2D(GL_TEXTURE_2D,0,GL_RGB32F,w,h,0,GL_RGB,GL_FLOAT,0);
        glFramebufferTexture2D(GL_FRAMEBUFFER,GL_COLOR_ATTACHMENT0,GL_TEXTURE_2D,this.pickingTextureId,0);

        //Create texture object for the depth buffer (JUST IN CASE)
        glEnable(GL_DEPTH_TEST);
        depthTexture = glGenTextures();
        glBindTexture(GL_TEXTURE_2D,depthTexture);
        glTexImage2D(GL_TEXTURE_2D,0,GL_DEPTH_COMPONENT, w,h,0,
                GL_DEPTH_COMPONENT,GL_FLOAT,0);
        glFramebufferTexture2D(GL_FRAMEBUFFER,GL_DEPTH_ATTACHMENT,GL_TEXTURE_2D,depthTexture,0);

        //disable reading
        glReadBuffer(GL_NONE);
        glDrawBuffer(GL_COLOR_ATTACHMENT0);

        if(glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE){
            assert false : "Error: Framebuffer is not complete";
            return false;
        }

        // UNbind
        glDisable(GL_DEPTH_TEST);
        glBindTexture(GL_TEXTURE_2D,0);
        glBindFramebuffer(GL_FRAMEBUFFER,0);
        return true;
    }
    public void enableWriting(){
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER,fboID);
    }
    public void disableWriting(){
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER,0);
    }
    public int readPixel(int x ,int y){
        glBindFramebuffer(GL_READ_FRAMEBUFFER,fboID);
        glReadBuffer(GL_COLOR_ATTACHMENT0);

        float[] pixels = new float[3];
        glReadPixels(x,y,1,1,GL_RGB,GL_FLOAT,pixels);

        return (int) (pixels[0]) - 1;
    }

    public int getHeight() {
        return h;
    }

    public int getWidth() {
        return w;
    }
}

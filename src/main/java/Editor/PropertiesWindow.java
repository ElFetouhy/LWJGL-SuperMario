package Editor;

import Motor.GameObject;
import Motor.MouseListener;
import Renderer.PickingTexture;
import Scenes.Scene;
import imgui.ImGui;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class PropertiesWindow {
    private GameObject activeGameObject = null;
    private PickingTexture pickingTexture = null;

    public PropertiesWindow(PickingTexture pickingTexture){
        this.pickingTexture = pickingTexture;
    }
    public void update(float dt, Scene currentScene){

        if(MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)){
            int x = (int)MouseListener.getScreenX();
            int y = (int)MouseListener.getScreenY();
            int gameObjectID = pickingTexture.readPixel(x,y);
            //if (!(x < 0 || x > pickingTexture.getWidth() || y < 0 || y > pickingTexture.getHeight()))
            activeGameObject = currentScene.getGameObject(gameObjectID);
        }
    }
    public void imgui(){
        if(activeGameObject != null){
            ImGui.begin("Properties");
            activeGameObject.imgui();
            ImGui.end();
        }
    }
}

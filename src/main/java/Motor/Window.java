package Motor;

import Renderer.Shader;
import Renderer.DebugDraw;
import Renderer.Framebuffer;
import Renderer.PickingTexture;
import Renderer.Renderer;
import Scenes.LevelEditorScene;
import Scenes.LevelScene;
import Scenes.Scene;
import Util.AssetPool;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.system.MemoryUtil.NULL;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL.*;
import static org.lwjgl.opengl.GL11.*;

public class Window {
    private int width, height;
    private String title;
    private long glfwWindow;
    public float r,g,b,a;
    private static Window window = null;
    private static Scene currentScene;

    private ImGuiLayer imGuiLayer;
    private Framebuffer framebuffer;
    private PickingTexture pickingTexture;

    private Window(){
        this.width = 1920;
        this.height = 1080;
        this.title = "Mario";
        r=g=b=a=0.4f;
    }

    public static void changeScene(int newScene){
        switch (newScene){
            case 0:
                currentScene = new LevelEditorScene();
                break;
            case 1:
                currentScene = new LevelScene();
                break;
            default:
                assert false: "'Unknown scene" + newScene +"'";
                break;
        }
        currentScene.load();
        currentScene.init();
        currentScene.start();
    }

    public void run(){
        System.out.println("Hellow LWJGL " + Version.getVersion() + "!");
        init();
        loop();

        // Free the memory
        glfwFreeCallbacks(glfwWindow);
        glfwDestroyWindow(glfwWindow);

        //Terminate GLFW and the free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void init() {
        // set an err callback
        GLFWErrorCallback.createPrint(System.err).set();

        //ini GLFW
        if(!glfwInit()){
            throw new IllegalStateException("Unable to initalize GLFW.");
        }

        //confg GLFW
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR,3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR,3);
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_MAXIMIZED,GLFW_TRUE);


        //Create the window
        glfwWindow = glfwCreateWindow(this.width, this.height, this.title,NULL,NULL);
        if(glfwWindow == NULL){
            throw new IllegalStateException("Failed to create the GLFW window.");
        }

        glfwSetCursorPosCallback(glfwWindow, MouseListener::mousePosCallback);
        glfwSetMouseButtonCallback(glfwWindow, MouseListener::mouseButtonCallback);
        glfwSetScrollCallback(glfwWindow, MouseListener::mouseScrollCallback);
        glfwSetKeyCallback(glfwWindow, KeyListener::keyCallback);
        glfwSetWindowSizeCallback(glfwWindow, (w,newWidth,newHeight) -> {
            Window.setWidth(newWidth);
            Window.setHeight(newHeight);
        });
        //MAke OpenGL context curret
        glfwMakeContextCurrent(glfwWindow);

        //V-sync
        glfwSwapInterval(1);

        //make window visible
        glfwShowWindow(glfwWindow);

        //¡¡Importante!!
        createCapabilities();

        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);

        this.framebuffer = new Framebuffer(1920,1080);
        this.pickingTexture = new PickingTexture(1920,1080);
        glViewport(0,0,1920,1080);

        this.imGuiLayer = new ImGuiLayer(glfwWindow,pickingTexture);
        this.imGuiLayer.initImGui();

        Window.changeScene(0);
    }

    private void loop() {
        float beginTime = (float) glfwGetTime();
        float endTime;
        float dt = -1.0f;

        Shader defaultShader = AssetPool.getShader("assets/shaders/default.glsl");
        Shader pickingShader = AssetPool.getShader("assets/shaders/pickingShader.glsl");

        while(!glfwWindowShouldClose(glfwWindow)){
            //Poll events
            glfwPollEvents();

            // REnder pass 1. Render to picking texture
            glDisable(GL_BLEND);
            pickingTexture.enableWriting();

            glViewport(0,0,1920,1080);
            glClearColor(0.0f,0.0f,0.0f,0.0f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            Renderer.bindShader(pickingShader);
            currentScene.render();
            if(MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)){
                int x = (int)MouseListener.getScreenX();
                int y = (int)MouseListener.getScreenY();

                System.out.println(pickingTexture.readPixel(x,y));
            }
            pickingTexture.disableWriting();
            glEnable(GL_BLEND);

            // Render pass 2. Render actual game
            DebugDraw.beginFrame();

            this.framebuffer.bind();
            glClearColor(r, g, b, a);
            glClear(GL_COLOR_BUFFER_BIT);

            if(dt >= 0) {
                Renderer.bindShader(defaultShader);
                DebugDraw.draw();
                currentScene.update(dt);
                currentScene.render();
            }
            this.framebuffer.unbind();

            imGuiLayer.update(dt, currentScene);
            glfwSwapBuffers(glfwWindow);
            MouseListener.endFrame();

            endTime = (float) glfwGetTime();
            dt = endTime - beginTime;
            beginTime = endTime;
        }
        currentScene.saveExit();
    }

    public static int getWidth() {
        return get().width;
    }
    public static int getHeight() {
        return get().height;
    }
    public static Window get(){
        if(Window.window == null)
            Window.window = new Window();

        return Window.window;
    }
    public static Scene getScene(){
        return get().currentScene;
    }
    public static Framebuffer getFramebuffer() {
        return get().framebuffer;
    }
    public static float getTargetAspectRatio(){
        return (float) 16/9;
    }
    private static void setHeight(int newHeight) {
        get().height =  newHeight;
    }
    private static void setWidth(Object newWidth) {
        get().width =(int)  newWidth;
    }


}

package Motor;

import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
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
                currentScene.init();
                currentScene.start();
                break;
            case 1:
//                currentScene = new LevelScene();
//                currentScene.init();
//                currentScene.start();
                break;
            default:
                assert false: "'Unknown scene" + newScene +"'";
                break;
        }
    }

    public static Window get(){
        if(Window.window == null)
            Window.window = new Window();

        return Window.window;
    }
    public static Scene getScene(){
        return get().currentScene;
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


        this.imGuiLayer = new ImGuiLayer(glfwWindow);
        this.imGuiLayer.initImGui();

        Window.changeScene(0);

    }

    private static void setHeight(int newHeight) {
        get().height =  newHeight;
    }

    private static void setWidth(Object newWidth) {
        get().width =(int)  newWidth;
    }

    private void loop() {
        float beginTime = (float) glfwGetTime();
        float endTime;
        float dt = -1.0f;

        while(!glfwWindowShouldClose(glfwWindow)){
            //Poll events
            glfwPollEvents();

            glClearColor(r, g, b, a);
            glClear(GL_COLOR_BUFFER_BIT);

            if(dt >= 0) {
                currentScene.update(dt);
            }

            imGuiLayer.update(dt, currentScene);

            glfwSwapBuffers(glfwWindow);


            endTime = (float) glfwGetTime();
            dt = endTime - beginTime;
            beginTime = endTime;
        }
    }

    public static int getWidth() {
        return get().width;
    }

    public static int getHeight() {
        return get().height;
    }


}

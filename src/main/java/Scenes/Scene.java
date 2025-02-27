package Scenes;
import Components.Component;
import Components.ComponentDeserializer;
import Motor.Camera;
import Motor.GameObject;
import Motor.GameObjectDeserializer;
import Renderer.Renderer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import imgui.ImGui;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class Scene {
    protected Renderer renderer = new Renderer();
    protected Camera camera;
    private boolean isRunning = false;
    protected List<GameObject> gameObjects= new ArrayList<>();
    protected boolean loadedLevel = false;

    public Scene(){}
    public void init(){

    }
    public void start(){
            for (GameObject go : gameObjects){
                go.start();
                this.renderer.add(go);
            }
            isRunning = true;
    }
    public void addGameObjectToScene(GameObject go){
        if(!isRunning){
            gameObjects.add(go);
        }else{
            gameObjects.add(go);
            go.start();
            this.renderer.add(go);
        }
    }
    public GameObject getGameObject(int gameObjectId){
        Optional<GameObject> result = this.gameObjects.stream()
                .filter(gameObject -> gameObject.getUid() == gameObjectId)
                .findFirst();
        return result.orElse(null);
    }

    public abstract void update(float dt);
    public abstract void render();
    public Camera camera(){
        return this.camera;
    }

    public void imgui(){

    }
    public void saveExit(){
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Component.class, new ComponentDeserializer())
                .registerTypeAdapter(GameObject.class, new GameObjectDeserializer())
                .create();

        try{
            FileWriter writer = new FileWriter("level.txt");
            writer.write(gson.toJson(this.gameObjects));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void load(){
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Component.class, new ComponentDeserializer())
                .registerTypeAdapter(GameObject.class, new GameObjectDeserializer())
                .create();

        String inFile = "";


        try{
            inFile = new String(Files.readAllBytes(Paths.get("level.txt")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(!inFile.isEmpty()){
            int maxGoId = -1;
            int maxCompId = -1;
            GameObject[] objects = gson.fromJson(inFile, GameObject[].class);
            for (int i = 0; i < objects.length; i++) {

                addGameObjectToScene(objects[i]);
                for (Component c : objects[i].getAllComponents()){
                    if(c.getUid() > maxCompId){
                        maxCompId = c.getUid();
                    }
                }
                if(objects[i].getUid() > maxGoId){
                    maxGoId = objects[i].getUid();
                }
            }
            maxGoId++;
            maxCompId++;
            System.out.println(maxGoId);
            System.out.println(maxCompId);
            GameObject.init(maxGoId);
            Component.init(maxCompId);

            this.loadedLevel = true;
        }
    }
}

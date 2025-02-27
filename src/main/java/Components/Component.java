package Components;


import Motor.GameObject;
import imgui.ImGui;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public abstract class Component {
    private static int ID_COUNTER = 0;
    private int uid = -1;
    public transient GameObject gameObject = null;

    public void update(float dt){

    }
    public void start(){

    }

    public void imgui() {
        try{
            Field[]  fields = this.getClass().getDeclaredFields();
            for(Field field : fields){
                if(Modifier.isTransient(field.getModifiers())){
                    continue;
                }
                boolean isPrivate = Modifier.isPrivate(field.getModifiers());
                if(isPrivate){
                    field.setAccessible(true);
                }
                Class type = field.getType();
                Object value = field.get(this);
                String name = field.getName();

                if(type == int.class){
                    int val = (int) value;
                    int[] imInt = {val};
                    if(ImGui.dragInt(name + ": ", imInt)){
                        field.set(this, imInt[0]);
                    }
                } else if (type == float.class) {
                    float val = (float) value;
                    float[] imFloat = {val};
                    if(ImGui.dragFloat(name +": ",imFloat)){
                        field.set(this, imFloat[0]);
                    }
                } else if (type == boolean.class) {
                    boolean val = (boolean) value;
                    if(ImGui.checkbox(name +": ",val)){
                        val = !val;
                        field.set(this,!val);
                    }
                } else if (type == Vector3f.class) {
                    Vector3f val = (Vector3f) value;
                    float[] imVec3F = {val.x,val.y,val.z};
                    if(ImGui.dragFloat3(name + ": ",imVec3F)){
                        val.set(imVec3F[0],imVec3F[1],imVec3F[2]);
                    }
                }else if (type == Vector4f.class) {
                    Vector4f val = (Vector4f) value;
                    float[] imVec4F = {val.x,val.y,val.z,val.w};
                    if(ImGui.dragFloat4(name + ": ",imVec4F)){
                        val.set(imVec4F[0],imVec4F[1],imVec4F[2],imVec4F[3]);
                    }
                }
                if(isPrivate){
                    field.setAccessible(false);
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void generateId(){
        if(this.uid == -1){
            this.uid = ID_COUNTER++;
        }
    }

    public int getUid() {
        return this.uid;
    }
    public static void init(int maxId){
        ID_COUNTER = maxId;
    }


}

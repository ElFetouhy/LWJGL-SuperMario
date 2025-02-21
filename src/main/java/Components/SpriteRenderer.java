package Components;

import Motor.Transform;
import Renderer.Texture;
import imgui.ImGui;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class SpriteRenderer extends Component {

    private Vector4f color = new Vector4f(1,1,1,1);;
    private Sprite sprite = new Sprite();

    private transient Transform lastTransform;
    private transient boolean isDirty = true;

    public SpriteRenderer(){}
//    public SpriteRenderer(Vector4f color){
//        this.color = color;
//        this.isDirty = true;
//    }
//    public SpriteRenderer(Sprite sprite){
//        this.sprite = sprite;
//        this.isDirty = true;
//    }

    @Override
    public void start() {
        this.lastTransform = gameObject.transform.copy();
    }

    @Override
    public void update(float dt) {
        if (!this.lastTransform.equals(this.gameObject.transform)){
            this.gameObject.transform.copy(this.lastTransform);
            isDirty = true;
        }
    }
    @Override
    public void imgui(){
        float[] imColors = {color.x,color.y,color.z,color.w};
        ImGui.text("Color Picker");
        if(ImGui.colorPicker4("Color picker: ", imColors)){
            this.color.set(imColors[0],imColors[1],imColors[2],imColors[3]);
            this.isDirty=true;
        }

    }

    public Vector4f getColor(){
        return this.color;
    }
    public Texture getTexture() {
        return sprite.getTexture();
    }
    public Vector2f[] getTexCoords(){
       return sprite.getTexCoords();
    }

    public void setSprite(Sprite sprite){
        this.sprite = sprite;
        this.isDirty = true;
    }

    public void setColor(Vector4f color) {
        if(!this.color.equals(color)) {
            this.isDirty = true;
            this.color = color;
        }
    }

    public boolean isDirty() {
        return this.isDirty;
    }


    public void setClean() {
        this.isDirty = false;
    }

    public void setTexture(Texture texture){
        this.sprite.setTexture(texture);
    }
}

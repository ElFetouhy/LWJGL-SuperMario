package Components;

import Motor.Camera;
import Motor.KeyListener;
import Motor.MouseListener;
import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_KP_DECIMAL;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_MIDDLE;

public class EditorCamera extends Component {
    private float dragDebounce = 0.01f;
    private Camera levelEditorCamera;
    private Vector2f clickOrigin;
    private boolean reset;

    private float dragSensitivity = 30.f;
    private float scrollSensitivity = 0.05f;
    private float lerpTime = 0.0f;

    public EditorCamera(Camera levelEditorCamera){
        this.levelEditorCamera = levelEditorCamera;
        this.clickOrigin = new Vector2f();
    }

    @Override
    public void update(float dt){
        if(MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_MIDDLE)
        && dragDebounce > 0){
            this.clickOrigin = new Vector2f(MouseListener.getOrthoX(), MouseListener.getOrthoY());
            dragDebounce -= dt;
            return;
        } else if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_MIDDLE)){
            Vector2f mousePos  = new Vector2f(MouseListener.getOrthoX(),MouseListener.getOrthoY());
            Vector2f distancia = new Vector2f(mousePos).sub(this.clickOrigin);
            levelEditorCamera.position.sub(distancia.mul(dt).mul(dragSensitivity));
            this.clickOrigin.lerp(mousePos, dt);
        }

        if(dragDebounce <= 0.0f && !MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_MIDDLE)){
            dragDebounce = 0.1f;
        }

        if(MouseListener.getScrollY() != 0.0f){
            float addValue = (float) Math.pow(Math.abs(MouseListener.getScrollY() * scrollSensitivity),
                    1 / levelEditorCamera.getZoom());
            addValue *= -Math.signum(MouseListener.getScrollY());
            levelEditorCamera.addZoom(addValue);
        }
        if(KeyListener.isKeyPressed(GLFW_KEY_KP_DECIMAL)){
            reset = true;
        }
        if(reset){
            levelEditorCamera.position.lerp(new Vector2f(),lerpTime);
            levelEditorCamera.setZoom(levelEditorCamera.getZoom() +
                    ((1.0f - levelEditorCamera.getZoom()) * lerpTime));
            lerpTime += 0.1f * dt;
            if(Math.abs(levelEditorCamera.position.x) <= 5.0f &&
                Math.abs(levelEditorCamera.position.y) >= 5.0f){
                this.lerpTime =0;
                levelEditorCamera.position.set(0f,0f);
                levelEditorCamera.setZoom(0.0f);
                reset = false;
            }
        }


    }
}

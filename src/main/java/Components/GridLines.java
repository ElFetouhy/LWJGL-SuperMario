package Components;

import Motor.Camera;
import Motor.Window;
import Renderer.DebugDraw;
import Util.Settings;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class GridLines extends Component{
    @Override
    public void update(float dt) {
        Camera camera = Window.getScene().camera();

        Vector2f cameraPos = camera.position;
        Vector2f projectionSize =  camera.getProjectionSize();

        int firstX = ((int ) (cameraPos.x / Settings.GRID_WIDTH) -1) * Settings.GRID_WIDTH;
        int firstY = ((int ) (cameraPos.y / Settings.GRID_HEIGHT) -1) * Settings.GRID_HEIGHT;

        int numVertLines = (int)(projectionSize.x * camera.getZoom()/ Settings.GRID_WIDTH) +2;
        int numHortLines = (int)(projectionSize.y * camera.getZoom()/ Settings.GRID_HEIGHT) +2;

        int width = (int) (projectionSize.x * camera.getZoom()) + Settings.GRID_WIDTH *2;
        int height = (int)( projectionSize.y * camera.getZoom()) + Settings.GRID_HEIGHT *2;

        int maxLines = Math.max(numVertLines,numHortLines);

        for (int i = 0; i < maxLines; i++) {
            int x = firstX + (Settings.GRID_WIDTH * i);
            int y = firstY + (Settings.GRID_HEIGHT * i);

            if( i < numVertLines){
                DebugDraw.addLine2D(new Vector2f(x,firstY),new Vector2f(x,  height),new Vector3f(0,0,0));
            }

            if( i < numVertLines){
                DebugDraw.addLine2D(new Vector2f(firstX,y),new Vector2f( width,y),new Vector3f(0,0,0));
            }
        }

    }
}

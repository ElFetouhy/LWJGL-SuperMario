package Renderer;

import Components.SpriteRenderer;
import Motor.Window;
import Util.AssetPool;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL30.*;

public class RenderBatch implements Comparable<RenderBatch> {
    /*//Vertex
    *
    * Pos            Color                          Texture_Coords Texid
    * float, float   float, float, float, float     float, float    float
    */
    private final int POSITION_SIZE = 2;
    private final int COLOR_SIZE = 4;
    private final int TEX_COORDS_SIZE = 2;
    private final int TEX_ID_SIZE = 1;
    private final int ENTITY_ID_SIZE = 1;
    private final int VERTEX_SIZE = 10;

    private final int POSITION_OFFSET = 0;
    private final int COLOR_OFFSET = POSITION_OFFSET + POSITION_SIZE * Float.BYTES;
    private final int TEX_COORDS_OFFSET = COLOR_OFFSET + COLOR_SIZE * Float.BYTES;
    private final int TEX_ID_OFFSET = TEX_COORDS_OFFSET + TEX_COORDS_SIZE * Float.BYTES;
    private final int ENTITY_ID_OFFSET = TEX_ID_OFFSET + TEX_ID_SIZE * Float.BYTES;
    private final int VERTEX_SIZE_BYTES = VERTEX_SIZE * Float.BYTES;

    private SpriteRenderer[] sprites;
    private int numSprites;
    private boolean hasRoom;
    private float[] vertices;
    private int[] texSlots ={0,1,2,3,4,5,6,7};

    private List<Texture> textureList;
    private int vaoID, vboID;
    private int maxBatchSize;
    private int zIndex;

    public RenderBatch(int maxBatchSize,int zIndex) {
        this.zIndex = zIndex;
        this.sprites = new SpriteRenderer[maxBatchSize];
        this.maxBatchSize = maxBatchSize;

        //4 vertices quads
        vertices = new float[maxBatchSize * 4 * VERTEX_SIZE];

        this.numSprites =0;
        this.hasRoom = true;
        this.textureList = new ArrayList<>();
    }

    public void start(){
        // Generate and bind Vertex Array Object
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        // Allocate space for vertices
        vboID =glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER,vboID);
        glBufferData(GL_ARRAY_BUFFER, (long) vertices.length * Float.BYTES,GL_DYNAMIC_DRAW);

        // Create and upload index Buffer eboID == ibID
        int ibID = glGenBuffers();
        int[] indices = generateIndices();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER,ibID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER,indices,GL_STATIC_DRAW);

        // Enable buffer pointers
        //for position
        glVertexAttribPointer(0,POSITION_SIZE,GL_FLOAT, false, VERTEX_SIZE_BYTES,POSITION_OFFSET);
        glEnableVertexAttribArray(0);
        // for color now
        glVertexAttribPointer(1,COLOR_SIZE,GL_FLOAT,false, VERTEX_SIZE_BYTES,COLOR_OFFSET);
        glEnableVertexAttribArray(1);
        //for texture coord
        glVertexAttribPointer(2,TEX_COORDS_SIZE,GL_FLOAT,false, VERTEX_SIZE_BYTES,TEX_COORDS_OFFSET);
        glEnableVertexAttribArray(2);
        // for texture id
        glVertexAttribPointer(3,TEX_ID_SIZE,GL_FLOAT,false, VERTEX_SIZE_BYTES,TEX_ID_OFFSET);
        glEnableVertexAttribArray(3);

        glVertexAttribPointer(4,ENTITY_ID_SIZE,GL_FLOAT,false,VERTEX_SIZE_BYTES,ENTITY_ID_OFFSET);
        glEnableVertexAttribArray(4);
    }
    public void addSprite(SpriteRenderer spr){
        int index = this.numSprites;
        this.sprites[index] = spr;
        this.numSprites++;

        if(spr.getTexture() != null){
            if(!textureList.contains(spr.getTexture())){
                textureList.add(spr.getTexture());
                //
            }
        }

        // Add properties to local vertices array
        loadVertexProperties(index);
        if(numSprites >= this.maxBatchSize){
            this.hasRoom = false;
        }
    }
    public void render(){
        boolean rebufferData = false;
        for (int i = 0; i < numSprites; i++) {
            SpriteRenderer spriteRenderer = sprites[i];
            if(spriteRenderer.isDirty()){
                loadVertexProperties(i);
                spriteRenderer.setClean();
                rebufferData = true;
            }
        }

        if(rebufferData){
            glBindBuffer(GL_ARRAY_BUFFER, vboID);
            glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);
        }
        // Use shader
        Shader shader = Renderer.getBoundShader();
        shader.uploadMat4f("uProjection", Window.getScene().camera().getProjectionMatrix());
        shader.uploadMat4f("uView", Window.getScene().camera().getViewMatrix());
        for (int i = 0; i < textureList.size(); i++) {
            glActiveTexture(GL_TEXTURE0 + i + 1);
            textureList.get(i).bind();
        }
        shader.uploadIntArray("uTextures",texSlots);

        glBindVertexArray(vaoID);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES, this.numSprites * 6, GL_UNSIGNED_INT,0);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);

        for (int i = 0; i < textureList.size(); i++) {
            textureList.get(i).unbind();
        }

        shader.detach();
    }
    public void loadVertexProperties(int index){
        SpriteRenderer sprite = this.sprites[index];

        // find offset
        int offset = index * 4 * VERTEX_SIZE;

        // flaot float      float float float float
        Vector4f color = sprite.getColor();

        Vector2f[] texCoords = sprite.getTexCoords();

        int textureID = 0;
        if(sprite.getTexture() != null){
            for (int i = 0; i < textureList.size(); i++) {
                if(textureList.get(i).equals(sprite.getTexture())){
                    textureID = i + 1;
                    break;
                }
            }
        }

        // add vertice with apporpieate properties
        float xAdd = 1.0f;
        float yAdd = 1.0f;
        for (int i = 0; i < 4; i++) {
            if( i == 1){
                yAdd = 0.0f;
            }else if(i ==2){
                xAdd = 0.0f;
            }else if(i == 3){
                yAdd = 1.0f;
            }
            //load position
            vertices[offset] = sprite.gameObject.transform.position.x + (xAdd * sprite.gameObject.transform.scale.x);
            vertices[offset +1] = sprite.gameObject.transform.position.y + (yAdd * sprite.gameObject.transform.scale.y);

            // load color
            vertices[offset + 2] = color.x;
            vertices[offset + 3] = color.y;
            vertices[offset + 4] = color.z;
            vertices[offset + 5] = color.w;

            //load texture coordinates
            vertices[offset + 6] = texCoords[i].x;
            vertices[offset + 7] = texCoords[i].y;
            //load texture id
            vertices[offset + 8] = textureID;
            //load entity id
            vertices[offset + 9] = sprite.gameObject.getUid() + 1;

            offset += VERTEX_SIZE;
        }

    }
    public int[] generateIndices(){
        // 6 Indices per quad ( 3 per triangle )
        int[] elements = new int[6 * maxBatchSize];
        for (int i = 0; i < maxBatchSize; i++) {
            loadElementIndices(elements,i);
        }
        return elements;
    }
    private void loadElementIndices(int[] elements, int index) {
        int offsetArrayIndex = 6 * index;
        int offset = 4 * index;

        // 3 , 2 , 0, 0, 2, 1     || 7, 6, 4, 4, 6, 5
        // Triangle 1
        elements[offsetArrayIndex] = offset + 3;
        elements[offsetArrayIndex + 1] = offset + 2;
        elements[offsetArrayIndex + 2] = offset + 0;
        //triangle 2
        elements[offsetArrayIndex + 3] = offset + 0;
        elements[offsetArrayIndex + 4] = offset + 2;
        elements[offsetArrayIndex + 5] = offset + 1;


    }
    public boolean hasRoom(){
        return this.hasRoom;
    }
    public boolean hasTextureRoom(){
        return this.textureList.size()  < 8;
    }
    public boolean hasTexture(Texture tex){
        return this.textureList.contains(tex);
    }
    public int zIndex() {
        return zIndex;
    }
    @Override
    public int compareTo(RenderBatch o) {
        return Integer.compare(this.zIndex, o.zIndex());
    }
}

package Renderer;

import org.joml.*;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glGetProgramInfoLog;

public class Shader {
    private int shaderProgramID;
    private boolean beingUsed = false;
    private String vertexSource,fragmentSource;
    private String filepath;

    public Shader(String filepath){
        this.filepath = filepath;
        try {
            String source = new String(Files.readAllBytes(Paths.get(filepath)));
            String[] splitString = source.split("(#type)( )+([a-zA-Z]+)");

            int index = source.indexOf("#type")+6;
            int eol = source.indexOf("\r\n",index);
            String firstPattern = source.substring(index,eol).trim();

            index = source.indexOf("#type",eol)+6;
            eol = source.indexOf("\r\n",index);
            String secondPatter = source.substring(index,eol).trim();

            if(firstPattern.equals("vertex")) {
                vertexSource = splitString[1];
            }else if(firstPattern.equals("fragment")){
                fragmentSource = splitString[1];
            }else{
                throw new IOException("Unexpected token 1");
            }

            if(secondPatter.equals("vertex")) {
                vertexSource = splitString[2];
            }else if(secondPatter.equals("fragment")){
                fragmentSource = splitString[2];
            }else{
                throw new IOException("Unexpected token 2");
            }


        }catch (IOException e ){
            e.printStackTrace();
            assert false: "Error: Could not open file for shader: '"+filepath+"'.";
        }
    }
    public void compile(){

        int vertexID,fragmentID;
        int success;
        //Frist load and compile vertex shader
        vertexID = glCreateShader(GL_VERTEX_SHADER);
        //Pass the shader source to the GPU
        glShaderSource(vertexID, vertexSource);
        glCompileShader(vertexID);

        //Check for erros in compilation
        success = glGetShaderi(vertexID, GL_COMPILE_STATUS);
        if(success == GL_FALSE){
            int len = glGetShaderi(vertexID, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: defaultShader.glsl \n Vertex shader compilation failed");
            System.out.println(glGetShaderInfoLog(vertexID,len));
            assert false: "";
        }

        //Load and compile fragment shader
        fragmentID = glCreateShader(GL_FRAGMENT_SHADER);
        //Pass the shader source to the GPU
        glShaderSource(fragmentID, fragmentSource);
        glCompileShader(fragmentID);
        //Check for erros in compilation
        success = glGetShaderi(fragmentID, GL_COMPILE_STATUS);
        if(success == GL_FALSE){
            int len = glGetShaderi(fragmentID, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: "+filepath+" \n Fragment shader compilation failed");
            System.out.println(glGetShaderInfoLog(fragmentID,len));
            assert false: "";
        }

        //Link shaders
        shaderProgramID = glCreateProgram();
        glAttachShader(shaderProgramID, vertexID);
        glAttachShader(shaderProgramID, fragmentID);
        glLinkProgram(shaderProgramID);

        success = glGetProgrami(shaderProgramID, GL_LINK_STATUS);
        if(success == GL_FALSE){
            int len = glGetProgrami(shaderProgramID, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: "+filepath+" \n Linking of shaders failed");
            System.out.println(glGetProgramInfoLog(shaderProgramID,len));
            assert false: "";
        }
    }
    public void use(){
        if(!beingUsed) {
            //Bind shader
            glUseProgram(shaderProgramID);
            beingUsed = true;
        }
    }
    public void detach(){
        glUseProgram(0);
        beingUsed = false;
    }
    public void uploadMat4f(String name, Matrix4f matrix4f){
        int varLocation = glGetUniformLocation(shaderProgramID,name);
        FloatBuffer matBuffer = BufferUtils.createFloatBuffer(16);
        matrix4f.get(matBuffer);
        glUniformMatrix4fv(varLocation, false, matBuffer);

    }
    public void uploadMat3f(String name, Matrix3f matrix3f){
        int varLocation = glGetUniformLocation(shaderProgramID,name);
        FloatBuffer matBuffer = BufferUtils.createFloatBuffer(9);
        matrix3f.get(matBuffer);
        glUniformMatrix4fv(varLocation, false, matBuffer);

    }
    public void uploadVec4f(String varName, Vector4f vec4f)
    {
        int varLocation = glGetUniformLocation(shaderProgramID,varName);
        use();
        glUniform4f(varLocation,vec4f.x,vec4f.y, vec4f.z, vec4f.w);
    }
    public void uploadVec3f(String varName, Vector3f vec3f)
    {
        int varLocation = glGetUniformLocation(shaderProgramID,varName);
        use();
        glUniform3f(varLocation,vec3f.x,vec3f.y,vec3f.z);
    }
    public void uploadVec2f(String varName, Vector2f vec2f)
    {
        int varLocation = glGetUniformLocation(shaderProgramID,varName);
        use();
        glUniform2f(varLocation,vec2f.x,vec2f.y);
    }
    public void uploadFloat(String varName, float val){
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform1f(varLocation,val);
    }
    public void uploadInt(String varName, int val){
        int varLocation = glGetUniformLocation(shaderProgramID,varName);
        use();
        glUniform1i(varLocation,val);
    }
    public void uploadTexture(String varName, int slot){
        int varLocation = glGetUniformLocation(shaderProgramID,varName);
        use();
        glUniform1i(varLocation,slot);
    }
    public void uploadIntArray(String varName, int[] array){
        int varLocation = glGetUniformLocation(shaderProgramID,varName);
        use();
        glUniform1iv(varLocation,array);
    }

}

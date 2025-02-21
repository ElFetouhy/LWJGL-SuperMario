package Util;

import org.joml.Matrix2f;
import org.joml.Vector2f;

import static org.joml.Math.cos;
import static org.joml.Math.sin;

public class JMath {
    public static void rotate( Vector2f vertice,  Vector2f center,float angulo){

        Matrix2f rotationMatrix =new Matrix2f(
            1* (float)cos( Math.toRadians(angulo)),-1*(float)sin( Math.toRadians(angulo)),
            1*(float)sin( Math.toRadians(angulo)),1*(float)cos( Math.toRadians(angulo))
        );
        vertice.sub(center); //normal
        vertice.mul(rotationMatrix);
        vertice.add(center); // Vertice normal rotado
    }
}

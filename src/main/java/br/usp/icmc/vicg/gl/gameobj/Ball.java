package br.usp.icmc.vicg.gl.gameobj;

import br.usp.icmc.vicg.gl.app.PoolGame;
import br.usp.icmc.vicg.gl.jwavefront.JWavefrontObject;
import br.usp.icmc.vicg.gl.matrix.Matrix4;
import br.usp.icmc.vicg.gl.util.Shader;

import javax.media.opengl.GL3;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

// diametro = 6 cm
public class Ball extends Actor {
    private JWavefrontObject model;
    private Matrix4 modelMatrix;
    private Matrix4 rotationMatrix;
    private static final String[] paths = {
            "./data/balls/ball00.obj",
            "./data/balls/ball01.obj",
            "./data/balls/ball02.obj",
            "./data/balls/ball03.obj",
            "./data/balls/ball04.obj",
            "./data/balls/ball05.obj",
            "./data/balls/ball06.obj",
            "./data/balls/ball07.obj",
            "./data/balls/ball08.obj",
            "./data/balls/ball09.obj",
            "./data/balls/ball10.obj",
            "./data/balls/ball11.obj",
            "./data/balls/ball12.obj",
            "./data/balls/ball13.obj",
            "./data/balls/ball14.obj",
            "./data/balls/ball15.obj"};
    private int ID;
    private final float radius = 0.03f;

    private float xSpeed;
    private float zSpeed;
    private float speed;
    private float theta;
    private float[] direction;
    private float[] velocity;

    private float[] alternateX;
    private float[] alternateY;
    private float[] alternateZ;

    public int getID() {
        return ID;
    }

    public Ball(float x, float y, float z, int ID) {
        super(x, y, z);
        this.ID = ID;
        setSize(radius, radius, radius);
        xSpeed = 2;
        zSpeed = 0;
    }
/*
    private float[] getNormal(float[] vector) {
        float denominator = vector[0] * vector[0] + vector[1] * vector[1] + vector[2] * vector[2];
        denominator = (float) Math.sqrt((double) denominator);
        return new float[]{vector[0] / denominator, vector[1] / denominator, vector[2] / denominator};
    }

    private float[] calculateRotationAxis(float[] direction) {
        return new float[]{
                direction[2],
                0,
                -direction[0]};
    }

    private float[] crossProduct(float[] vector1, float[] vector2) {
        return new float[]{
                vector1[1] * vector2[2] - (vector1[2] * vector2[1]),
                vector1[2] * vector2[0] - (vector1[0] * vector2[2]),
                vector1[0] * vector2[1] - (vector1[1] * vector2[0])};
    }

    private float[] getBallX() {
        float[] result = new float[]{1,0,0};
        result[0] = (float) (result[0] * Math.cos((double) rotationY) + (result[2] * Math.sin((double) rotationY)));
        result[2] = (float) (result[0] * -Math.sin((double) rotationY) + (result[2] * Math.cos((double) rotationY)));

        result[0] = (float) (result[0] * Math.cos((double) rotationY) + (result[1] * -Math.sin((double) rotationY)));
        result[1] = (float) (result[0] * Math.sin((double) rotationY) + (result[1] * Math.cos((double) rotationY)));

        result[1] = (float) (result[1] * Math.cos((double) rotationY) + (result[2] * -Math.sin((double) rotationY)));
        result[2] = (float) (result[1] * Math.sin((double) rotationY) + (result[2] * Math.cos((double) rotationY)));

        return result;
    }
*/
    @Override
    public void update() {
        /*
        velocity[0] = xSpeed;
        velocity[1] = 0;
        velocity[2] = zSpeed;
        speed = (float) Math.sqrt(xSpeed * xSpeed + zSpeed * zSpeed);
        theta = speed / radius;
        direction = getNormal(velocity);
        alternateZ = calculateRotationAxis(direction);
        alternateY = getNormal(crossProduct(alternateZ, getBallX()));
        alternateX = crossProduct(alternateY, alternateZ);
        rotationMatrix.loadIdentity();
        rotationMatrix.multiply(new float[]{
                alternateX[0], alternateY[0], alternateZ[0], 0,
                alternateX[1], alternateY[1], alternateZ[1], 0,
                alternateX[2], alternateY[2], alternateZ[2], 0,
                0, 0, 0, 1
        });/*
        rotationMatrix.multiply(new float[]{
                (float) Math.cos(theta), (float) Math.sin(theta), 0, 0,
                (float) -Math.sin(theta), (float) Math.cos(theta), 0, 0,
                0, 0, 1, 0,
                0, 0, 0 ,1
        });
        rotationMatrix.multiply(new float[]{
                alternateX[0], alternateX[1], alternateX[2], 0,
                alternateY[0], alternateY[1], alternateY[2], 0,
                alternateZ[0], alternateZ[1], alternateZ[2], 0,
                0, 0, 0, 1
        });*/

        modelMatrix.loadIdentity();
        modelMatrix.translate(x, y, z);
        modelMatrix.scale(sizeX, sizeY, sizeZ);
        modelMatrix.multiply(rotationMatrix.matrix);
        modelMatrix.rotate(rotationY, 0, 1, 0);
        modelMatrix.rotate(rotationZ, 0, 0, 1);
        modelMatrix.rotate(rotationX, 1, 0, 0);
        modelMatrix.bind();
        if(visible) {
            model.draw();
        }
    }

    @Override
    public void draw() {
        visible = true;
        update();
    }

    @Override
    public void erase() {
        if(visible) {
            model.dispose();
        }
        visible = false;
    }

    @Override
    public void init(GL3 gl, Shader shader) {
        model = new JWavefrontObject(new File(paths[ID]));
        modelMatrix = new Matrix4();
        rotationMatrix = new Matrix4();
        modelMatrix.init(gl, shader.getUniformLocation("u_modelMatrix"));
        visible = false;
        try {
            model.init(gl, shader);
            model.unitize();
            model.dump();
        }
        catch (IOException ex) {
            Logger.getLogger(PoolGame.class.getName()).log(Level.SEVERE, null, ex);
        }
        direction = new float[3];
        velocity = new float[3];
        alternateX = new float[3];
        alternateY = new float[3];
        alternateZ = new float[3];
    }
}

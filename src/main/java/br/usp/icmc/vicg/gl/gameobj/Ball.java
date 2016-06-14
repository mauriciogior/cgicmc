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
    private Matrix4 ballSystemMatrix;
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
    private float[] rotationAxis;
    private float[] ballRotationAxis;
    private float[] velocity;

    private float[] ballXAxis;
    private float[] ballYAxis;
    private float[] ballZAxis;

    public float getxSpeed() {
        return xSpeed;
    }

    public void setxSpeed(float xSpeed) {
        this.xSpeed = xSpeed;
    }

    public float getzSpeed() {
        return zSpeed;
    }

    public void setzSpeed(float zSpeed) {
        this.zSpeed = zSpeed;
    }

    public Ball(float x, float y, float z, int ID) {
        super(x, y, z);
        this.ID = ID;
        setSize(radius, radius, radius);
    }

    private float[] getNormal(float[] vector) {
        float denominator = vector[0] * vector[0] + vector[1] * vector[1] + vector[2] * vector[2];
        denominator = (float) Math.sqrt((double) denominator);
        return new float[]{vector[0] / denominator, vector[1] / denominator, vector[2] / denominator};
    }

    private float[] crossProduct(float[] vector1, float[] vector2) {
        return new float[]{
                vector1[1] * vector2[2] - (vector1[2] * vector2[1]),
                vector1[2] * vector2[0] - (vector1[0] * vector2[2]),
                vector1[0] * vector2[1] - (vector1[1] * vector2[0])};
    }

    private float[] transformVector(Matrix4 matrix, float[] vector) {
        float[] result = new float[3];
        result[0] = matrix.matrix[0] * vector[0] + matrix.matrix[4] * vector[1] + matrix.matrix[8] * vector[2];
        result[1] = matrix.matrix[1] * vector[0] + matrix.matrix[5] * vector[1] + matrix.matrix[9] * vector[2];
        result[2] = matrix.matrix[2] * vector[0] + matrix.matrix[6] * vector[1] + matrix.matrix[10] * vector[2];
        return result;
    }

    private void updateBallSystem() {
        ballSystemMatrix.loadIdentity();
        ballSystemMatrix.multiply(new float[]{
                ballXAxis[0], ballYAxis[0], ballZAxis[0], 0,
                ballXAxis[1], ballYAxis[1], ballZAxis[1], 0,
                ballXAxis[2], ballYAxis[2], ballZAxis[2], 0,
                0, 0, 0, 1
        });
    }

    @Override
    public void update() {
        velocity[0] = xSpeed;
        velocity[1] = 0;
        velocity[2] = zSpeed;
        speed = (float) Math.sqrt(xSpeed * xSpeed + zSpeed * zSpeed);
        if(speed > 0) {
            theta = (speed / radius) * 57.2958f;
            direction = getNormal(velocity);
            rotationAxis = crossProduct(direction, new float[]{0, -1, 0});
            ballRotationAxis = transformVector(ballSystemMatrix, rotationAxis);
            rotationMatrix.rotate(theta, ballRotationAxis[0], ballRotationAxis[1], ballRotationAxis[2]);
            ballXAxis = transformVector(rotationMatrix, new float[]{1, 0, 0});
            ballYAxis = transformVector(rotationMatrix, new float[]{0, 1, 0});
            ballZAxis = transformVector(rotationMatrix, new float[]{0, 0, 1});
            updateBallSystem();
        }

        modelMatrix.loadIdentity();
        x += xSpeed;
        z += zSpeed;
        modelMatrix.translate(x, y, z);
        modelMatrix.scale(sizeX, sizeY, sizeZ);
        modelMatrix.multiply(rotationMatrix.matrix);
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
        ballSystemMatrix = new Matrix4();
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
        ballXAxis = new float[]{1, 0, 0};
        ballYAxis = new float[]{0, 1, 0};
        ballZAxis = new float[]{0, 0, 1};
        rotationMatrix.loadIdentity();
        rotationMatrix.rotate(90, 0, 1, 0);
        rotationMatrix.rotate(-90, 0, 0, 1);
        ballXAxis = transformVector(rotationMatrix, ballXAxis);
        ballYAxis = transformVector(rotationMatrix, ballYAxis);
        ballZAxis = transformVector(rotationMatrix, ballZAxis);
        updateBallSystem();
    }
}

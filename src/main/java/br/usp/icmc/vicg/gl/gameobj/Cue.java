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

public class Cue extends Actor {
    JWavefrontObject model;
    Matrix4 modelMatrix;
    Ball whiteBall;
    Camera camera;
    public float[] cameraBallVector;
    public Cue(float x, float y, float z, Ball whiteBall, Camera camera) {
        super(x, y, z);
        this.whiteBall = whiteBall;
        this.camera = camera;
        rotationX = -90;
        sizeX = 0.8f;
        sizeY = 0.8f;
        sizeZ = 0.8f;
    }

    private float[] getNormal(float[] vector) {
        float denominator = vector[0] * vector[0] + vector[1] * vector[1] + vector[2] * vector[2];
        denominator = (float) Math.sqrt((double) denominator);
        return new float[]{vector[0] / denominator, vector[1] / denominator, vector[2] / denominator};
    }

    private float dotProduct(float[] vector1, float[] vector2) {
        return vector1[0] * vector2[0] + vector1[1] * vector2[1] + vector1[2] * vector2[2];
    }

    @Override
    public void update() {
        cameraBallVector = getNormal(new float[]{whiteBall.getX() - camera.getX(), 0, whiteBall.getZ() - camera.getZ()});
        if(camera.getX() < whiteBall.getX()) {
            rotationZ = (float) Math.acos(dotProduct(new float[]{0, 0, 1}, cameraBallVector)) * 57.2958f + 180;
        }
        else {
            rotationZ = (float) -Math.acos(dotProduct(cameraBallVector, new float[]{0, 0, 1})) * 57.2958f + 180;
        }

        modelMatrix.loadIdentity();
        modelMatrix.translate(camera.getX(), 0.5f, camera.getZ());
        modelMatrix.rotate(rotationX, 1, 0, 0);
        modelMatrix.rotate(rotationY, 0, 1, 0);
        modelMatrix.rotate(rotationZ, 0, 0, 1);
        modelMatrix.scale(sizeX, sizeY, sizeZ);
        modelMatrix.bind();
        if(visible && camera.getTarget() == Camera.Target.WHITEBALL) {
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
        model = new JWavefrontObject(new File("./data/cue/Cue.obj"));
        modelMatrix = new Matrix4();
        modelMatrix.init(gl, shader.getUniformLocation("u_modelMatrix"));
        visible = false;
        cameraBallVector = new float[3];
        try {
            model.init(gl, shader);
            model.unitize();
            model.dump();
        }
        catch (IOException ex) {
            Logger.getLogger(PoolGame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

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

public class Floor extends Actor {
    JWavefrontObject model;
    Matrix4 modelMatrix;
    public Floor(float x, float y, float z) {
        super(x, y, z);
        sizeX = 4;
        sizeZ = 4;
    }

    @Override
    public void update() {
        modelMatrix.loadIdentity();
        modelMatrix.translate(x, y, z);
        modelMatrix.rotate(rotationX, 1, 0, 0);
        modelMatrix.rotate(rotationY, 0, 1, 0);
        modelMatrix.rotate(rotationZ, 0, 0, 1);
        modelMatrix.scale(sizeX, sizeY, sizeZ);
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
        model = new JWavefrontObject(new File("./data/floor/floor.obj"));
        modelMatrix = new Matrix4();
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
    }
}

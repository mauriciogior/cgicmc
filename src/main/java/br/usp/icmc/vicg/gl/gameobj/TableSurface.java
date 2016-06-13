package br.usp.icmc.vicg.gl.gameobj;

import br.usp.icmc.vicg.gl.core.Material;
import br.usp.icmc.vicg.gl.matrix.Matrix4;
import br.usp.icmc.vicg.gl.model.Cube;
import br.usp.icmc.vicg.gl.model.SimpleModel;
import br.usp.icmc.vicg.gl.util.Shader;

import javax.media.opengl.GL3;

// test only class
public class TableSurface extends Actor{
    SimpleModel model;
    Matrix4 modelMatrix;

    public TableSurface(float x, float y, float z) {
        super(x, y, z);
        setSize(1, 0.01f, 2);
        setPosition(0, 0.34f, 0);
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
            model.bind();
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
        model = new Cube();
        modelMatrix = new Matrix4();
        modelMatrix.init(gl, shader.getUniformLocation("u_modelMatrix"));
        visible = false;
        model.init(gl, shader);
    }
}

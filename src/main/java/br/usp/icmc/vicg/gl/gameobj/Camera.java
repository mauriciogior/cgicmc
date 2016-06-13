package br.usp.icmc.vicg.gl.gameobj;

import br.usp.icmc.vicg.gl.matrix.Matrix4;
import br.usp.icmc.vicg.gl.util.Shader;

import javax.media.opengl.GL3;

public class Camera extends Actor {
    private Matrix4 projectionMatrix;
    private Matrix4 viewMatrix;

    private float angle;
    private float aspect;
    private float dnear;
    private float dfar;

    private Ball whiteBall;
    private float rotationAngle;

    public enum Target {
        ORIGIN,
        WHITEBALL
    }

    public Camera(float x, float y, float z) {
        super(x, y, z);
        angle = 60;
        aspect = 1;
        dnear = 1;
        dfar = 10;
        rotationAngle = 0;
    }

    public void rotate(float angle) {
        rotationAngle += angle;
    }

    @Override
    public void update() {
        projectionMatrix.loadIdentity();
        projectionMatrix.perspective(angle, aspect, dnear, dfar);
        projectionMatrix.bind();

        viewMatrix.loadIdentity();
        viewMatrix.lookAt(
                (float) (x * Math.sin(rotationAngle)), y, (float) (z * Math.cos(rotationAngle)),
                0, 0, 0,
                0, 1, 0);
        viewMatrix.bind();
    }

    @Override
    public void draw() {
        update();
    }

    @Override
    public void erase() {

    }

    @Override
    public void init(GL3 gl, Shader shader) {
        projectionMatrix = new Matrix4();
        viewMatrix = new Matrix4();
        projectionMatrix.init(gl, shader.getUniformLocation("u_projectionMatrix"));
        viewMatrix.init(gl, shader.getUniformLocation("u_viewMatrix"));
    }
}

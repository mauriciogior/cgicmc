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

    private float defaultX;
    private float defaultY;
    private float defaultZ;

    public enum Target {
        ORIGIN,
        WHITEBALL
    }
    private Target target;

    public void setTarget(Target target) {
        this.target = target;
    }

    public Target getTarget() {
        return target;
    }

    public Camera(float x, float y, float z, Ball whiteBall) {
        super(x, y, z);
        defaultX = x;
        defaultY = y;
        defaultZ = z;
        angle = 45;
        aspect = 1.8f;
        dnear = 0.1f;
        dfar = 10;
        rotationAngle = 0;
        this.whiteBall = whiteBall;
        target = Target.ORIGIN;
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
        if(target == Target.ORIGIN) {
            x = (float) (defaultX * Math.sin(rotationAngle));
            z = (float) (defaultZ * Math.cos(rotationAngle));
            viewMatrix.lookAt(
                    x, defaultY + 0.5f, z,
                    0, 0, 0,
                    0, 1, 0);
        }
        else if(target == Target.WHITEBALL) {
            x = (float) (whiteBall.getX() + (defaultX / 2 * Math.sin(rotationAngle)));
            z = (float) (whiteBall.getZ() + (defaultZ / 2 * Math.cos(rotationAngle)));
            viewMatrix.lookAt(
                    x, defaultY, z,
                    whiteBall.getX(), 0.4f, whiteBall.getZ(),
                    0, 1, 0);
        }
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

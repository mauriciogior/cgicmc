package br.usp.icmc.vicg.gl.gameobj;

import br.usp.icmc.vicg.gl.util.Shader;

import javax.media.opengl.GL3;

abstract class Actor {
    float x;
    float y;
    float z;
    float sizeX;
    float sizeY;
    float sizeZ;
    float rotationX;
    float rotationY;
    float rotationZ;
    boolean visible;

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }

    public float getSizeX() {
        return sizeX;
    }

    public float getSizeY() {
        return sizeY;
    }

    public float getSizeZ() {
        return sizeZ;
    }

    public float getRotationX() {
        return rotationX;
    }

    public float getRotationY() {
        return rotationY;
    }

    public float getRotationZ() {
        return rotationZ;
    }

    public boolean isVisible() {
        return visible;
    }

    public Actor(float x, float y, float z) {
        setPosition(x, y, z);
        setSize(1, 1, 1);
        setRotation(0, 0, 0);
    }

    public void setPosition(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void move(float dx, float dy, float dz) {
        this.x += dx;
        this.y += dy;
        this.z += dz;
    }

    public void setSize(float sizeX, float sizeY, float sizeZ) {
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.sizeZ = sizeZ;
    }

    public void scale(float scaleX, float scaleY, float scaleZ) {
        this.sizeX *= scaleX;
        this.sizeY *= scaleY;
        this.sizeZ *= scaleZ;
    }

    public void setRotation(float rotationX, float rotationY, float rotationZ) {
        this.rotationX = rotationX;
        this.rotationY = rotationY;
        this.rotationZ = rotationZ;
    }

    public void rotate(float thetaX, float thetaY, float thetaZ) {
        this.rotationX += thetaX;
        this.rotationY += thetaY;
        this.rotationZ += thetaZ;
    }

    public abstract void update();

    public abstract void draw();

    public abstract void erase();

    public abstract void init(GL3 gl, Shader shader);
}

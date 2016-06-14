package br.usp.icmc.vicg.gl.gameobj;

/**
 * Created bz mauricio on 6/13/16.
 */
public class LVector {
    public float x;
    public float z;

    public LVector(float x, float z) {
        this.x = x;
        this.z = z;
    }

    public void normalise() {
        float distance = (float) Math.sqrt(x*x + z*z);
        x = x * (1.0f / distance);
        z = z * (1.0f / distance);
    }

    public float dot(LVector v) {
        return (this.x * v.x) + (this.z * v.z);
    }

    public LVector add(LVector v){
        float newX = x + v.x;
        float newz = z + v.z;

        return new LVector(newX, newz);
    }

    public LVector multiply(float scalar) {
        float newX = x * scalar;
        float newz = z * scalar;

        return new LVector(newX, newz);
    }
}
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
    private static final float radius = 0.03f;
    private static final float tan = radius * (float) Math.sqrt(3);

    public static final float decay = 0.00005f;
    public static final float x0 = -1 * (((radius * 5) + (radius * 5/2)) / 2);
    public static final float y0 = 0.35f;
    public static final float z0 = -0.5f;
    public float vx;
    public float vz;

    private float bx;
    private float bz;

    private float speed;
    private float theta;
    private float[] direction;
    private float[] rotationAxis;
    private float[] ballRotationAxis;
    private float[] velocity;

    private float[] ballXAxis;
    private float[] ballYAxis;
    private float[] ballZAxis;

    public boolean inRole = false;

    public Ball(float x, float y, float z, int ID) {
        super(x, y, z);
        this.ID = ID;
        setSize(radius, radius, radius);

        vx = 0;
        vz = 0;
        bx = x;
        bz = z;
    }

    public void resetPosition() {
        int rID = ID;

        if (ID == 0) {
            setPosition(x + (4 * radius), y, 1.25f + z);
        } else if (ID <= 5) {
            setPosition(x + ((ID - 1) * 2.1f * radius), y, z);
        } else if (ID <= 9) {
            rID -= 5;
            setPosition(x + ((((rID - 1) * 2.1f) + 1) * radius), y, z + tan * 1.1f);
        } else if (ID <= 12) {
            rID -= 8;
            setPosition(x + ((rID - 1) * 2.1f * radius), y, z + (tan * 2.1f));
        } else if (ID <= 14) {
            rID -= 11;
            setPosition(x + ((((rID - 1) * 2.1f) + 1) * radius), y, z + (tan * 3.1f));
        } else {
            setPosition(x + (4.1f * radius), y, z + (tan * 4.1f));
        }
    }

    public void setSpeed(float vx, float vz) {
        this.vx = vx;
        this.vz = vz;
    }

    public void updatePosition(boolean reverse, boolean collision) {
        float decay = collision ? this.decay * 4 : this.decay;
        bx = x;
        bz = z;

        // por enquanto nao encacapa a branca
        if (ID > 0) {
            if (x + vx >= 0.46f && z + vz >= 0.95f) inRole = true;
            if (x + vx >= 0.46f && z + vz <= -0.95f) inRole = true;
            if (x + vx <= -0.46f && z + vz <= -0.95f) inRole = true;
            if (x + vx <= -0.46f && z + vz >= 0.95f) inRole = true;
            if (x + vx <= -0.46f && Math.abs(z + vz) <= 0.02f) inRole = true;
            if (x + vx >= 0.46f && Math.abs(z + vz) <= 0.02f) inRole = true;
        }

        if (reverse) {
            x -= vx;
            z -= vz;

            if (x + vx > 0.47f || x + vx < -0.47f) {
                vx *= -1;
            }

            if (z + vz > 0.97f || z + vz < -0.97f) {
                vz *= -1;
            }

            return;
        }

        if (x + vx >= 0.47f || x + vx <= -0.47f) {
            vx *= -1;
            decay *= 2; // colisão aumenta atrito
        }

        if (z + vz >= 0.97f || z + vz <= -0.97f) {
            vz *= -1;
            decay *= 2; // colisão aumenta atrito
        }

        x += vx;
        z += vz;

        if (vx > 0) vx -= decay;
        else vx += decay;

        if (vz > 0) vz -= decay;
        else vz += decay;

        if (Math.abs(vx) < this.decay * 2) vx = 0;
        if (Math.abs(vz) < this.decay * 2) vz = 0;
    }

    public void collision(Ball ball) {

        if (ball.collided(this)) {

            float xDistance = (ball.x - this.x);
            float zDistance = (ball.z - this.z);

            LVector normalVector = new LVector(xDistance, zDistance);
            normalVector.normalise();

            LVector tangentVector = new LVector((normalVector.z * -1), normalVector.x);

            // create ball scalar normal direction.
            float ball1scalarNormal = normalVector.dot(new LVector(this.vx, this.vz));
            float ball2scalarNormal = normalVector.dot(new LVector(ball.vx, ball.vz));

            // create scalar velocity in the tagential direction.
            float ball1scalarTangential = tangentVector.dot(new LVector(this.vx, this.vz));
            float ball2scalarTangential = tangentVector.dot(new LVector(ball.vx, ball.vz));

            float ball1ScalarNormalAfter = (2 * ball2scalarNormal) / 2;
            float ball2ScalarNormalAfter = (2 * ball1scalarNormal) / 2;

            LVector ball1scalarNormalAfter_vector = normalVector.multiply(ball1ScalarNormalAfter);
            LVector ball2scalarNormalAfter_vector = normalVector.multiply(ball2ScalarNormalAfter);

            LVector ball1ScalarNormalVector = (tangentVector.multiply(ball1scalarTangential));
            LVector ball2ScalarNormalVector = (tangentVector.multiply(ball2scalarTangential));

            LVector ball1Velocity = ball1ScalarNormalVector.add(ball1scalarNormalAfter_vector);
            LVector ball2Velocity = ball2ScalarNormalVector.add(ball2scalarNormalAfter_vector);

            this.vx = ball1Velocity.x;
            this.vz = ball1Velocity.z;
            ball.vx = ball2Velocity.x;
            ball.vz = ball2Velocity.z;

            this.x = bx;
            this.z = bz;
            ball.x = ball.bx;
            ball.z = ball.bz;

            this.updatePosition(false, true);
            ball.updatePosition(false, true);

            //System.out.printf("x = %f, z = %f, or = %d, tar = %d, dvx = %f, vz = %f\n", ball.x, ball.z, ID, ball.ID, ball.vx, ball.vz);
        }
    }

    public boolean collided(Ball ball) {
        if (Math.abs(ball.vx) < decay * 2 && Math.abs(ball.vz) < decay * 2
                && Math.abs(vx) < decay * 2 && Math.abs(vz) < decay * 2) return false;
        return Math.sqrt(Math.pow((ball.x - x), 2) + Math.pow((ball.z - z), 2)) <= (radius * 2);
    }

    @Override
    public boolean equals(Object o) {
        return ((Ball) o).ID == ID;
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
        velocity[0] = vx;
        velocity[1] = 0;
        velocity[2] = vz;
        speed = (float) Math.sqrt(vx * vx + vz * vz);
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
        modelMatrix.translate(x, y, z);
        modelMatrix.scale(sizeX, sizeY, sizeZ);
        modelMatrix.multiply(rotationMatrix.matrix);
        modelMatrix.bind();

        if (visible && !inRole) {
            model.draw();
            updatePosition(false, false);
        }
    }

    @Override
    public void draw() {
        visible = true;
        update();
    }

    @Override
    public void erase() {
        if (visible) {
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

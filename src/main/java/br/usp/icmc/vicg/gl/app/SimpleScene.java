package br.usp.icmc.vicg.gl.app;

/**
 * Created by mauricio on 4/5/16.
 */

import br.usp.icmc.vicg.gl.core.Light;
import br.usp.icmc.vicg.gl.core.Material;
import br.usp.icmc.vicg.gl.jwavefront.JWavefrontObject;
import br.usp.icmc.vicg.gl.matrix.Matrix4;
import br.usp.icmc.vicg.gl.model.SimpleModel;
import br.usp.icmc.vicg.gl.model.SolidSphere;
import br.usp.icmc.vicg.gl.model.Sphere;
import br.usp.icmc.vicg.gl.model.Square;
import br.usp.icmc.vicg.gl.util.Shader;
import br.usp.icmc.vicg.gl.util.ShaderFactory;
import com.jogamp.opengl.util.AnimatorBase;
import com.jogamp.opengl.util.FPSAnimator;

import javax.media.opengl.*;
import javax.media.opengl.awt.GLCanvas;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Ceccon on 10/03/2016.
 */
public class SimpleScene implements GLEventListener
{
    private final int NUMBER_OF_SPHERES = 8;
    private final Shader shader; // Gerenciador dos shaders
    private final SimpleModel sphere;
    private final Square table;
    private final Light light;
    private final Material material;
    private final JWavefrontObject tableModel;

    private int color_handle;

    private final Matrix4 projectionMatrix;
    private final Matrix4 viewMatrix;
    private Matrix4 matrix;


    float x;
    float[] y = new float[NUMBER_OF_SPHERES];
    float vy[] = new float[NUMBER_OF_SPHERES];
    float dt = 0.01f;
    float g = 0.12f;

    public SimpleScene()
    {
        shader = ShaderFactory.getInstance(ShaderFactory.ShaderType.COMPLETE_SHADER);
        sphere = new SolidSphere();
        table = new Square();
        light = new Light();
        material = new Material();

        tableModel = new JWavefrontObject(new File("./data/table/Pool-Table-Jay-Hardy.obj"));

        matrix = new Matrix4();
        projectionMatrix = new Matrix4();
        viewMatrix = new Matrix4();

        generateBalls();
    }

    @Override
    public void init(GLAutoDrawable glad)
    {
        GL3 gl = glad.getGL().getGL3();

        gl.glClearColor(0, 0, 0, 0);

        shader.init(gl);
        shader.bind();

        matrix.init(gl, shader.getUniformLocation("u_modelMatrix"));
        projectionMatrix.init(gl, shader.getUniformLocation("u_projectionMatrix"));
        viewMatrix.init(gl, shader.getUniformLocation("u_viewMatrix"));

        try {
            //init the model
            tableModel.init(gl, shader);
            tableModel.unitize();
            tableModel.dump();
        } catch (IOException ex) {
            Logger.getLogger(Example12.class.getName()).log(Level.SEVERE, null, ex);
        }

        color_handle = shader.getUniformLocation("u_color");

        // Inicializa o sistema de coordenadas
        projectionMatrix.loadIdentity();
        projectionMatrix.perspective(45f, 1.0f, 0.1f, 10.0f);
        projectionMatrix.bind();

        viewMatrix.loadIdentity();
        viewMatrix.lookAt(0.0f, 2.0f, 2.0f,
                0.0f, 0.0f, 0.0f,
                0.0f, -1.0f, 0.0f);
        viewMatrix.bind();

        light.setPosition(new float[]{10.0f, 10.0f, 5.0f, 1.0f});
        light.setAmbientColor(new float[]{1.0f, 1.0f, 1.0f, 1.0f});
        light.setSpecularColor(new float[]{1.0f, 1.0f, 1.0f, 1.0f});
        light.init(gl, shader);
        light.bind();

        material.init(gl, shader);
        material.setAmbientColor(new float[]{0.0f, 0.0f, 0.0f, 1.0f});
        material.bind();

        sphere.init(gl, shader);
        table.init(gl, shader);

        setValuesForScene();
    }

    private void setValuesForTable() {
        matrix.loadIdentity();
        matrix.rotate(90f, 1f, 0f, 0f);
        matrix.rotate(90f, 0f, 1f, 0f);
        matrix.bind();

        light.setAmbientColor(new float[]{0.7f, 0.7f, 0.7f, 1.0f});
        light.bind();
        //light.setDiffuseColor(new float[]{0.75f, 0.75f, 0.75f, 1.0f});
        //light.setSpecularColor(new float[]{0.7f, 0.7f, 0.7f, 1.0f});

        material.setSpecularColor(new float[]{0.4f, 1.0f, 0.4f, 1.0f});
        material.setDiffuseColor(new float[]{0.0f, 1.0f, 0.0f, 1.0f});
        material.bind();
    }

    private void setValuesForScene() {
        // Inicializa o sistema de coordenadas
        projectionMatrix.loadIdentity();
        projectionMatrix.perspective(45f, 1.0f, 0.1f, 10.0f);
        projectionMatrix.bind();

        viewMatrix.loadIdentity();
        viewMatrix.lookAt(0.0f, 2.0f, 2.0f,
                0.0f, 0.0f, 0.0f,
                0.0f, -1.0f, 0.0f);
        viewMatrix.bind();
    }

    @Override
    public void dispose(GLAutoDrawable glad)
    {
        sphere.dispose();
        table.dispose();
    }

    HSV hsv = new HSV();
    RGB rgb = new RGB();

    private static java.util.List<Ball> ballList;

    public static class LVector {
        public float x;
        public float y;

        public LVector(float x, float y) {
            this.x = x;
            this.y = y;
        }

        public void normalise() {
            float distance = (float) Math.sqrt(x*x + y*y);
            x = x * (1.0f / distance);
            y = y * (1.0f / distance);
        }

        public float dot(LVector v) {
            return (this.x * v.x) + (this.y * v.y);
        }

        public LVector add(LVector v){
            float newX = x + v.x;
            float newY = y + v.y;

            return new LVector(newX, newY);
        }

        public LVector multiply(float scalar) {
            float newX = x * scalar;
            float newY = y * scalar;

            return new LVector(newX, newY);
        }
    }

    public static class Ball {
        public short id;
        public float x;
        public float y;
        public float vx;
        public float vy;
        public RGB color;
        public static final float decay = 0.0001f;
        public static final float radius = 0.02f;
        public static final float x0 = -1 * (((radius * 5) + (radius * 5/2)) / 2);
        public static final float y0 = -0.5f;
        public static final float dist = 1.4f;

        private static final float tan = radius * (float) Math.sqrt(3);

        public Ball(float x, float y, Color color) {
            this.id = (short) ballList.size();
            this.x = x;
            this.y = y;
            vx = 0f;
            vy = 0f;

            this.color = new RGB();
            this.color.r = color.getRed();
            this.color.g = color.getGreen();
            this.color.b = color.getBlue();
        }

        public void calculate(boolean reverse) {
            if (reverse) {
                x -= vx;
                y -= vy;

                if (x + vx + Ball.radius > 0.5f || x + vx - Ball.radius < -0.5f) {
                    vx *= -1;
                }

                if (y + vy + Ball.radius > 1f || y + vy - Ball.radius < -1f) {
                    vy *= -1;
                }

                return;
            }

            if (x + vx + Ball.radius >= 0.5f || x + vx - Ball.radius <= -0.5f) {
                vx *= -1;
            }

            if (y + vy + Ball.radius >= 1f || y + vy - Ball.radius <= -1f) {
                vy *= -1;
            }

            x += vx;
            y += vy;

            if (vx > 0) vx -= decay;
            else vx += decay;

            if (vy > 0) vy -= decay;
            else vy += decay;

            if (Math.abs(vx) < decay * 5) vx = 0;
            if (Math.abs(vy) < decay * 5) vy = 0;
        }

        public void collision(Ball ball) {

            if (ball.collided(this)) {

                float xDistance = (ball.x - this.x);
                float yDistance = (ball.y - this.y);

                LVector normalVector = new LVector(xDistance, yDistance);
                normalVector.normalise();

                LVector tangentVector = new LVector((normalVector.y * -1), normalVector.x);

                // create ball scalar normal direction.
                float ball1scalarNormal = normalVector.dot(new LVector(this.vx, this.vy));
                float ball2scalarNormal = normalVector.dot(new LVector(ball.vx, ball.vy));

                // create scalar velocity in the tagential direction.
                float ball1scalarTangential = tangentVector.dot(new LVector(this.vx, this.vy));
                float ball2scalarTangential = tangentVector.dot(new LVector(ball.vx, ball.vy));

                float ball1ScalarNormalAfter = (2 * ball2scalarNormal) / 2;
                float ball2ScalarNormalAfter = (2 * ball1scalarNormal) / 2;

                LVector ball1scalarNormalAfter_vector = normalVector.multiply(ball1ScalarNormalAfter);
                LVector ball2scalarNormalAfter_vector = normalVector.multiply(ball2ScalarNormalAfter);

                LVector ball1ScalarNormalVector = (tangentVector.multiply(ball1scalarTangential));
                LVector ball2ScalarNormalVector = (tangentVector.multiply(ball2scalarTangential));

                LVector ball1Velocity = ball1ScalarNormalVector.add(ball1scalarNormalAfter_vector);
                LVector ball2Velocity = ball2ScalarNormalVector.add(ball2scalarNormalAfter_vector);

                //this.calculate(true);
                //ball.calculate(true);

                this.vx = ball1Velocity.x;
                this.vy = ball1Velocity.y;
                ball.vx = ball2Velocity.x;
                ball.vy = ball2Velocity.y;

                this.calculate(false);
                ball.calculate(false);

                System.out.printf("or = %d, tar = %d, dvx = %f, vy = %f\n", id, ball.id, ball.vx, ball.vy);
            }
        }

        public boolean collided(Ball ball) {
            return Math.sqrt(Math.pow((ball.x - x), 2) + Math.pow((ball.y - y), 2)) <= (radius * 2);
        }

        @Override
        public boolean equals(Object o) {
            return ((Ball) o).id == id;
        }
    }

    private void generateBalls() {
        ballList = new ArrayList<>();

        Ball whiteBall = new Ball(Ball.x0 + (Ball.radius * 4), Ball.y0 + Ball.dist, Color.WHITE);
        whiteBall.vy = -0.045f;
        whiteBall.vx = 0.0025f;

        ballList.add(whiteBall);

        ballList.add(new Ball(Ball.x0 + (Ball.radius * 0), Ball.y0 + (Ball.tan * 0), Color.BLUE));
        ballList.add(new Ball(Ball.x0 + (Ball.radius * 2), Ball.y0 + (Ball.tan * 0), Color.BLUE));
        ballList.add(new Ball(Ball.x0 + (Ball.radius * 4), Ball.y0 + (Ball.tan * 0), Color.BLUE));
        ballList.add(new Ball(Ball.x0 + (Ball.radius * 6), Ball.y0 + (Ball.tan * 0), Color.BLUE));
        ballList.add(new Ball(Ball.x0 + (Ball.radius * 8), Ball.y0 + (Ball.tan * 0), Color.BLUE));

        ballList.add(new Ball(Ball.x0 + (Ball.radius * 1), Ball.y0 + (Ball.tan * 1), Color.BLUE));
        ballList.add(new Ball(Ball.x0 + (Ball.radius * 3), Ball.y0 + (Ball.tan * 1), Color.BLUE));
        ballList.add(new Ball(Ball.x0 + (Ball.radius * 5), Ball.y0 + (Ball.tan * 1), Color.BLUE));
        ballList.add(new Ball(Ball.x0 + (Ball.radius * 7), Ball.y0 + (Ball.tan * 1), Color.BLUE));

        ballList.add(new Ball(Ball.x0 + (Ball.radius * 2), Ball.y0 + (Ball.tan * 2), Color.BLUE));
        ballList.add(new Ball(Ball.x0 + (Ball.radius * 4), Ball.y0 + (Ball.tan * 2), Color.BLUE));
        ballList.add(new Ball(Ball.x0 + (Ball.radius * 6), Ball.y0 + (Ball.tan * 2), Color.BLUE));

        ballList.add(new Ball(Ball.x0 + (Ball.radius * 3), Ball.y0 + (Ball.tan * 3), Color.BLUE));
        ballList.add(new Ball(Ball.x0 + (Ball.radius * 5), Ball.y0 + (Ball.tan * 3), Color.BLUE));

        ballList.add(new Ball(Ball.x0 + (Ball.radius * 4), Ball.y0 + (Ball.tan * 4), Color.BLUE));
    }

    private int delay = 0;

    int alpha = 0;
    int beta = 0;
    int delta = 5;

    @Override
    public void display(GLAutoDrawable glad)  {

        hsv.h = 360f / 4f;
        GL3 gl = glad.getGL().getGL3();

        gl.glClear(GL3.GL_COLOR_BUFFER_BIT | GL3.GL_DEPTH_BUFFER_BIT);

        hsv2rgb(hsv, rgb);
        matrix.loadIdentity();
        matrix.scale(1f, 2f, 1);
        matrix.bind();

        gl.glUniform3f(color_handle, rgb.r, rgb.g, rgb.b);

        light.setDiffuseColor(new float[]{0.0f, 1.0f, 0.0f, 1.0f});
        light.bind();

        material.setSpecularColor(new float[]{0.4f, 1.0f, 0.4f, 1.0f});
        material.setDiffuseColor(new float[]{0.0f, 1.0f, 0.0f, 1.0f});
        material.bind();

        table.bind();
        table.draw();

        setValuesForTable();
        tableModel.draw();

        if (delay < 10) delay++;

        for (Ball ball : ballList) {
            if (delay >= 10) ball.calculate(false);

            matrix.loadIdentity();
            matrix.translate(ball.x, ball.y, 0f);
            matrix.scale(Ball.radius, Ball.radius, Ball.radius);
            matrix.bind();
            gl.glUniform3f(color_handle, ball.color.r, ball.color.g, ball.color.b);

            material.setSpecularColor(new float[]{0.4f, 0.4f, 1.0f, 1.0f});
            material.setDiffuseColor(new float[]{0.0f, 0.0f, 1.0f, 1.0f});
            light.setDiffuseColor(new float[]{0.0f, 0.0f, 1.0f, 1.0f});

            light.bind();
            material.bind();

            sphere.bind();
            sphere.draw();
        }

        if (delay >= 10) {
            for (int i = 0; i < ballList.size() - 1; i++) {
                for (int j = 0; j < ballList.size(); j++) {
                    if (j != i) ballList.get(i).collision(ballList.get(j));
                }
            }
        }

        gl.glFlush();
    }

    @Override
    public void reshape(GLAutoDrawable glad, int i, int i1, int i2, int i3)
    {
    }

    static class RGB
    {
        public float r, g, b;
    }
    static class HSV
    {
        public float h, s = 1f, v = 1f;
    }
    void hsv2rgb(HSV in, RGB out)
    {
        float hh, p, q, t, ff;
        int i;

        if(in.s <= 0.0)
        {
            out.r = in.v;
            out.g = in.v;
            out.b = in.v;
            return;
        }

        hh = in.h;
        if(hh >= 360.0f)
            hh = 0f;

        hh /= 60.0;
        i = (int)hh;
        ff = hh - i;

        p = in.v * (1.0f - in.s);
        q = in.v * (1.0f - (in.s * ff));
        t = in.v * (1.0f - (in.s * (1.0f - ff)));

        switch(i) {
            case 0:
                out.r = in.v;
                out.g = t;
                out.b = p;
                break;
            case 1:
                out.r = q;
                out.g = in.v;
                out.b = p;
                break;
            case 2:
                out.r = p;
                out.g = in.v;
                out.b = t;
                break;
            case 3:
                out.r = p;
                out.g = q;
                out.b = in.v;
                break;
            case 4:
                out.r = t;
                out.g = p;
                out.b = in.v;
                break;
            case 5:
            default:
                out.r = in.v;
                out.g = p;
                out.b = q;
                break;
        }
    }
    public static void main(String[] args) {
        // Get GL3 profile (to work with OpenGL 4.0)
        GLProfile profile = GLProfile.get(GLProfile.GL3);

        // Configurations
        GLCapabilities glcaps = new GLCapabilities(profile);
        glcaps.setDoubleBuffered(true);
        glcaps.setHardwareAccelerated(true);

        // Create canvas
        GLCanvas glCanvas = new GLCanvas(glcaps);

        // Add listener to panel
        SimpleScene listener = new SimpleScene();
        glCanvas.addGLEventListener(listener);

        Frame frame = new Frame("Example");
        frame.setSize(900, 900);
        frame.add(glCanvas);
        final AnimatorBase animator = new FPSAnimator(glCanvas, 30);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        animator.stop();
                        System.exit(0);
                    }
                }).start();
            }
        });
        frame.setVisible(true);
        animator.start();
    }
}

package br.usp.icmc.vicg.gl.app;

import br.usp.icmc.vicg.gl.core.Light;
import br.usp.icmc.vicg.gl.gameobj.*;
import br.usp.icmc.vicg.gl.util.Shader;
import br.usp.icmc.vicg.gl.util.ShaderFactory;
import br.usp.icmc.vicg.gl.util.ShaderFactory.ShaderType;
import com.jogamp.opengl.util.AnimatorBase;
import com.jogamp.opengl.util.FPSAnimator;

import javax.media.opengl.*;
import javax.media.opengl.awt.GLCanvas;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class PoolGame extends KeyAdapter implements GLEventListener {

    private final Shader shader; // Gerenciador dos shaders
    private final PoolTable poolTable;
    private final Floor[] floors;
    private final Cue cue;
    private final Ball[] balls;
    private final Camera camera;
    private final Light light;

    public PoolGame() {
        // Carrega os shaders
        shader = ShaderFactory.getInstance(ShaderType.COMPLETE_SHADER);

        // declara os objetos e suas posições
        poolTable = new PoolTable(0, 0, 0);
        floors = new Floor[4];
        balls = new Ball[16];

        balls[0] = new Ball(Ball.x0, Ball.y0, Ball.z0, 0);
        camera = new Camera(2, 1.5f, 2, balls[0]);
        cue = new Cue(0, 0.5f, 0, balls[0], camera);
        balls[0].resetPosition();

        for(int i = 1; i < balls.length; i++) {
            balls[i] = new Ball(Ball.x0, Ball.y0, Ball.z0, i);
            balls[i].resetPosition();
        }
        floors[0] = new Floor(4, -0.4f, 4);
        floors[1] = new Floor(-4, -0.4f, 4);
        floors[2] = new Floor(4, -0.4f, -4);
        floors[3] = new Floor(-4, -0.4f, -4);

        light = new Light();
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        // Get pipeline
        GL3 gl = drawable.getGL().getGL3();

        // Print OpenGL version
        System.out.println("OpenGL Version: " + gl.glGetString(GL.GL_VERSION) + "\n");

        gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        gl.glClearDepth(1.0f);

        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glEnable(GL.GL_CULL_FACE);

        //inicializa os shaders
        shader.init(gl);

        //ativa os shaders
        shader.bind();

        //inicializa os objetos da cena
        camera.init(gl, shader);
        poolTable.init(gl, shader);
        cue.init(gl, shader);
        floors[0].init(gl, shader);
        floors[1].init(gl, shader);
        floors[2].init(gl, shader);
        floors[3].init(gl, shader);

        for(int i = 0; i < balls.length; i++) {
            balls[i].init(gl, shader);
        }

        //init the light
        light.init(gl, shader);
        light.setPosition(new float[]{0, 5, 0, 0});
        light.setAmbientColor(new float[]{0.1f, 0.1f, 0.1f, 1});
        light.setDiffuseColor(new float[]{1, 1, 1, 1});
        light.setSpecularColor(new float[]{0.7f, 0.7f, 0.7f, 1});
        light.bind();

        test = 0;
    }

    int test;
    @Override
    public void display(GLAutoDrawable drawable) {
        // Recupera o pipeline
        GL3 gl = drawable.getGL().getGL3();

        // Limpa o frame buffer com a cor definida
        gl.glClear(GL3.GL_COLOR_BUFFER_BIT | GL3.GL_DEPTH_BUFFER_BIT);

        camera.draw();

        poolTable.draw();
        floors[0].draw();
        floors[1].draw();
        floors[2].draw();
        floors[3].draw();
        cue.draw();
        balls[0].draw();

        for(int i = 1; i < balls.length; i++) {
            if (balls[i].inRole) continue;

            balls[i].draw();
        }

        for (int i = 0; i < balls.length - 1; i++) {
            if (balls[i].inRole) continue;

            for (int j = 0; j < balls.length; j++) {
                if (balls[j].inRole) continue;

                if (j != i) balls[i].collision(balls[j]);
            }
        }

        // Força execução das operações declaradas
        gl.glFlush();
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
        poolTable.erase();
        floors[0].erase();
        floors[1].erase();
        floors[2].erase();
        floors[3].erase();
        cue.erase();
    }
    float counter = 0.005f;
    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_PAGE_UP:
                break;
            case KeyEvent.VK_PAGE_DOWN:
                break;
            case KeyEvent.VK_LEFT://gira sobre o eixo-y
                camera.rotate((float) (Math.PI) * -0.01f);
                break;
            case KeyEvent.VK_RIGHT://gira sobre o eixo-y
                camera.rotate((float) (Math.PI) * 0.01f);
                break;
            case KeyEvent.VK_SPACE:
                if(camera.getTarget() == Camera.Target.ORIGIN) {
                    camera.setTarget(Camera.Target.WHITEBALL);
                }
                else {
                    camera.setTarget(Camera.Target.ORIGIN);
                }
                break;

            case KeyEvent.VK_B:
                if (camera.getTarget() == Camera.Target.ORIGIN) return;
                cue.shoot();
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
        PoolGame listener = new PoolGame();
        glCanvas.addGLEventListener(listener);

        Frame frame = new Frame("Pool Game");
        frame.setSize(1366, 728);
        frame.add(glCanvas);
        frame.addKeyListener(listener);
        final AnimatorBase animator = new FPSAnimator(glCanvas, 60);

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

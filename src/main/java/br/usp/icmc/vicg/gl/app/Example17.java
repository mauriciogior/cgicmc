package br.usp.icmc.vicg.gl.app;

import br.usp.icmc.vicg.gl.core.Light;
import br.usp.icmc.vicg.gl.core.Material;
import br.usp.icmc.vicg.gl.matrix.Matrix4;
import br.usp.icmc.vicg.gl.model.SimpleModel;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.media.opengl.GL;
import javax.media.opengl.GL3;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;

import br.usp.icmc.vicg.gl.util.Shader;
import br.usp.icmc.vicg.gl.util.ShaderFactory;

import com.jogamp.opengl.util.AnimatorBase;
import com.jogamp.opengl.util.FPSAnimator;

public class Example17 implements GLEventListener {

  private final Shader shader;
  private final Matrix4 modelMatrix;
  private final Matrix4 projectionMatrix;
  private final Matrix4 viewMatrix;
  private final Light light;
  private final Material material;

  private final SimpleModel triangle1;
  private final SimpleModel triangle2;

  public Example17() {
    // Carrega os shaders
    shader = ShaderFactory.getInstance(ShaderFactory.ShaderType.LIGHT_SHADER);

    modelMatrix = new Matrix4();
    projectionMatrix = new Matrix4();
    viewMatrix = new Matrix4();
    light = new Light();
    material = new Material();

    triangle1= new Triangle1();
    triangle2= new Triangle2();
  }

  @Override
  public void init(GLAutoDrawable drawable) {
    // Get pipeline
    GL3 gl = drawable.getGL().getGL3();

    // Print OpenGL version
    System.out.println("OpenGL Version: " + gl.glGetString(GL.GL_VERSION) + "\n");

    gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
    
    gl.glEnable(GL.GL_DEPTH_TEST);

    //inicializa os shaders
    shader.init(gl);

    //ativa os shaders
    shader.bind();

    //inicializa a matrix Model and Projection
    modelMatrix.init(gl, shader.getUniformLocation("u_modelMatrix"));
    projectionMatrix.init(gl, shader.getUniformLocation("u_projectionMatrix"));
    viewMatrix.init(gl, shader.getUniformLocation("u_viewMatrix"));

    projectionMatrix.loadIdentity();
    projectionMatrix.ortho(-5.0f, 5.0f, -5.0f, 5.0f, -5.0f, 5.0f);
    projectionMatrix.bind();

    modelMatrix.loadIdentity();
    modelMatrix.bind();

    viewMatrix.loadIdentity();
    viewMatrix.bind();

    //inicializa a luz
    light.setPosition(new float[]{0.0f, 0.0f, 10.0f, 1.0f});
    light.setAmbientColor(new float[]{0.1f, 0.1f, 0.1f, 1.0f});
    light.setDiffuseColor(new float[]{1.0f, 1.0f, 1.0f, 1.0f});
    light.setSpecularColor(new float[]{1.0f, 1.0f, 1.0f, 1.0f});
    light.init(gl, shader);
    light.bind();

    //deine material
    material.init(gl, shader);
    material.setAmbientColor(new float[]{1.0f, 1.0f, 1.0f, 1.0f});
    material.setDiffuseColor(new float[]{1.0f, 0.0f, 0.0f, 1.0f});
    material.setSpecularColor(new float[]{1.0f, 1.0f, 1.0f, 1.0f});
    material.setSpecularExponent(64.0f);
    material.bind();

    //cria o objeto a ser desenhado
    triangle1.init(gl, shader);
    triangle2.init(gl, shader);
  }

  @Override
  public void display(GLAutoDrawable drawable) {
    // Recupera o pipeline
    GL3 gl = drawable.getGL().getGL3();

    // Limpa o frame buffer com a cor definida
    gl.glClear(GL3.GL_COLOR_BUFFER_BIT | GL3.GL_DEPTH_BUFFER_BIT);

    material.setDiffuseColor(new float[]{1.0f, 0.0f, 0.0f, 1.0f});
    material.bind();
    triangle1.bind();
    triangle1.draw();
    
    material.setDiffuseColor(new float[]{0.0f, 0.0f, 1.0f, 1.0f});
    material.bind();
    triangle2.bind();
    triangle2.draw();

    // Força execução das operações declaradas
    gl.glFlush();
  }

  @Override
  public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
  }

  @Override
  public void dispose(GLAutoDrawable drawable) {
  }

  class Triangle1 extends SimpleModel {

    public Triangle1() {
      vertex_buffer = new float[]{
        -2, -2, 0,
        2, -2, 0,
        0, 2, 0};

      normal_buffer = new float[]{
        0, 0, 1,
        0, 0, 1,
        0, 0, 1
      };
    }

    @Override
    public void draw() {
      draw(GL.GL_TRIANGLES);
    }
  }
  
  class Triangle2 extends SimpleModel {

    public Triangle2() {
      vertex_buffer = new float[]{
        0, 0, -2,
        4, 0, -2,
        2, 4, -2};

      normal_buffer = new float[]{
        0, 0, 1,
        0, 0, 1,
        0, 0, 1
      };
    }

    @Override
    public void draw() {
      draw(GL.GL_TRIANGLES);
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
    Example17 listener = new Example17();
    glCanvas.addGLEventListener(listener);

    Frame frame = new Frame("Example 16");
    frame.setSize(1000, 1000);
    frame.add(glCanvas);
    final AnimatorBase animator = new FPSAnimator(glCanvas, 1);

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

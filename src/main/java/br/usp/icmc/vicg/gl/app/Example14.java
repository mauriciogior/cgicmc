package br.usp.icmc.vicg.gl.app;

import br.usp.icmc.vicg.gl.core.Light;
import br.usp.icmc.vicg.gl.core.Material;
import br.usp.icmc.vicg.gl.matrix.Matrix4;
import br.usp.icmc.vicg.gl.model.TextureRectangle;
import br.usp.icmc.vicg.gl.model.TextureSimpleModel;
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
import br.usp.icmc.vicg.gl.util.ShaderFactory.ShaderType;

import com.jogamp.opengl.util.AnimatorBase;
import com.jogamp.opengl.util.FPSAnimator;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Example14 implements GLEventListener {

  private final Shader shader; // Gerenciador dos shaders
  private final Matrix4 modelMatrix;
  private final Matrix4 projectionMatrix;
  private final Matrix4 viewMatrix;
  private final TextureSimpleModel rectangle;
  private final Light light;
  private final Material material;

  public Example14() {
    // Carrega os shaders
    shader = ShaderFactory.getInstance(ShaderType.TEXTURE_SHADER);
    modelMatrix = new Matrix4();
    projectionMatrix = new Matrix4();
    viewMatrix = new Matrix4();

    rectangle = new TextureRectangle();
    light = new Light();
    material = new Material();
  }

  @Override
  public void init(GLAutoDrawable drawable) {
    // Get pipeline
    GL3 gl = drawable.getGL().getGL3();

    // Print OpenGL version
    System.out.println("OpenGL Version: " + gl.glGetString(GL.GL_VERSION) + "\n");

    gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
    gl.glClearDepth(1.0f);

    gl.glEnable(GL.GL_DEPTH_TEST);
    gl.glDisable(GL.GL_CULL_FACE);

    //inicializa os shaders
    shader.init(gl);

    //ativa os shaders
    shader.bind();

    //inicializa a matrix Model and Projection
    modelMatrix.init(gl, shader.getUniformLocation("u_modelMatrix"));
    projectionMatrix.init(gl, shader.getUniformLocation("u_projectionMatrix"));
    viewMatrix.init(gl, shader.getUniformLocation("u_viewMatrix"));
    
    // Inicializa o sistema de coordenadas
    projectionMatrix.loadIdentity();
    projectionMatrix.ortho(
            -1, 1,
            -1, 1,
            -2, 2);
    projectionMatrix.bind();

    viewMatrix.loadIdentity();
    viewMatrix.lookAt(
            0, 0, 1,
            0, 0, 0,
            0, 1, 0);
    viewMatrix.bind();

    light.init(gl, shader);
    light.setPosition(new float[]{2.0f, 2.0f, 2.0f, 1.0f});
    light.setAmbientColor(new float[]{0.0f, 0.0f, 0.0f, 1.0f});
    light.setDiffuseColor(new float[]{1.0f, 1.0f, 1.0f, 1.0f});
    light.setSpecularColor(new float[]{1.0f, 1.0f, 1.0f, 1.0f});
    light.bind();

    material.init(gl, shader);

    //cria o objeto a ser desenhado
    rectangle.init(gl, shader);
    try {
      rectangle.loadTexture("images/face.jpg");
    } catch (IOException ex) {
      Logger.getLogger(Example14.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  @Override
  public void display(GLAutoDrawable drawable) {
    // Recupera o pipeline
    GL3 gl = drawable.getGL().getGL3();

    // Limpa o frame buffer com a cor definida
    gl.glClear(GL3.GL_COLOR_BUFFER_BIT | GL3.GL_DEPTH_BUFFER_BIT);

    material.setAmbientColor(new float[]{0.0f, 0.0f, 1.0f, 0.25f});
    material.setDiffuseColor(new float[]{0.0f, 0.0f, 1.0f, 0.25f});
    material.setSpecularColor(new float[]{0.9f, 0.9f, 0.9f, 0.25f});
    material.setSpecularExponent(32);
    material.bind();

    modelMatrix.loadIdentity();
    modelMatrix.bind();

    rectangle.bind();
    rectangle.draw();

    // Força execução das operações declaradas
    gl.glFlush();
  }

  @Override
  public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
  }

  @Override
  public void dispose(GLAutoDrawable drawable) {
    // Apaga o buffer
    rectangle.dispose();
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
    Example14 listener = new Example14();
    glCanvas.addGLEventListener(listener);

    Frame frame = new Frame("Example 05");
    frame.setSize(600, 600);
    frame.add(glCanvas);
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

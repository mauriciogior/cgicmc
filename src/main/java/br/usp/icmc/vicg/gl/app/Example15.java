package br.usp.icmc.vicg.gl.app;

import br.usp.icmc.vicg.gl.core.Light;
import br.usp.icmc.vicg.gl.core.Material;
import br.usp.icmc.vicg.gl.matrix.Matrix4;
import br.usp.icmc.vicg.gl.model.NormalMappingRectangle;
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
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Example15 extends KeyAdapter implements GLEventListener {

  private final Shader shader; // Gerenciador dos shaders
  private final Matrix4 modelMatrix;
  private final Matrix4 projectionMatrix;
  private final Matrix4 viewMatrix;
  private final NormalMappingRectangle rectangle;
  private final Light light;
  private final Material material;
  
  private float alpha;
  private float beta;

  public Example15() {
    // Carrega os shaders
    shader = ShaderFactory.getInstance(ShaderType.NORMALMAP_SHADER);
    modelMatrix = new Matrix4();
    projectionMatrix = new Matrix4();
    viewMatrix = new Matrix4();

    rectangle = new NormalMappingRectangle();
    light = new Light();
    material = new Material();
    
    alpha = 0.0f;
    beta = 0.0f;
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
            -0.75f, 0.75f,
            -0.75f, 0.75f,
            -2, 2);
    projectionMatrix.bind();

    viewMatrix.loadIdentity();
    viewMatrix.lookAt(
            0, 0, 1,
            0, 0, 0,
            0, 1, 0);
    viewMatrix.bind();

    light.init(gl, shader);
    light.setPosition(new float[]{0.0f, 0.0f, 0.25f, 1.0f});
    light.setAmbientColor(new float[]{0.2f, 0.2f, 0.2f, 1.0f});
    light.setDiffuseColor(new float[]{1.0f, 1.0f, 1.0f, 1.0f});
    light.setSpecularColor(new float[]{1.0f, 1.0f, 1.0f, 1.0f});
    light.bind();

    material.init(gl, shader);
    material.setAmbientColor(new float[]{1.0f, 1.0f, 1.0f, 1.0f});
    material.setDiffuseColor(new float[]{0.0f, 0.0f, 1.0f, 1.0f});
    material.setSpecularColor(new float[]{0.9f, 0.9f, 0.9f, 1.0f});
    material.setSpecularExponent(4);
    material.bind();

    //cria o objeto a ser desenhado
    rectangle.init(gl, shader);
    try {
      rectangle.loadTexture("images/wand_boden.jpg");
      rectangle.loadNormalMap("images/wand_boden_normal.jpg");
    } catch (IOException ex) {
      Logger.getLogger(Example15.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  @Override
  public void display(GLAutoDrawable drawable) {
    // Recupera o pipeline
    GL3 gl = drawable.getGL().getGL3();

    // Limpa o frame buffer com a cor definida
    gl.glClear(GL3.GL_COLOR_BUFFER_BIT | GL3.GL_DEPTH_BUFFER_BIT);
    
    light.init(gl, shader);
    light.setPosition(new float[]{beta, alpha, 0.25f, 1.0f});
    light.bind();

    modelMatrix.loadIdentity();
//    modelMatrix.rotate(45, 0, 1, 0);
//    modelMatrix.rotate(-30, 1, 0, 0);
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
  
  @Override
  public void keyPressed(KeyEvent e) {

    switch (e.getKeyCode()) {
      case KeyEvent.VK_UP://gira sobre o eixo-x
        alpha = alpha + 0.1f;
        break;
      case KeyEvent.VK_DOWN://gira sobre o eixo-x
        alpha = alpha - 0.1f;
        break;
      case KeyEvent.VK_LEFT://gira sobre o eixo-y
        beta = beta - 0.1f;
        break;
      case KeyEvent.VK_RIGHT://gira sobre o eixo-y
        beta = beta + 0.1f;
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
    Example15 listener = new Example15();
    glCanvas.addGLEventListener(listener);

    Frame frame = new Frame("Example 05");
    frame.addKeyListener(listener);
    frame.setSize(400, 400);
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

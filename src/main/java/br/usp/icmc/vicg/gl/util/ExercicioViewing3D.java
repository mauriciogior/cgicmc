package br.usp.icmc.vicg.gl.util;

import br.usp.icmc.vicg.gl.matrix.Matrix4;
import br.usp.icmc.vicg.gl.model.SimpleModel;
import com.jogamp.opengl.util.AnimatorBase;
import com.jogamp.opengl.util.FPSAnimator;
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

/**
 * ExercicioViewing3D.java <BR>
 * author: Brian Paul (converted to Java by Ron Cemer and Sven Goethel)
 * <P>
 *
 * This version is equal to Brian Paul's version 1.2 1999/10/21
 */
public class ExercicioViewing3D implements GLEventListener {

  private final Shader shader; // Gerenciador dos shaders
  private final Matrix4 modelMatrix;
  private final Matrix4 projectionMatrix;
  private final Matrix4 viewMatrix;
  private final SimpleModel rectangle;

  public ExercicioViewing3D() {
    // Carrega os shaders
    shader = ShaderFactory.getInstance(ShaderFactory.ShaderType.VIEW_MODEL_PROJECTION_MATRIX_SHADER);
    modelMatrix = new Matrix4();
    projectionMatrix = new Matrix4();
    viewMatrix = new Matrix4();
    rectangle = new MyForm();
  }

  @Override
  public void init(GLAutoDrawable drawable) {
    // Get pipeline
    GL3 gl = drawable.getGL().getGL3();

    gl.glClearColor(0, 0, 0, 0);

    //inicializa os shaders
    shader.init(gl);

    //ativa os shaders
    shader.bind();

    //inicializa a matrix Model and Projection
    modelMatrix.init(gl, shader.getUniformLocation("u_modelMatrix"));
    projectionMatrix.init(gl, shader.getUniformLocation("u_projectionMatrix"));
    viewMatrix.init(gl, shader.getUniformLocation("u_viewMatrix"));

//    // Inicializa o sistema de coordenadas
//    projectionMatrix.loadIdentity();
//    projectionMatrix.perspective(45, 5, 0.1f, 10);
//    projectionMatrix.bind();
//    
//    System.out.println("Mpers =");
//    projectionMatrix.print();
//    System.out.println("");
    // Inicializa o sistema de coordenadas
    projectionMatrix.loadIdentity();
    projectionMatrix.ortho(
            -2.0f, 2.0f,
            -1.0f, 2.0f,
            -4.0f, 4.0f);
    projectionMatrix.bind();

    System.out.println("Mortho =");
    projectionMatrix.print();
    System.out.println("");

    viewMatrix.loadIdentity();
    viewMatrix.lookAt(2, 1, 0,
            0, 0, 0,
            0, 1, -1);
    viewMatrix.bind();

    System.out.println("Mwc,vc =");
    viewMatrix.print();
    System.out.println("");

    modelMatrix.loadIdentity();
    modelMatrix.rotate(45, 1, 2, 1);
    modelMatrix.scale(2, 2, 2);
    modelMatrix.bind();

    System.out.println("Model =");
    modelMatrix.print();
    System.out.println("");

    gl.glViewport(0, 0, 500, 500);

    //cria o objeto a ser desenhado
    rectangle.init(gl, shader);
  }

  @Override
  public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
  }

  @Override
  public void display(GLAutoDrawable drawable) {
    // Recupera o pipeline
    GL3 gl = drawable.getGL().getGL3();

    // Limpa o frame buffer com a cor definida
    gl.glClear(GL3.GL_COLOR_BUFFER_BIT);

    rectangle.bind();
    rectangle.draw();

    // Força execução das operações declaradas
    gl.glFlush();
  }

  @Override
  public void dispose(GLAutoDrawable glad) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  class MyForm extends SimpleModel {

    public MyForm() {
      vertex_buffer = new float[]{
        2, 0, 0,
        0, 1, 0,
        0, 0, 1};
    }

    @Override
    public void draw() {
      draw(GL.GL_LINE_LOOP);
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
    ExercicioViewing3D listener = new ExercicioViewing3D();
    glCanvas.addGLEventListener(listener);

    Frame frame = new Frame("ExercicioViewing3D");
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

package br.usp.icmc.vicg.gl.raytracing;

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
import com.jogamp.opengl.util.AnimatorBase;
import com.jogamp.opengl.util.FPSAnimator;

public class RayTracing implements GLEventListener {

  private final Shader shader;
  private final SimpleModel rectangle;

  public RayTracing() {
    // Carrega os shaders
    shader = new Shader("raytracing_vertex.glsl", "raytracing_fragment.glsl");
    rectangle = new MyForm();
  }

  @Override
  public void init(GLAutoDrawable drawable) {
    // Get pipeline
    GL3 gl = drawable.getGL().getGL3();

    // Print OpenGL version
    System.out.println("OpenGL Version: " + gl.glGetString(GL.GL_VERSION) + "\n");

    gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);

    //inicializa os shaders
    shader.init(gl);

    //ativa os shaders
    shader.bind();

    //cria o objeto a ser desenhado
    rectangle.init(gl, shader);
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
  public void reshape(GLAutoDrawable drawable, int x, int y, 
          int width, int height) {
  }

  @Override
  public void dispose(GLAutoDrawable drawable) {
    // Apaga o buffer
    rectangle.dispose();
  }

  class MyForm extends SimpleModel {

    public MyForm() {
      vertex_buffer = new float[]{
        -1, -1, 0,
        1, -1, 0,
        1, 1, 0,
        1, 1, 0,
        -1, 1, 0,
        -1, -1, 0};
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
    RayTracing listener = new RayTracing();
    glCanvas.addGLEventListener(listener);

    Frame frame = new Frame("Raytracing");
    frame.setSize(1000,1000);
    frame.add(glCanvas);
    final AnimatorBase animator = new FPSAnimator(glCanvas, 1, true);

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

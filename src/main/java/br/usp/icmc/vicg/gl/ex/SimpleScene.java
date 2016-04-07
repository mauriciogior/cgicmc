/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.usp.icmc.vicg.gl.ex;

import br.usp.icmc.vicg.gl.jwavefront.JWavefrontObject;
import br.usp.icmc.vicg.gl.matrix.Matrix4;
import br.usp.icmc.vicg.gl.model.SimpleModel;
import br.usp.icmc.vicg.gl.model.Sphere;
import br.usp.icmc.vicg.gl.model.WiredCube;
import br.usp.icmc.vicg.gl.util.Shader;
import br.usp.icmc.vicg.gl.util.ShaderFactory;
import javax.media.opengl.GL;
import javax.media.opengl.GL3;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;

/**
 *
 * @author paulovich
 */
public class SimpleScene implements GLEventListener {

  private Shader shader;
  private SimpleModel cube;
  private SimpleModel sphere;
  private JWavefrontObject object;
  private Matrix4 modelMatrix;
  private float delta;
  private float inc;
  
  private int color_handle;
  
  public SimpleScene() {
    shader = ShaderFactory.getInstance(ShaderFactory.ShaderType.SIMPLE_COLOR_SHADER);
    cube = new WiredCube();
    sphere = new Sphere();
    modelMatrix = new Matrix4();
    delta = 0;
    inc = 0.01f;
  }
  
  @Override
  public void init(GLAutoDrawable glad) {
    GL3 gl = glad.getGL().getGL3();

    gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
    
    shader.init(gl);
    shader.bind();
    
    cube.init(gl, shader);
    sphere.init(gl, shader);
    
    modelMatrix.init(gl, shader.getUniformLocation("u_modelMatrix"));
    
    color_handle = shader.getUniformLocation("u_color");
  }

  @Override
  public void display(GLAutoDrawable glad) {
    GL3 gl = glad.getGL().getGL3();
    
    gl.glClear(GL.GL_COLOR_BUFFER_BIT);
    
    gl.glUniform3f(color_handle, 1.0f, 0.0f, 0.0f);
    
    modelMatrix.loadIdentity();
    modelMatrix.translate(0.5f, 0.5f, 0);
    modelMatrix.rotate(30, 1, 1, 0);
    modelMatrix.scale(0.25f, 0.25f, 0.25f);
    modelMatrix.bind();
    cube.bind();
    cube.draw();
    
    gl.glUniform3f(color_handle, 0.0f, 1.0f, 0.0f);
    
    if(delta > 0.5f || delta < 0) {
      inc *= -1;
    }
    delta += inc;
    
    modelMatrix.loadIdentity();
    modelMatrix.translate(0, delta, 0);
    modelMatrix.translate(0.5f, -0.5f, 0);
    modelMatrix.rotate(200*delta, 1, 1, 0);
    modelMatrix.scale(0.125f, 0.125f, 0.125f);
    modelMatrix.bind();
    sphere.bind();
    sphere.draw();
    
    modelMatrix.loadIdentity();
    modelMatrix.translate(0.5f, 0.5f, 0);
    modelMatrix.rotate(30, 1, 1, 0);
    modelMatrix.scale(0.125f, 0.125f, 0.125f);
    modelMatrix.bind();
    sphere.bind();
    sphere.draw();
  }

  @Override
  public void dispose(GLAutoDrawable glad) {
  }

  @Override
  public void reshape(GLAutoDrawable glad, int i, int i1,
          int i2, int i3) {
  }

}

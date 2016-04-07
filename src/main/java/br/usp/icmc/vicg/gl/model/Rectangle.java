/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.usp.icmc.vicg.gl.model;

import javax.media.opengl.GL;

/**
 *
 * @author paulovich
 */
public class Rectangle extends SimpleModel {

  public Rectangle() {
    vertex_buffer = new float[]{
      -0.5f, -0.5f, 0.0f,
      0.5f, -0.5f, 0.0f,
      0.5f, 0.5f, 0.0f,
      
      0.5f, 0.5f, 0.0f,
      -0.5f, 0.5f, 0.0f,
      -0.5f, -0.5f, 0.0f};

    normal_buffer = new float[]{
      0, 0, 1,
      0, 0, 1,
      0, 0, 1,
      
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

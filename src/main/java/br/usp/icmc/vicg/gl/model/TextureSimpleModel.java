/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.usp.icmc.vicg.gl.model;

import br.usp.icmc.vicg.gl.util.Shader;
import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.util.awt.ImageUtil;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.media.opengl.GL;
import javax.media.opengl.GL3;
import javax.media.opengl.GLProfile;

/**
 *
 * @author PC
 */
public abstract class TextureSimpleModel {

  protected GL3 gl;
  private int vertex_positions_handle;
  private int vertex_normals_handle;
  private int vertex_texcoords_handle;
  private int texture_handle;
  private int[] vao;
  private com.jogamp.opengl.util.texture.Texture texturedata;
  
  protected float[] vertex_buffer;
  protected float[] normal_buffer;
  protected float[] texture_buffer;

  public abstract void draw();

  public void draw(int primitive) {
    // Desenha o buffer carregado em memória (triangulos)
    gl.glDrawArrays(primitive, 0, vertex_buffer.length / 3);
    gl.glBindVertexArray(0);
  }

  public void init(GL3 gl, Shader shader) {
    this.gl = gl;
    this.vertex_positions_handle = shader.getAttribLocation("a_position");
    this.vertex_normals_handle = shader.getAttribLocation("a_normal");
    this.vertex_texcoords_handle = shader.getAttribLocation("a_texcoord");
    this.texture_handle = shader.getUniformLocation("u_texture");
    create_object(gl);
  }

  public void bind() {
    gl.glActiveTexture(GL3.GL_TEXTURE0);
    texturedata.bind(gl);
    
    gl.glBindVertexArray(vao[0]);
  }

  public void dispose() {
  }

  public void loadTexture(String filename) throws IOException {
    BufferedImage image = ImageIO.read(getClass().getClassLoader().getResourceAsStream(filename));
    ImageUtil.flipImageVertically(image); //vertically flip the image

    texturedata = AWTTextureIO.newTexture(GLProfile.get(GLProfile.GL3), image, true);
    texturedata.setTexParameteri(gl, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
    texturedata.setTexParameteri(gl, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
  }

  private void create_object(GL3 gl) {
    vao = new int[1];
    gl.glGenVertexArrays(1, vao, 0);
    gl.glBindVertexArray(vao[0]);

    // create vertex positions buffer
    int vbo[] = new int[3];
    gl.glGenBuffers(3, vbo, 0);

    //the positions buffer
    gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, vbo[0]); // Bind vertex buffer 
    gl.glBufferData(GL3.GL_ARRAY_BUFFER, vertex_buffer.length * Buffers.SIZEOF_FLOAT,
            Buffers.newDirectFloatBuffer(vertex_buffer), GL3.GL_STATIC_DRAW);
    gl.glEnableVertexAttribArray(vertex_positions_handle);
    gl.glVertexAttribPointer(vertex_positions_handle, 3, GL3.GL_FLOAT, false, 0, 0);
    gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, 0);

    if (normal_buffer != null) {
      //the normals buffer
      gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, vbo[1]); // Bind normals buffer
      gl.glBufferData(GL3.GL_ARRAY_BUFFER, normal_buffer.length * Buffers.SIZEOF_FLOAT,
              Buffers.newDirectFloatBuffer(normal_buffer), GL3.GL_STATIC_DRAW);
      gl.glEnableVertexAttribArray(vertex_normals_handle);
      gl.glVertexAttribPointer(vertex_normals_handle, 3, GL3.GL_FLOAT, false, 0, 0);
      gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, 0);
    }

    if (texture_buffer != null) {
      gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vbo[2]); // Bind normals buffer
      gl.glBufferData(GL3.GL_ARRAY_BUFFER, texture_buffer.length * Buffers.SIZEOF_FLOAT,
              Buffers.newDirectFloatBuffer(texture_buffer), GL3.GL_STATIC_DRAW);
      gl.glEnableVertexAttribArray(vertex_texcoords_handle);
      gl.glVertexAttribPointer(vertex_texcoords_handle, 2, GL3.GL_FLOAT, false, 0, 0);
      gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, 0);
      gl.glUniform1i(texture_handle, 0);
    }

  }
}

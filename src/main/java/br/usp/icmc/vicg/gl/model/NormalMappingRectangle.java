/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.usp.icmc.vicg.gl.model;

import br.usp.icmc.vicg.gl.util.Shader;
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
 * @author paulovich
 */
public class NormalMappingRectangle extends TextureSimpleModel {

  private int normalmap_handle;
  private com.jogamp.opengl.util.texture.Texture normalmapdata;

  public NormalMappingRectangle() {
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

    texture_buffer = new float[]{
      0, 0,
      1, 0,
      1, 1,
      1, 1,
      0, 1,
      0, 0
    };
  }

  @Override
  public void init(GL3 gl, Shader shader) {
    super.init(gl, shader);
    
    normalmap_handle = shader.getUniformLocation("u_normalmap");
    gl.glUniform1i(normalmap_handle, 1);
  }

  @Override
  public void bind() {
    super.bind();
    
    gl.glActiveTexture(GL3.GL_TEXTURE1);
    normalmapdata.bind(gl);
  }

  public void loadNormalMap(String filename) throws IOException {
    BufferedImage image = ImageIO.read(getClass().getClassLoader().getResourceAsStream(filename));
    ImageUtil.flipImageVertically(image); //vertically flip the image

    normalmapdata = AWTTextureIO.newTexture(GLProfile.get(GLProfile.GL3), image, true);
    normalmapdata.setTexParameteri(gl, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
    normalmapdata.setTexParameteri(gl, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
  }

  @Override
  public void draw() {
    draw(GL.GL_TRIANGLES);
  }
}

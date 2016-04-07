package br.usp.icmc.vicg.gl.core;

import br.usp.icmc.vicg.gl.util.Shader;
import javax.media.opengl.GL3;

import com.jogamp.common.nio.Buffers;
import java.util.Arrays;

public class Light {

  private GL3 gl;
  
  private float[] ambientColor;
  private float[] diffuseColor;
  private float[] specularColor;
  private float[] position;
  private float constAttenuation;
  private float linAttenuation;
  private float quadAttenuation;

  private int constattHandle;
  private int linattHandle;
  private int quadattHandle;
  private int positionHandle;
  private int ambientColorHandle;
  private int diffuseColorHandle;
  private int specularColorHandle;

  public Light() {
    setPosition(new float[]{0.0f, 0.0f, 1.0f, 0.0f});
    setAmbientColor(new float[]{0.0f, 0.0f, 0.0f, 1.0f});
    setDiffuseColor(new float[]{1.0f, 1.0f, 1.0f, 1.0f});
    setSpecularColor(new float[]{1.0f, 1.0f, 1.0f, 1.0f});
    setConstantAttenuation(1.0f);
    setLinearAttenuation(0.0f);
    setQuadraticAttenuation(0.0f);
  }

  public final void setPosition(float[] position) {
    this.position = Arrays.copyOf(position, position.length);
  }

  public final void setAmbientColor(float[] ambientColor) {
    this.ambientColor = Arrays.copyOf(ambientColor, ambientColor.length);
  }

  public final void setDiffuseColor(float[] diffuseColor) {
    this.diffuseColor = Arrays.copyOf(diffuseColor, diffuseColor.length);
  }

  public final void setSpecularColor(float[] specularColor) {
    this.specularColor = Arrays.copyOf(specularColor, specularColor.length);
  }

  public final void setConstantAttenuation(float attenuation) {
    this.constAttenuation = attenuation;
  }

  public final void setLinearAttenuation(float attenuation) {
    this.linAttenuation = attenuation;
  }

  public final void setQuadraticAttenuation(float attenuation) {
    this.quadAttenuation = attenuation;
  }

  public void init(GL3 gl, Shader shader) {
    this.gl = gl;
    
    this.positionHandle = shader.getUniformLocation("u_light.position");
    this.ambientColorHandle = shader.getUniformLocation("u_light.ambientColor");
    this.diffuseColorHandle = shader.getUniformLocation("u_light.diffuseColor");
    this.specularColorHandle = shader.getUniformLocation("u_light.specularColor");

    this.constattHandle = shader.getUniformLocation("u_light.constantAttenuation");
    this.linattHandle = shader.getUniformLocation("u_light.linearAttenuation");
    this.quadattHandle = shader.getUniformLocation("u_light.quadraticAttenuation");
  }

  public void bind() {
    gl.glUniform4fv(positionHandle, 1, Buffers.newDirectFloatBuffer(position));
    gl.glUniform4fv(ambientColorHandle, 1, Buffers.newDirectFloatBuffer(ambientColor));
    gl.glUniform4fv(diffuseColorHandle, 1, Buffers.newDirectFloatBuffer(diffuseColor));
    gl.glUniform4fv(specularColorHandle, 1, Buffers.newDirectFloatBuffer(specularColor));
    
    gl.glUniform1f(constattHandle, constAttenuation);
    gl.glUniform1f(linattHandle, linAttenuation);
    gl.glUniform1f(quadattHandle, quadAttenuation);
  }
}

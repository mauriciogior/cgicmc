/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.usp.icmc.vicg.gl.util;

/**
 *
 * @author paulovich
 */
public class ShaderFactory {

  public enum ShaderType {

    SIMPLE_SHADER,
    SIMPLE_COLOR_SHADER,
    TRANSFORM_SHADER,
    MODEL_MATRIX_SHADER,
    MODEL_PROJECTION_MATRIX_SHADER,
    VIEW_MODEL_PROJECTION_MATRIX_SHADER,
    LIGHT_SHADER,
    TEXTURE_SHADER,
    NORMALMAP_SHADER,
    COMPLETE_SHADER
  };

  public static Shader getInstance(ShaderType type) {
    if (type == ShaderType.SIMPLE_SHADER) {
      return new Shader("simple_vertex.glsl", "simple_fragment.glsl");
    } else if (type == ShaderType.SIMPLE_COLOR_SHADER) {
      return new Shader("model_vertex.glsl", "simple_fragment_color.glsl");
    } else if (type == ShaderType.TRANSFORM_SHADER) {
      return new Shader("transform_vertex.glsl", "simple_fragment.glsl");
    } else if (type == ShaderType.MODEL_MATRIX_SHADER) {
      return new Shader("model_vertex.glsl", "simple_fragment.glsl");
    } else if (type == ShaderType.MODEL_PROJECTION_MATRIX_SHADER) {
      return new Shader("model_projection_vertex.glsl", "simple_fragment.glsl");
    } else if (type == ShaderType.VIEW_MODEL_PROJECTION_MATRIX_SHADER) {
      return new Shader("view_model_projection_vertex.glsl", "simple_fragment.glsl");
    } else if (type == ShaderType.LIGHT_SHADER) {
      return new Shader("light_vertex.glsl", "light_fragment.glsl");
    } else if (type == ShaderType.TEXTURE_SHADER) {
      return new Shader("texture_vertex.glsl", "texture_fragment.glsl");
    }  else if (type == ShaderType.NORMALMAP_SHADER) {
      return new Shader("normalmap_vertex.glsl", "normalmap_fragment.glsl");
    } else if (type == ShaderType.COMPLETE_SHADER) {
      return new Shader("complete_vertex.glsl", "complete_fragment.glsl");
    }
    return null;
  }
}

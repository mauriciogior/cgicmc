/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.usp.icmc.vicg.gl.model;

import java.util.ArrayList;
import javax.media.opengl.GL;

/**
 *
 * @author PC
 */
public class SolidSphere extends SimpleModel {

  private final int POLUDNIKOV = 36;
  private final int ROVNOBEZIEK = 18;
  private final double UHOL_KROK = ((2 * Math.PI / 360.0) * 10.0);

  public SolidSphere() {
    ArrayList<Float> vertex = new ArrayList<>();

    int p2 = POLUDNIKOV / 2;
    int r2 = ROVNOBEZIEK / 2;
    for (int y = -r2; y < r2; ++y) {
      double cy = Math.cos(y * UHOL_KROK);
      double cy1 = Math.cos((y + 1) * UHOL_KROK);
      double sy = Math.sin(y * UHOL_KROK);
      double sy1 = Math.sin((y + 1) * UHOL_KROK);

      for (int i = -p2; i < p2; ++i) {
        double ci = Math.cos(i * UHOL_KROK);
        double si = Math.sin(i * UHOL_KROK);

        vertex.add((float) (ci * cy));
        vertex.add((float) (sy));
        vertex.add((float) (si * cy));

        vertex.add((float) (ci * cy1));
        vertex.add((float) (sy1));
        vertex.add((float) (si * cy1));
      }
    }

    vertex_buffer = new float[vertex.size()];
    normal_buffer = new float[vertex.size()];
    for (int i = 0; i < vertex.size(); i++) {
      vertex_buffer[i] = normal_buffer[i] = vertex.get(i);
    }
  }

  @Override
  public void draw() {
    draw(GL.GL_TRIANGLE_STRIP);
  }
}

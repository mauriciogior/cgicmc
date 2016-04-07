/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.usp.icmc.vicg.gl.ex;

import com.jogamp.opengl.util.AnimatorBase;
import com.jogamp.opengl.util.FPSAnimator;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.JFrame;

/**
 *
 * @author paulovich
 */
public class Main {
  
  public static void main(String args[]) {
    GLProfile profile = GLProfile.get(GLProfile.GL3);
    
    GLCapabilities cap = new GLCapabilities(profile);
    cap.setDoubleBuffered(true);
    cap.setHardwareAccelerated(true);
    
    GLCanvas canvas = new GLCanvas(cap);
    canvas.addGLEventListener(new SimpleScene());
    
    AnimatorBase animator = new FPSAnimator(canvas, 30);
    
    JFrame frame = new JFrame();
    frame.getContentPane().add(canvas);
    frame.setSize(800, 800);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setVisible(true);
    
    animator.start();
  
  }
  
}




















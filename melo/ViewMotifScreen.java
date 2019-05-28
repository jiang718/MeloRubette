/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package melo;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 *
 * @
 */
public class ViewMotifScreen extends javax.swing.JPanel {

    public ViewMotifScreen(Score motifT) {
        motif = motifT;
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setPreferredSize(new Dimension(500, 500));
        initComponents();
    }

    private void initComponents() {
    }
    
    
    private Score motif; 
}

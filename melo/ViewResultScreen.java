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
public class ViewResultScreen extends javax.swing.JPanel {

    public ViewResultScreen(MeloRubette meloRubetteT) {
        meloRubette = meloRubetteT;
        initComponents();
    }

    private void initLayout() {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    }

    private void initComponents() {
        weightedMotifPanel = new SingleResultPanel(meloRubette);
        pitchOnsetAxisPanel = new PitchOnsetAxisPanel(meloRubette);

        this.add(weightedMotifPanel);
        this.add(pitchOnsetAxisPanel);
        initLayout();
    }
    
    
    private SingleResultPanel weightedMotifPanel; 
    private PitchOnsetAxisPanel pitchOnsetAxisPanel; 
    private MeloRubette meloRubette;
}

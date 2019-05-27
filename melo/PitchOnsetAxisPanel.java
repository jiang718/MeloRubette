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
public class PitchOnsetAxisPanel extends javax.swing.JPanel {

    public PitchOnsetAxisPanel(MeloRubette meloRubetteT) {
        meloRubette = meloRubetteT;
        initComponents();
    }

    private void initLayout() {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    }

    private void initComponents() {
        weightedScorePanel = new SingleResultPanel(meloRubette);
        weightedOnsetPanel = new SingleResultPanel(meloRubette);

        this.add(weightedScorePanel);
        this.add(weightedOnsetPanel);
        initLayout();
    }
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private SingleResultPanel weightedOnsetPanel; 
    private SingleResultPanel weightedScorePanel; 
    private MeloRubette meloRubette;
}

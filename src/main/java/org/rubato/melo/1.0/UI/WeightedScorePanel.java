/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rubato.melo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.*;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.lang.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import java.util.*;

/**
 *
 * @
 */
public class WeightedScorePanel extends SingleResultPanel {

    public WeightedScorePanel(MeloRubette meloRubetteT) {
        super(meloRubetteT);
        this.setLayout(new FlowLayout());
        this.setPreferredSize(new Dimension(1600, 600));

        init(); }

    private void init() {
        score = meloRubette.getWeightedScore();

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new FlowLayout());
    }



    /************** Actions START ****************/
    /************** Actions END ****************/

    private Score score;
}


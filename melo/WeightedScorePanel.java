/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package melo;

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

    public WeightedMotifPanel(MeloRubette meloRubetteT) {
        super(meloRubetteT);
        this.setLayout(new FlowLayout());
        this.setPreferredSize(new Dimension(1600, 600));

        init();
    }

    private void init() {
        score = meloRubette.getWeightedScore();

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new FlowLayout());
    }



    /************** Actions START ****************/
    /************** Actions END ****************/

    private Score score;
}

class ScoreRenderer extends JLabel implements ListCellRenderer<Score> {
    @Override
    public Component getListCellRendererComponent(JList<? extends Score> list, Score score,
		int index, boolean isSelected, boolean cellHasFocus) {
        setText("Motif " + (index + 1));
        if (isSelected) {
            //setBackground(HIGHLIGHT_COLOR);
            System.out.println("Motif Number " + (index+1) + " is selected");
            setOpaque(true);
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
            setText("Motif " + (index + 1));
            System.out.println("Motif Number " + (index+1) + " is selected (after)");
        } else {
            System.out.println("Motif Number " + (index+1) + " is not selected");
            setOpaque(false);
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }
        return this;
    }
     
}

class ShelfIdJListSelectionHandler implements ListSelectionListener {
    public ShelfIdJListSelectionHandler(WeightedMotifPanel fatherT) {
        father = fatherT;
    }
    public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) {
            return;
        }
        father.changeMotifShelfSelection(e.getFirstIndex(), e.getLastIndex());
    }
    private WeightedMotifPanel father;
}

class MotifJListSelectionHandler implements ListSelectionListener {
    public MotifJListSelectionHandler(WeightedMotifPanel fatherT) {
        father = fatherT;
    }
    public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) {
            return;
        }
        father.showViewMotifPanel();
    }
    WeightedMotifPanel father;
}

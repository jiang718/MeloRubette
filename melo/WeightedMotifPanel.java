/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package melo;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.*;
import java.awt.FlowLayout;
import java.awt.Point;
import java.lang.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import java.util.*;

/**
 *
 * @
 */
public class WeightedMotifPanel extends SingleResultPanel {

    public WeightedMotifPanel(MeloRubette meloRubetteT) {
        super(meloRubetteT);
        initComponents();
    }


    private void initComponents() {
        motifLib = meloRubette.getMotifLib();
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new FlowLayout());

        motifLibPanel = new JPanel();
        motifLibPanel.setLayout(new FlowLayout());
        //motifLibPanel.setBackground(Color.red);

        //motifLibPanel.setBorder(BorderFactory.createLineBorder(Color.black));

        initMotifLibPanel();

        motifShelfPanel = new JPanel();
        motifShelfPanel.setLayout(new FlowLayout());
        initMotifShelfPanel();
        
        initMotifLibPanelListener();

        contentPanel.add(motifLibPanel);
        contentPanel.add(motifShelfPanel);

        this.add(contentPanel);

        initLayout();
    }

    private void initMotifLibPanel() {
        DefaultListModel<String> model = new DefaultListModel<>();  
        for (int shelfId = 0; shelfId < motifLib.size(); shelfId++) {
            model.addElement("Motif Shelf of Length (" + (shelfId + 1) + ")");
        }
        motifShelfIdJList = new JList<>(model);
        if (motifLib.size() > 0) {
            motifShelfIdJList.setSelectedIndex(0);
        }
        motifShelfIdScrollPane = new JScrollPane();
	    motifLibPanel.add(motifShelfIdJList);
        motifLibPanel.add(motifShelfIdScrollPane);
        motifShelfIdScrollPane.setViewportView(motifShelfIdJList);
        motifShelfIdScrollPane.setPreferredSize(new Dimension(790, 150)); 
        motifLibPanel.setPreferredSize(new Dimension(800, 150));
    }

    private void initMotifShelfPanel() {
        motifLibJList = new ArrayList<>(); 
        for (int shelfId = 0; shelfId < motifLib.size(); shelfId++) { 
            DefaultListModel<Score> model = new DefaultListModel<>();  
            for (int motifId = 0; motifId < motifLib.get(shelfId).size(); motifId++) {
                model.addElement(motifLib.get(shelfId).get(motifId));
            }
			JList<Score> motifJList = new JList<>(model);
			motifJList.setCellRenderer(new ScoreRenderer());
            motifLibJList.add(motifJList);
        }
        int shelfId = motifShelfIdJList.getSelectedIndex();
        System.out.println("Selected shelf Id: " + shelfId);

        motifScrollPane = new JScrollPane();
        motifShelfPanel.add(motifScrollPane);
        //motifShelfPanel.setBackground(Color.green);
        if (shelfId >= 0) {
	        motifShelfPanel.add(motifLibJList.get(shelfId));	
            motifScrollPane.setViewportView(motifLibJList.get(shelfId));
            motifScrollPane.setPreferredSize(new Dimension(790, 150)); 
        }
        motifShelfPanel.setPreferredSize(new Dimension(800, 150));
    }

    private void initMotifLibPanelListener() {
        motifShelfIdJList.getSelectionModel()
            .addListSelectionListener(
                new ShelfIdJListSelectionHandler(this)
            );
    }


    private void initLayout() {
        this.setLayout(new FlowLayout());
        this.setPreferredSize(new Dimension(1600, 150));
    }


    /************** Actions START ****************/
    public void changeMotifShelfSelection(int firstIndex, int lastIndex) {
        if (firstIndex != lastIndex) {
            motifShelfPanel.remove(motifLibJList.get(firstIndex));
            motifShelfPanel.add(motifLibJList.get(lastIndex));
            System.out.println("I really changed!!!!! from " + firstIndex + " " + lastIndex);

            motifScrollPane.setViewportView(motifLibJList.get(lastIndex));
            motifScrollPane.getViewport().setViewPosition(new Point(0,0));
        }
    }
    /************** Actions END ****************/

	private JPanel motifLibPanel;
    private JPanel motifShelfPanel;

    private List<List<Score>> motifLib;
    private JList motifShelfIdJList;
    private JScrollPane motifShelfIdScrollPane;
    private JScrollPane motifScrollPane;
    private List<JList<Score>> motifLibJList;
}

class ScoreRenderer extends JLabel implements ListCellRenderer<Score> {
    @Override
    public Component getListCellRendererComponent(JList<? extends Score> list, Score score,
		int index, boolean isSelected, boolean cellHasFocus) {
        setText("Motif " + (index + 1));
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


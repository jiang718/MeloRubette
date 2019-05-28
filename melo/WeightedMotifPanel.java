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
public class WeightedMotifPanel extends SingleResultPanel {

    public WeightedMotifPanel(MeloRubette meloRubetteT) {
        super(meloRubetteT);
        this.setLayout(new FlowLayout());
        this.setPreferredSize(new Dimension(1600, 150));

        init();
    }


    private void init() {
        motifLib = meloRubette.getMotifLib();

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new FlowLayout());

        motifLibPanel = new JPanel();
        motifLibPanel.setLayout(new FlowLayout());
        initMotifLibPanel();

        motifShelfPanel = new JPanel();
        motifShelfPanel.setLayout(new FlowLayout());
        initMotifShelfPanel();

        contentPanel.add(motifLibPanel);
        contentPanel.add(motifShelfPanel);
        this.add(contentPanel);

        //init listener
        initMotifLibPanelListener();
    }

    private void initMotifLibPanel() {
        motifLibPanel.setPreferredSize(new Dimension(800, 150));

        DefaultListModel<String> model = new DefaultListModel<>();  
        for (int shelfId = 0; shelfId < motifLib.size(); shelfId++) {
            model.addElement("Motif Shelf of Length (" + (shelfId + 1) + ")");
        }
        motifShelfIdJList = new JList<>(model);
        if (motifLib.size() > 0) {
            motifShelfIdJList.setSelectedIndex(0);
        }

        motifShelfIdScrollPane = new JScrollPane();
        motifShelfIdScrollPane.setPreferredSize(new Dimension(790, 150));

	    motifLibPanel.add(motifShelfIdJList);
        motifLibPanel.add(motifShelfIdScrollPane);

        //setViewportView can only apply to the component that has been added to the panel
        motifShelfIdScrollPane.setViewportView(motifShelfIdJList);
    }

    private void initMotifShelfPanel() {
        motifShelfPanel.setPreferredSize(new Dimension(800, 150));

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
        motifScrollPane.setPreferredSize(new Dimension(790, 150)); 

        motifShelfPanel.add(motifScrollPane);
        if (shelfId >= 0) {
            motifJList = motifLibJList.get(shelfId);
	        motifShelfPanel.add(motifJList);	
            motifScrollPane.setViewportView(motifJList);
        }
    }

    private void initMotifLibPanelListener() {
        motifShelfIdJList.getSelectionModel()
            .addListSelectionListener(
                new ShelfIdJListSelectionHandler(this)
            );
        for (int shelfId = 0; shelfId < motifLib.size(); shelfId++) {
            motifLibJList.get(shelfId).getSelectionModel()
                .addListSelectionListener(
                    new MotifJListSelectionHandler(this)
                );
        }
    }



    /************** Actions START ****************/
    public void changeMotifShelfSelection(int firstIndex, int lastIndex) {
        if (firstIndex != lastIndex) {
            motifShelfPanel.remove(motifLibJList.get(firstIndex));
            motifJList = motifLibJList.get(lastIndex);
            motifJList.clearSelection();
            motifShelfPanel.add(motifJList);
            System.out.println("I really changed!!!!! from " + firstIndex + " " + lastIndex);

            motifScrollPane.setViewportView(motifLibJList.get(lastIndex));
            motifScrollPane.getViewport().setViewPosition(new Point(0,0));

        }
    }

    public void showViewMotifPanel() {
        JFrame frame = new JFrame("Motif");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        viewMotifScreen = new ViewMotifScreen((Score)motifJList.getSelectedValue());

        frame.getContentPane().add(BorderLayout.CENTER, viewMotifScreen);
        frame.pack();
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation(dim.width/2-frame.getSize().width/2, frame.getHeight()/2-frame.getSize().height/2);
        frame.setVisible(true);
        frame.setResizable(false);
        viewMotifScreen.requestFocus();
    }
    /************** Actions END ****************/

	private JPanel motifLibPanel;
    private JPanel motifShelfPanel;
    private JPanel viewMotifScreen;

    private List<List<Score>> motifLib;
    private JList motifShelfIdJList;
    private List<JList<Score>> motifLibJList;
    private JList<Score> motifJList;

    private JScrollPane motifShelfIdScrollPane;
    private JScrollPane motifScrollPane;
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

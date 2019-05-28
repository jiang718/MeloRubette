/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package melo;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;

/**
 *
 * @
 */
public class SingleResultPanel extends javax.swing.JPanel {

    public SingleResultPanel(MeloRubette meloRubetteT) {
        meloRubette = meloRubetteT;
        initComponents();
    }

    private void initLayout() {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setSize(1024, 200);
    }

    private void initComponents() {
        initLayout();

        //
        

        //JTextArea textArea = new JTextArea(15, 50);
        //textArea.setWrapStyleWord(true);
        //textArea.setEditable(false);
        //JScrollPane scroller = new JScrollPane(textArea);
        //scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        //scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        //JPanel inputpanel = new JPanel();
        //inputpanel.setLayout(new FlowLayout());
        //JTextField input = new JTextField(20);
        //JButton button = new JButton("Enter");
        //DefaultCaret caret = (DefaultCaret) textArea.getCaret();
        //caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        //this.add(scroller);
        //inputpanel.add(input);
        //inputpanel.add(button);
        //this.add(inputpanel);
    }

    protected MeloRubette meloRubette;
}

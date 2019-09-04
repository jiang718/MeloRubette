/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rubato.melo;
import org.jfree.chart.*;

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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *
 * @
 */

public class WeightedScorePanel extends SingleResultPanel {

    public WeightedScorePanel(MeloRubette meloRubetteT) {
        super(meloRubetteT);
        this.setLayout(new FlowLayout());
        this.setPreferredSize(new Dimension(1600, 600));

        init();
    }

    private void init() {
        score = meloRubette.getWeightedScore();

        // XYDataset dataset = createDataset();
        // JFreeChart chart = createChart(dataset);
        // ChartPanel chartPanel = new ChartPanel(chart);
        // chartPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        // chartPanel.setBackground(Color.white);
        // add(chartPanel);
        // System.out.println("Add chartPanel to weightedScorePanel");
    }

    // private XYDataset createDataset() {

    //     XYSeries series = new XYSeries("2016");
    //     series.add(18, 567);
    //     series.add(20, 612);
    //     series.add(25, 800);
    //     series.add(30, 980);
    //     series.add(40, 1410);
    //     series.add(50, 2350);

    //     XYSeriesCollection dataset = new XYSeriesCollection();
    //     dataset.addSeries(series);

    //     return dataset;
    // }

    //private JFreeChart createChart(XYDataset dataset) {

    //    JFreeChart chart = ChartFactory.createXYLineChart(
    //            "Average salary per age", 
    //            "Age", 
    //            "Salary (€)", 
    //            dataset, 
    //            PlotOrientation.VERTICAL,
    //            true, 
    //            true, 
    //            false 
    //    );

    //    // XYPlot plot = chart.getXYPlot();

    //    // XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
    //    // renderer.setSeriesPaint(0, Color.RED);
    //    // renderer.setSeriesStroke(0, new BasicStroke(2.0f));

    //    // plot.setRenderer(renderer);
    //    // plot.setBackgroundPaint(Color.white);

    //    // plot.setRangeGridlinesVisible(true);
    //    // plot.setRangeGridlinePaint(Color.BLACK);

    //    // plot.setDomainGridlinesVisible(true);
    //    // plot.setDomainGridlinePaint(Color.BLACK);

    //    // chart.getLegend().setFrame(BlockBorder.NONE);

    //    // chart.setTitle(new TextTitle("Average Salary per Age",
    //    //                 new Font("Serif", java.awt.Font.BOLD, 18)
    //    //         )
    //    // );

    //    return chart;

    //}


    /************** Actions START ****************/
    /************** Actions END ****************/

    private Score score;
}


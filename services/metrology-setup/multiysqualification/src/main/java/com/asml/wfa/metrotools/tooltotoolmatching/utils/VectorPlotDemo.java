package com.asml.wfa.metrotools.tooltotoolmatching.utils;

import com.asml.wfa.metrotools.tooltotoolmatching.gui.widgets.*;
import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.asml.wfa.common.guicomponents.widgets.datasetselector.DatasetSelector;
import com.jidesoft.plaf.LookAndFeelFactory;

public class VectorPlotDemo extends JFrame {

    private static final long serialVersionUID = 1L;
    private final VectorWaferPlot waferPlot;

    public VectorPlotDemo() {

        waferPlot = new VectorWaferPlot("Plot", "nm", true);
        waferPlot.resetZoomingAndScalingDelayed();

        initPlot1();

        waferPlot.addAll();

        final DatasetSelector dsSel = new DatasetSelector();
        dsSel.addDatasetSelectorComponent(waferPlot);

        this.setTitle("Vector Plot Demo");
        this.setSize(1280, 800);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setLayout(new BorderLayout());
        this.add(dsSel);
        this.setVisible(true);
    }

    private void initPlot1() {
        waferPlot.addVectorValue(7.5, 7.5, 7.5, 7.5, 20, -20, true);

        waferPlot.addField(0, 0, 20, 20, 2, 3);
        waferPlot.addField(-20, 0, 20, 20, 2, 3);
        waferPlot.addField(0, -20, 20, 20, 2, 3);
        waferPlot.addField(20, 0, 20, 20, 2, 3);
        waferPlot.addField(0, 20, 20, 20, 2, 3);
    }

    public static void main(final String[] args) {
        LookAndFeelFactory.installDefaultLookAndFeelAndExtension();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new VectorPlotDemo();
            }
        });
    }
}

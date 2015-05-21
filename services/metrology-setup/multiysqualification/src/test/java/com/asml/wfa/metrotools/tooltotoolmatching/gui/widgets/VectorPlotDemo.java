package com.asml.wfa.metrotools.tooltotoolmatching.gui.widgets;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.asml.wfa.common.guicomponents.widgets.datasetselector.DatasetSelector;
import com.jidesoft.plaf.LookAndFeelFactory;

public class VectorPlotDemo extends JFrame {

    private static final long serialVersionUID = 1L;
    private final VectorWaferPlot waferPlot;
  private final VectorWaferPlot waferPlot2;

    public VectorPlotDemo() {

        waferPlot = new VectorWaferPlot("Plot", "nm", true);
        waferPlot.resetZoomingAndScalingDelayed();

        initPlot1();

        waferPlot.addAll();
        
        waferPlot2 = new VectorWaferPlot("Plot", "nm", true);
  

        initPlot2();

        
        waferPlot2.addAll();

        final DatasetSelector dsSel = new DatasetSelector();
        dsSel.addDatasetSelectorComponent(waferPlot);
        dsSel.addDatasetSelectorComponent(waferPlot2);
        
          dsSel.forceThumbnailUpdate();
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
    
    private void initPlot2() {
        waferPlot2.addVectorValue(3, 3, 8, 8, 20, -20, true);

        waferPlot2.addField(0, 0, 20, 20, 2, 3);
        waferPlot2.addField(-10, 0, 10, 20, 2, 3);
        waferPlot2.addField(0, -10, 20, 20, 2, 3);
        waferPlot2.addField(10, 0, 20, 20, 2, 3);
        waferPlot2.addField(0, 10, 20, 20, 2, 3);
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

package com.asml.wfa.metrotools.tooltotoolmatching.gui.widgets;

import java.awt.Color;
import java.awt.Event;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JFileChooser;

import org.apache.commons.math.util.FastMath;

import com.asml.wfa.common.guicomponents.widgets.plots.ExportEvent;
import com.asml.wfa.common.guicomponents.widgets.plots.ExportListener;
import com.asml.wfa.common.guicomponents.widgets.plots.dataplot2d.v2.DP2DMeanPlus3SigmaValueRangeProvider;
import com.asml.wfa.common.guicomponents.widgets.plots.dataplot2d.v2.DataPlot2D;
import com.asml.wfa.common.guicomponents.widgets.plots.dataplot2d.v2.DataPlot2DGlobalComponentDockPosition;
import com.asml.wfa.common.guicomponents.widgets.plots.dataplot2d.v2.DataPlot2DObject;
import com.asml.wfa.common.guicomponents.widgets.plots.dataplot2d.v2.mouse.DataPlot2DMouseDragHandler;
import com.asml.wfa.common.guicomponents.widgets.plots.dataplot2d.v2.mouse.DataPlot2DMouseMultiSelectionHandler;
import com.asml.wfa.common.guicomponents.widgets.plots.dataplot2d.v2.mouse.DataPlot2DMouseWheelScaleHandler;
import com.asml.wfa.common.guicomponents.widgets.plots.dataplot2d.v2.mouse.DataPlot2DMouseWheelZoomHandler;
import com.asml.wfa.common.guicomponents.widgets.plots.dataplot2d.v2.standardObjects.DataPointObject;
import com.asml.wfa.common.guicomponents.widgets.plots.dataplot2d.v2.standardObjects.WaferBackgroundObject;
import com.asml.wfa.common.guicomponents.widgets.plots.dataplot2d.v2.standardObjects.WaferPlotLegend;
import com.asml.wfa.common.guicomponents.widgets.plots.dataplot2d.v2.toggle.DP2DDisableValueColorsToggle;
import com.asml.wfa.common.guicomponents.widgets.plots.dataplot2d.v2.toggle.DP2DHideFlyersToggle;



public class VectorWaferPlot extends DataPlot2D {
    private static final long serialVersionUID = 1L;

    private final WaferPlotLegend legend = new WaferPlotLegend();
    private final List<DataPlot2DObject> objects = new LinkedList<>();
    private boolean drawAsDots = false;

    private final String plotName;


    public VectorWaferPlot(final String plotName, final String unit, final boolean showLegend) {
        this(plotName, unit, false, showLegend);
    }

    public VectorWaferPlot(final String plotName, final String unit, final boolean drawAsDots, final boolean showLegend) {
        super(plotName, new DP2DMeanPlus3SigmaValueRangeProvider());

        this.drawAsDots = drawAsDots;
        this.plotName = plotName;
      
        if (showLegend) {
            legend.setUnit(unit);
            addGlobalDataObject(legend, DataPlot2DGlobalComponentDockPosition.EAST);
        }

        this.addWaferBackground();
    
        this.addMouseHandler(new DataPlot2DMouseWheelZoomHandler());
        this.addMouseHandler(new DataPlot2DMouseWheelScaleHandler(), Event.CTRL_MASK);
        this.addMouseHandler(new DataPlot2DMouseDragHandler());
        this.addMouseHandler(new DataPlot2DMouseMultiSelectionHandler(), Event.ALT_MASK);

        this.addToggle(new DP2DDisableValueColorsToggle());
        this.addToggle(new DP2DHideFlyersToggle());

      

    }

  
    public void addField(final double xPos, final double yPos, final double width, final double height,
            final int nrDiesX, final int nrDiesY) {
        this.addDataObject(new DirectionalFieldObject(xPos, yPos, width, height, nrDiesX, nrDiesY));
    }

  
    public void addVectorValue(final double interFieldX, final double interFieldY, final double intraFieldX, final double intraFieldY,
            final double xValue, final double yValue, final boolean isValid) {
        addVectorValue(null, interFieldX, interFieldY, intraFieldX, intraFieldY, xValue, yValue, isValid);
    }

    /**
     * Add vector, note use only the interfields for position
     * @param id vector id 
     * @param interFieldX interfield x
     * @param interFieldY interfield y
     * @param intraFieldX intrafield x (only for export)
     * @param intraFieldY intrafield y (only for export
     * @param xValue value x
     * @param yValue value Y
     * @param isValid valid true false
     */
    public void addVectorValue(final String id, final double interFieldX, final double interFieldY, final double intraFieldX,
            final double intraFieldY, final double xValue, final double yValue, final boolean isValid) {
        if (drawAsDots) {
            final DataPointObject dataPoint = new DataPointObject(interFieldX, interFieldY, FastMath.hypot(xValue, yValue), isValid);
            objects.add(dataPoint);
        } else {
            final VectorDataPoint vectorPoint = new VectorDataPoint(interFieldX, interFieldY, xValue, yValue, isValid);
            objects.add(vectorPoint);
        }

      
    }

  
    public void addAll() {
        this.addDataObjects(objects);
    }

    private void addWaferBackground() {
        final WaferBackgroundObject bgObj = new WaferBackgroundObject();

        bgObj.setBackgroundColor(null);
        bgObj.setEdgeColor(Color.black);

        addDataObject(bgObj);
    }

    @Override
    public void clear() {
        objects.clear();
      
        super.clear();
        this.addWaferBackground();

       
    }

}
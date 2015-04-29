package com.asml.wfa.metrotools.tooltotoolmatching.gui.view;

import java.awt.BorderLayout;
import java.awt.Event;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;
import javax.swing.JPanel;

import com.asml.wfa.common.guicomponents.widgets.datasetselector.DatasetSelector;
import com.asml.wfa.common.guicomponents.widgets.plots.Plot;
import com.asml.wfa.common.guicomponents.widgets.plots.dataplot2d.v2.DataPlot2D;
import com.asml.wfa.common.guicomponents.widgets.plots.dataplot2d.v2.DataPlot2DObject;
import com.asml.wfa.common.guicomponents.widgets.plots.dataplot2d.v2.mouse.DataPlot2DMouseDragHandler;
import com.asml.wfa.common.guicomponents.widgets.plots.dataplot2d.v2.mouse.DataPlot2DMouseWheelScaleHandler;
import com.asml.wfa.common.guicomponents.widgets.plots.dataplot2d.v2.mouse.DataPlot2DMouseWheelZoomHandler;
import com.asml.wfa.common.guicomponents.widgets.plots.dataplot2d.v2.standardObjects.DataPointObject;
import com.asml.wfa.common.guicomponents.widgets.plots.dataplot2d.v2.standardObjects.WaferBackgroundObject;
import com.asml.wfa.metrotools.tooltotoolmatching.gui.state.ApplicationState;
import com.asml.wfa.metrotools.tooltotoolmatching.gui.widgets.VectorWaferPlot;
import com.asml.wfa.xml.adel.adelmetrology.XmlMeasurement;
import com.asml.wfa.xml.adel.adelmetrology.XmlMetrologyWafer;

/**
 * View to render result screen of Sso.
 * 
 */
public class ToolToTollMatchingResultView extends JPanel {

    @Inject
    private ApplicationState applicationState;

    private static final long serialVersionUID = 1701894457726885406L;

    public ToolToTollMatchingResultView() {
        this.setLayout(new BorderLayout());
    }

    public void updateView() {
        // 1. clear the current panel
        this.removeAll();

        // 2. add a new data set selector
        final DatasetSelector ds = new DatasetSelector();
        this.add(ds);
        this.doLayout();

        final VectorWaferPlot waferPlot = new VectorWaferPlot("Plot", "nm", true);
        waferPlot.resetZoomingAndScalingDelayed();

        final List<XmlMeasurement> wfm1 = applicationState.getMetrologyFiles().get(0).getWafers().get(0).getMeasurements();
        final List<XmlMeasurement> wfm2 = applicationState.getMetrologyFiles().get(1).getWafers().get(0).getMeasurements();

        for (int i = 0; i < wfm1.size(); i++) {
            waferPlot.addField(wfm1.get(i).getFieldPosition().getX(), wfm1.get(i).getFieldPosition().getY(), 20, 20, 0, 0);
            waferPlot.addVectorValue(wfm1.get(i).getFieldPosition().getX() + wfm1.get(i).getTargetPosition().getX(), wfm1.get(i).getFieldPosition()
                    .getY()
                    + wfm1.get(i).getTargetPosition().getY(), wfm2.get(i).getOverlay().getX(), wfm2.get(i).getOverlay().getY(), wfm1.get(i)
                    .getOverlay().getX()
                    - wfm2.get(i).getOverlay().getX(), wfm1.get(i).getOverlay().getY() - wfm2.get(i).getOverlay().getY(), true);
        }

        waferPlot.addAll();
        ds.addDatasetSelectorComponent(waferPlot);
         ds.addDatasetSelectorComponent(waferPlot);
        ds.forceThumbnailUpdate();

        this.validate();
        this.repaint();

    }
}

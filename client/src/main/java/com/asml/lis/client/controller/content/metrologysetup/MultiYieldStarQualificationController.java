package com.asml.lis.client.controller.content.metrologysetup;

import com.asml.lis.client.MainApp;
import com.asml.lis.client.service.metrologysetup.MultiYSQualificationService;
import com.asml.lis.client.service.metrologysetup.impl.MultiYSQualificationServiceImpl;
import com.asml.lis.client.service.metrologysetup.model.PlotData;
import com.asml.wfa.common.guicomponents.widgets.datasetselector.DatasetSelector;
import com.asml.wfa.metrotools.tooltotoolmatching.gui.widgets.VectorWaferPlot;
import java.awt.Desktop;
import java.awt.Dimension;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import javafx.util.Pair;
import javax.swing.SwingUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FXML Controller class
 *
 * @author Catalin Tudorache <catalin.tudorache@asml.com>
 */
public class MultiYieldStarQualificationController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(MultiYieldStarQualificationController.class);

    @FXML
    private SwingNode waferPlotContainer;
    @FXML
    private Button selectFilesButton;
    @FXML
    private ListView selectedFilesListView;
    @FXML
    private Button matchSelectedButton;

    private VectorWaferPlot waferPlot;

    private MultiYSQualificationServiceImpl service;

    private List<List<PlotData>> plotContent;

    /**
     * Initializes the controller class.
     *
     * @param url URL
     * @param rb resource bundle
     */
    @Override
    public void initialize(final URL url, final ResourceBundle rb) {
        log.info("Initializing MultiYieldStarQualification controller");

        plotContent = new ArrayList<>();
        final FileChooser fileChooser = new FileChooser();

        selectFilesButton.setOnAction(
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(final ActionEvent e) {
                        configureFileChooser(fileChooser);
                        List<File> files = fileChooser.showOpenMultipleDialog(selectFilesButton.getContextMenu());

                        // populate selected files list view
                        populateListView(files);

                        // parse file   
                        //      
                        for (File file : files) {
                            List<PlotData> plotData = service.parseMetrologyFiles(file);
                            plotContent.add(plotData);
                        }
                    }
                });

        matchSelectedButton.setOnAction(
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(final ActionEvent e) {
                        createSwingContent(waferPlotContainer, plotContent);

                    }
                });
    }

    private void populateListView(List<File> files) {
        List<String> ls = new ArrayList<>();
        files.stream().forEach((file) -> {
            ls.add(file.getName());
        });

        selectedFilesListView.setItems(FXCollections.observableArrayList(ls));

    }

    private static void configureFileChooser(final FileChooser fileChooser) {
        fileChooser.setTitle("View Pictures");
        fileChooser.setInitialDirectory(
                new File(System.getProperty("user.home"))
        );
    }

    private void createSwingContent(final SwingNode swingNode, List<List<PlotData>> plotContent) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                log.debug("Creating Swing content");

                final DatasetSelector dsSel = new DatasetSelector();
                for (int i = 0; i < plotContent.size() - 1; i++) {
                    for (int j = i + 1; j < plotContent.size(); j++) {
                        waferPlot = new VectorWaferPlot("Plot", "nm", true);
                        waferPlot.resetZoomingAndScalingDelayed();

                        initPlot(plotContent.get(i), plotContent.get(j));

                        waferPlot.addAll();

                        dsSel.addDatasetSelectorComponent(waferPlot);

                    }
                }

                dsSel.setPreferredSize(new Dimension(800, 600));
                swingNode.setContent(dsSel);
            }
        });
    }

    private void initPlot(List<PlotData> plotData, List<PlotData> plotData2) {

        for (int i = 0; i < plotData.size(); i++) {
            waferPlot.addVectorValue(plotData.get(i).getFieldPositionX(), plotData.get(i).getFieldPositionY(), plotData.get(i).getTargetPositionX(), plotData.get(i).getTargetPositionY(), plotData.get(i).getOverlayX() - plotData2.get(i).getOverlayX(), plotData.get(i).getOverlayY() - plotData2.get(i).getOverlayY(), true);

            waferPlot.addField(plotData.get(i).getFieldPositionX(), plotData.get(i).getFieldPositionY(), 20, 20, 0, 0);

        }
    }

    public void setPlaceholderLabel(final String text) {

    }

}

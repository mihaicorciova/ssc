package com.asml.lis.client.controller.content.metrologysetup;

import com.asml.lis.client.service.metrologysetup.impl.MultiYSQualificationServiceImpl;
import com.asml.lis.client.service.metrologysetup.model.GenericModel;
import com.asml.lis.client.service.metrologysetup.model.MachineData;
import com.asml.lis.client.service.metrologysetup.model.PlotData;
import com.asml.lis.client.service.metrologysetup.model.ProfileData;
import com.asml.wfa.common.guicomponents.widgets.datasetselector.DatasetSelector;
import com.asml.wfa.metrotools.tooltotoolmatching.gui.widgets.VectorWaferPlot;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import javax.swing.SwingUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FXML Controller class
 *
 * @author Catalin Tudorache <catalin.tudorache@asml.com>
 */
public class MultiYieldStarQualificationController extends Application implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(MultiYieldStarQualificationController.class);
    @FXML
    private SwingNode waferPlotContainer;
    @FXML
    private Button selectFilesButton;
    @FXML
    private ListView selectedFilesListView;
    @FXML
    private Button matchSelectedButton;
    @FXML
    private ChoiceBox apertureChoiceBox;
    @FXML
    private ChoiceBox referenceMachineChoiceBox;
    @FXML
    private ChoiceBox targetLabelChoiceBox;
    @FXML
    private ChoiceBox matchingTypeMachineChoiceBox;
    @FXML
    private TableView matchingTableView;
    @FXML
    TableColumn<GenericModel, Object> machineNamesTableColumn;
    @FXML
    TableColumn<GenericModel, Object> meanXTableColumn;
    @FXML
    TableColumn<GenericModel, Object> p2PXTableColumn;
    @FXML
    TableColumn<GenericModel, Object> meanYTableColumn;
    @FXML
    TableColumn<GenericModel, Object> p2PYTableColumn;

    private VectorWaferPlot waferPlot;
    private MultiYSQualificationServiceImpl service;
    private Map<String, MachineData> plotContent;
    private static final String MYSQ_LAYOUT_FILE = "/fxml/MultiYieldStarQualification.fxml";
    private static final double SCENE_MIN_WIDTH = 1280;
    private static final double SCENE_MIN_HEIGHT = 720;
    private static final String[] APERTURE_TYPE = {"0 deg", "180 deg", "TIS"};
    private static final String[] MATCHING_TYPE = {"Mean", "Abs(Mean)+3S"};

    /**
     * Initializes the controller class.
     *
     * @param url URL
     * @param rb resource bundle
     */
    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loader = new FXMLLoader();

        Parent rootNode = (AnchorPane) loader.load(getClass().getResourceAsStream(MYSQ_LAYOUT_FILE));
        Scene scene = new Scene(rootNode, SCENE_MIN_WIDTH, SCENE_MIN_HEIGHT);
        // scene.getStylesheets().add(MAIN_CSS_FILE);
        stage.setTitle("Litho InSight");

        stage.setMinWidth(SCENE_MIN_WIDTH);
        stage.setMinHeight(SCENE_MIN_HEIGHT);
        stage.setScene(scene);

        stage.show();

    }

    @Override
    public void initialize(final URL url, final ResourceBundle rb) {
        log.info("Initializing MultiYieldStarQualification controller");

        service = new MultiYSQualificationServiceImpl();

        final FileChooser fileChooser = new FileChooser();

        selectFilesButton.setOnAction(
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(final ActionEvent e) {
                        configureFileChooser(fileChooser);
                        List<File> files = fileChooser.showOpenMultipleDialog(selectFilesButton.getContextMenu());
                        plotContent = new HashMap<>();
                        // populate selected files list view
                        populateListView(files);

                        // parse file   
                        //      
                        plotContent = service.parseMetrologyFiles(files);
                        populateReferenceMachineChoiceBox(plotContent);
                        populateTargetLabelChoiceBox(plotContent.get(referenceMachineChoiceBox.getSelectionModel().getSelectedItem().toString()).getProfileData());
                    }
                });

        populateApertureChoiceBox();
        populateMatchingTypeMachineChoiceBox();

        matchSelectedButton.setOnAction(
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(final ActionEvent e) {

                        createSwingContent(waferPlotContainer, plotContent);
                        populateMatchingTable(plotContent);
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

    private void populateMatchingTable(Map<String, MachineData> plotContent) {

        machineNamesTableColumn.setCellValueFactory(new PropertyValueFactory<GenericModel, Object>("one"));
        meanXTableColumn.setCellValueFactory(new PropertyValueFactory<GenericModel, Object>("two"));
        p2PXTableColumn.setCellValueFactory(new PropertyValueFactory<GenericModel, Object>("three"));
        meanYTableColumn.setCellValueFactory(new PropertyValueFactory<GenericModel, Object>("four"));
        p2PYTableColumn.setCellValueFactory(new PropertyValueFactory<GenericModel, Object>("five"));

        ObservableList data = FXCollections.observableArrayList();

        for (Map.Entry<String, MachineData> entry : plotContent.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(referenceMachineChoiceBox.getSelectionModel().getSelectedItem().toString())) {
                data.add(new GenericModel(entry.getKey(), "REF", "REF", "REF", "REF"));
            }
            else{
         //   if(entry.getValue().getProfileData().get(targetLabelChoiceBox.getSelectionModel().getSelectedItem().toString()).getPlotData().)
            }

        }
        matchingTableView.getItems().setAll(data);
    }

    private void populateReferenceMachineChoiceBox(Map<String, MachineData> plotContent) {

        referenceMachineChoiceBox.setItems(FXCollections.observableArrayList(plotContent.keySet()));
        referenceMachineChoiceBox.getSelectionModel().selectFirst();
    }

    private void populateTargetLabelChoiceBox(Map<String, ProfileData> plotContent) {

        targetLabelChoiceBox.setItems(FXCollections.observableArrayList(plotContent.keySet()));
        targetLabelChoiceBox.getSelectionModel().selectFirst();
    }

    private void populateApertureChoiceBox() {

        apertureChoiceBox.setItems(FXCollections.observableArrayList(APERTURE_TYPE));
        apertureChoiceBox.getSelectionModel().selectFirst();
    }

    private void populateMatchingTypeMachineChoiceBox() {

        matchingTypeMachineChoiceBox.setItems(FXCollections.observableArrayList(MATCHING_TYPE));
        matchingTypeMachineChoiceBox.getSelectionModel().selectFirst();
    }

    private static void configureFileChooser(final FileChooser fileChooser) {
        fileChooser.setTitle("View Pictures");
        fileChooser.setInitialDirectory(
                new File(System.getProperty("user.home"))
        );
    }

    private void createSwingContent(final SwingNode swingNode, Map<String, MachineData> plotContent) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                log.debug("Creating Swing content");

                final DatasetSelector dsSel = new DatasetSelector();
                for (String key : plotContent.keySet()) {
                    for (String key2 : plotContent.keySet()) {

                        if (!key.equals(key2)) {
                            waferPlot = new VectorWaferPlot("Plot" + key + key2, "nm", true);
                            waferPlot.resetZoomingAndScalingDelayed();
                            for (String key3 : plotContent.get(key).getProfileData().keySet()) {
                                initPlot(plotContent.get(key).getProfileData().get(key3).getPlotData(), plotContent.get(key2).getProfileData().get(key3).getPlotData());
                            }
                            waferPlot.addAll();
                            dsSel.addDatasetSelectorComponent(waferPlot);
                        }

                    }
                }

                dsSel.setPreferredSize(new Dimension(800, 600));
                swingNode.setContent(dsSel);
            }
        });
    }

    private void initPlot(List<PlotData> plotData, List<PlotData> plotData2) {

        for (int i = 0; i < plotData.size(); i++) {
            waferPlot.addVectorValue(plotData.get(i).getFieldPositionX() + plotData.get(i).getTargetPositionX(), plotData.get(i).getFieldPositionY() + plotData.get(i).getTargetPositionY(), plotData.get(i).getFieldPositionX() + plotData.get(i).getTargetPositionX(), plotData.get(i).getFieldPositionY() + plotData.get(i).getTargetPositionY(), plotData.get(i).getOverlayX() - plotData2.get(i).getOverlayX(), plotData.get(i).getOverlayY() - plotData2.get(i).getOverlayY(), true);

            waferPlot.addField(plotData.get(i).getFieldPositionX(), plotData.get(i).getFieldPositionY(), 20, 20, 0, 0);

        }
    }

    public AnchorPane getMYSQModuleView() throws IOException {
        return (AnchorPane) new FXMLLoader().load(getClass().getResourceAsStream(MYSQ_LAYOUT_FILE));
    }

}

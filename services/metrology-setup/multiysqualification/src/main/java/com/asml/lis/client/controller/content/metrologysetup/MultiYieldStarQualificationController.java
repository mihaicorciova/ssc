package com.asml.lis.client.controller.content.metrologysetup;

import static com.asml.lis.client.ui.common.AlertMessage.EXPORTING_FILE_IN_USE;
import static com.asml.lis.client.ui.common.AlertMessage.NO_MATCHING_FILES;
import static com.asml.lis.client.ui.common.AlertMessage.PPT_EXPORTED;
import static com.asml.lis.client.ui.common.AlertMessage.PPT_NOT_EXPORTED;

import java.awt.Dimension;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
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
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.apache.poi.ss.formula.atp.DateParser;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.asml.lis.client.service.metrologysetup.impl.MultiYSQualificationServiceImpl;
import com.asml.lis.client.service.metrologysetup.model.GenericModel;
import com.asml.lis.client.service.metrologysetup.model.MachineData;
import com.asml.lis.client.service.metrologysetup.model.PlotData;
import com.asml.lis.client.service.metrologysetup.model.ProfileData;
import com.asml.lis.client.service.metrologysetup.util.PptExportTools;
import com.asml.lis.client.ui.common.Constants;
import com.asml.lis.client.ui.common.UiCommonTools;
import com.asml.wfa.common.guicomponents.widgets.datasetselector.DatasetSelector;
import com.asml.wfa.metrotools.tooltotoolmatching.gui.widgets.VectorWaferPlot;

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
    private Button exportPptButton;
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
    private TableView<GenericModel> matchingTableView;
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

                        if (plotContent == null || plotContent.isEmpty()) {
                            
                            UiCommonTools.getInstance().showAlertDialog(NO_MATCHING_FILES.getTitle(), NO_MATCHING_FILES.getHeader(),
                                    NO_MATCHING_FILES.getText(), AlertType.WARNING);
                        } else {
                            createSwingContent(waferPlotContainer, plotContent);
                            exportPptButton.setDisable(false);
                            populateMatchingTable(plotContent);
                            
                            
                        referenceMachineChoiceBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {

                            public void changed(ObservableValue ov, String value, String new_value) {
                                populateMatchingTable(plotContent);
                                createSwingContent(waferPlotContainer, plotContent);

                            }

                        }
                        );
                        matchingTypeMachineChoiceBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {

                            public void changed(ObservableValue ov, String value, String new_value) {
                                populateMatchingTable(plotContent);
                                createSwingContent(waferPlotContainer, plotContent);

                            }

                        }
                        );

                        apertureChoiceBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {

                            public void changed(ObservableValue ov, String value, String new_value) {
                                populateMatchingTable(plotContent);
                                createSwingContent(waferPlotContainer, plotContent);

                            }

                        }
                        );
                        }

                    }
                });
        
        exportPptButton.setDisable(true);
        exportPptButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent e) {
                if (waferPlotContainer.getContent() == null) {
                    UiCommonTools.getInstance().showAlertDialog(PPT_NOT_EXPORTED.getTitle(), PPT_NOT_EXPORTED.getHeader(),
                            PPT_NOT_EXPORTED.getText(), AlertType.WARNING);
                } else {
                    try {
                        File exportPptFile =
                                UiCommonTools.getInstance().getFileByChooser(((Window) exportPptButton.getContextMenu()), "PPT files (*.ppt)",
                                        ".ppt");
                        log.debug("Export content to file: " + exportPptFile.getAbsolutePath());
                        JPanel panelToExport = (JPanel) waferPlotContainer.getContent().getComponents()[0];
                        
                        byte[] picture = PptExportTools.getPictureFromJComponent(panelToExport);
                        
                        String templatePath = getClass().getResource(Constants.ASML_PPT_TEMPLATE_PATH).getPath();
                        
                        log.debug("Creates a new XMLSlideShow from Template file: " + templatePath);
                        XMLSlideShow ppt = new XMLSlideShow(new FileInputStream(templatePath));
                        
                        PptExportTools.addPictureToNewPptSlide(ppt, picture, 0.5, "Export Wafer Plot");
                        
                        String[][] content = getTableContent(matchingTableView);
                        PptExportTools.exportTableToSlide(ppt, matchingTableView, content, "Exported Table");

                        // remove unused last slide which was used as template to create new slides for each exported component
                        ppt.removeSlide(ppt.getSlides().length - 1);

                        // creating a file object
                        FileOutputStream out = new FileOutputStream(exportPptFile);

                        // saving the changes to a file
                        ppt.write(out);
                        out.close();

                        UiCommonTools.getInstance().showAlertDialog(PPT_EXPORTED.getTitle(), PPT_EXPORTED.getHeader(), PPT_EXPORTED.getText(),
                                AlertType.INFORMATION);

                    } catch (FileNotFoundException ex) {
                        UiCommonTools.getInstance().showAlertDialog(EXPORTING_FILE_IN_USE.getTitle(), EXPORTING_FILE_IN_USE.getHeader(),
                                ex.getMessage() + "\n" + EXPORTING_FILE_IN_USE.getText(), AlertType.ERROR);

                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
    }

    private String[][] getTableContent(TableView<GenericModel> fxTable) {
        String[][] content = new String[fxTable.getItems().size()][fxTable.getColumns().size()];
        
        int rowNo = 0;
        for (GenericModel genericModel : fxTable.getItems()) {
            content[rowNo][0] = "" + genericModel.getOne();
            content[rowNo][1] = "" + genericModel.getTwo();
            content[rowNo][2] = "" + genericModel.getThree();
            content[rowNo][3] = "" + genericModel.getFour();
            content[rowNo][4] = "" + genericModel.getFive();
            rowNo++;
        }
        return content;
    }

    private void populateListView(List<File> files) {
        List<String> ls = new ArrayList<>();
        files.stream().forEach((file) -> {
            ls.add(file.getName());
        });

        selectedFilesListView.setItems(FXCollections.observableArrayList(ls));

    }

    private void populateMatchingTable(Map<String, MachineData> plotContent) {

        machineNamesTableColumn.setCellValueFactory(new PropertyValueFactory<>("one"));
        meanXTableColumn.setCellValueFactory(new PropertyValueFactory<>("two"));
        p2PXTableColumn.setCellValueFactory(new PropertyValueFactory<>("three"));
        meanYTableColumn.setCellValueFactory(new PropertyValueFactory<>("four"));
        p2PYTableColumn.setCellValueFactory(new PropertyValueFactory<>("five"));

        ObservableList data = FXCollections.observableArrayList();
        List<PlotData> refpd = new ArrayList<>();

        refpd = plotContent.get(referenceMachineChoiceBox.getSelectionModel().getSelectedItem().toString()).getProfileData().get(targetLabelChoiceBox.getSelectionModel().getSelectedItem().toString()).getPlotData();
        log.debug("Creqate table" + referenceMachineChoiceBox.getSelectionModel().getSelectedItem().toString());
        for (Map.Entry<String, MachineData> entry : plotContent.entrySet()) {

            if (entry.getKey().equalsIgnoreCase(referenceMachineChoiceBox.getSelectionModel().getSelectedItem().toString())) {

                data.add(new GenericModel(entry.getKey(), "REF", "REF", "REF", "REF"));
            } else {

                List<Double> dovx = new ArrayList<>();
                List<Double> dovy = new ArrayList<>();
                Double sumx = 0d;
                Double sumy = 0d;
                Double tempx = 0d;
                Double tempy = 0d;

                List<PlotData> pd = entry.getValue().getProfileData().get(targetLabelChoiceBox.getSelectionModel().getSelectedItem().toString()).getPlotData();

                for (int i = 0; i < pd.size(); i++) {
                    if (apertureChoiceBox.getSelectionModel().getSelectedItem().toString().startsWith(String.valueOf(pd.get(i).getAperture().intValue()))) {
                        sumx += refpd.get(i).getOverlayX() - pd.get(i).getOverlayX();
                        sumy += refpd.get(i).getOverlayY() - pd.get(i).getOverlayY();
                        dovx.add(Math.abs(refpd.get(i).getOverlayX() - pd.get(i).getOverlayX()));
                        dovy.add(Math.abs(refpd.get(i).getOverlayY() - pd.get(i).getOverlayY()));
                    }
                }
                Collections.sort(dovx);
                Collections.sort(dovy);
                if (dovx.size() > 0) {
                    if (matchingTypeMachineChoiceBox.getSelectionModel().getSelectedItem().toString().contains("Abs")) {

                        for (int i = 0; i < dovx.size(); i++) {
                            tempx += (Math.abs(sumx / dovx.size()) - dovx.get(i)) * (Math.abs(sumx / dovx.size()) - dovx.get(i)) / dovx.size();
                            tempy += (Math.abs(sumy / dovx.size()) - dovy.get(i)) * (Math.abs(sumy / dovx.size()) - dovy.get(i)) / dovx.size();

                        }

                        data.add(new GenericModel(entry.getKey(), String.valueOf(Math.abs(sumx / pd.size()) + 3 * Math.sqrt(tempx)), dovx.get(dovx.size() - 1 - (int) Math.round(dovx.size() * 0.003d)), String.valueOf(Math.abs(sumy / pd.size()) + 3 * Math.sqrt(tempy)), dovy.get(dovy.size() - 1 - (int) Math.round(dovy.size() * 0.003d))));
                    } else {
                        data.add(new GenericModel(entry.getKey(), String.valueOf(sumx / pd.size()), dovx.get(dovx.size() - 1 - (int) Math.round(dovx.size() * 0.003d)), String.valueOf(sumy / pd.size()), dovy.get(dovy.size() - 1 - (int) Math.round(dovy.size() * 0.003d))));

                    }
                }
                else{
                data.add(new GenericModel(entry.getKey(), "NaN", "NaN", "NaN", "NaN"));
                }
            }

        }

        matchingTableView.setItems(data);

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
                log.debug("Creqate sw" + referenceMachineChoiceBox.getSelectionModel().getSelectedItem().toString());
                for (String key2 : plotContent.keySet()) {

                    if (!key2.equalsIgnoreCase(referenceMachineChoiceBox.getSelectionModel().getSelectedItem().toString())) {
                        waferPlot = new VectorWaferPlot("Plot" + referenceMachineChoiceBox.getSelectionModel().getSelectedItem().toString() + key2, "nm", true);
                        waferPlot.resetZoomingAndScalingDelayed();
                        for (String key3 : plotContent.get(key2).getProfileData().keySet()) {
                            initPlot(plotContent.get(referenceMachineChoiceBox.getSelectionModel().getSelectedItem().toString()).getProfileData().get(key3).getPlotData(), plotContent.get(key2).getProfileData().get(key3).getPlotData());
                        }
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
            if (apertureChoiceBox.getSelectionModel().getSelectedItem().toString().startsWith(String.valueOf(plotData.get(i).getAperture().intValue()))) {
                waferPlot.addVectorValue(plotData.get(i).getFieldPositionX() + plotData.get(i).getTargetPositionX(), plotData.get(i).getFieldPositionY() + plotData.get(i).getTargetPositionY(), plotData.get(i).getFieldPositionX() + plotData.get(i).getTargetPositionX(), plotData.get(i).getFieldPositionY() + plotData.get(i).getTargetPositionY(), plotData.get(i).getOverlayX() - plotData2.get(i).getOverlayX(), plotData.get(i).getOverlayY() - plotData2.get(i).getOverlayY(), true);

                waferPlot.addField(plotData.get(i).getFieldPositionX(), plotData.get(i).getFieldPositionY(), 20, 20, 0, 0);
            }
        }
    }

    public AnchorPane getMYSQModuleView() throws IOException {
        return (AnchorPane) new FXMLLoader().load(getClass().getResourceAsStream(MYSQ_LAYOUT_FILE));
    }

}

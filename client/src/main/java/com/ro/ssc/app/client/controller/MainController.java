package com.ro.ssc.app.client.controller;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.ro.ssc.app.client.controller.sidemenu.SideMenuNoImagesController;
import com.ro.ssc.app.client.licensing.LicenseStatus;
import com.ro.ssc.app.client.licensing.TrialKeyGenerator;
import com.ro.ssc.app.client.licensing.TrialKeyValidator;
import com.ro.ssc.app.client.ui.commons.UiCommonTools;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class MainController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(MainController.class);

    // main component fxml files
    private static final String OVERALLREPORT_LAYOUT_FILE = "/fxml/OverallReport.fxml";
    private static final String SIDE_MENU_LAYOUT_FILE = "/fxml/SideMenuNoImages.fxml";
    private static final String STATUS_BAR_LAYOUT_FILE = "/fxml/StatusBar.fxml";
    private static final String SUMARY_FILE = "/fxml/Sumary.fxml";
    private static final String SINGLEREPORT_LAYOUT_FILE = "/fxml/SingleReport.fxml";
    private static final String SINGLEABS_LAYOUT_FILE = "/fxml/SingleAbs.fxml";
    private static final String OVERALLABS_LAYOUT_FILE = "/fxml/OverallAbs.fxml";
    private static final String MONTHLY_LAYOUT_FILE = "/fxml/MonthlyReport.fxml";
    // style sheet files
    private static final String SIDE_MENU_CSS_FILE = "/styles/SideMenu.css";
    private static final String STATUS_BAR_CSS_FILE = "/styles/StatusBar.css";
    private static final Long MILLIS_PER_MINUTE = 1000l;
    // main content containers
    @FXML
    private AnchorPane sideMenuContainer;
    @FXML
    private AnchorPane contentContainer;
    @FXML
    private AnchorPane statusBarContainer;
    @FXML
    private AnchorPane contentTabPane;
    private LicenseStatus licenseStatus;
    private TrialKeyValidator licenseService = new TrialKeyValidator();
    private AnchorPane sumaryPane;
    private AnchorPane overallReportPane;
    private AnchorPane singleReportPane;
    private AnchorPane overallAbsPane;
    private AnchorPane singleAbsPane;
    private AnchorPane monthlyReportPane;
    private String MDB_PATH = "opt";

    private DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd");
    // controllers

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        log.info("Initializing main controller");
        File destDir = new File(MDB_PATH);
        if (!destDir.exists()) {
            destDir.mkdirs();
        } else {
            File file = new File(MDB_PATH + "/status.txt");
            try {
                if (!file.exists()) {
                    file.createNewFile();
                }
                String content = Files.toString(file, Charsets.UTF_8);
                try {
                    if (DateTime.parse(content, dtf).isBeforeNow()) {
                        Optional<String> result = UiCommonTools.getInstance().showExpDialogStatus("Licenta Expirata", "Va rugam contactati vanzatorul softului pentru codul de deblocare ", TrialKeyGenerator.generateKey(DateTime.now().toString(dtf)));
                        if (result.isPresent()) {
                            if (TrialKeyValidator.decodeKey(result.get()).equals(Files.toString(file, Charsets.UTF_8).concat("0"))) {
                                Files.write("NO_EXP", file, Charsets.UTF_8);

                            } else {
                                return;
                            }
                        } else {
                            return;
                        }

                    }
                } catch (Exception e) {
                }
                log.debug("cont" + content + " file" + file);
                if (content.contains("111111111111111")) {
                    Files.write(DateTime.now().toString(dtf), file, Charsets.UTF_8);

                    Optional<String> result = UiCommonTools.getInstance().showExpDialogStatus("Licenta Expirata", "Va rugam contactati vanzatorul softului pentru codul de deblocare ", TrialKeyGenerator.generateKey(DateTime.now().toString(dtf)));
                    if (result.isPresent()) {
                        if (TrialKeyValidator.decodeKey(result.get()).equals(Files.toString(file, Charsets.UTF_8).concat("0"))) {
                            Files.write("NO_EXP", file, Charsets.UTF_8);

                        } else {
                            return;
                        }
                    } else {
                        return;
                    }
                } else if (!content.contains("NO_EXP")) {
                    Files.append("1", file, Charsets.UTF_8);
                }
            } catch (FileNotFoundException ex) {
                log.error("Exception in finding file " + ex.getMessage());
            } catch (IOException ex) {
                log.error("Exception in writing file " + ex.getMessage());
            }
        }

        try {

            // load side menu
            final FXMLLoader sideMenuLoader = new FXMLLoader();
            final AnchorPane sideMenu = sideMenuLoader.load(getClass().getResourceAsStream(SIDE_MENU_LAYOUT_FILE));
            AnchorPane.setLeftAnchor(sideMenu, 0.0);
            AnchorPane.setTopAnchor(sideMenu, 0.0);
            AnchorPane.setRightAnchor(sideMenu, 0.0);
            AnchorPane.setBottomAnchor(sideMenu, 0.0);
            sideMenuContainer.getChildren().add(sideMenu);
            sideMenuContainer.getStylesheets().add(SIDE_MENU_CSS_FILE);
            ((SideMenuNoImagesController) sideMenuLoader.getController()).setMainController(this);

            // load status bar
            final FXMLLoader statusBarLoader = new FXMLLoader();
            final AnchorPane statusBar = statusBarLoader.load(getClass().getResourceAsStream(STATUS_BAR_LAYOUT_FILE));
            AnchorPane.setRightAnchor(statusBar, 10.0);
            statusBarContainer.getChildren().add(statusBar);
            statusBarContainer.getStylesheets().add(STATUS_BAR_CSS_FILE);

            handleSumaryViewLaunch();
        } catch (Exception ex) {
            log.error("Failed to load components", ex);
        }
    }

    /**
     * Get content tab pane component.
     *
     * @return content TabPane
     */
    public AnchorPane getContentTabPane() {
        return contentTabPane;
    }

    public void handleSumaryViewLaunch() throws IOException {

        // load side menu
        if (sumaryPane == null) {
            final FXMLLoader sumaryPaneLoader = new FXMLLoader();
            sumaryPane = sumaryPaneLoader.load(getClass().getResourceAsStream(SUMARY_FILE));
            AnchorPane.setLeftAnchor(sumaryPane, 0.0);
            AnchorPane.setTopAnchor(sumaryPane, 0.0);
            AnchorPane.setRightAnchor(sumaryPane, 0.0);
            AnchorPane.setBottomAnchor(sumaryPane, 0.0);
        }
        contentContainer.getChildren().setAll(sumaryPane);
    }

    public void handleOverallReportViewLaunch() throws IOException {
        // load side menu

        final FXMLLoader overallReportPaneLoader = new FXMLLoader();
        overallReportPane = overallReportPaneLoader.load(getClass().getResourceAsStream(OVERALLREPORT_LAYOUT_FILE));
        AnchorPane.setLeftAnchor(overallReportPane, 0.0);
        AnchorPane.setTopAnchor(overallReportPane, 0.0);
        AnchorPane.setRightAnchor(overallReportPane, 0.0);
        AnchorPane.setBottomAnchor(overallReportPane, 0.0);

        contentContainer.getChildren().setAll(overallReportPane);
    }

    public void handleMonthlyViewLaunch() throws IOException {
        // load side menu

        final FXMLLoader overallReportPaneLoader = new FXMLLoader();
        monthlyReportPane = overallReportPaneLoader.load(getClass().getResourceAsStream(MONTHLY_LAYOUT_FILE));
        AnchorPane.setLeftAnchor(monthlyReportPane, 0.0);
        AnchorPane.setTopAnchor(monthlyReportPane, 0.0);
        AnchorPane.setRightAnchor(monthlyReportPane, 0.0);
        AnchorPane.setBottomAnchor(monthlyReportPane, 0.0);

        contentContainer.getChildren().setAll(monthlyReportPane);
    }

    public void handleSingleReportViewLaunch() throws IOException {

        final FXMLLoader singleReportPaneLoader = new FXMLLoader();
        singleReportPane = singleReportPaneLoader.load(getClass().getResourceAsStream(SINGLEREPORT_LAYOUT_FILE));
        AnchorPane.setLeftAnchor(singleReportPane, 0.0);
        AnchorPane.setTopAnchor(singleReportPane, 0.0);
        AnchorPane.setRightAnchor(singleReportPane, 0.0);
        AnchorPane.setBottomAnchor(singleReportPane, 0.0);

        contentContainer.getChildren().setAll(singleReportPane);
    }

    public void handleOverallAbsViewLaunch() throws IOException {

        final FXMLLoader singleReportPaneLoader = new FXMLLoader();
        overallAbsPane = singleReportPaneLoader.load(getClass().getResourceAsStream(OVERALLABS_LAYOUT_FILE));
        AnchorPane.setLeftAnchor(overallAbsPane, 0.0);
        AnchorPane.setTopAnchor(overallAbsPane, 0.0);
        AnchorPane.setRightAnchor(overallAbsPane, 0.0);
        AnchorPane.setBottomAnchor(overallAbsPane, 0.0);

        contentContainer.getChildren().setAll(overallAbsPane);
    }

    public void handleSingleAbsViewLaunch() throws IOException {

        final FXMLLoader singleReportPaneLoader = new FXMLLoader();
        singleAbsPane = singleReportPaneLoader.load(getClass().getResourceAsStream(SINGLEABS_LAYOUT_FILE));
        AnchorPane.setLeftAnchor(singleAbsPane, 0.0);
        AnchorPane.setTopAnchor(singleAbsPane, 0.0);
        AnchorPane.setRightAnchor(singleAbsPane, 0.0);
        AnchorPane.setBottomAnchor(singleAbsPane, 0.0);

        contentContainer.getChildren().setAll(singleAbsPane);
    }
}

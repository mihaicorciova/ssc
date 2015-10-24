package com.ro.ssc.app.client.controller;

import com.ro.ssc.app.client.controller.sidemenu.SideMenuNoImagesController;
import com.ro.ssc.app.client.licensing.LicenseStatus;
import com.ro.ssc.app.client.licensing.TrialKeyValidator;
import com.ro.ssc.app.client.model.commons.Configuration;
import com.ro.ssc.app.client.ui.commons.UiCommonTools;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;

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
    private String MDB_PATH = "opt";
    // controllers

    private Timer licenseTimer;
    private TimerTask licenseRefreshTask = new TimerTask() {
        @Override
        public void run() {
            licenseStatus = licenseService.getLicenseStatus();
        }
    };

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        log.info("Initializing main controller");

        licenseStatus = licenseService.getLicenseStatus();
        licenseTimer = new Timer("LicenseCheckThread", true);
        long interval = Configuration.LICENSE_CHECK_INTERVAL.getAsInteger() * MILLIS_PER_MINUTE;
        licenseTimer.schedule(licenseRefreshTask, interval, interval);
       
        
           File destDir = new File(MDB_PATH);
            if (!destDir.exists()) {
                        destDir.mkdirs();
                    }
        if (licenseStatus.isExpired()&& destDir.exists() || destDir.listFiles().length>0) {
            // don't initialize importing if the license is expired
           
 
                File file = new File(MDB_PATH+"/status.txt");
               
                OutputStream out = null;
                try {

                    out = new FileOutputStream(file);
                   out.write(1);
                   out.flush();
                } catch (FileNotFoundException ex) {
                    log.error("Exception in finding file" + ex.getMessage());
                } catch (IOException ex) {
                    log.error("Exception in writing file" + ex.getMessage());
                }

            
        
            Configuration.IS_EXPIRED.setValue("true");
            UiCommonTools.getInstance().showInfoDialogStatus("Licenta Expirata", "Data expirarii " + licenseStatus.getExpireDate(), "Va rugam contactati vanzatorul softului.");
          //  return;
      }
        // load components
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

package com.asml.wfa.dashboard.gui;

import javafx.application.Application;
import javafx.stage.Stage;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.commons.lang3.StringUtils;
import org.jboss.weld.environment.se.StartMain;
import org.jboss.weld.environment.se.WeldContainer;

/**
 * Dashboard, main entry point
 * 
 */
public class Dashboard extends Application {
    private static WeldContainer container;
    private static String modelDirectory;
    protected DashboardController controller;

    public static void main(final String[] args) throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException,
            IllegalAccessException {
        /*
         * TODO: check performance: the application might be bootstrapped twice, check if this is the case or if the startup process can be improved
         * by letting jboss start the JFX application, or omitting jboss (KOEO)
         */
        container = new StartMain(args).go();
        modelDirectory = extractModelDirectory(args);
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        launch(args);
    }

    /**
     * Start the JavaFX application
     * 
     * @param stage
     *            stage
     */
    @Override
    public void start(final Stage stage) {
        controller = new DashboardController(stage, container);
    }

    private static String extractModelDirectory(final String[] args) {
        if (args == null || args.length == 0) {
            return null;
        } else {
            return args[0];
        }
    }

    /**
     * Get the model directory set in the command line arguments
     * 
     * @return model dir
     */
    public static String getModelDirectory() {
        if (StringUtils.isEmpty(modelDirectory)) {
            return "";
        }
        return modelDirectory;
    }

    /**
     * Get the attached controller
     * 
     * @return controller that controls the dashboard
     */
    public DashboardController getController() {
        return controller;
    }
}

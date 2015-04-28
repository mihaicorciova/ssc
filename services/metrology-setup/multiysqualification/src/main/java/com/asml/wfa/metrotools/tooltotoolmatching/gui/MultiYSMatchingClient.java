package com.asml.wfa.metrotools.tooltotoolmatching.gui;

import com.asml.lcp.middleware.common.ejb.util.BeanProvider;
import com.jidesoft.plaf.LookAndFeelFactory;
import org.jboss.weld.environment.se.StartMain;
import org.jboss.weld.environment.se.WeldContainer;
import org.jboss.weld.environment.se.bindings.Parameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.swing.*;
import java.util.List;

/** Launching class for ToolToToolMatching application */
public class MultiYSMatchingClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(MultiYSMatchingClient.class);

    @Inject
    @Parameters
    private List<String> parameters;

    @Inject
    private MultiYSMatchingMainWindow mainWindow;

    /**
     * Main.
     * 
     * @param args
     *            command line arguments
     */
    public static void main(final String[] args) {
        try {
            LookAndFeelFactory.installDefaultLookAndFeelAndExtension();

            final WeldContainer container = new StartMain(args).go();
            final MultiYSMatchingClient app = container.instance().select(MultiYSMatchingClient.class).get();
            BeanProvider.setBeanManager(container.getBeanManager());
            app.main();
            app.mainWindow.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        } catch (final Throwable e) {
            LOGGER.error("", e);
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Entry point to start the ModelAdvisorClient service residing in the Weld container.
     */
    public void main() {
        try {
            startApplication(parameters);
        } catch (final Throwable e) {
            LOGGER.error("", e);
        }
    }

    /**
     * Start the Model Advisor Client.
     * 
     * @param args
     *            the command line arguments
     */
    private void startApplication(final List<String> args) {
        LOGGER.info(String.format("Start application %s", args));

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    mainWindow.initialize(null);
                } catch (final Exception e) {
                    final String msg = "Cannot start client application. " + e.getMessage();
                    JOptionPane.showMessageDialog(null, msg, "ToolToToolMatching Error", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace(System.out);
                    System.exit(1);
                } catch (final VirtualMachineError vme) {
                    final String msg = "The virtual machine stopped working. " + vme.getMessage();
                    JOptionPane.showMessageDialog(null, msg, "Virtual Machine Error", JOptionPane.ERROR_MESSAGE);
                    vme.printStackTrace(System.out);
                    System.exit(1);
                }
            }

        });
    }

}
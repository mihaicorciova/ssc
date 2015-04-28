package com.asml.wfa.metrotools.tooltotoolmatching.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.inject.Inject;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import com.asml.standalone.common.constants.ApplicationConstants;
import com.asml.wfa.common.guicomponents.commons.GUIConstants;
import com.asml.wfa.metrotools.tooltotoolmatching.gui.controller.MultiYSMatchingClientController;

/**
 * Application parent window for Sso standlone application.
 * 
 */
public class MultiYSMatchingMainWindow extends JFrame {

    @Inject
    private MultiYSMatchingClientController tttmController;

    private static final long serialVersionUID = 1L;

    /**
     * Initialize the ToolToToolMatching main window.
     * 
     * @param resourceDirectory
     *            the directory containing the resources
     */
    public void initialize(final String resourceDirectory) {
        setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
        tttmController.initController(resourceDirectory);
        initializeMainWindow();
    }

    /**
     * Initialize the main window.
     */
    private void initializeMainWindow() {
        setTitle("MultiYieldStarMatching");
        setLayout(new BorderLayout());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        setPreferredSize(new Dimension(GUIConstants.SCREENWIDTH, GUIConstants.SCREENHEIGHT));

        add(createMenu(), BorderLayout.NORTH);
        add(createApplicationPanel(), BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * Create menu.
     * 
     * @return the menu
     */
    private JMenuBar createMenu() {
        return new JMenuBar();
    }

    /**
     * Create the application panel.
     * 
     * @return the created panel.
     */
    private JPanel createApplicationPanel() {
        final JPanel appPanel = new JPanel();

        appPanel.setLayout(new BorderLayout());
        appPanel.setPreferredSize(new Dimension(GUIConstants.SCREENWIDTH, GUIConstants.SCREENHEIGHT));

        final JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true);
        splitPane.setDividerLocation(ApplicationConstants.INITIALDIVIDER);
        splitPane.setBackground(Color.white);

        splitPane.setLeftComponent(tttmController.getInputView());
        splitPane.setRightComponent(tttmController.getResultView());

        appPanel.add(splitPane, BorderLayout.CENTER);

        return appPanel;
    }
}

package com.asml.wfa.metrotools.tooltotoolmatching.gui.view;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import net.miginfocom.swing.MigLayout;
import ca.odell.glazedlists.EventList;

import com.asml.wfa.common.guicomponents.commons.GUIConstants;
import com.asml.wfa.common.guicomponents.widgets.icons.WidgetIconFactory;
import com.asml.wfa.common.guicomponents.widgets.tablebuilder.TableBuilder;
import com.asml.wfa.common.guicomponents.widgets.yesnobutton.YesNoButton;
import com.asml.wfa.guicommons.model.ModelContainer;
import com.asml.wfa.guicommons.model.ModelVT;
import com.asml.wfa.guicommons.widgets.ModelOverviewPanel;
import com.asml.wfa.metrotools.tooltotoolmatching.gui.controller.MultiYSMatchingClientController;
import com.asml.wfa.metrotools.tooltotoolmatching.gui.model.MultiYSMatchingInputModel;
import com.asml.wfa.xml.adel.adelmetrology.AdelMetrology;
import com.jidesoft.swing.TitledSeparator;

/**
 * View to render input screen of Sso standlone.
 */
public class ToolToToolMatchingInputView extends JPanel {

    private static final String SELECT_ADEL_METROLOGY = "Select ADELmetrology";

    private static final int DEFAULT_INPUT_WIDTH = 5;
    private static final long serialVersionUID = 1L;

    /** Load data **/
    private final List<ActionListener> loadDataListeners = new LinkedList<>();
    private File selectedFile;

    private final JFileChooser fileChooser = new JFileChooser();
    private final JButton openMetrology = new JButton(SELECT_ADEL_METROLOGY);

    private JFormattedTextField apertureInputField;

    private JList<String> fileSelection;
    private JFormattedTextField meanInputField;
    private JFormattedTextField p2pInputField;

    /** apply **/
    private JButton matchTools;

    /**
     * Default constructor.
     * 
     * @param ssoModel
     *            {@link MultiYSMatchingInputModel} data to initialize the view.
     */
    public ToolToToolMatchingInputView() {
        createComponents();
        configureLayout();
    }

    /**
     * Adds a listener to calculate yieldstar button.
     * 
     * @param applySsoListener
     *            {@link ActionListener} to be called when there is apply confirmation
     */
    public void addApplyListener(final ActionListener applyMatchingListener) {
        matchTools.addActionListener(applyMatchingListener);
    }

    /**
     * Adds a listener to YTM settings dialog. This listener will be called when there is dialog.
     * 
     * @param ytmSettingsListener
     *            {@link ActionListener}
     */

    public void addLoadDataListener(final ActionListener listener) {
        loadDataListeners.add(listener);
    }

    public File getSelectedFile() {
        return selectedFile;
    }

    /**
     * Updates view with the given instance {@link MultiYSMatchingInputModel}
     * 
     * @param ssoInputModel
     *            {@link MultiYSMatchingInputModel} that is used to update the view
     */

    public MultiYSMatchingInputModel getUserInput() {
        // retrieve user input
        final MultiYSMatchingInputModel inputModel = new MultiYSMatchingInputModel();

        final double meanSpec = Double.parseDouble(meanInputField.getText());
        final double p2pSpec = Double.parseDouble(p2pInputField.getText());
        final String aperture = apertureInputField.getText();

        inputModel.setMeanSpec(meanSpec);
        inputModel.setP2PSpec(p2pSpec);
        inputModel.setAperture(aperture);

        return inputModel;
    }

    public void updateView(final MultiYSMatchingInputModel matchingInputModel) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // update view components with input values form ssoInputModel
                updateFileTable(matchingInputModel.getFileNames());

                meanInputField.setValue(matchingInputModel.getMeanSpec());
                p2pInputField.setValue(matchingInputModel.getP2PSpec());
                apertureInputField.setText(matchingInputModel.getAperture());
            }

            private void updateFileTable(final List<String> fileNames) {
                if (fileNames.size() > 0) {
                    final DefaultListModel<String> model = new DefaultListModel<String>();
                    for (final String file : fileNames) {
                        model.addElement(file);
                    }
                    fileSelection.setModel(model);
                }

            }

        });
    }

    /**
     * Initializes view component
     */
    private void configureLayout() {
        this.setLayout(new MigLayout("", "12[][][]", "[][]"));

        // Titles
        final JLabel loadDataTitle = new JLabel("Load Data");
        final JLabel fileSelectionTitle = new JLabel("Selected Files");
        final JLabel matchingSettingsTitle = new JLabel("Matching Specs");

        loadDataTitle.setFont(GUIConstants.H1_FONT);
        fileSelectionTitle.setFont(GUIConstants.H1_FONT);
        matchingSettingsTitle.setFont(GUIConstants.H1_FONT);

        // data load
        add(new TitledSeparator(loadDataTitle, SwingConstants.LEFT), "gaptop 10, span 3, growx, wrap");
        add(openMetrology, "wrap");

        // model selection
        add(new TitledSeparator(fileSelectionTitle, SwingConstants.LEFT), "gaptop 10, span 3, growx, wrap");

        add(fileSelection, "gaptop 3, growx, wrap");

        // advanced sso settings
        add(new TitledSeparator(matchingSettingsTitle, SwingConstants.LEFT), "gaptop 10, span 3, growx, wrap");
        add(new JLabel("Mean Spec"));
        add(meanInputField, "wrap");

        add(new JLabel("P2P Spec"));
        add(p2pInputField, "wrap");
        add(new JLabel("Aperture"));
        add(apertureInputField, "wrap");

        // apply button
        add(matchTools, "gaptop 15, span 3, align right");
    }

    /**
     * Creates input components
     */
    private void createComponents() {
        // data selection panel
        openMetrology.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                if (fileChooser.showOpenDialog(ToolToToolMatchingInputView.this) == JFileChooser.APPROVE_OPTION) {
                    selectedFile = fileChooser.getSelectedFile();

                    final ActionEvent event =
                            new ActionEvent(ToolToToolMatchingInputView.this, ToolToToolMatchingInputView.SELECT_ADEL_METROLOGY.hashCode(),
                                    ToolToToolMatchingInputView.SELECT_ADEL_METROLOGY);
                    for (final ActionListener listener : loadDataListeners) {
                        listener.actionPerformed(event);
                    }
                }
            }
        });

        fileSelection = new JList<String>();

        // advanced sso settings
        meanInputField = new JFormattedTextField(NumberFormat.getNumberInstance());
        configureTextField(meanInputField, "Mean Spec");

        p2pInputField = new JFormattedTextField(NumberFormat.getNumberInstance());
        configureTextField(p2pInputField, "P2P Spec");

        apertureInputField = new JFormattedTextField();
        configureTextField(apertureInputField, "Aperture");

        // Apply button
        matchTools = new JButton("Match Selected");
    }

    /**
     * Sets name, default alignment and width for a text field
     * 
     * @param textField
     *            {@link JFormattedTextField} that is to be configured.
     * @param name
     *            name to be set for the text field.
     */
    private void configureTextField(final JFormattedTextField textField, final String name) {
        textField.setName(name);
        textField.setHorizontalAlignment(SwingConstants.RIGHT);
        textField.setColumns(DEFAULT_INPUT_WIDTH);
    }

    /**
     * Creates model selection panel
     * 
     * @param initialModels
     *            initial list of models
     * @return create {@link ModelOverviewPanel}
     */

}

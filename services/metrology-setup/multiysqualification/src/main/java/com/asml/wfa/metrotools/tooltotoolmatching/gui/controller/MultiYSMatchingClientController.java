package com.asml.wfa.metrotools.tooltotoolmatching.gui.controller;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.asml.lcp.wfa.estimator.attributes.WaferDataValueAttributes;
import com.asml.lcp.wfa.estimator.modeling.Parameter;
import com.asml.standalone.common.util.EstimatorInputConversionUtil;
import com.asml.wfa.commons.domainmodel.Lot;
import com.asml.wfa.guicommons.model.ModelContainer;
import com.asml.wfa.metrotools.tooltotoolmatching.gui.adel.AdelMetroDocumentReader;
import com.asml.wfa.metrotools.tooltotoolmatching.gui.model.MultiYSMatchingInputModel;
import com.asml.wfa.metrotools.tooltotoolmatching.gui.state.ApplicationState;
import com.asml.wfa.metrotools.tooltotoolmatching.gui.view.ToolToToolMatchingInputView;
import com.asml.wfa.metrotools.tooltotoolmatching.gui.view.ToolToTollMatchingResultView;
import com.asml.wfa.metrotools.tooltotoolmatching.utils.ModelManager;

import com.asml.wfa.xml.adel.jaxb.XMLException;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

/**
 * Controller to manage main view, input interaction and processing.
 * 
 * @author Ajith Ganesan
 * @version Initial Version, 3/25/2015
 */
public class MultiYSMatchingClientController {
    private static final Logger LOGGER = LoggerFactory.getLogger(MultiYSMatchingClientController.class);
    @Inject
    private MultiYSMatchingInputModel defaultInputModel;

    @Inject
    private ToolToToolMatchingInputView tttmInputView;

    @Inject
    private ToolToTollMatchingResultView tttmResultView;

    @Inject
    private ModelManager modelManager;

    @Inject
    private ApplicationState applicationState;

    /**
     * Initialize controller
     */
    public void initController(final String resourceDirectory) {
        modelManager.initialize(resourceDirectory, applicationState);
        tttmInputView.addLoadDataListener(new LoadDataListener());
        tttmInputView.addApplyListener(new ApplyMatchingListener());
        tttmInputView.updateView(defaultInputModel);

    }

    /**
     * Retrieves model names as List from {@link ModelContainer} in {@link ApplicationState}
     * 
     * @return List<String> with model names.
     */
    public MultiYSMatchingInputModel getMatchingModel() {
        return defaultInputModel;
    }

    public void setMatchingModel(final MultiYSMatchingInputModel matchingModel) {
        this.defaultInputModel = matchingModel;
    }

    /**
     * Listener for Calculate sample scheme apply action. <br>
     * <ul>
     * <li>Retrieves user input from view
     * <li>Invokes sample scheme algorithm with values
     * <li>Updates the view with the result
     * </ul>
     */

    class ApplyMatchingListener implements ActionListener {

        @Override
        public void actionPerformed(final ActionEvent e) {

            updateResultView();
        }
    }

    class LoadDataListener implements ActionListener {
        @Override
        public void actionPerformed(final ActionEvent event) {
            final File file = tttmInputView.getSelectedFile();
            try (final InputStream stream = Files.newInputStream(file.toPath())) {
                applicationState.addMetrologyFile(AdelMetroDocumentReader.loadAdelMetrology(stream));
                final List<String> ls = defaultInputModel.getFileNames();
                ls.add(file.getName());
                System.out.println(file.getName());
                defaultInputModel.setFileNames(ls);

                tttmInputView.updateView(defaultInputModel);

            } catch (final XMLException | IOException e) {
                throw new IllegalStateException(String.format("Cannot read '%s' for reading.", file.toString()));
            }
        }
    }

    public Component getInputView() {
        return tttmInputView;
    }

    public void updateResultView() {
        tttmResultView.updateView();
    }

    public Component getResultView() {
        return tttmResultView;
    }

}
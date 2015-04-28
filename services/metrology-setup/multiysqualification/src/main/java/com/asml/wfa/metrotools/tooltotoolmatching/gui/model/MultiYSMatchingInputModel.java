package com.asml.wfa.metrotools.tooltotoolmatching.gui.model;

import java.util.ArrayList;
import java.util.List;

import com.asml.wfa.metrotools.tooltotoolmatching.gui.view.ToolToToolMatchingInputView;

/**
 * Input model that is to be rendered by {@link ToolToToolMatchingInputView}. Note that the model could be used to set defaults.
 * 
 * @author Ajith Ganesan
 * @version Initial Version, 3/25/2015
 */
public class MultiYSMatchingInputModel {

    private static final double DEFAULT_MEAN_SPEC = 0.3d;
    private static final double DEFAULT_P2P_SPEC = 0.6d;
    private static final String DEFAULT_APERTURE = "0 deg";
    // this is just a place holder, modify if necessary
    private List<String> fileNames;
    private List<String> selectedFileNames;

    private double meanSpec;
    private double p2pSpec;
    private String aperture;

    /**
     * Default constructor. Initializes the input model with defaults.
     */
    public MultiYSMatchingInputModel() {
        this.fileNames = new ArrayList<String>();
        this.selectedFileNames = new ArrayList<String>();
        this.meanSpec = DEFAULT_MEAN_SPEC;
        this.p2pSpec = DEFAULT_P2P_SPEC;
        this.aperture = DEFAULT_APERTURE;
    }

    public double getMeanSpec() {
        return meanSpec;
    }

    public void setMeanSpec(final double meanSpec) {
        this.meanSpec = meanSpec;
    }

    public double getP2PSpec() {
        return p2pSpec;
    }

    public void setP2PSpec(final double p2pSpec) {
        this.p2pSpec = p2pSpec;
    }

    public String getAperture() {
        return aperture;
    }

    public void setAperture(final String aperture) {
        this.aperture = aperture;
    }

    public List<String> getFileNames() {
        return fileNames;
    }

    public void setFileNames(final List<String> fileNames) {
        this.fileNames = fileNames;
    }

    public List<String> getSelectedFileNames() {
        return selectedFileNames;
    }

    public void setSelectedFileNames(final List<String> selectedFileNames) {
        this.selectedFileNames = selectedFileNames;
    }

}

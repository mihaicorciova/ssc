/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asml.lis.client.service.metrologysetup.model;

import java.util.List;

/**
 *
 * @author Mihai Corciova <mihai.corciova@asml.com>
 */
public class ProfileData {

    public Integer getNoComponentFiles() {
        return noComponentFiles;
    }

    public void setNoComponentFiles(Integer noComponentFiles) {
        this.noComponentFiles = noComponentFiles;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public List<PlotData> getPlotData() {
        return plotData;
    }

    public void setPlotData(List<PlotData> plotData) {
        this.plotData = plotData;
    }

    public ProfileData(Integer noComponentFiles, String profileName, List<PlotData> plotData) {
        this.noComponentFiles = noComponentFiles;
        this.profileName = profileName;
        this.plotData = plotData;
    }

    private Integer noComponentFiles;
    private String profileName;
    private List<PlotData> plotData;

}

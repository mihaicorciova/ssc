/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asml.lis.client.service.metrologysetup.model;

import java.util.List;
import java.util.Map;

/**
 *
 * @author Mihai Corciova <mihai.corciova@asml.com>
 */
public class MachineData {

    private final String machineName;
    private Map<String, ProfileData> profileData;

    public Map<String, ProfileData> getProfileData() {
        return profileData;
    }

    public void setProfileData(Map<String, ProfileData> profileData) {
        this.profileData = profileData;
    }

    public String getMachineName() {
        return machineName;
    }

    public MachineData(String machineName, Map<String, ProfileData> profileData) {
        this.machineName = machineName;
        this.profileData = profileData;
    }
}

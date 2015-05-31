package com.ro.ssc.app.client.services.impl;

import com.ro.ssc.app.client.model.commons.Project;
import com.ro.ssc.app.client.model.commons.User;
import com.ro.ssc.app.client.services.ProjectService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author Catalin Tudorache <catalin.tudorache@asml.com>
 */
public class ProjectServiceImpl implements ProjectService {

    @Override
    public Project getProjectByName(final String name) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ObservableList<String> getProjectsByUser(final User user) {
        final ObservableList<String> projectList = FXCollections.observableArrayList();

        // TODO: change this to repository call
        projectList.add("Node1 Project");
        projectList.add("Node2 Project");
        projectList.add("Node3 Project");
        projectList.add("Node4 Project");
        projectList.add("Node5 Project");
        projectList.add("Node6 Project");

        return projectList;
    }

    @Override
    public ObservableList<Project> getAllProjects() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}

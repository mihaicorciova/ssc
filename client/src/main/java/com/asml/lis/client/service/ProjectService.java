package com.asml.lis.client.service;

import com.asml.lis.client.model.common.Project;
import com.asml.lis.client.model.common.User;
import javafx.collections.ObservableList;

/**
 *
 * @author Catalin Tudorache <catalin.tudorache@asml.com>
 */
public interface ProjectService {

    /**
     * Get project by name.
     *
     * @param name project name
     * @return project
     */
    Project getProjectByName(final String name);

    /**
     * Get projects created by user.
     *
     * @param user
     * @return project list
     */
    ObservableList<String> getProjectsByUser(final User user);

    /**
     * Get all projects.
     *
     * @return project list
     */
    ObservableList<Project> getAllProjects();

}

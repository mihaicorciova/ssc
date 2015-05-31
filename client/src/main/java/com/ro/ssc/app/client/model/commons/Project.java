package com.ro.ssc.app.client.model.commons;

import java.util.Date;
import javafx.beans.property.StringProperty;

/**
 *
 * @author Catalin Tudorache <catalin.tudorache@asml.com>
 */
public class Project {

    private StringProperty projectName;
    private User createdBy;
    private Date createdAt;
    private Date updatedAt;

    /**
     * Constructor.
     *
     * @param projectName project name
     * @param createdBy created by
     */
    public Project(final StringProperty projectName, final User createdBy) {
        this(projectName, createdBy, new Date(), null);
    }

    /**
     * Constructor.
     *
     * @param projectName project name
     * @param createdBy created by
     * @param createdAt created at
     * @param updatedAt updated at
     */
    public Project(final StringProperty projectName, final User createdBy, final Date createdAt, final Date updatedAt) {
        this.projectName = projectName;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public StringProperty getProjectName() {
        return projectName;
    }

    public void setProjectName(final StringProperty projectName) {
        this.projectName = projectName;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(final User createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(final Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(final Date updatedAt) {
        this.updatedAt = updatedAt;
    }

}

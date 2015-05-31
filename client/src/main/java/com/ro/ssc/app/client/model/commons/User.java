package com.ro.ssc.app.client.model.commons;

import com.ro.ssc.app.client.model.commons.enums.UserProfileType;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author Catalin Tudorache <catalin.tudorache@asml.com>
 */
public class User {

    private StringProperty username;
    private StringProperty firstName;
    private StringProperty lastName;
    private UserProfileType profile;
    private StringProperty email;

    /**
     *
     * @param pUsername username
     * @param pFirstName first name
     * @param pLastName last name
     * @param pProfile user profile
     * @param pEmail email
     */
    public User(final String pUsername, final String pFirstName, final String pLastName, final UserProfileType pProfile, final String pEmail) {
        username = new SimpleStringProperty(pUsername);
        firstName = new SimpleStringProperty(pFirstName);
        lastName = new SimpleStringProperty(pLastName);
        profile = pProfile;
        email = new SimpleStringProperty(pEmail);
    }

    /**
     * @return username
     */
    public StringProperty getUsername() {
        return username;
    }

    /**
     * @param pUsername username
     */
    public void setUsername(final StringProperty pUsername) {
        username = pUsername;
    }

    /**
     * @return first name
     */
    public StringProperty getFirstName() {
        return firstName;
    }

    /**
     * @param pName first name
     */
    public void setFirstName(final StringProperty pName) {
        firstName = pName;
    }

    /**
     * @return last name
     */
    public StringProperty getLastName() {
        return lastName;
    }

    /**
     * @param pName last name
     */
    public void setLastName(final StringProperty pName) {
        lastName = pName;
    }

    /**
     * @return user profile
     */
    public UserProfileType getProfile() {
        return profile;
    }

    /**
     * @param profile user profile
     */
    public void setProfile(final UserProfileType profile) {
        this.profile = profile;
    }

    /**
     * @return email
     */
    public StringProperty getEmail() {
        return email;
    }

    /**
     * @param pEmail email
     */
    public void setEmail(final StringProperty pEmail) {
        email = pEmail;
    }

}

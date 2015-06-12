package com.ro.ssc.app.client.model.commons;

import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author Catalin Tudorache <catalin.tudorache@asml.com>
 */
public class User {

    String name;
    String userId;
    String cardNo;
    String department;

   
    Boolean isNghtShift;
    List<Event> events;

    public User(String name, String userId, String cardNo, String department, List<Event> events) {
        this.name = name;
        this.userId = userId;
        this.cardNo = cardNo;
        this.events = events;
        this.department = department;
    }

    
     public Boolean getIsNghtShift() {
        return isNghtShift;
    }

    public void setIsNghtShift(Boolean isNghtShift) {
        this.isNghtShift = isNghtShift;
    }
    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getName() {
        return name;
    }

    public String getUserId() {
        return userId;
    }

    public String getCardNo() {
        return cardNo;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ro.ssc.app.client.model.commons;

import javafx.beans.property.StringProperty;
import org.joda.time.DateTime;

/**
 *
 * @author DauBufu
 */
public class Event {

    private DateTime eventDateTime;
    private String description;
    private String addr;
    private Boolean passed;
    private Boolean isAccountable;
    private int aaa;

    public int getAaa() {
        return aaa;
    }

    public void setAaa(int aaa) {
        this.aaa = aaa;
    }

    public Event(DateTime eventDateTime, String description, String addr, Boolean passed) {
        this.eventDateTime = eventDateTime;
        this.description = description;
        this.addr = addr;
        this.passed = passed;
    }

    public Boolean getIsAccountable() {
        return isAccountable;
    }

    public void setIsAccountable(Boolean isAccountable) {
        this.isAccountable = isAccountable;
    }

    public DateTime getEventDateTime() {
        return eventDateTime;
    }

    public void setEventDateTime(DateTime eventDateTime) {
        this.eventDateTime = eventDateTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getPassed() {
        return passed;
    }

    public void setPassed(Boolean passed) {
        this.passed = passed;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

}

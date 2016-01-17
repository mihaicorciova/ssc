/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ro.ssc.app.client.model.commons;

import java.util.List;
import org.joda.time.DateTime;

/**
 *
 * @author DauBufu
 */
public class DailyData {

    String userName;
    DateTime date;
    String firstInEvent;
    String lastOutEvent;
    long workTime;
    long pauseTime;
    List<Event> wrongEvents;

    public DailyData(String userName, DateTime date, String firstInEvent, String lastOutEvent, long workTime, long pauseTime, List<Event> wrongEvents) {
        this.userName = userName;
        this.date = date;
        this.firstInEvent = firstInEvent;
        this.lastOutEvent = lastOutEvent;
        this.workTime = workTime;
        this.pauseTime = pauseTime;
        this.wrongEvents = wrongEvents;
    }

  
    public String getUserId() {
        return userName;
    }

    public void setUserId(String userId) {
        this.userName = userId;
    }

    public long getWorkTime() {
        return workTime;
    }

    public void setWorkTime(long workTime) {
        this.workTime = workTime;
    }

    public long getPauseTime() {
        return pauseTime;
    }

    public void setPauseTime(long pauseTime) {
        this.pauseTime = pauseTime;
    }

    public DateTime getDate() {
        return date;
    }

    public void setDate(DateTime date) {
        this.date = date;
    }

    public String getFirstInEvent() {
        return firstInEvent;
    }

    public void setFirstInEvent(String firstInEvent) {
        this.firstInEvent = firstInEvent;
    }

    public String getLastOutEvent() {
        return lastOutEvent;
    }

    public void setLastOutEvent(String lastOutEvent) {
        this.lastOutEvent = lastOutEvent;
    }

    public List<Event> getWrongEvents() {
        return wrongEvents;
    }

    public void setWrongEvents(List<Event> wrongEvents) {
        this.wrongEvents = wrongEvents;
    }

}

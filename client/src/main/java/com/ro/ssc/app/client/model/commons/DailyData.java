/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ro.ssc.app.client.model.commons;

import java.util.ArrayList;
import java.util.List;
import org.joda.time.DateTime;

/**
 *
 * @author DauBufu
 */
public class DailyData {

    String userId;
    DateTime date;
    String firstInEvent;
    String lastOutEvent;
    String startDayTime;
    long workTime;
    long pauseTime;
    long dailyPause;
    int dailyHours;
    List<Event> wrongEvents;

    public DailyData(String userId, DateTime date, String firstInEvent, String lastOutEvent, String startDayTime, long workTime, long pauseTime, long dailyPause, int dailyHours, List<Event> wrongEvents) {
        this.userId = userId;
        this.date = date;
        this.firstInEvent = firstInEvent;
        this.lastOutEvent = lastOutEvent;
        this.startDayTime = startDayTime;
        this.workTime = workTime;
        this.pauseTime = pauseTime;
        this.dailyPause = dailyPause;
        this.dailyHours = dailyHours;
        this.wrongEvents = wrongEvents;
    }

 

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public long getDailyPause() {
        return dailyPause;
    }

    public void setDailyPause(long dailyPause) {
        this.dailyPause = dailyPause;
    }

    public int getShiftHours() {
        return dailyHours;
    }

    public void setShiftHours(int dailyHours) {
        this.dailyHours = dailyHours;
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

    public String getStartDayTime() {
        return startDayTime;
    }

    public void setStartDayTime(String startDayTime) {
        this.startDayTime = startDayTime;
    }

    public int getDailyHours() {
        return dailyHours;
    }

    public void setDailyHours(int dailyHours) {
        this.dailyHours = dailyHours;
    }

    public List<Event> getWrongEvents() {
        return wrongEvents;
    }

    public void setWrongEvents(List<Event> wrongEvents) {
        this.wrongEvents = wrongEvents;
    }

}

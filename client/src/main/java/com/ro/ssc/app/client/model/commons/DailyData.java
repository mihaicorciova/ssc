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

    String userId;
    DateTime date;
    String firstInEvent;
    String lastOutEvent;
    long earlyTime;
    long workTime;
    long pauseTime;
    long nightTime;
    long overTime;
    long lateTime;
     String absence;
    String additionalDetails;
     

    public DailyData(String userId, DateTime date, String firstInEvent, String lastOutEvent, long earlyTime, long workTime, long pauseTime, long nightTime,long overTime, long lateTime, String absence, String additionalDetails) {
        this.userId = userId;
        this.date = date;
        this.firstInEvent = firstInEvent;
        this.lastOutEvent = lastOutEvent;
        this.earlyTime = earlyTime;
        this.workTime = workTime;
        this.pauseTime = pauseTime;
        this.nightTime= nightTime;
        this.overTime = overTime;
        this.lateTime = lateTime;
        this.absence = absence;
        this.additionalDetails = additionalDetails;
    }

    public long getEarlyTime() {
        return earlyTime;
    }

    public void setEarlyTime(long earlyTime) {
        this.earlyTime = earlyTime;
    }

    public long getOverTime() {
        return overTime;
    }

    public void setOverTime(long overTime) {
        this.overTime = overTime;
    }

    public long getLateTime() {
        return lateTime;
    }

    public void setLateTime(long lateTime) {
        this.lateTime = lateTime;
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

    public String getAbsence() {
        return absence;
    }

    public void setAbsence(String absence) {
        this.absence = absence;
    }

    public long getNightTime() {
        return nightTime;
    }

    public void setNightTime(long nightTime) {
        this.nightTime = nightTime;
    }

 

    public String getAdditionalDetails() {
        return additionalDetails;
    }

    @Override
    public String toString() {
        return "DailyData{" + "userId=" + userId + ", date=" + date + ", firstInEvent=" + firstInEvent + ", lastOutEvent=" + lastOutEvent + ", workTime=" + workTime + ", pauseTime=" + pauseTime + ", additionalDetails=" + additionalDetails + '}';
    }
    
    
}

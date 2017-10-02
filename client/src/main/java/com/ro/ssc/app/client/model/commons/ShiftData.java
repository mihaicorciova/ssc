/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ro.ssc.app.client.model.commons;

/**
 *
 * @author DauBufu
 */
public class ShiftData {

    private String shiftId;
    private String shiftName;
    private String shiftBreakTime;
    private String shiftStartHour;
    private ShiftCorrection sc; 
    private String shiftEndHour;
    private String holiday;
    private boolean hasOvertime;

   public ShiftData(String shiftId, String shiftName, String shiftBreakTime, String shiftStartHour, String shiftEndHour, ShiftCorrection sc, boolean hasOvertime) {
        this.shiftId = shiftId;
        this.shiftName = shiftName;
        this.shiftBreakTime = shiftBreakTime;
        this.shiftStartHour = shiftStartHour;
        this.shiftEndHour = shiftEndHour;
        this.sc=sc;
        this.hasOvertime = hasOvertime;
        this.holiday="";
    }

   public ShiftData(ShiftData shiftData, String holiday) {
        this.shiftId = shiftData.getShiftId();
        this.shiftName = shiftData.getShiftName();
        this.shiftBreakTime = shiftData.getShiftBreakTime();
        this.shiftStartHour = shiftData.getShiftStartHour();
        this.shiftEndHour = shiftData.getShiftEndHour();
        this.sc=sc;
        this.hasOvertime = shiftData.isHasOvertime();
        this.holiday=holiday;
    }
   
    public String getHoliday() {
        return holiday;
    }

    public void setHoliday(String holyday) {
        this.holiday = holyday;
    }

    public ShiftCorrection getSc() {
        return sc;
    }

    public void setSc(ShiftCorrection sc) {
        this.sc = sc;
    }

   
   
 
   

    public String getShiftId() {
        return shiftId;
    }

    public void setShiftId(String shiftId) {
        this.shiftId = shiftId;
    }

    public String getShiftName() {
        return shiftName;
    }

    public void setShiftName(String shiftName) {
        this.shiftName = shiftName;
    }

    public String getShiftBreakTime() {
        return shiftBreakTime;
    }

    public void setShiftBreakTime(String shiftBreakTime) {
        this.shiftBreakTime = shiftBreakTime;
    }

    public String getShiftStartHour() {
        return shiftStartHour;
    }

    public void setShiftStartHour(String shiftStartHour) {
        this.shiftStartHour = shiftStartHour;
    }

    public String getShiftEndHour() {
        return shiftEndHour;
    }

    public void setShiftEndHour(String shiftEndHour) {
        this.shiftEndHour = shiftEndHour;
    }

    public boolean isHasOvertime() {
        return hasOvertime;
    }

    public void setHasOvertime(boolean hasOvertime) {
        this.hasOvertime = hasOvertime;
    }

    @Override
    public String toString() {
        return "ShiftData{" + "shiftId=" + shiftId + ", shiftName=" + shiftName + ", shiftBreakTime=" + shiftBreakTime + ", shiftStartHour=" + shiftStartHour + ", sc=" + sc + ", shiftEndHour=" + shiftEndHour + ", holiday=" + holiday + ", hasOvertime=" + hasOvertime + '}';
    }

    
   
}

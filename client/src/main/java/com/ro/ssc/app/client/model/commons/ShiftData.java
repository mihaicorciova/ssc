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
    private String shiftEndHour;
      private String shiftAdjustIn;
    private String shiftAdjustOut;
    private boolean hasOvertime;

    public ShiftData(String shiftId, String shiftName, String shiftBreakTime, String shiftStartHour, String shiftEndHour, String shiftAdjustIn, String shiftAdjustOut, boolean hasOvertime) {
        this.shiftId = shiftId;
        this.shiftName = shiftName;
        this.shiftBreakTime = shiftBreakTime;
        this.shiftStartHour = shiftStartHour;
        this.shiftEndHour = shiftEndHour;
        this.shiftAdjustIn = shiftAdjustIn;
        this.shiftAdjustOut = shiftAdjustOut;
        this.hasOvertime = hasOvertime;
    }

   
    public String getShiftAdjustIn() {
        return shiftAdjustIn;
    }

    public void setShiftAdjustIn(String shiftAdjustIn) {
        this.shiftAdjustIn = shiftAdjustIn;
    }

    public String getShiftAdjustOut() {
        return shiftAdjustOut;
    }

    public void setShiftAdjustOut(String shiftAdjustOut) {
        this.shiftAdjustOut = shiftAdjustOut;
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
        return "ShiftData{" + "shiftId=" + shiftId + ", shiftName=" + shiftName + ", shiftBreakTime=" + shiftBreakTime + ", shiftStartHour=" + shiftStartHour + ", shiftEndHour=" + shiftEndHour + '}';
    }
}

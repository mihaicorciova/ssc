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
   
    private ShiftCorrection sc; 
    private ShiftHours sh;
    private String holiday;
    private boolean hasOvertime;

   public ShiftData(String shiftId, String shiftName, ShiftHours sh, ShiftCorrection sc, boolean hasOvertime) {
        this.shiftId = shiftId;
        this.shiftName = shiftName;
        this.sh=sh;
        this.sc=sc;
        this.hasOvertime = hasOvertime;
        this.holiday="";
    }

   public ShiftData(ShiftData shiftData, String holiday) {
        this.shiftId = shiftData.getShiftId();
        this.shiftName = shiftData.getShiftName();
        this.sh=shiftData.getSh();
        this.sc=shiftData.getSc();
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

    public ShiftHours getSh() {
        return sh;
    }

    public void setSh(ShiftHours sh) {
        this.sh = sh;
    }

 

    public boolean isHasOvertime() {
        return hasOvertime;
    }

    public void setHasOvertime(boolean hasOvertime) {
        this.hasOvertime = hasOvertime;
    }

    @Override
    public String toString() {
        return "ShiftData{" + "shiftId=" + shiftId + ", shiftName=" + shiftName + ", sc=" + sc + ", sh=" + sh + ", holiday=" + holiday + ", hasOvertime=" + hasOvertime + '}';
    }

   
    
   
}

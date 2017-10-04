package com.ro.ssc.app.client.model.commons;

/**
 * Created by mcorciova on 10/4/2017.
 */
public class ShiftHours {
    private  String shiftBreakTime;
    private  String shiftStartHour;
    private  String shiftEndHour;
    private  String shiftNightStart;
    private  String shiftNightEnd;

    public ShiftHours(String shiftBreakTime, String shiftStartHour, String shiftEndHour, String shiftNightStart, String shiftNightEnd) {
        this.shiftBreakTime = shiftBreakTime;
        this.shiftStartHour = shiftStartHour;
        this.shiftEndHour = shiftEndHour;
        this.shiftNightStart = shiftNightStart;
        this.shiftNightEnd = shiftNightEnd;
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

    public String getShiftNightStart() {
        return shiftNightStart;
    }

    public void setShiftNightStart(String shiftNightStart) {
        this.shiftNightStart = shiftNightStart;
    }

    public String getShiftNightEnd() {
        return shiftNightEnd;
    }

    public void setShiftNightEnd(String shiftNightEnd) {
        this.shiftNightEnd = shiftNightEnd;
    }
}

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
public class ShiftCorrection {

    private String adjustIn;
    private String adjustOut;
    private String penaltyTimeIn;
    private String penaltyAmountIn;
    private String penaltyTimeOut;
    private String penaltyAmountOut;

    public ShiftCorrection(String adjustIn, String adjustOut, String penaltyTimeIn, String penaltyAmountIn, String penaltyTimeOut, String penaltyAmountOut) {
        this.adjustIn = adjustIn;
        this.adjustOut = adjustOut;
        this.penaltyTimeIn = penaltyTimeIn;
        this.penaltyAmountIn = penaltyAmountIn;
        this.penaltyTimeOut = penaltyTimeOut;
        this.penaltyAmountOut = penaltyAmountOut;
    }

    
    
    public String getAdjustIn() {
        return adjustIn;
    }

    public String getAdjustOut() {
        return adjustOut;
    }

    public String getPenaltyTimeIn() {
        return penaltyTimeIn;
    }

    public String getPenaltyAmountIn() {
        return penaltyAmountIn;
    }

    public String getPenaltyTimeOut() {
        return penaltyTimeOut;
    }

    public String getPenaltyAmountOut() {
        return penaltyAmountOut;
    }

    @Override
    public String toString() {
        return "ShiftCorrection{" + "adjustIn=" + adjustIn + ", adjustOut=" + adjustOut + ", penaltyTimeIn=" + penaltyTimeIn + ", penaltyAmountIn=" + penaltyAmountIn + ", penaltyTimeOut=" + penaltyTimeOut + ", penaltyAmountOut=" + penaltyAmountOut + '}';
    }
    
    
    
}

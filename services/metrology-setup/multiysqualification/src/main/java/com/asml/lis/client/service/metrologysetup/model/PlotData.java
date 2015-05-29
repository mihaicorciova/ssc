/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asml.lis.client.service.metrologysetup.model;

/**
 *
 * @author Mihai Corciova <mihai.corciova@asml.com>
 */
public class PlotData {

    private final Double aperture;
    private final Double fieldPositionX;
    private final Double fieldPositionY;
    private final Double targetPositionX;
    private final Double targetPositionY;
    private final Double overlayX;
    private final Double overlayY;

    public Double getFieldPositionX() {
        return fieldPositionX;
    }

    public Double getFieldPositionY() {
        return fieldPositionY;
    }

    public Double getTargetPositionX() {
        return targetPositionX;
    }

    public Double getTargetPositionY() {
        return targetPositionY;
    }

    public Double getOverlayX() {
        return overlayX;
    }

    public Double getOverlayY() {
        return overlayY;
    }

    public Double getAperture() {
        return aperture;
    }

    public PlotData(final Double aperture, final Double fieldPositionX, final Double fieldPositionY, final Double targetPositionX, final Double targetPositionY, final Double overlayX, final Double overlayY) {
        this.aperture = aperture;
        this.fieldPositionX = fieldPositionX;
        this.fieldPositionY = fieldPositionY;
        this.targetPositionX = targetPositionX;
        this.targetPositionY = targetPositionY;
        this.overlayX = overlayX;
        this.overlayY = overlayY;
    }

}

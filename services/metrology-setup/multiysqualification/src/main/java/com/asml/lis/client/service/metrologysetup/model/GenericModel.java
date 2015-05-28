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
import com.asml.lis.client.controller.content.metrologysetup.MultiYieldStarQualificationController;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import javafx.scene.control.TableRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generic data model is a wrapper for ArrayList to easily populate tableViews
 * and other javafx controls. This class would be perfect if java reflection
 * supported magic getters and setters like php or if there was a nice way to
 * dynamically add them during runtime.
 */
public class GenericModel {

   private static final Logger log = LoggerFactory.getLogger(MultiYieldStarQualificationController.class);
   
    private Object one;
    private Object two;
    private Object three;
    private Object four;
    private Object five;
    private Object six;

    public GenericModel(Object... args) {
        Field[] fields = getClass().getDeclaredFields();
        int i = 1;
        for (Object arg : args) {
            try {
                fields[i++].set(this, arg);
            } catch (IllegalArgumentException | IllegalAccessException ex) {
                log.error( "Exception:", ex);
            }
        }
    }

    /**
     * @return the one
     */
    public Object getOne() {
        return one;
    }

    /**
     * @return the two
     */
    public Object getTwo() {
        return two;
    }

    /**
     * @return the three
     */
    public Object getThree() {
        return three;
    }

    /**
     * @return the four
     */
    public Object getFour() {
        return four;
    }

    /**
     * @return the five
     */
    public Object getFive() {
        return five;
    }

    /**
     * @return the six
     */
    public Object getSix() {
        return six;
    }
}
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
public enum ExcelEnum2 {

    USER_NAME("Nume", 1),
    IN("Intrare", 2),
    OUT("Iesire", 4),
    WORK("Timp Lucru", 5),
    PAUSE("Timp Pauza",6),
    TOTAL("Timp Total",7),
    CONTROL("Timp",101);


    private final String key;
    private final Integer defaultValue;

    ExcelEnum2(String key, Integer defaultValue) {
        this.key = key;
        this.defaultValue = defaultValue;
    }

    /**
     * @return the property corresponding to the key or null if not found
     */
 

    /**
     * @return the property corresponding to the key converted to Integer or
     * null if not found
     */
    public Integer getAsInteger() {
      return this.defaultValue;
    }

    /**
     * @return the property corresponding to the key converted to Double or null
   
    /**
     * @return the key that identifies this property either as Java VM parameter
     * name or config properties file entry
     */
    public String getKey() {
        return key;
    }
}

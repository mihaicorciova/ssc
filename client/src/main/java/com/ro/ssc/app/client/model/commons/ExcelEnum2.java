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

    USER_NAME("Nume", 0),
    IN("Intrare", 1),
    OUT("Iesire", 3),
    WORK("Timp Lucru", 4),
    PAUSE("Timp Pauza",5),
    TOTAL("Timp Total",6),
    I("Timp",100),
    O("Timp",101),
    W("Timp",102);


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

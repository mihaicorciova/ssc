/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ro.ssc.app.client.model.commons;

import com.ro.ssc.app.client.controller.MainController;
import java.util.EnumMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author DauBufu
 */
public enum ExcelEnum {

    USER_NAME("User Name", 3),
    CARD_NO("Card NO", 1),
    USER_ID("User ID", 2),
    DEPARTMENT("Departament", 4),
    TIMESTAMP("DateTime",5),
    ADDRESS("Addr",6),
    PASSED("Pass",7),
    DESCRIPTION("Description",8);
    
    private static final Logger log = LoggerFactory.getLogger(ExcelEnum.class);
    private final String key;
    private final Integer defaultValue;
  
    ExcelEnum(String key, Integer defaultValue) {
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

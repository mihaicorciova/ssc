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
import java.io.IOException;
import java.io.InputStream;
import java.util.EnumMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class responsible for managing project configuration properties.
 *
 */
public enum Configuration {

    // license check interval in minutes
    LICENSE_CHECK_INTERVAL(Configuration.NON_CONFIGURABLE, "60"),
    // encrypted expiration date of the application
    TRIAL_KEY("ssc.licensing.trialKey", null),
    // encrypted expiration date of the application
    IS_EXPIRED("ssc.mdb.status", "false"),
    IS_STB("ssc.stb.flag",null),
      IS_MON("ssc.mon.flag",null),
    HAS_LOGO("ssc.logo.present",null),
    // application version (value has to be set by another object)
    VERSION(Configuration.NON_CONFIGURABLE, "2.0");

    private static final Logger LOGGER = LoggerFactory.getLogger(Configuration.class);

    private static final String CONFIG_FILE = "config.properties";
    private static final String NON_CONFIGURABLE = "-";
    private static final Map<Configuration, String> CONFIG_MAP = new EnumMap<>(Configuration.class);

    static {
        readConfigurationFrom(CONFIG_FILE);
    }

    private final String key;
    private final String defaultValue;

    Configuration(String key, String defaultValue) {
        this.key = key;
        this.defaultValue = defaultValue;
    }

    private static void readConfigurationFrom(String fileName) {
        LOGGER.info("Reading configuration settings from file: {}", fileName);

        // populate the config with default values in case the config file loading fails
        for (Configuration c : values()) {
            
                CONFIG_MAP.put(c, c.defaultValue);
            
        }
        // read the properties file
        Properties properties = null;
        try (InputStream resource = Configuration.class.getClassLoader().getResourceAsStream(fileName)) {
            properties = new Properties();
            properties.load(resource);
        } catch (SecurityException | IllegalArgumentException | IOException | NullPointerException e) {
            LOGGER.error("Error while reading the configuration settings file. Default settings will be used instead.", e);
        }

        for (Configuration config : values()) {
            if (NON_CONFIGURABLE.equals(config.key)) {
                continue;
            }

            // try to read the values from Java VM arguments first
            String value = System.getProperty(config.key);

            // if not available, try to read it from the properties file
            if (value == null && properties != null) {
                value = properties.getProperty(config.key);
            }

            // if found, add it to the configuration map
            if (value != null) {
                CONFIG_MAP.put(config, value);
                LOGGER.info("Running with {} = {}", config.key, value);
            } else {
                LOGGER.warn("Could not find setting for '{}' in configuration file. The default value will be used instead: {}", config.key, config.defaultValue);
            }
        }
    }

    /**
     * @return the property corresponding to the key or null if not found
     */
    public String getAsString() {
        return CONFIG_MAP.get(this);
    }

    /**
     * Sets the parameter value to the string representation of the supplied
     * argument `
     */
    public void setValue(Object value) {
        CONFIG_MAP.put(this, (value == null) ? null : value.toString());
    }

    /**
     * @return the property corresponding to the key converted to Integer or
     * null if not found
     */
    public Integer getAsInteger() {
        try {
            return Integer.parseInt(getAsString());
        } catch (NullPointerException | NumberFormatException e) {
            LOGGER.warn("Failed to retrive configuration setting as an integer value (key: {}, value: {}).", this.key, getAsString(), e);
        }
        return null;
    }

    /**
     * @return the property corresponding to the key converted to Double or null
     * if not found
     */
    public Double getAsDouble() {
        try {
            return Double.parseDouble(getAsString());
        } catch (NullPointerException | NumberFormatException e) {
            LOGGER.warn("Failed to retrive configuration setting as a floating point value (key: {}, value: {}).", this.key, getAsString(), e);
        }
        return null;
    }

    /**
     * @return the property corresponding to the key converted to Boolean or
     * null if not found
     */
    public Boolean getAsBoolean() {
        return Boolean.parseBoolean(getAsString());
    }

    /**
     * @return the key that identifies this property either as Java VM parameter
     * name or config properties file entry
     */
    public String getKey() {
        return key;
    }
}

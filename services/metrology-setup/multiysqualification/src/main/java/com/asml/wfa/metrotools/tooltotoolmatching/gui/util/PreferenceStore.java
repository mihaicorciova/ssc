package com.asml.wfa.metrotools.tooltotoolmatching.gui.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import javax.enterprise.context.ApplicationScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Read and write files to the ini file
 */
@ApplicationScoped
public class PreferenceStore {
    private static final String INI_FILE = "flow-sso.ini";
    private final Properties properties = new Properties();
    private static final Logger LOGGER = LoggerFactory.getLogger(PreferenceStore.class);

    /**
     * save property
     * 
     * @param property
     *            property
     * @param value
     *            value
     */
    public void save(final String property, final String value) {
        LOGGER.debug("Saving property {} with value {}", property, value);

        properties.put(property, value);
        saveToDisk();

        LOGGER.trace("Property {} saved", property);
    }

    /**
     * Read property
     * 
     * @param property
     *            property
     * @return value
     */
    public String read(final String property) {
        LOGGER.trace("Reading property: {}", property);
        String value = "";

        readFromDisk();
        if (properties.containsKey(property)) {
            value = properties.getProperty(property);
        }

        LOGGER.debug("Property {} with value {} read", property, value);
        return value;
    }

    /**
     * Remove property from file Testing purpose mostly
     * 
     * @param property
     *            property
     */
    void remove(final String property) {
        if (!properties.containsKey(property)) {
            return;
        }

        properties.remove(property);
        saveToDisk();
    }

    private void saveToDisk() {
        try {
            properties.store(new FileOutputStream(INI_FILE), "SSO flow ini settings");
        } catch (final IOException e) {
            LOGGER.warn("Error writing settings to ini file: {}.", e);
        }
    }

    private void readFromDisk() {
        try {
            properties.load(new FileInputStream(INI_FILE));
        } catch (final FileNotFoundException e) {
            LOGGER.debug("Ini-file {} not found, creating a new one if possible", INI_FILE);
        } catch (final IOException e) {
            LOGGER.warn("Error reading settings from ini file: {}.", e);
        }
    }
}
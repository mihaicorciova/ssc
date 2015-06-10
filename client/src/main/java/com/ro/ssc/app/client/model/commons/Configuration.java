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

    // inspired from:
    // http://stackoverflow.com/questions/17809130/how-to-switch-from-a-hardcoded-static-config-file-to-a-properties-file/17809286#17809286
    // hostname and port for project MongoDB install
    MONGODB_HOST("productivitymonitor.mongodb.host", "localhost"),
    MONGODB_PORT("productivitymonitor.mongodb.port", "27017"),
    // base URL for LIS platform server
    LIS_SERVER_URL("lisp.server.http.baseUrl", "http://172.19.234.61:8080"),
    // if notifications are enabled
    NOTIFICATION_ENABLED("productivitymonitor.notifications.enabled", "true"),
    // URL of the local server where the project is set to receive notifications from NOSE
    NOTIFICATION_URL("productivitymonitor.notifications.receiver.baseUrl", "http://172.19.234.203:8080/productivitymonitor/rest"),
    // if bulk import is enabled
    IMPORTER_ENABLED("productivitymonitor.importer.enabled", "true"),
    // how many days back it should ask for files
    IMPORTER_DAYSBACK("productivitymonitor.importer.daysBack", "180"),
    // how many threads should be used for the bulk import process (0 means use one thread for each compute core)
    IMPORTER_THREADS("productivitymonitor.importer.threads", "4"),
    // determines if zipped MongoDB data file importing features should be enabled
    RAW_IMPORT(Configuration.NON_CONFIGURABLE, "false"),
    // determines if zipped MongoDB data file exporting features should be enabled
    RAW_EXPORT(Configuration.NON_CONFIGURABLE, "false"),
    // should existing data associated to the machines for which data file is imported be cleared before import
    PURGE_ON_IMPORT("productivitymonitor.rawdata.clearMachineDataBeforeImport", "false"),
    // if data cleanup is enabled
    CLEANUP_ENABLED("productivitymonitor.cleanup.enabled", "true"),
    // interval in hours between cleanup runs
    CLEANUP_INTERVAL("productivitymonitor.cleanup.runEveryHours", "48"),
    // should entire database contents be deleted on startup
    DROP_DB_STARTUP("productivitymonitor.cleanup.purgeEverythingOnStartup", "false"),
    // if machines refresh is enabled
    MACHINES_REFRESH_ENABLED("productivitymonitor.machines.enabled", "true"),
    // interval in minutes between machine list refresh
    MACHINES_REFRESH_INTERVAL("productivitymonitor.machines.refreshEveryMinutes", "60"),
    // webapp date period maximum length
    FILTER_PERIOD_MAX_RANGE("productivitymonitor.webapp.dateFilter.maxRangeDays", "31"),
    // webapp how far back is the earliest selectable day
    FILTER_PERIOD_DAYSBACK("productivitymonitor.webapp.dateFilter.daysBack", "180"),
    // webapp date period preset length
    FILTER_PERIOD_DEFAULT("productivitymonitor.webapp.dateFilter.initialRangeDays", "7"),
    // webapp live data refresh interval
    LIVE_DATA_REFRESH_INTERVAL("productivitymonitor.webapp.liveData.refreshEverySeconds", "1800"),
    // URL of the MGMT VM from LCP used for retrieving the LCP version
    LCP_MACHINE_NUMBER("productivitymonitor.lcp.machineNumberUrl", "http://10.40.0.200/sp/output/mnumber"),
    // license check interval in minutes
    LICENSE_CHECK_INTERVAL(Configuration.NON_CONFIGURABLE, "60"),
    // encrypted expiration date of the application
    TRIAL_KEY("productivitymonitor.licensing.trialKey", null),
    // application version (value has to be set by another object)
    VERSION(Configuration.NON_CONFIGURABLE, "0.0.1"),
    // midnight daily calculation task interval (24 hours in miliseconds)
    MIDNIGHT_DAILY_VALUES_INTERVAL(Configuration.NON_CONFIGURABLE, "86400000");

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

package com.bbob.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Properties;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;

public abstract class ApplicationConfig {

    private static final Logger LOGGER = Logger
            .getLogger(ApplicationConfig.class);

    private static String configFile = "/../config/application.properties";

    private static Properties properties = null;

    static {
        properties = new Properties();
        try {

            String test = System.getProperty("testMode");
            if (test != null) {
                configFile = "/application.properties";
            }
            InputStream inputStream = ApplicationConfig.class
                    .getResourceAsStream(configFile);
            if (inputStream != null) {
                Reader reader = new InputStreamReader(inputStream, "UTF-8");
                properties.load(reader);
                inputStream.close();
            }
        } catch (IOException e) {
            LOGGER.error("Error loading config file. ", e);
            e.printStackTrace();
        }
    }

    public static char getChar(String key) {
        return properties.getProperty(key).trim().charAt(0);
    }

    public static String getString(String key) {
        return properties.getProperty(key).trim();
    }

    public static boolean getBoolean(String key) {
        return BooleanUtils.toBoolean(getString(key));
    }

    public static int getInt(String key) {
        return NumberUtils.toInt(getString(key));
    }

    public static long getLong(String key) {
        return NumberUtils.toLong(getString(key));
    }

    public static double getDouble(String key) {
        return NumberUtils.toDouble(getString(key));
    }

}

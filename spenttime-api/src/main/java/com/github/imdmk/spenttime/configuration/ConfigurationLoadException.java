package com.github.imdmk.spenttime.configuration;

public class ConfigurationLoadException extends RuntimeException {
    public ConfigurationLoadException(Throwable cause) {
        super("Failed to load configuration", cause);
    }

    public ConfigurationLoadException(String message) {
        super(message);
    }

    public ConfigurationLoadException(String message, Throwable cause) {
        super(message, cause);
    }

}

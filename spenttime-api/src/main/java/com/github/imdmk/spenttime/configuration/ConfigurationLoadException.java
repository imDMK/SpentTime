package com.github.imdmk.spenttime.configuration;

public class ConfigurationLoadException extends RuntimeException {
    public ConfigurationLoadException(Throwable cause) {
        super("Failed to load configuration", cause);
    }
}

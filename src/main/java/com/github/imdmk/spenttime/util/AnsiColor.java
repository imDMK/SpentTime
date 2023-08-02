package com.github.imdmk.spenttime.util;

public class AnsiColor {

    public static final String RESET = "\u001B[0m";

    public static final String GREEN = "\033[0;32m";
    public static final String YELLOW = "\033[0;33m";
    public static final String RED = "\033[0;31m";

    private AnsiColor() {
        throw new UnsupportedOperationException("This is utility class.");
    }
}

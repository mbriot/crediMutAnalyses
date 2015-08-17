package com.mbriot.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Log {
    private static final Logger log = LoggerFactory.getLogger("bankAccount");

    public static void info(String message, Object... options) {
        log.info(String.format(message, options));
    }

    public static void debug(String message, Object... options) {
        log.debug(String.format(message, options));
    }

    public static void trace(String message, Object... options) {
        log.debug(String.format(message, options));
    }

    public static void error(String message, Object... options) {
        log.error(String.format(message, options));
    }

    public static void error(Throwable throwable, String message, Object... options) {
        log.error(String.format(message, options), throwable);
    }
}

package me.xnexusacs.asteria.logging;

public interface AsteriaLogHandler {
    void log(LogLevel level, String message, Throwable throwable);
}


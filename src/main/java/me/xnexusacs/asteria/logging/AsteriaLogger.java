package me.xnexusacs.asteria.logging;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AsteriaLogger<T> {

    private final String className;
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    public AsteriaLogger(Class<T> clazz) {
        this.className = clazz.getSimpleName();
    }

    private void log(LogLevel level, String message, Throwable throwable) {
        String time = LocalDateTime.now().format(timeFormatter);
        String base = String.format("[%s] [%s] [%s] %s", time, className, level.getTag(), message);

        System.out.println(base);

        if (throwable != null) {
            throwable.printStackTrace(System.out);
        }
    }

    public void log(LogLevel level, String message) {
        log(level, message, null);
    }

    public void info(String message) {
        log(LogLevel.INFORMATION, message);
    }

    public void warn(String message) {
        log(LogLevel.WARNING, message);
    }

    public void error(String message) {
        log(LogLevel.ERROR, message);
    }

    public void error(String message, Throwable throwable) {
        log(LogLevel.ERROR, message, throwable);
    }

    public void fatal(String message) {
        log(LogLevel.FATAL, message);
    }

    public void fatal(String message, Throwable throwable) {
        log(LogLevel.FATAL, message, throwable);
    }

    public void debug(String message, boolean shouldShow) {
        if (!shouldShow)
            return;

        log(LogLevel.DEBUG, message);
    }

    public void verbose(String message) {
        log(LogLevel.STDOUT, message);
    }
}

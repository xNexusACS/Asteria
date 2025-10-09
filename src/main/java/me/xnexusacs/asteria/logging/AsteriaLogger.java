package me.xnexusacs.asteria.logging;

import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AsteriaLogger<T> {

    private final String className;
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    private final List<AsteriaLogHandler> handlers = Collections.synchronizedList(new ArrayList<>());

    private final PrintStream out = new PrintStream(System.out) {
        @Override
        public void println(String x) {
            if (x != null && x.startsWith("[STDOUT]:")) {
                x = x.substring(9).trim();
            }
            super.println(x);
        }
    };

    public AsteriaLogger(Class<T> clazz) {
        this.className = clazz.getSimpleName();
    }

    private void log(LogLevel level, String message, Throwable throwable) {
        String time = LocalDateTime.now().format(timeFormatter);
        String base = String.format("[%s] [%s] [%s] %s", time, className, level.getTag(), message);

        boolean anyHandled = false;
        synchronized (handlers) {
            if (!handlers.isEmpty()) {
                for (AsteriaLogHandler handler : handlers) {
                    try {
                        handler.log(level, base, throwable);
                        anyHandled = true;
                    } catch (Exception e) {
                        System.err.println("[AsteriaLogger] Handler threw exception: " + e.getMessage());
                    }
                }
            }
        }

        if (!anyHandled && handlers.isEmpty()) {
            out.println(base);

            if (throwable != null)
                throwable.printStackTrace(out);
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

    public void addLogHandler(AsteriaLogHandler handler) {
        if (handler != null && !handlers.contains(handler))
            handlers.add(handler);
    }

    public void removeLogHandler(AsteriaLogHandler handler) {
        handlers.remove(handler);
    }

    public void clearHandlers() {
        handlers.clear();
    }

    public List<AsteriaLogHandler> getHandlers() {
        return new ArrayList<>(handlers);
    }
}

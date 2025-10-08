package me.xnexusacs.asteria.logging;

public enum LogLevel {

    INFORMATION("INFO"),
    WARNING("WARN"),
    ERROR("ERR"),
    FATAL("FATAL"),
    DEBUG("DEBUG"),
    STDOUT("STDOUT");

    private final String tag;

    LogLevel(String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }
}

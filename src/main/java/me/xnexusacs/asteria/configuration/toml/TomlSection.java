package me.xnexusacs.asteria.configuration.toml;

import java.util.LinkedHashMap;
import java.util.Map;

public class TomlSection {

    private final String name;
    private final Map<String, Object> values = new LinkedHashMap<>();

    public TomlSection(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Map<String, Object> getValues() {
        return values;
    }

    public void set(String key, Object value) {
        values.put(key, value);
    }

    public Object get(String key) {
        return values.get(key);
    }

    public String getString(String key) {
        Object v = values.get(key);
        return v != null ? v.toString() : null;
    }

    public Integer getInt(String key) {
        Object v = values.get(key);
        return v instanceof Number ? ((Number) v).intValue() : null;
    }

    public Double getDouble(String key) {
        Object v = values.get(key);
        return v instanceof Number ? ((Number) v).doubleValue() : null;
    }

    public Float getFloat(String key) {
        Object v = values.get(key);
        return v instanceof Number ? ((Number) v).floatValue() : null;
    }

    public Boolean getBoolean(String key) {
        Object v = values.get(key);
        return v instanceof Boolean ? (Boolean) v : null;
    }
}

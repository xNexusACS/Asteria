package me.xnexusacs.asteria.configuration.toml;

import java.util.LinkedHashMap;
import java.util.Map;

public class TomlFile {

    private final Map<String, Object> rootValues = new LinkedHashMap<>();
    private final Map<String, TomlSection> sections = new LinkedHashMap<>();

    public Map<String, Object> getRootValues() {
        return rootValues;
    }

    public Map<String, TomlSection> getSections() {
        return sections;
    }

    public void set(String key, Object value) {
        rootValues.put(key, value);
    }

    public Object get(String key) {
        return rootValues.get(key);
    }

    public TomlSection getSection(String name) {
        return sections.get(name);
    }

    public void addSection(TomlSection section) {
        sections.put(section.getName(), section);
    }

    public String getString(String key) {
        Object v = rootValues.get(key);
        return v != null ? v.toString() : null;
    }

    public Integer getInt(String key) {
        Object v = rootValues.get(key);
        return v instanceof Number ? ((Number) v).intValue() : null;
    }

    public Double getDouble(String key) {
        Object v = rootValues.get(key);
        return v instanceof Number ? ((Number) v).doubleValue() : null;
    }

    public Float getFloat(String key) {
        Object v = rootValues.get(key);
        return v instanceof Number ? ((Number) v).floatValue() : null;
    }

    public Boolean getBoolean(String key) {
        Object v = rootValues.get(key);
        return v instanceof Boolean ? (Boolean) v : null;
    }
}

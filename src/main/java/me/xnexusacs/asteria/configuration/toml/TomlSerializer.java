package me.xnexusacs.asteria.configuration.toml;

import java.util.Map;

public class TomlSerializer {

    public static String serialize(TomlFile file) {
        StringBuilder sb = new StringBuilder();

        for (Map.Entry<String, Object> e : file.getRootValues().entrySet()) {
            sb.append(e.getKey())
                    .append(" = ")
                    .append(toTomlValue(e.getValue()))
                    .append("\n");
        }

        if (!file.getRootValues().isEmpty()) {
            sb.append("\n");
        }

        for (TomlSection section : file.getSections().values()) {
            sb.append("[").append(section.getName()).append("]").append("\n");

            for (Map.Entry<String, Object> e : section.getValues().entrySet()) {
                sb.append(e.getKey())
                        .append(" = ")
                        .append(toTomlValue(e.getValue()))
                        .append("\n");
            }

            sb.append("\n");
        }

        return sb.toString().trim();
    }

    private static String toTomlValue(Object val) {
        if (val == null) return "null";
        if (val instanceof String) return "\"" + escapeString((String) val) + "\"";
        if (val instanceof Boolean || val instanceof Number) return val.toString();
        return "\"" + escapeString(val.toString()) + "\"";
    }

    private static String escapeString(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}

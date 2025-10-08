package me.xnexusacs.asteria.configuration.json;

import java.lang.reflect.Field;
import java.util.StringJoiner;

public class JsonSerializer {

    public static String serialize(Object obj) {
        if (obj == null) return "null";

        Class<?> clazz = obj.getClass();
        StringJoiner joiner = new StringJoiner(", ", "{", "}");

        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Object value = field.get(obj);
                String name = "\"" + field.getName() + "\"";
                String jsonValue = toJsonValue(value);
                joiner.add(name + ": " + jsonValue);
            } catch (IllegalAccessException ignored) {
            }
        }

        return joiner.toString();
    }

    private static String toJsonValue(Object value) {
        if (value == null) return "null";

        if (value instanceof String) {
            return "\"" + escapeString((String) value) + "\"";
        }
        if (value instanceof Boolean || value instanceof Integer ||
                value instanceof Float || value instanceof Double) {
            return value.toString();
        }

        return "\"" + escapeString(value.toString()) + "\"";
    }

    private static String escapeString(String str) {
        return str.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}

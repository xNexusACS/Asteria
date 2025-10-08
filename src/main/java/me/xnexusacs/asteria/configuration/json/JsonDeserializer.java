package me.xnexusacs.asteria.configuration.json;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class JsonDeserializer {

    public static <T> T deserialize(String json, Class<T> clazz) {
        if (json == null || json.trim().isEmpty() || json.trim().equals("null")) {
            return null;
        }

        json = json.trim();
        if (json.startsWith("{")) json = json.substring(1);
        if (json.endsWith("}")) json = json.substring(0, json.length() - 1);

        Map<String, String> map = parseKeyValuePairs(json);

        try {
            T instance = clazz.getDeclaredConstructor().newInstance();

            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                String rawValue = map.get(field.getName());
                if (rawValue == null) continue;

                Object converted = parseValue(rawValue, field.getType());
                field.set(instance, converted);
            }

            return instance;
        } catch (Exception e) {
            throw new RuntimeException("Error deserializing JSON to " + clazz.getSimpleName(), e);
        }
    }

    private static Map<String, String> parseKeyValuePairs(String json) {
        Map<String, String> map = new HashMap<>();

        boolean inQuotes = false;
        StringBuilder key = new StringBuilder();
        StringBuilder value = new StringBuilder();
        boolean readingKey = true;

        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);

            if (c == '\"') {
                inQuotes = !inQuotes;
                continue;
            }

            if (!inQuotes && c == ':') {
                readingKey = false;
                continue;
            }

            if (!inQuotes && c == ',') {
                map.put(key.toString().trim(), value.toString().trim());
                key.setLength(0);
                value.setLength(0);
                readingKey = true;
                continue;
            }

            if (readingKey) key.append(c);
            else value.append(c);
        }

        if (!key.isEmpty()) {
            map.put(key.toString().trim(), value.toString().trim());
        }

        Map<String, String> cleanMap = new HashMap<>();
        for (var entry : map.entrySet()) {
            String k = entry.getKey().replace("\"", "");
            String v = entry.getValue();
            cleanMap.put(k, v);
        }

        return cleanMap;
    }

    private static Object parseValue(String raw, Class<?> type) {
        raw = raw.trim();

        if (raw.equals("null")) return null;

        if (type == String.class) {
            if (raw.startsWith("\"") && raw.endsWith("\"")) {
                return raw.substring(1, raw.length() - 1)
                        .replace("\\n", "\n")
                        .replace("\\r", "\r")
                        .replace("\\t", "\t")
                        .replace("\\\"", "\"")
                        .replace("\\\\", "\\");
            }
            return raw;
        }

        if (type == boolean.class || type == Boolean.class)
            return Boolean.parseBoolean(raw);

        if (type == int.class || type == Integer.class)
            return Integer.parseInt(raw);

        if (type == float.class || type == Float.class)
            return Float.parseFloat(raw);

        if (type == double.class || type == Double.class)
            return Double.parseDouble(raw);

        return null;
    }
}

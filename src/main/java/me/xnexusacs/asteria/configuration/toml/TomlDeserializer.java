package me.xnexusacs.asteria.configuration.toml;

public class TomlDeserializer {

    public static TomlFile deserialize(String toml) {
        TomlFile file = new TomlFile();
        TomlSection current = null;

        String[] lines = toml.split("\\r?\\n");
        for (String rawLine : lines) {
            String line = rawLine.trim();
            if (line.isEmpty() || line.startsWith("#")) continue;

            if (line.startsWith("[") && line.endsWith("]")) {
                String name = line.substring(1, line.length() - 1).trim();
                current = new TomlSection(name);
                file.addSection(current);
                continue;
            }

            int eq = line.indexOf('=');
            if (eq == -1) continue;

            String key = line.substring(0, eq).trim();
            String rawValue = line.substring(eq + 1).trim();
            Object value = parseValue(rawValue);

            if (current == null)
                file.set(key, value);
            else
                current.set(key, value);
        }

        return file;
    }

    private static Object parseValue(String raw) {
        if (raw.equalsIgnoreCase("true")) return true;
        if (raw.equalsIgnoreCase("false")) return false;
        if (raw.equalsIgnoreCase("null")) return null;

        if (raw.startsWith("\"") && raw.endsWith("\""))
            return unescapeString(raw.substring(1, raw.length() - 1));

        try {
            if (raw.contains(".")) {
                double d = Double.parseDouble(raw);
                return raw.endsWith("f") || raw.endsWith("F") ? (float) d : d;
            }
            return Integer.parseInt(raw);
        } catch (NumberFormatException e) {
            return raw;
        }
    }

    private static String unescapeString(String s) {
        return s.replace("\\\"", "\"").replace("\\\\", "\\");
    }
}

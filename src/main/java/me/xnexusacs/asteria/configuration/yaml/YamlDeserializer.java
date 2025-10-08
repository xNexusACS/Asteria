package me.xnexusacs.asteria.configuration.yaml;

import java.lang.reflect.Field;

public class YamlDeserializer {

    public static <T> T deserialize(String yaml, Class<T> clazz) {
        T obj;
        try {
            obj = clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create instance of " + clazz.getName(), e);
        }

        String[] lines = yaml.split("\n");
        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                continue;
            }

            String[] parts = trimmed.split(":", 2);
            if (parts.length != 2) {
                continue;
            }

            String key = parts[0].trim();
            String value = parts[1].trim();

            if ((value.startsWith("\"") && value.endsWith("\"")) ||
                    (value.startsWith("'") && value.endsWith("'"))) {
                value = value.substring(1, value.length() - 1);
            }

            try {
                Field field = clazz.getDeclaredField(key);
                field.setAccessible(true);

                Class<?> fieldType = field.getType();
                Object converted;

                if (fieldType == String.class) {
                    converted = value;
                } else if (fieldType == int.class || fieldType == Integer.class) {
                    try {
                        converted = Integer.parseInt(value);
                    } catch (NumberFormatException e) {
                        converted = 0;
                    }
                } else if (fieldType == boolean.class || fieldType == Boolean.class) {
                    converted = Boolean.parseBoolean(value);
                } else if (fieldType == double.class || fieldType == Double.class) {
                    try {
                        converted = Double.parseDouble(value);
                    } catch (NumberFormatException e) {
                        converted = 0.0;
                    }
                } else {
                    try {
                        converted = fieldType.getMethod("valueOf", String.class).invoke(null, value);
                    } catch (Exception e1) {
                        try {
                            converted = fieldType.getConstructor(String.class).newInstance(value);
                        } catch (Exception e2) {
                            converted = null;
                        }
                    }
                }

                if (converted != null) {
                    field.set(obj, converted);
                }
            }
            catch (NoSuchFieldException e) {
                System.out.println("Unknown field " + key + " in " + clazz.getName());
            } catch (Exception e) {
                System.out.println("Error parsing " + key + "=" + value);
            }
        }

        return obj;
    }
}

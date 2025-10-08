package me.xnexusacs.asteria.configuration.yaml;

import java.beans.IntrospectionException;
import java.lang.reflect.Field;


public class YamlSerializer {

    public static <T> String serialize(T obj) throws IntrospectionException {
        StringBuilder sb = new StringBuilder();
        Class<?> type = obj.getClass();

        for (Field field : type.getDeclaredFields()) {
            field.setAccessible(true);

            try {
                Object value = field.get(obj);
                if (value == null) continue;

                YamlDescription description = field.getAnnotation(YamlDescription.class);
                if (description != null) {
                    sb.append("# ").append(description.value()).append("\n");
                }

                sb.append(field.getName()).append(": ").append(value).append("\n");
            } catch (Exception e) {
                System.out.println("Error serializing field " + field.getName() + ": " + e.getMessage());
            }
        }

        return sb.toString();
    }
}

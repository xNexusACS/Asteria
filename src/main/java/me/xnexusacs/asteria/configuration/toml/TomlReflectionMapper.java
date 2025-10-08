package me.xnexusacs.asteria.configuration.toml;

import java.lang.reflect.Field;

public class TomlReflectionMapper {

    public static <T> TomlFile toTomlFile(T obj) throws IllegalAccessException {
        TomlFile file = new TomlFile();

        for (Field field : obj.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            Object value = field.get(obj);

            if (value == null) continue;

            if (!isPrimitiveOrWrapper(value.getClass()) && !(value instanceof String)) {
                TomlSection section = new TomlSection(field.getName());
                for (Field subField : value.getClass().getDeclaredFields()) {
                    subField.setAccessible(true);
                    section.set(subField.getName(), subField.get(value));
                }
                file.addSection(section);
            } else {
                file.set(field.getName(), value);
            }
        }

        return file;
    }

    public static <T> T fromTomlFile(TomlFile file, Class<T> clazz) throws Exception {
        T instance = clazz.getDeclaredConstructor().newInstance();

        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);

            Object value = file.getRootValues().get(field.getName());
            if (value != null) {
                field.set(instance, value);
                continue;
            }

            TomlSection section = file.getSection(field.getName());
            if (section != null) {
                Class<?> fieldType = field.getType();
                Object subObj = fieldType.getDeclaredConstructor().newInstance();

                for (Field subField : fieldType.getDeclaredFields()) {
                    subField.setAccessible(true);
                    Object subVal = section.getValues().get(subField.getName());
                    if (subVal != null) subField.set(subObj, subVal);
                }

                field.set(instance, subObj);
            }
        }

        return instance;
    }

    private static boolean isPrimitiveOrWrapper(Class<?> type) {
        return type.isPrimitive() ||
                type == Boolean.class || type == Integer.class ||
                type == Double.class || type == Float.class ||
                type == Long.class || type == Short.class ||
                type == Byte.class || type == Character.class;
    }
}

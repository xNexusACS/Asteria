package me.xnexusacs.asteria.configuration.toml;

import java.lang.reflect.Field;

public class TomlReflectionMapper {

    public static <T> TomlFile toTomlFile(T obj) throws IllegalAccessException {
        TomlFile file = new TomlFile();

        for (Field field : obj.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            Object value = field.get(obj);
            if (value == null) continue;

            Class<?> fieldClass = value.getClass();
            boolean isPrimitive = isPrimitiveOrWrapper(fieldClass) || value instanceof String;

            if (isPrimitive) {
                file.set(field.getName(), value);
            } else {
                String sectionName = fieldClass.isAnnotationPresent(TomlClassSection.class)
                        ? fieldClass.getAnnotation(TomlClassSection.class).value()
                        : field.isAnnotationPresent(TomlClassSection.class)
                        ? field.getAnnotation(TomlClassSection.class).value()
                        : field.getName();

                TomlSection section = new TomlSection(sectionName);

                for (Field subField : fieldClass.getDeclaredFields()) {
                    subField.setAccessible(true);
                    Object subVal = subField.get(value);
                    if (subVal != null) {
                        section.set(subField.getName(), subVal);
                    }
                }

                file.addSection(section);
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

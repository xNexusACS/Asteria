package me.xnexusacs.asteria.configuration.toml;

public class TomlObjectMapper {

    public static <T> String serialize(T obj) {
        try {
            TomlFile file = TomlReflectionMapper.toTomlFile(obj);
            return TomlSerializer.serialize(file);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize object to TOML: " + e.getMessage(), e);
        }
    }

    public static <T> T deserialize(String toml, Class<T> clazz) {
        try {
            TomlFile file = TomlDeserializer.deserialize(toml);
            return TomlReflectionMapper.fromTomlFile(file, clazz);
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize TOML to object: " + e.getMessage(), e);
        }
    }
}

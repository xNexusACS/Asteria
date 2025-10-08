package me.xnexusacs.asteria.configuration.toml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class TomlManager {

    private final String configPath;

    public TomlManager(String path) {
        this.configPath = path;

        File configDir = new File(configPath);
        if (!configDir.exists()) {
            //noinspection ResultOfMethodCallIgnored
            configDir.mkdirs();
        }
    }

    public <T> TomlConfig<T> initTomlConfig(String fileName, Class<T> clazz) throws IOException {
        File file = new File(configPath, fileName);

        if (file.exists()) {
            return new TomlConfig<>(file.getAbsolutePath(), clazz);
        }

        try {
            T obj = clazz.getDeclaredConstructor().newInstance();
            String toml = TomlObjectMapper.serialize(obj);

            try (FileWriter writer = new FileWriter(file)) {
                writer.write(toml);
            }

            return new TomlConfig<>(file.getAbsolutePath(), clazz);
        } catch (IOException | InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException("Error initializing TOML config: " + fileName, e);
        }
    }
}

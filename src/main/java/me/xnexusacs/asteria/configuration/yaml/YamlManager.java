package me.xnexusacs.asteria.configuration.yaml;

import java.beans.IntrospectionException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class YamlManager {

    private final String configPath;

    public YamlManager(String path) {
        configPath = path;

        File configDir = new File(configPath);

        if (!configDir.exists()) {
            //noinspection ResultOfMethodCallIgnored
            configDir.mkdirs();
        }
    }

    public <T> YamlConfig<T> initYamlConfig(String fileName, Class<T> clazz) throws IOException {
        File file = new File(configPath, fileName);

        if (file.exists()) {
            return new YamlConfig<>(file.getAbsolutePath(), clazz);
        }

        try {
            T obj = clazz.getDeclaredConstructor().newInstance();
            String yaml = YamlSerializer.serialize(obj);

            try (FileWriter writer = new FileWriter(file)) {
                writer.write(yaml);
            }

            return new YamlConfig<>(file.getAbsolutePath(), clazz);
        } catch (IOException | InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException("Error initializing YAML config: " + fileName, e);
        } catch (IntrospectionException e) {
            throw new RuntimeException(e);
        }
    }
}

package me.xnexusacs.asteria.configuration.json;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class JsonManager {

    private final String configPath;

    public JsonManager(String path) {
        this.configPath = path;

        File configDir = new File(configPath);
        if (!configDir.exists()) {
            //noinspection ResultOfMethodCallIgnored
            configDir.mkdirs();
        }
    }

    public <T> JsonConfig<T> initJsonConfig(String fileName, Class<T> clazz) throws IOException {
        File file = new File(configPath, fileName);

        if (file.exists()) {
            return new JsonConfig<>(file.getAbsolutePath(), clazz);
        }

        try {
            T obj = clazz.getDeclaredConstructor().newInstance();
            String json = JsonSerializer.serialize(obj);

            try (FileWriter writer = new FileWriter(file)) {
                writer.write(json);
            }

            return new JsonConfig<>(file.getAbsolutePath(), clazz);
        } catch (IOException | InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException("Error initializing JSON config: " + fileName, e);
        }
    }
}

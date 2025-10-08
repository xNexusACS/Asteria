package me.xnexusacs.asteria.configuration.yaml;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.nio.file.*;
import java.util.function.Consumer;

public class YamlConfig<T> {

    private final Path path;
    private final WatchService watchService;

    private T currentConfig;

    public interface OnConfigReloaded<T> {
        void onReload(T config);
    }

    private OnConfigReloaded<T> onConfigReloaded;

    public T getCurrentConfig() {
        return currentConfig;
    }

    public void setOnConfigReloaded(OnConfigReloaded<T> listener) {
        this.onConfigReloaded = listener;
    }

    public YamlConfig(String pathStr, Class<T> clazz) throws IOException {
        this.path = Paths.get(pathStr);
        loadConfig(clazz);

        this.watchService = FileSystems.getDefault().newWatchService();
        Path dir = this.path.getParent();
        if (dir == null) {
            throw new IllegalArgumentException("Path must have a parent directory");
        }
        dir.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);

        Thread watchThread = new Thread(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    WatchKey key = watchService.take();
                    for (WatchEvent<?> event : key.pollEvents()) {
                        Path changed = (Path) event.context();
                        if (changed.equals(this.path.getFileName())) {
                            loadConfig(clazz);
                        }
                    }
                    key.reset();
                }
            } catch (InterruptedException ignored) {
            }
        });
        watchThread.setDaemon(true);
        watchThread.setName("Asteria Yaml Config Thread");
        watchThread.start();
    }

    private void loadConfig(Class<T> clazz) {
        try {
            String yaml = Files.readString(path);
            currentConfig = YamlDeserializer.deserialize(yaml, clazz);
            if (onConfigReloaded != null) {
                onConfigReloaded.onReload(currentConfig);
            }
        } catch (Exception e) {
            System.out.println("Failed to load config from " + path);
        }
    }

    public void update(Consumer<T> updateAction) {
        updateAction.accept(currentConfig);
        try {
            String yaml = YamlSerializer.serialize(currentConfig);
            Files.writeString(path, yaml);
            if (onConfigReloaded != null) {
                onConfigReloaded.onReload(currentConfig);
            }
        } catch (IOException e) {
            System.out.println("Failed to write config to " + path);
        } catch (IntrospectionException e) {
            System.out.println("Failed to serialize config");
        }
    }
}

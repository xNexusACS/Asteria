package me.xnexusacs.asteria.configuration.toml;

import java.io.IOException;
import java.nio.file.*;
import java.util.function.Consumer;

public class TomlConfig<T> {

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

    public TomlFile getTomlFile() {
        try {
            return TomlReflectionMapper.toTomlFile(currentConfig);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to convert currentConfig to TomlFile", e);
        }
    }

    public void setOnConfigReloaded(OnConfigReloaded<T> listener) {
        this.onConfigReloaded = listener;
    }

    public TomlConfig(String pathStr, Class<T> clazz) throws IOException {
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
        watchThread.setName("Asteria TOML Config Thread");
        watchThread.start();
    }

    private void loadConfig(Class<T> clazz) {
        try {
            String toml = Files.readString(path);
            currentConfig = TomlObjectMapper.deserialize(toml, clazz);
            if (onConfigReloaded != null) {
                onConfigReloaded.onReload(currentConfig);
            }
        } catch (Exception e) {
            System.out.println("Failed to load TOML config from " + path + ": " + e.getMessage());
        }
    }

    public void update(Consumer<T> updateAction) {
        updateAction.accept(currentConfig);
        try {
            String toml = TomlObjectMapper.serialize(currentConfig);
            Files.writeString(path, toml);
            if (onConfigReloaded != null) {
                onConfigReloaded.onReload(currentConfig);
            }
        } catch (IOException e) {
            System.out.println("Failed to write TOML config to " + path + ": " + e.getMessage());
        }
    }
}

package me.xnexusacs.asteria.configuration.json;


import java.io.IOException;
import java.nio.file.*;
import java.util.function.Consumer;

public class JsonConfig<T> {

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

    public JsonConfig(String pathStr, Class<T> clazz) throws IOException {
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
        watchThread.setName("Asteria JSON Config Thread");
        watchThread.start();
    }

    private void loadConfig(Class<T> clazz) {
        try {
            String json = Files.readString(path);
            currentConfig = JsonDeserializer.deserialize(json, clazz);
            if (onConfigReloaded != null) {
                onConfigReloaded.onReload(currentConfig);
            }
        } catch (Exception e) {
            System.out.println("Failed to load JSON config from " + path + ": " + e.getMessage());
        }
    }

    public void update(Consumer<T> updateAction) {
        updateAction.accept(currentConfig);
        try {
            String json = JsonSerializer.serialize(currentConfig);
            Files.writeString(path, json);
            if (onConfigReloaded != null) {
                onConfigReloaded.onReload(currentConfig);
            }
        } catch (IOException e) {
            System.out.println("Failed to write JSON config to " + path + ": " + e.getMessage());
        }
    }
}

package DirectoryWatcher;

import sample.Client;

import static java.nio.file.StandardWatchEventKinds.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileWatcher implements Runnable {
    protected List<FileListener> listeners = new ArrayList<>();

    protected final File folder;

    Client client;
    protected final List<WatchService> watchServices = new ArrayList<>();

    public FileWatcher(File folder, Client client) {
        this.folder = folder;
        this.client = client;
    }

    public void watch() {
        if (folder.exists()) {
            Thread thread = new Thread(this);
            thread.start();
        }
    }

    @Override
    public void run() {
            try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
                Path path = Paths.get(folder.getAbsolutePath());
                path.register(watchService, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE);
                watchServices.add(watchService);
                boolean poll = true;
                while (client.isAlive && poll) {
                    poll = pollEvents(watchService);
                }
            } catch (IOException | InterruptedException | ClosedWatchServiceException e) {
                Thread.currentThread().interrupt();
            }
        System.out.println("exit");
    }

    protected boolean pollEvents(WatchService watchService) throws InterruptedException {
        WatchKey key = watchService.take();
        Path path = (Path) key.watchable();
        if (client.isAlive)
            for (WatchEvent<?> event : key.pollEvents()) {
                notifyListeners(event.kind(), path.resolve((Path) event.context()).toFile());
            }
        return key.reset();
    }
    protected void notifyListeners(WatchEvent.Kind<?> kind, File file) {
        FileEvent event = new FileEvent(file);
        if (kind == ENTRY_CREATE) {
            for (FileListener listener : listeners) {
                listener.onCreated(event);
            }
            if (file.isDirectory()) {
                new FileWatcher(file, client).setListeners(listeners).watch();
            }
        }
        else if (kind == ENTRY_MODIFY) {
            if (file.isDirectory())
                return;
            for (FileListener listener : listeners) {
                listener.onModified(event);
            }
        }
        else if (kind == ENTRY_DELETE) {
            for (FileListener listener : listeners) {
                listener.onDeleted(event);
            }
        }
    }
    public FileWatcher addListener(FileListener listener) {
        listeners.add(listener);
        return this;
    }

    public FileWatcher removeListener(FileListener listener) {
        listeners.remove(listener);
        return this;
    }

    public List<FileListener> getListeners() {
        return listeners;
    }

    public FileWatcher setListeners(List<FileListener> listeners) {
        this.listeners = listeners;
        return this;
    }

    public List<WatchService> getWatchServices() {
        return Collections.unmodifiableList(watchServices);
    }
}

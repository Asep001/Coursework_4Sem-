package DirectoryWatcher;

import sample.Client;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DirectoryWatcher {
    Client client;

    public DirectoryWatcher(String pathToFolder, Client client){
        this.client = client;
        addObserver(pathToFolder);
    }

    private void addObserver(String pathToFolder){
        List<FileListener> listeners = new ArrayList<>();

        FileAdapter listener = new FileAdapter(client);

        listeners.add(listener);
        addObserverOnFolder(pathToFolder, listeners);
    }

    private void addObserverOnFolder(String pathToFolder, List<FileListener> listeners){
        File folder = new File(pathToFolder);

        new FileWatcher(folder, client).setListeners(listeners).watch();

        File[] fileList = folder.listFiles();

        if (fileList == null) {
            return;
        }
        for(File file : fileList) {
            if (file.isDirectory())
                addObserverOnFolder(file.getAbsolutePath(), listeners);
        }
    }

}

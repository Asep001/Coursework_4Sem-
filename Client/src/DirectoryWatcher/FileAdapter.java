package DirectoryWatcher;

import javafx.application.Platform;
import sample.Client;

import java.io.FileNotFoundException;
import java.io.IOException;

public class FileAdapter implements FileListener {
    Client client;
    FileAdapter(Client client){
        this.client = client;
    }

    @Override
    public void onCreated(FileEvent event) {
        System.out.println("file created " + event.getFile().getName());

        try {
            client.uploadFile(event.getFile(), client.MainDirectoryParent);
        } catch (FileNotFoundException e) {
            synchronized(this) {
                try {
                    this.wait(1000);
                } catch (InterruptedException q) {
                    q.printStackTrace();
                }
            }
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }

        Platform.runLater(()-> {
            try {
                client.refreshFileTree();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });

    }

    @Override
    public void onModified(FileEvent event) {
        System.out.println("file modified " + event.getFile().getName());
        client.deleteFile(event.getFile(), client.MainDirectoryParent);

        try {
            client.uploadFile(event.getFile(), client.MainDirectoryParent);
        } catch (FileNotFoundException e) {
            synchronized(this) {
                try {
                    this.wait(1000);
                } catch (InterruptedException q) {
                    q.printStackTrace();
                }
            }
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }

        Platform.runLater(()-> {
            try {
                client.refreshFileTree();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onDeleted(FileEvent event) {
        System.out.println("file deleted " + event.getFile().getName());
        client.deleteFile(event.getFile(), client.MainDirectoryParent);
        Platform.runLater(()-> {
            try {
                client.refreshFileTree();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });
    }
}

package sample;

import javafx.application.Platform;
import java.io.IOException;

public class RefreshTimerTask extends java.util.TimerTask {
    private final Client client;

    RefreshTimerTask(Client client){
        this.client = client;
    }

    @Override
    public void run() {
        Platform.runLater(()-> {
            try {
                if (client.isAlive) {
                    client.refreshFileTree();
                    client.setTimer(client.getTimerPeriod());
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });
    }
}

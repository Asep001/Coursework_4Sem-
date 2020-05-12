package sample;

import javafx.stage.Stage;

import java.io.IOException;

public class Client {
    public long id;

    createWindowsServise createWindowsServise;

    Stage primaryStage;

    Requests requests;

    Client(Stage primaryStage){
        this.primaryStage = primaryStage;
    }

    public void openRegistration(){
        this.id = -1;
        createWindowsServise.createRegistrationWindow("Здравствуйте", this);
    }

    public void start(String serverIp, String servetPort) {

        requests = new Requests(serverIp, servetPort);

        createWindowsServise = new createWindowsServise(this.primaryStage,this.requests);
        openRegistration();
    }
}

package sample;

import javafx.stage.Stage;

import java.io.IOException;

public class Client {
    public long id;

    createWindowsServise createWindowsServise;

    Client(Stage primaryStage){
        createWindowsServise = new createWindowsServise(primaryStage);
    }

    public void openRegistration(){
        this.id = -1;
        createWindowsServise.createRegistrationWindow("Здравствуйте", this);
    }

    public void start() {
        openRegistration();
    }
}

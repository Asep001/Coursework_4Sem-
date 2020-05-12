package sample;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;

public class createWindowsServise {
    Stage primaryStage;


    Requests requests = new Requests();

    createWindowsServise(Stage primaryStage){
        this.primaryStage = primaryStage;
    }

    public void createRegistrationWindow(String fileName, Client client){


        TextField nameField = new TextField();
        nameField.setPromptText("Insert your name");

        TextField passwordField = new TextField();
        passwordField.setPromptText("Insert your password");

        Button singIn = new Button();
        singIn.setText("SingIn");
        singIn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    String name = nameField.getText();
                    String password = passwordField.getText();
                    if (!name.equals("") && !password.equals("")){
                        long respons = requests.singUpRequest(name, password);
                        if (respons == -1)
                            createWindowWithLabel("Проверте все поля на правильность");
                        else {
                            client.id = respons;
                            createWindowWithLabel(Long.toString(respons));
                            Stage stage = (Stage) singIn.getScene().getWindow();
                            stage.close();

                        }
                    }else {
                        createWindowWithLabel("Заполните все поля");
                    }

                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });


        Button logIn = new Button();
        logIn.setText("LogIn");
        logIn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    String name = nameField.getText();
                    String password = passwordField.getText();
                    if (!name.equals("") && !password.equals("")){
                        long respons = requests.logInRequest(name, password);
                        if (respons == -1)
                            createWindowWithLabel("Проверте все поля на правильность");
                        else {
                            client.id = respons;
                            createWindowWithLabel(Long.toString(respons));
                            Stage stage = (Stage) logIn.getScene().getWindow();
                            stage.close();
                        }
                    }else {
                        createWindowWithLabel("Заполните все поля");
                    }

                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });


        StackPane secondaryLayout = new StackPane();

        secondaryLayout.getChildren().add(singIn);
        StackPane.setAlignment(singIn, Pos.BOTTOM_LEFT);

        secondaryLayout.getChildren().add(logIn);
        StackPane.setAlignment(logIn, Pos.BOTTOM_RIGHT);

        secondaryLayout.getChildren().add(nameField);
        StackPane.setAlignment(nameField, Pos.TOP_CENTER);

        secondaryLayout.getChildren().add(passwordField);
        StackPane.setAlignment(passwordField, Pos.CENTER);


        Scene secondScene = new Scene(secondaryLayout, 300, 150);

        Stage newWindow = new Stage();
        newWindow.setTitle(fileName);
        newWindow.setScene(secondScene);

        newWindow.initModality(Modality.WINDOW_MODAL);
//служит для того чтобы окно не закрывалось кнопкой закрытия окна
        newWindow.setOnCloseRequest(Event::consume);

        newWindow.initOwner(primaryStage);

        newWindow.setX(primaryStage.getX()+150);
        newWindow.setY(primaryStage.getY()+100);

        newWindow.show();
    }


    public void createWindowWithLabel(String label){
        Label secondLabel = new Label(label);
        StackPane secondaryLayout = new StackPane();
        secondaryLayout.getChildren().add(secondLabel);
        Scene secondScene = new Scene(secondaryLayout, 230, 100);
        Stage newWindow = new Stage();
        newWindow.setTitle("INFO");
        newWindow.setScene(secondScene);
        newWindow.initModality(Modality.WINDOW_MODAL);
        newWindow.initOwner(primaryStage);
        newWindow.setX(primaryStage.getX() + 200);
        newWindow.setY(primaryStage.getY() + 100);
        newWindow.show();
    }


}

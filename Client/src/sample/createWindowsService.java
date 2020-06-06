package sample;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;


import java.io.IOException;

public class createWindowsService {
    Stage primaryStage;
    Requests requests;
    Client client;


    createWindowsService(Stage primaryStage, Requests requests, Client client){
        this.requests = requests;
        this.primaryStage = primaryStage;
        this.client = client;
    }

    public void createRegistrationWindow(String fileName){
        TextField nameField = new TextField();
        nameField.setPromptText("Введите ваш логин");

        TextField passwordField = new TextField();
        passwordField.setPromptText("Ввелите ваш пароль");

        Button singIn = new Button();
        singIn.setText("Регистрация");
        singIn.setOnAction(event -> {
            try {
                String name = nameField.getText();
                String password = passwordField.getText();
                if (!name.equals("") && !password.equals("")) {
                    long respons = requests.singUpRequest(name, password);

                    if (respons == -1)
                        createWindowWithLabel("Проверте все поля на правильность");
                    else {
                        client.id = respons;
                        client.loadDirectory();
                        createWindowWithLabel(Long.toString(respons));
                    }
                } else {
                    createWindowWithLabel("Заполните все поля");
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });

        Button logIn = new Button();
        logIn.setText("Войти");
        logIn.setOnAction(event -> {
            try {
                String name = nameField.getText();
                String password = passwordField.getText();
                if (!name.equals("") && !password.equals("")) {
                    final ProgressDialog progress = new ProgressDialog();
                    progress.showDialog();//запуск прогресс бара

                    long response = requests.logInRequest(name, password);
                    if (response == -1)
                        createWindowWithLabel("Проверте все поля на правильность");
                    else {
                        client.id = response;

                        client.initialiseLogin();

                        Stage stage = (Stage) logIn.getScene().getWindow();
                        stage.close();
                    }
                    progress.closeDialog();
                } else {
                    createWindowWithLabel("Заполните все поля");
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });


        Button help = new Button();
        help.setText("Помощь");
        help.setOnAction(event -> {
            String helpStr = "Для того чтобы зарегстрировать пользователя\n" +
                    "необходимо нажать на кнопку \"Зарегистрироваться\", \n" +
                    "после этого вам будет предложено выбрать папку,\n" +
                    "которую вы хотите синхронизировать.\n" +
                    "Для входи в аккаутн необходимо заполнить все поля \n" +
                    "и нажать на кнопку \"Войти\"";
            client.createWindowsService.createWindowWithLabel(helpStr);
        });

        StackPane secondaryLayout = new StackPane();

        secondaryLayout.getChildren().add(singIn);
        StackPane.setAlignment(singIn, Pos.BOTTOM_LEFT);

        secondaryLayout.getChildren().add(logIn);
        StackPane.setAlignment(logIn, Pos.BOTTOM_RIGHT);

        secondaryLayout.getChildren().add(help);
        StackPane.setAlignment(help, Pos.BOTTOM_CENTER);

        secondaryLayout.getChildren().add(nameField);
        StackPane.setAlignment(nameField, Pos.TOP_CENTER);

        secondaryLayout.getChildren().add(passwordField);
        StackPane.setAlignment(passwordField, Pos.CENTER);

        Scene secondScene = new Scene(secondaryLayout, 300, 150);

        Stage newWindow = new Stage();
        newWindow.setTitle(fileName);
        newWindow.setScene(secondScene);

        newWindow.initModality(Modality.WINDOW_MODAL);
        newWindow.setOnCloseRequest(event -> {
            if (client.isAlive)
                client.exitFromAcc();
            Platform.exit();
            System.exit(0);
        });

        newWindow.initOwner(primaryStage);

        newWindow.setX(primaryStage.getX()+100);
        newWindow.setY(primaryStage.getY()+100);

        newWindow.show();
    }


    public void createWindowWithLabel(String label){
        Label secondLabel = new Label(label);
        StackPane secondaryLayout = new StackPane();
        secondaryLayout.getChildren().add(secondLabel);

        int width = 230;
        int height = 100;
        if (label.length()>70){
            width = 400;
            height = 300;
        }


        Scene secondScene = new Scene(secondaryLayout, width, height);
        Stage newWindow = new Stage();
        newWindow.setTitle("INFO");
        newWindow.setScene(secondScene);
        newWindow.initModality(Modality.WINDOW_MODAL);
        newWindow.initOwner(primaryStage);
        newWindow.setX(primaryStage.getX() + 200);
        newWindow.setY(primaryStage.getY() + 100);
        newWindow.show();
    }


    public void createStyleChooser(Parent parent){
        Label secondLabel = new Label("Выберите цвет фона");
        Label treeLabel = new Label("Выберите цвет рабочей области");
        StackPane secondaryLayout = new StackPane();

        ColorPicker backgroundColor = new ColorPicker();
        backgroundColor.setValue(Color.WHEAT);
        backgroundColor.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                client.setBackgroundColor(backgroundColor.getValue());
            }
        });

        ColorPicker treeColor = new ColorPicker();
        treeColor.setValue(Color.WHEAT);
        treeColor.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                client.setTreeColor(treeColor.getValue());

            }
        });


        secondaryLayout.getChildren().add(secondLabel);
        StackPane.setAlignment(secondLabel, Pos.CENTER_LEFT);

        secondaryLayout.getChildren().add(backgroundColor);
        StackPane.setAlignment(backgroundColor, Pos.CENTER_RIGHT);

        secondaryLayout.getChildren().add(treeLabel);
        StackPane.setAlignment(treeLabel, Pos.BOTTOM_LEFT);

        secondaryLayout.getChildren().add(treeColor);
        StackPane.setAlignment(treeColor, Pos.BOTTOM_RIGHT);


        Scene secondScene = new Scene(secondaryLayout, 350, 100);
        Stage newWindow = new Stage();
        newWindow.setTitle("Изменение цвета");
        newWindow.setScene(secondScene);
        newWindow.initModality(Modality.WINDOW_MODAL);
        newWindow.initOwner(primaryStage);
        newWindow.setX(primaryStage.getX() + 200);
        newWindow.setY(primaryStage.getY() + 100);
        newWindow.show();
    }

    public void createAdditionalMenuForFile(String fileId, String fileName){

        Label secondLabel = new Label(fileName);

        Button downloadBtn = new Button();
        downloadBtn.setText("Загрузить файл");
        downloadBtn.setOnAction(event -> {
            client.downloadFiles(fileId, fileName);
            Stage stage = (Stage) downloadBtn.getScene().getWindow();
            stage.close();
        });

        Button infoBtn = new Button();
        infoBtn.setText("Информация о файле");
        infoBtn.setOnAction(event -> {
            client.getFileInfo(fileId, fileName);
        });

        StackPane secondaryLayout = new StackPane();

        secondaryLayout.getChildren().add(secondLabel);
        StackPane.setAlignment(secondLabel, Pos.TOP_CENTER);

        secondaryLayout.getChildren().add(downloadBtn);
        StackPane.setAlignment(downloadBtn, Pos.CENTER);

        secondaryLayout.getChildren().add(infoBtn);
        StackPane.setAlignment(infoBtn, Pos.BOTTOM_CENTER);

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

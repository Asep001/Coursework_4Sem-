package sample;

import javafx.beans.value.ChangeListener;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import java.io.*;
import java.net.MalformedURLException;


public class Controller {

    public Button exit;
    public TextField ipField;
    public TextField portField;
    public Button connectBtn;
    public Button refreshBtn;
    public ChoiceBox<String> delayChoice;
    public Button styleBtn;
    public Button helpBtn;

    private Client client;
    Stage primaryStage;

    final String SERVER_IP = "192.168.43.48";
    final String SERVER_PORT = "8001";

    public void setController(Client client, Stage primaryStage) throws MalformedURLException {
        this.client = client;
        this.primaryStage = primaryStage;
        exit.setDisable(true);
        refreshBtn.setDisable(true);
        styleBtn.setDisable(true);
        delayChoice.setDisable(true);
        helpBtn.setDisable(true);

        setImageToRefreshBtn("images/refresh.png");

        ipField.setText(SERVER_IP);
        portField.setText(SERVER_PORT);

        setChoiceBox();
    }

    private void setChoiceBox(){
        delayChoice.getItems().add("1 минута");
        delayChoice.getItems().add("2 минуты");
        delayChoice.getItems().add("5 минут");
        delayChoice.getItems().add("10 минут");
        delayChoice.getItems().add("30 минут");

        delayChoice.setValue("5 минут");

        ChangeListener<String> changeListener =
                (observableValue, s, t1) -> client.setTimerPeriod(Integer.parseInt(t1.split(" ")[0]) * 1000 * 60);
        delayChoice.getSelectionModel().selectedItemProperty().addListener(changeListener);
    }

    private void setImageToRefreshBtn(String pathToImage) throws MalformedURLException {
        File file = new File(pathToImage);
        String localUrl = file.toURI().toURL().toString();
        Image imageRefresh = new Image(localUrl);
        refreshBtn.graphicProperty().setValue(new ImageView(imageRefresh));
    }

    public void  ExitFormProfile(){
        client.exitFromAcc();
        client.openRegistration();

    }

    public void connectToTheServer(){
        String serverIp = ipField.getText();
        String serverPort = portField.getText();
        if (!serverIp.equals("") && !serverPort.equals(""))
            client.start(serverIp,serverPort);

        connectBtn.setDisable(true);
        exit.setDisable(false);
        refreshBtn.setDisable(false);
        styleBtn.setDisable(false);
        delayChoice.setDisable(false);
        helpBtn.setDisable(false);
    }

    public void refresh() throws IOException, InterruptedException {
        client.refreshFileTree();
    }

    public void changeStyle(){
        Parent parent = refreshBtn.getParent();
        client.createWindowsService.createStyleChooser(parent);
    }

    public void help(){
        String helpString = "Уважаемый пользователь, здравствуйте!\n" +
                "Вы перешли в меню помщи.\n" +
                "Для того  чтобы скачать файл или получить информацию о нём\n" +
                "нажмину дважды на необходимый вайм файл. После этого \n" +
                "на экране появится покошко с выбором действия\n" +
                "и выберете необходимое вам.\n" +
                "Для обновления необходимо нажать на кнопку со стрелочкой\n" +
                "справа вверху от области выбора файла.\n" +
                "Для выхода из учётной записи нажмите на кнопку \"Выход\".\n" +
                "Для изменения фора окна нажмите на кнопку \"Изменение цвета\"\n" +
                "и в появившемся меню выберите цвет.";

        client.createWindowsService.createWindowWithLabel(helpString);
    }


}


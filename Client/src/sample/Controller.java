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

    private Client client;
    Stage primaryStage;

    public void setController(Client client, Stage primaryStage) throws MalformedURLException {
        this.client = client;
        this.primaryStage = primaryStage;
        exit.setDisable(true);
        refreshBtn.setDisable(true);
        styleBtn.setDisable(true);
        delayChoice.setDisable(true);

        setImageToRefreshBtn("images/refresh.png");

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
    }

    public void refresh() throws IOException, InterruptedException {
        client.refreshFileTree();
    }

    public void changeStyle(){
        Parent parent = refreshBtn.getParent();
        client.createWindowsService.createStyleChooser(parent);
    }


}


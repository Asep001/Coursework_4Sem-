package sample;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class Controller {

    public Button exit;
    public TextField ipField;
    public TextField portField;
    public Button connectBtn;

    private Client client;

    public void setController(Client client ){
       this.client = client;

        exit.setDisable(true);
    }

    public void  ExitFormProfile(){
        client.openRegistration();
    }

    public void connectToTheServer(){
        String serverIp = ipField.getText();
        String serverPort = portField.getText();
        if (!serverIp.equals("") && !serverPort.equals(""))
            client.start(serverIp,serverPort);

        connectBtn.setDisable(true);
        exit.setDisable(false);
    }

}


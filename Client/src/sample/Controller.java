package sample;

import javafx.scene.control.Button;
import javafx.stage.Stage;

public class Controller {

    public Button exit;

    private Client client;

    public void setController(Client client ){
       this.client = client;
    }

    public void  ExitFormProfile(){
        client.openRegistration();
    }

}


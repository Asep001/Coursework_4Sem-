package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader();
        Parent root = loader.load(getClass().getResource("sample.fxml").openStream());
        primaryStage.setTitle("Cursach");
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.setResizable(false);
        primaryStage.show();
        Controller controller = loader.getController();
        Client client = new Client(primaryStage);
        controller.setController(client);
        client.start();

    }


    public static void main(String[] args) {
        launch(args);
    }
}

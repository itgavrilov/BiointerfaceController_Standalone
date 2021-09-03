package ru.gsa.biointerfaceController_standalone;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ru.gsa.biointerfaceController_standalone.controllers.ProxyGUI;

import java.io.IOException;
import java.util.ResourceBundle;

import static ru.gsa.biointerfaceController_standalone.controllers.BiointerfaceData.connection;


/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 07.11.2019.
 */
public class Main extends Application {

    private static void handle(javafx.stage.WindowEvent event) {
        if (connection != null)
            connection.disconnect();

        Platform.exit();
        System.exit(0);
    }

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage)  {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ProxyGUI.fxml"));

        try {
            stage.setScene(new Scene(fxmlLoader.load()));
            stage.show();
            stage.setOnCloseRequest(Main::handle);
            ProxyGUI proxyGUI = fxmlLoader.getController();
            proxyGUI.uploadContent(this.getClass());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

package ru.gsa.biointerface;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ru.gsa.biointerface.persistence.DAOException;
import ru.gsa.biointerface.persistence.DBHandler;
import ru.gsa.biointerface.ui.ProxyGUI;
import ru.gsa.biointerface.ui.UIException;
import ru.gsa.biointerface.ui.window.metering.MeteringController;

import java.io.IOException;
import java.net.URL;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 07.11.2019.
 */
public class Main extends Application implements ResourceSource {

    private static void handle(javafx.stage.WindowEvent event) {
        MeteringController.disconnect();
        try {
            DBHandler.getInstance().disconnect();
        } catch (DAOException e) {
            e.printStackTrace();
        }
        Platform.exit();
        System.exit(0);
    }

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ProxyGUI.fxml"));

        try {
            stage.setScene(new Scene(fxmlLoader.load()));
            stage.setOnCloseRequest(Main::handle);
            ProxyGUI proxyGUI = fxmlLoader.getController();
            proxyGUI.uploadContent(this);
        } catch (IOException | UIException e) {
            e.printStackTrace();
        }
    }

    @Override
    public URL getResource(String name) {
        return getClass().getResource(name);
    }
}

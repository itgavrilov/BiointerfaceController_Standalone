package ru.gsa.biointerfaceController_standalone;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import static ru.gsa.biointerfaceController_standalone.controllers.BiointerfaceController.connection;


/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 07.11.2019.
 */
public class Main extends Application {
    /**
     * @param event
     */
    private static void handle(javafx.stage.WindowEvent event) {
        if (connection != null) connection.disconnect();
        Platform.exit();
        System.exit(0);
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        launch();
    }

    /**
     * чтобы создать JavaFX приложения, достаточно реализовать метод start(Stage)
     *
     * @param stage
     * @throws Exception
     */
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxml/biointerfaceData.fxml"));
        // создаем сцену с заданными шириной и высотой и содержащую наш корневым контейнером, и связываем ее с окном
        Scene scene = new Scene(fxmlLoader.load(), 1000, 1000);
        stage.setScene(scene);
        stage.setMinHeight(480);
        stage.setMinWidth(640);
        stage.setTitle("Biointerface channel data"); // задаем заголовок окна
        stage.show(); // запускаем окно

        stage.setOnCloseRequest(Main::handle);
    }
}

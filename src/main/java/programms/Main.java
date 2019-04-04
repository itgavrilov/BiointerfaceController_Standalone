package programms;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import static controllers.BiointerfaceController.comPortServer;

public class Main extends Application {

    private static void handle(javafx.stage.WindowEvent event){
        try {
            if(comPortServer != null)comPortServer.stop();
            Platform.exit();
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // чтобы создать JavaFX приложения, достаточно реализовать метод start(Stage)
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/biointerfaceData.fxml"));
        // создаем сцену с заданными шириной и высотой и содержащую наш корневым контейнером, и связываем ее с окном
        Scene scene  = new Scene(root,1000,1000);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Biointerface channel data"); // задаем заголовок окна
        primaryStage.show(); // запускаем окно

        primaryStage.setOnCloseRequest(Main::handle);
    }

    // метод main в JavaFX приложениях не является обязательным
    public static void main(String[] args) {
        launch();
    }
}

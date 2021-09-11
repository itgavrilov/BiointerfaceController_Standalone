package ru.gsa.biointerfaceController_standalone.uiLayer;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import ru.gsa.biointerfaceController_standalone.uiLayer.window.WindowController;

public interface TransitionGUI extends Initializable {
    WindowController transition(FXMLLoader loader) throws UIException;

    void show();
}

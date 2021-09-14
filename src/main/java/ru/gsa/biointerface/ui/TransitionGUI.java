package ru.gsa.biointerface.ui;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import ru.gsa.biointerface.ui.window.Window;

public interface TransitionGUI extends Initializable {
    Window transition(FXMLLoader loader) throws UIException;

    void show();
}

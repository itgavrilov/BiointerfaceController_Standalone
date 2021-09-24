package ru.gsa.biointerface.ui;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import ru.gsa.biointerface.ui.window.Window;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public interface TransitionGUI extends Initializable {
    Window transition(FXMLLoader loader) throws UIException;

    void show();
}

package ru.gsa.biointerface.ui.window;

import javafx.fxml.Initializable;
import ru.gsa.biointerface.ResourceSource;
import ru.gsa.biointerface.ui.TransitionGUI;
import ru.gsa.biointerface.ui.UIException;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public interface Window extends Initializable {
    Window setResourceAndTransition(ResourceSource resourceSource, TransitionGUI transitionGUI);
    void showWindow() throws UIException;
    String getTitleWindow();
    void resizeWindow(double height, double width);
}

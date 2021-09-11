package ru.gsa.biointerfaceController_standalone.uiLayer.window;

import javafx.fxml.Initializable;
import ru.gsa.biointerfaceController_standalone.ResourceSource;
import ru.gsa.biointerfaceController_standalone.uiLayer.TransitionGUI;
import ru.gsa.biointerfaceController_standalone.uiLayer.UIException;


public interface WindowController extends Initializable {

    WindowController setResourceAndTransition(ResourceSource resourceSource, TransitionGUI transitionGUI);

    void showWindow() throws UIException;

    String getTitleWindow();

    void resizeWindow(double height, double width);
}

package ru.gsa.biointerfaceController_standalone.uiLayer.window;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import ru.gsa.biointerfaceController_standalone.ResourceSource;
import ru.gsa.biointerfaceController_standalone.uiLayer.TransitionGUI;
import ru.gsa.biointerfaceController_standalone.uiLayer.UIException;

import java.net.URL;
import java.util.ResourceBundle;

public abstract class AbstractWindow implements WindowController {
    protected ResourceSource resourceSource;
    protected TransitionGUI transitionGUI;

    @FXML
    protected AnchorPane anchorPaneRoot;

    public WindowController generateNewWindow(String resource) throws UIException {
        FXMLLoader loader = new FXMLLoader(resourceSource.getResource(resource));

        return transitionGUI.transition(loader)
                .setResourceAndTransition(resourceSource, transitionGUI);
    }

    @Override
    public WindowController setResourceAndTransition(ResourceSource resourceSource, TransitionGUI transitionGUI) {
        if (resourceSource == null)
            throw new NullPointerException("resourceSource is null");
        if (transitionGUI == null)
            throw new NullPointerException("transitionGUI is null");

        this.resourceSource = resourceSource;
        this.transitionGUI = transitionGUI;

        return this;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}

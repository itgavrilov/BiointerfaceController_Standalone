package ru.gsa.biointerface.ui.window;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import ru.gsa.biointerface.ResourceSource;
import ru.gsa.biointerface.ui.TransitionGUI;
import ru.gsa.biointerface.ui.UIException;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public abstract class AbstractWindow implements Window {
    protected ResourceSource resourceSource;
    protected TransitionGUI transitionGUI;

    @FXML
    protected AnchorPane anchorPaneRoot;

    public Window generateNewWindow(String resource) throws UIException {
        FXMLLoader loader = new FXMLLoader(resourceSource.getResource(resource));

        return transitionGUI.transition(loader)
                .setResourceAndTransition(resourceSource, transitionGUI);
    }

    @Override
    public Window setResourceAndTransition(ResourceSource resourceSource, TransitionGUI transitionGUI) {
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

package ru.gsa.biointerface.ui;

import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import ru.gsa.biointerface.ResourceSource;
import ru.gsa.biointerface.ui.window.Window;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static javafx.scene.layout.AnchorPane.*;

public class ProxyGUI implements TransitionGUI {
    private Stage stage;
    private ResourceSource resourceSource;
    private Window controller;

    @FXML
    private AnchorPane anchorPaneRoot;
    @FXML
    private MenuBar toolbar;
    @FXML
    private AnchorPane fieldForWindow;

    public void onICDs() {
        FXMLLoader loader = new FXMLLoader(resourceSource.getResource("Icds.fxml"));
        try {
            transition(loader)
                    .setResourceAndTransition(resourceSource, this)
                    .showWindow();
        } catch (UIException e) {
            e.printStackTrace();
        }
    }

    public void onDevices() {
        FXMLLoader loader = new FXMLLoader(resourceSource.getResource("Devices.fxml"));
        try {
            transition(loader)
                    .setResourceAndTransition(resourceSource, this)
                    .showWindow();
        } catch (UIException e) {
            e.printStackTrace();
        }
    }

    public void onExaminations() {
        FXMLLoader loader = new FXMLLoader(resourceSource.getResource("Examinations.fxml"));
        try {
            transition(loader)
                    .setResourceAndTransition(resourceSource, this)
                    .showWindow();
        } catch (UIException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        anchorPaneRoot.sceneProperty().addListener((observableScene, oldScene, newScene) -> {
            if (oldScene == null && newScene != null) {
                newScene.windowProperty().addListener((observableWindow, oldWindow, newWindow) -> {
                    if (oldWindow == null && newWindow != null) {
                        stage = (Stage) newWindow.getScene().getWindow();
                        stage.heightProperty().addListener(this::changedSizeWindow);
                        stage.widthProperty().addListener(this::changedSizeWindow);
                    }
                });
            }
        });
    }

    private void changedSizeWindow(ObservableValue<? extends Number> observable, Number oldHeight, Number newHeight) {
        resizeWindow(stage.getHeight() - 36, stage.getWidth() - 14);
    }

    public void uploadContent(ResourceSource resourceSource) throws UIException {
        if (resourceSource == null)
            throw new NullPointerException("mainClass is null");

        this.resourceSource = resourceSource;
        try {
            transition(new FXMLLoader(resourceSource.getResource("PatientRecords.fxml")))
                    .setResourceAndTransition(resourceSource, this)
                    .showWindow();
        } catch (UIException e) {
            e.printStackTrace();
            throw new UIException("uploadContent error", e);
        }
    }

    public void resizeWindow(double height, double width) {
        if (controller != null)
            controller.resizeWindow(height - toolbar.getHeight(), width);
    }

    @Override
    public Window transition(FXMLLoader loader) throws UIException {
        if (loader == null)
            throw new NullPointerException("content is null");
        if (stage == null)
            throw new NullPointerException("stage is null");

        stage.close();
        fieldForWindow.getChildren().clear();

        try {
            AnchorPane node = loader.load();
            controller = loader.getController();

            fieldForWindow.getChildren().add(node);
            setTopAnchor(node, 0.0);
            setBottomAnchor(node, 0.0);
            setLeftAnchor(node, 0.0);
            setRightAnchor(node, 0.0);

            stage.setTitle("BiointerfaceController(standalone)".concat(controller.getTitleWindow()));
            stage.setMinHeight(node.getMinHeight() + toolbar.getPrefHeight() + 36);
            stage.setHeight(node.getPrefHeight() + toolbar.getPrefHeight() + 36);
            stage.setMaxHeight(node.getMaxHeight() + toolbar.getPrefHeight() + 36);

            stage.setMinWidth(node.getMinWidth() + 14);
            stage.setWidth(node.getPrefWidth() + 14);
            stage.setMaxWidth(node.getMaxWidth() + 14);

            resizeWindow(node.getPrefHeight() + toolbar.getHeight(), node.getPrefWidth());
            return controller;
        } catch (IOException e) {
            e.printStackTrace();
            throw new UIException("node is null", e);
        }
    }

    @Override
    public void show() {
        stage.show();
    }
}

package ru.gsa.biointerfaceController_standalone.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static javafx.scene.layout.AnchorPane.*;

public class ProxyGUI implements Content {
    private Stage stage;
    private Class mainClass;
    private Content controller;

    @FXML
    private AnchorPane anchorPaneRoot;
    @FXML
    private MenuBar toolbar;
    @FXML
    private AnchorPane fieldForGUI;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        anchorPaneRoot.sceneProperty().addListener((observableScene, oldScene, newScene) -> {
            if (oldScene == null && newScene != null) {
                newScene.windowProperty().addListener((observableWindow, oldWindow, newWindow) -> {

                    if (oldWindow == null && newWindow != null) {
                        stage = (Stage) newWindow.getScene().getWindow();

                        stage.heightProperty().addListener((observable, oldHeight, newHeight) -> {
                            resizeWindow(stage.getHeight(), stage.getWidth());
                        });

                        stage.widthProperty().addListener((observable, oldWidth, newWidth) -> {
                            resizeWindow(stage.getHeight(), stage.getWidth());
                        });

                    }
                });
            }
        });
    }

    @Override
    public void uploadContent(Class mainClass){
        if(mainClass == null)
            throw new NullPointerException("mainClass is null");

        FXMLLoader fxmlLoader = new FXMLLoader(mainClass.getResource("BiointerfaceData.fxml"));

        this.mainClass = mainClass;

        try {
            loadContent(fxmlLoader);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadContent(FXMLLoader loader) throws IOException {
        if(loader == null)
            throw new NullPointerException("content is null");
        if(stage == null)
            throw new RuntimeException("stage is not load");

        AnchorPane node = null;
        try {
            node = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            throw new NullPointerException("node is null");
        }

        controller = loader.getController();
        controller.uploadContent(mainClass);

        stage.setTitle(getTitle() + controller.getTitle());

        fieldForGUI.getChildren().add(node);

        setTopAnchor(node, 0.0);
        setBottomAnchor(node, 0.0);
        setLeftAnchor(node, 0.0);
        setRightAnchor(node, 0.0);
    }

    public void resizeWindow(double height, double width){
        if(controller != null)
            controller.resizeWindow(height, width);
    }

    @Override
    public String getTitle() {
        return "BiointerfaceController(standalone)";
    }

}

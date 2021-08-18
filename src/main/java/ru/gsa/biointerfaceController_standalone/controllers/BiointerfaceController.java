package ru.gsa.biointerfaceController_standalone.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import ru.gsa.biointerfaceController_standalone.controllers.channel.ChannelGUI;
import ru.gsa.biointerfaceController_standalone.devace.Devise;
import ru.gsa.biointerfaceController_standalone.controllers.channel.CheckBoxOfChannelGUI;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 07.11.2019.
 */
public class BiointerfaceController implements Initializable {
    static public Devise devise;
    private final Set<ChannelGUI<Integer>> channelGUI = new TreeSet<>();
    private final Set<CheckBoxOfChannelGUI> checkBoxOfChannel = new TreeSet<>();

    @FXML
    private AnchorPane anchorPaneRoot;
    @FXML
    private AnchorPane anchorPaneMain;
    @FXML
    private AnchorPane anchorPaneChannel;
    @FXML
    private Button buttonScanningSerialPorts;
    @FXML
    private ComboBox<String> availableSerialPorts;
    @FXML
    private Button buttonComStart;
    @FXML
    private Button buttonReboot;
    @FXML
    private Slider allSliderZoom;
    @FXML
    private VBox channelVBox;
    @FXML
    private VBox checkBoxOfChannelVBox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        allSliderZoom.setMax(14);
        allSliderZoom.setValue(10);

        anchorPaneRoot.sceneProperty().addListener((observableScene, oldScene, newScene) -> {
            if (oldScene == null && newScene != null) {
                newScene.windowProperty().addListener((observableWindow, oldWindow, newWindow) -> {
                    if (oldWindow == null && newWindow != null) {

                        getStage().heightProperty().addListener((obs, oldVal, newVal) -> {
                            anchorPaneRoot.setPrefHeight((double) newVal - 34);
                            onZoomFinished();
                        });

                        getStage().widthProperty().addListener((obs, oldVal, newVal) -> {
                            anchorPaneRoot.setPrefWidth((double) newVal);
                            onZoomFinished();
                        });

                    }
                });
            }
        });
    }

    private Stage getStage() {
        return (Stage) anchorPaneRoot.getScene().getWindow();
    }

    public void buttonScanningSerialPortsPush() {
        if (devise != null && devise.isConected())
            devise.stop();
        clearInterface();
        Devise.scanningSerialPort();
        controlInterface(
                false,
                true,
                false,
                false
        );
    }

    public void comboBoxComShowing() {
        if (devise != null && devise.isConected())
            devise.stop();
        clearInterface();

        controlInterface(
                true,
                true,
                false,
                false
        );
        availableSerialPorts.getItems().addAll(Devise.getSerialPortNames());
    }

    public void comboBoxComSelect() {
        if (availableSerialPorts.getValue() != null && !"".equals(availableSerialPorts.getValue())) {
            devise = Devise.getInstance(availableSerialPorts.getValue());
            devise.buildingGUIChannels(channelGUI);
            devise.buildingCheckBoxOfChannelGUI(checkBoxOfChannel);

            checkBoxOfChannel.forEach(o -> {
                channelGUI.stream()
                        .filter(c -> c.getIndex() == o.getIndex())
                        .findFirst()
                        .orElseThrow(NoSuchElementException::new).setVisible(o.isSelected());

                o.setOnAction(event -> {
                    channelGUI.stream()
                            .filter(c -> c.getIndex() == o.getIndex())
                            .findFirst()
                            .orElseThrow(NoSuchElementException::new).setVisible(o.isSelected());

                    devise.setEnableChannel(o.getIndex(), o.isSelected());

                    channelVBox.getChildren().clear();
                    channelVBox.getChildren().addAll(
                            channelGUI.stream()
                                    .filter(Node::isVisible)
                                    .collect(Collectors.toSet())
                    );
                    onZoomFinishedChannelGUI();
                });
            });

            allSliderZoom.setOnMouseReleased(e -> devise.setCapacity((int) allSliderZoom.getValue()));
            channelVBox.getChildren().addAll(channelGUI.stream()
                    .filter(Node::isVisible)
                    .collect(Collectors.toSet())
            );
            checkBoxOfChannelVBox.getChildren().addAll(checkBoxOfChannel);
            onZoomFinished();
            if (devise.isConected()) {
                controlInterface(
                        true,
                        true,
                        true,
                        true
                );
                channelGUI.forEach(o -> o.setReady(true));
            }
        }
    }

    private void clearInterface(){
        channelVBox.getChildren().clear();
        checkBoxOfChannelVBox.getChildren().clear();
        availableSerialPorts.getItems().clear();
        availableSerialPorts.setValue("");
        buttonComStart.setText("Start");
    }

    public void buttonComStartPush() {
        if (devise.isConected()) {
            if (devise.isTransmission()) {
                devise.stopTransmission();
                controlInterface(
                        true,
                        true,
                        true,
                        true
                );
                buttonComStart.setText("Start");
            } else {
                devise.startTransmission();
                controlInterface(
                        false,
                        false,
                        true,
                        false);
                buttonComStart.setText("Stop");
            }
        }
    }

    public void buttonPushComReboot() {
        if (devise.isConected())
            devise.reboot();
        clearInterface();
        controlInterface(
                true,
                false,
                false,
                false);
    }

    private void controlInterface(boolean enableButtonScanning,
                                  boolean enableNumberOfCOM,
                                  boolean enableComStart,
                                  boolean enableSliderZoom) {

        buttonScanningSerialPorts.setDisable(!enableButtonScanning);
        availableSerialPorts.setDisable(!enableNumberOfCOM);
        buttonComStart.setDisable(!enableComStart);
        buttonReboot.setDisable(!enableComStart);
        allSliderZoom.setDisable(!enableSliderZoom);
        checkBoxOfChannel.forEach(o -> o.setDisable(!enableSliderZoom));
    }

    public void onZoomFinished() {
        anchorPaneMain.setPrefHeight(anchorPaneRoot.getPrefHeight() - 32);
        anchorPaneMain.setPrefWidth(anchorPaneRoot.getPrefWidth());

        anchorPaneChannel.setPrefHeight(anchorPaneMain.getPrefHeight());
        anchorPaneChannel.setPrefWidth(anchorPaneMain.getPrefWidth() - 120);

        channelVBox.setPrefHeight(anchorPaneChannel.getPrefHeight());
        channelVBox.setPrefWidth(anchorPaneChannel.getPrefWidth());

        onZoomFinishedChannelGUI();
    }

    private void onZoomFinishedChannelGUI() {
        channelGUI.forEach(o -> {
            o.setPrefHeight(channelVBox.getPrefHeight() / channelGUI.stream().filter(Node::isVisible).count());
            o.setPrefWidth(channelVBox.getPrefWidth());
            o.resizeWindow();
        });
    }
}


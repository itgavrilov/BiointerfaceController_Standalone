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
import ru.gsa.biointerfaceController_standalone.connection.Connection;
import ru.gsa.biointerfaceController_standalone.controllers.channel.ChannelGUI;
import ru.gsa.biointerfaceController_standalone.controllers.channel.CheckBoxOfChannelGUI;
import ru.gsa.biointerfaceController_standalone.connection.ConnectionFactory;

import java.net.URL;
import java.util.NoSuchElementException;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 07.11.2019.
 */
public class BiointerfaceController implements Initializable {
    static public Connection connection;
    private final Set<ChannelGUI<Integer>> channelGUIs = new TreeSet<>();
    private final Set<CheckBoxOfChannelGUI> checkBoxesOfChannel = new TreeSet<>();

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
        clearInterface();
        ConnectionFactory.scanningSerialPort();
        controlInterface(
                false,
                true,
                false,
                false
        );
    }

    public void comboBoxComShowing() {
        clearInterface();
        controlInterface(
                true,
                true,
                false,
                false
        );
        availableSerialPorts.getItems().addAll(ConnectionFactory.getSerialPortNames());
    }

    public void comboBoxComSelect() {
        if (availableSerialPorts.getValue() != null && !"".equals(availableSerialPorts.getValue())) {
            connection = ConnectionFactory.getInstance(availableSerialPorts.getValue());
            buildingChannelsGUI();
            buildingCheckBoxOfChannelGUI(checkBoxesOfChannel);

            checkBoxesOfChannel.forEach(o -> {
                channelGUIs.stream()
                        .filter(c -> c.getIndex() == o.getIndex())
                        .findFirst()
                        .orElseThrow(NoSuchElementException::new).setVisible(o.isSelected());

                o.setOnAction(event -> {
                    channelGUIs.stream()
                            .filter(c -> c.getIndex() == o.getIndex())
                            .findFirst()
                            .orElseThrow(NoSuchElementException::new).setVisible(o.isSelected());

                    connection.setEnableChannel(o.getIndex(), o.isSelected());

                    channelVBox.getChildren().clear();
                    channelVBox.getChildren().addAll(
                            channelGUIs.stream()
                                    .filter(Node::isVisible)
                                    .collect(Collectors.toSet())
                    );
                    onZoomFinishedChannelGUI();
                });
            });

            allSliderZoom.setOnMouseReleased(e -> connection.setCapacity((int) allSliderZoom.getValue()));
            channelVBox.getChildren().addAll(channelGUIs.stream()
                    .filter(Node::isVisible)
                    .collect(Collectors.toSet())
            );
            checkBoxOfChannelVBox.getChildren().addAll(checkBoxesOfChannel);
            onZoomFinished();

            if (connection.isConnected()) {
                controlInterface(
                        true,
                        true,
                        true,
                        true
                );
                channelGUIs.forEach(o -> o.setReady(true));
            }
        }
    }

    public void buildingChannelsGUI() {
        channelGUIs.clear();

        for (char i = 0; i < connection.getCountOfChannels(); i++) {
            channelGUIs.add(new ChannelGUI<>(i));
        }
        connection.setSamplesOfChannels(channelGUIs);
    }

    public void buildingCheckBoxOfChannelGUI(Set<CheckBoxOfChannelGUI> checkBoxOfChannel) {
        checkBoxOfChannel.clear();

        for (char i = 0; i < connection.getCountOfChannels(); i++) {
            checkBoxOfChannel.add(new CheckBoxOfChannelGUI(i, connection.isEnableChannel(i)));
        }
    }

    private void clearInterface() {
        channelVBox.getChildren().clear();
        checkBoxOfChannelVBox.getChildren().clear();
        availableSerialPorts.getItems().clear();
        availableSerialPorts.setValue("");
        buttonComStart.setText("Start");
    }

    public void buttonComStartPush() {
        if (connection.isConnected()) {
            if (connection.isTransmission()) {
                connection.stopTransmission();
                controlInterface(
                        true,
                        true,
                        true,
                        true
                );
                buttonComStart.setText("Start");
            } else {
                connection.startTransmission();
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
        if (connection.isConnected())
            connection.reboot();
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
        checkBoxesOfChannel.forEach(o -> o.setDisable(!enableSliderZoom));
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
        channelGUIs.forEach(o -> {
            o.setPrefHeight(channelVBox.getPrefHeight() / channelGUIs.stream().filter(Node::isVisible).count());
            o.setPrefWidth(channelVBox.getPrefWidth());
            o.resizeWindow();
        });
    }
}


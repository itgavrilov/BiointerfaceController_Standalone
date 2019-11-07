package controllers;

import channel.Channel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import jssc.SerialPortList;
import server.com_port.ComPacks;
import server.com_port.ComPortHandler;
import server.com_port.ComPortServer;
import servo.Servo;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class BiointerfaceController implements Initializable{
    @FXML
    private AnchorPane anchorPaneRoot;
    @FXML
    private AnchorPane anchorPaneMain;
    @FXML
    private AnchorPane anchorPaneControl;
    @FXML
    private AnchorPane anchorPaneChannel;
    @FXML
    private AnchorPane anchorPaneServo;

    @FXML
    private ComboBox<String> numberOfCOM;
    @FXML
    private Button buttonComStart;
    @FXML
    private Button buttonReboot;
    @FXML
    private Slider allSliderZoom;

    @FXML
    private CheckBox channel1checkBox;
    @FXML
    private CheckBox channel2checkBox;
    @FXML
    private CheckBox channel3checkBox;
    @FXML
    private CheckBox channel4checkBox;
    @FXML
    private CheckBox channel5checkBox;

    @FXML
    private VBox channelBox;

    @FXML
    private VBox servoBox;

    private final List<Channel> channel = new ArrayList<>();
    private final List<Servo> servo = new ArrayList<>();

    private boolean receiveFromCOM = false;

    static public ComPortServer comPortServer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        allSliderZoom.setMax(14);
        allSliderZoom.setValue(10);

        buildingСhannelGUIs(allSliderZoom);

        allSliderZoom.setOnMouseReleased(event ->{
            channel.forEach(o->o.setSliderZoomValue((int)allSliderZoom.getValue()));
        });

        anchorPaneMain.sceneProperty().addListener((observableScene, oldScene, newScene) -> {
            if (oldScene == null && newScene != null) {
                newScene.windowProperty().addListener((observableWindow, oldWindow, newWindow) -> {
                    if (oldWindow == null && newWindow != null) {

                        getStage().widthProperty().addListener((obs, oldVal, newVal) -> {
                            anchorPaneRoot.setPrefWidth((double)newVal);
                            onZoomFinished();
                        });

                        getStage().heightProperty().addListener((obs, oldVal, newVal) -> {
                            anchorPaneRoot.setPrefHeight((double)newVal);
                            onZoomFinished();
                        });
                    }
                });
            }
        });
    }


    private Stage getStage() {
        Stage stage = (Stage) anchorPaneMain.getScene().getWindow();
        return stage;
    }

    private void buildingСhannelGUIs(Slider sliderZoom){
        anchorPaneMain.setPrefHeight(anchorPaneRoot.getPrefHeight()-32);

        channel.add(new Channel(1, sliderZoom, channel1checkBox));
        channel.add(new Channel(2, sliderZoom, channel2checkBox));
        channel.add(new Channel(3, sliderZoom, channel3checkBox));
        channel.add(new Channel(4, sliderZoom, channel4checkBox));
        channel.add(new Channel(5, sliderZoom, channel5checkBox));

        channelBox.getChildren().addAll(channel);
        channelBox.setPrefHeight(anchorPaneMain.getPrefHeight());
        channel.forEach(o->o.setPrefHeight(anchorPaneMain.getPrefHeight()/channel.size()));

        servo.add(new Servo(1));
        servo.add(new Servo(2));
        servo.add(new Servo(3));
        servo.add(new Servo(4));

        servoBox.getChildren().addAll(servo);
        servoBox.setPrefHeight(anchorPaneMain.getPrefHeight());
        servo.forEach(o->o.setPrefHeight(anchorPaneMain.getPrefHeight()/servo.size()));
    }

    public void comboBoxComShown() throws Exception {
        controlInterface(
                true,
                false,
                false,
                false
        );
        numberOfCOM.getItems().clear();
        numberOfCOM.getItems().addAll(SerialPortList.getPortNames());
        if(comPortServer != null && !comPortServer.isStopped()){
            buttonPushComReboot();
        }
    }

    public void comboBoxComSelect() throws Exception {
        if(numberOfCOM.getValue() != "") {
            if(comPortServer == null || comPortServer.isStopped()){
                comPortServer = new ComPortServer(numberOfCOM.getValue());
                comPortServer.handler(new ComPortHandler(channel, servo));
                comPortServer.start();
                if (comPortServer.isStarting() || comPortServer.isRunning()) {
                    controlInterface(
                            true,
                            true,
                            true,
                            true
                    );
                    channel.forEach(o->o.setReady(true));
                }
            }
        } else {
            buttonPushComReboot();
        }
    }

    public void buttonComStartPush() {
        if(comPortServer.isRunning()){
            if (receiveFromCOM) {
                comPortServer.sendPackage(ComPacks.STOP_TRANSMISSION);
                receiveFromCOM = false;
                controlInterface(
                        true,
                        true,
                        true,
                        true
                );
                buttonComStart.setText("Start");
            } else {
                comPortServer.sendPackage(ComPacks.START_TRANSMISSION);
                receiveFromCOM = true;
                controlInterface(
                        false,
                        true,
                        true,
                        false);
                buttonComStart.setText("Stop");
            }
        }
    }

    public void buttonPushComReboot() throws Exception {
        if(comPortServer.isRunning()) {
            comPortServer.sendPackage(ComPacks.REBOOT);
            comPortServer.stop();
        }
        receiveFromCOM = false;
        controlInterface(
                true,
                false,
                false,
                false);
        buttonComStart.setText("Start");
        channel.forEach(o->o.setReady(false));
        numberOfCOM.setValue("");
    }

    private void controlInterface(boolean enableNumberOfCOM,
                                  boolean enableComStart,
                                  boolean enableReboot,
                                  boolean enableSliderZoom){
        numberOfCOM.setDisable(!enableNumberOfCOM);
        buttonComStart.setDisable(!enableComStart);
        buttonReboot.setDisable(!enableReboot);
        allSliderZoom.setDisable(!enableSliderZoom);
        channel.forEach(o->o.graphicsSliderZoom.setDisable(!enableSliderZoom));
    }

    public void onZoomFinished(){
        anchorPaneMain.setPrefHeight(anchorPaneRoot.getPrefHeight()-32);

        channelBox.setPrefHeight(anchorPaneMain.getPrefHeight());
        channel.forEach(o->o.setPrefHeight(anchorPaneMain.getPrefHeight()/channel.size()));

        servoBox.setPrefHeight(anchorPaneMain.getPrefHeight());
        servo.forEach(o->o.setPrefHeight(anchorPaneMain.getPrefHeight()/servo.size()));
    }
}


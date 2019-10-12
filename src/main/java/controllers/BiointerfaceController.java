package controllers;

import channel.ChannelGraphic;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;
import jssc.SerialPortList;
import server.com_port.ComPacks;
import server.com_port.ComPortHandler;
import server.com_port.ComPortServer;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class BiointerfaceController implements Initializable{

    @FXML
    private ComboBox<String> numberOfCOM;
    @FXML
    private Button buttonComOpen;
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
    private VBox waveformBox;

    private final List<ChannelGraphic> channelGraphics = new ArrayList<>();

    private boolean receiveFromCOM = false;
    //private MyUsbDevice myUsbDevice;

    static public ComPortServer comPortServer;


    private void buildingWaveform(){
        channelGraphics.add(new ChannelGraphic(1, channel1checkBox));
        channelGraphics.add(new ChannelGraphic(2, channel2checkBox));
        channelGraphics.add(new ChannelGraphic(3, channel3checkBox));
        channelGraphics.add(new ChannelGraphic(4, channel4checkBox));
        channelGraphics.add(new ChannelGraphic(5, channel5checkBox));

        for(ChannelGraphic o: channelGraphics)
            o.building();

        waveformBox.getChildren().addAll(channelGraphics);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        allSliderZoom.setValue(10);
        buildingWaveform();

        allSliderZoom.setOnMouseReleased(event -> {
            for (ChannelGraphic o : channelGraphics) {
                o.setSliderZoomValue((int)allSliderZoom.getValue());
            }
        });

//        try {
//            myUsbDevice = new MyUsbDevice();
//        } catch (UsbException e) {
//            e.printStackTrace();
//        }
    }

    public void comboBoxComShown(){
        numberOfCOM.getItems().remove(0, numberOfCOM.getItems().size());
        numberOfCOM.getItems().addAll(SerialPortList.getPortNames());
    }

    public void comboBoxComSelect() {
        if(numberOfCOM.getValue()!=null) {
            comPortServer = new ComPortServer(numberOfCOM.getValue());
            comPortServer.handler(new ComPortHandler(channelGraphics));
            controlInterface(true, true, false,false);
        }
    }

    public void buttonComOpenPush() throws Exception {
        if(comPortServer.isRunning()){
            comPortServer.stop();
            controlInterface(true, true, false,false);
            buttonComOpen.setText("Open");
            for(ChannelGraphic o: channelGraphics){
                o.setReady(false);
            }
        } else {
            comPortServer.start();
            if (comPortServer.isStarted()) {
                controlInterface(false, true, true,true);
                buttonComOpen.setText("Close");
                for(ChannelGraphic o: channelGraphics){
                    o.setReady(true);
                }
            }
        }

    }

    public void buttonComStartPush() {
        if(comPortServer.isRunning()){
            if (receiveFromCOM) {
                comPortServer.sendPackage(ComPacks.STOP_TRANSMISSION);
                receiveFromCOM = false;
                controlInterface(false, true, true, true);
                buttonComStart.setText("Start");
            } else {
                comPortServer.sendPackage(ComPacks.START_TRANSMISSION);
                receiveFromCOM = true;
                controlInterface(false, false, true,true);
                buttonComStart.setText("Stop");
            }
        }
    }

    public void buttonPushComReboot() throws Exception {
        if(comPortServer.isRunning()) {
            comPortServer.sendPackage(ComPacks.REBOOT);
        }
        numberOfCOM.getItems().remove(0, numberOfCOM.getItems().size());
        controlInterface(true, false, false,false);
        buttonComOpen.setText("Open");
        buttonComOpen.setText("Start");
        for(ChannelGraphic o: channelGraphics){
            o.setReady(false);
        }
        while(comPortServer.getSizeSendBuffer() != 0);
        comPortServer.stop();
    }

    private void controlInterface(boolean enableNumberOfCOM, boolean enableComOpen,  boolean enableComStart, boolean enableReboot){
        numberOfCOM.setDisable(!enableNumberOfCOM);
        buttonComOpen.setDisable(!enableComOpen);
        buttonComStart.setDisable(!enableComStart);
        buttonReboot.setDisable(!enableReboot);
    }
}


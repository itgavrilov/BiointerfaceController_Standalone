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

    public void comboBoxComSelect() throws Exception {
        if(numberOfCOM.getValue()!=null) {
            comPortServer = new ComPortServer(numberOfCOM.getValue());
            comPortServer.handler(new ComPortHandler(channelGraphics));

            if(comPortServer.isRunning()){
                comPortServer.sendPackage(ComPacks.STOPT_RANSMISSION);
                comPortServer.stop();
                numberOfCOM.getItems().remove(0, numberOfCOM.getItems().size());
            } else {
                controlInterface(false, false, true);
            }
        } else {
            controlInterface(false, true, true);
        }
    }

    public void buttonComOpenPush() throws Exception {
        if(comPortServer.isRunning()){
            comPortServer.sendPackage(ComPacks.STOPT_RANSMISSION);
            comPortServer.stop();
            controlInterface(false, false, true);
            buttonComOpen.setText("Start");
        } else {
            comPortServer.start();

            if (comPortServer.isStarted()) {
                controlInterface(true, false, false);
                buttonComOpen.setText("Stop");
                comPortServer.sendPackage(ComPacks.START_TRANSMISSION);
            } else numberOfCOM.getItems().remove(0, numberOfCOM.getItems().size());
        }
    }

    public void buttonPushComReboot(){
        if(comPortServer.isRunning()) {
            comPortServer.sendPackage(ComPacks.REBOOT);
        }
        numberOfCOM.getItems().remove(0, numberOfCOM.getItems().size());
        controlInterface(false, true, true);
        buttonComOpen.setText("Start");
    }

    private void controlInterface(boolean disableNumberOfCOM, boolean comOpen,  boolean disableControlReboot){
        numberOfCOM.setDisable(disableNumberOfCOM);
        buttonComOpen.setDisable(comOpen);
        buttonReboot.setDisable(disableControlReboot);
    }
}


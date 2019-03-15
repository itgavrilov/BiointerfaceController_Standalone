package controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;
import jssc.SerialPortList;
import programms.ChannelChart;
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

    private List<ChannelChart> channelCharts = new ArrayList<ChannelChart>();

    static public ComPortServer comPortServer;

    private void buildingWaveform(){
        channelCharts.add(new ChannelChart(1, channel1checkBox));
        channelCharts.add(new ChannelChart(2, channel2checkBox));
        channelCharts.add(new ChannelChart(3, channel3checkBox));
        channelCharts.add(new ChannelChart(4, channel4checkBox));
        channelCharts.add(new ChannelChart(5, channel5checkBox));

        for(ChannelChart o: channelCharts)
            o.building();

        waveformBox.getChildren().addAll(channelCharts);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        allSliderZoom.setValue(10);
        buildingWaveform();

        allSliderZoom.setOnMouseReleased(event -> {
            for (ChannelChart o : channelCharts) {
                o.setSliderZoomValue((int)allSliderZoom.getValue());
            }
        });
    }

    public void comboBoxComShown(){
        numberOfCOM.getItems().remove(0, numberOfCOM.getItems().size());
        numberOfCOM.getItems().addAll(SerialPortList.getPortNames());
    }

    public void comboBoxComSelect() throws Exception {
        if(numberOfCOM.getValue()!=null) {
            comPortServer = new ComPortServer(numberOfCOM.getValue());
            comPortServer.handler(new ComPortHandler(channelCharts));

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
        controlInterface(false, false, true);
        buttonComOpen.setText("Start");
    }

    private void controlInterface(boolean disableNumberOfCOM, boolean comOpen,  boolean disableControlReboot){
        numberOfCOM.setDisable(disableNumberOfCOM);
        buttonComOpen.setDisable(comOpen);
        buttonReboot.setDisable(disableControlReboot);
    }
}


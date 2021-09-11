package ru.gsa.biointerfaceController_standalone.uiLayer.window;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import ru.gsa.biointerfaceController_standalone.businessLayer.Examination;
import ru.gsa.biointerfaceController_standalone.businessLayer.PatientRecord;
import ru.gsa.biointerfaceController_standalone.businessLayer.serialPortConnection.Connection;
import ru.gsa.biointerfaceController_standalone.businessLayer.serialPortConnection.ConnectionFactory;
import ru.gsa.biointerfaceController_standalone.uiLayer.UIException;
import ru.gsa.biointerfaceController_standalone.uiLayer.channel.Channel;
import ru.gsa.biointerfaceController_standalone.uiLayer.channel.CheckBoxOfChannel;
import ru.gsa.biointerfaceController_standalone.uiLayer.channel.CompositeNode;

import java.net.URL;
import java.util.NoSuchElementException;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 07.11.2019.
 */
public class BiointerfaceData extends AbstractWindow implements WindowControllerWithProperty<Examination>{
    static public Connection connection;
    private final Set<CompositeNode<AnchorPane, Channel>> channelGUIs = new TreeSet<>();
    private final Set<CheckBoxOfChannel> checkBoxesOfChannel = new TreeSet<>();
    Examination examination;
    @FXML
    private AnchorPane anchorPaneControl;
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

    public WindowControllerWithProperty<Examination> setProperty(Examination examination) {
        if (examination == null)
            throw new NullPointerException("examination is null");

        this.examination = examination;

        return this;
    }

    @Override
    public void showWindow() throws UIException {
        if(resourceSource == null || transitionGUI == null)
            throw new UIException("resourceSource or transitionGUI is null. First call setResourceAndTransition()");
        if (examination == null)
            throw new UIException("examination is null. First call setParameter()");

        allSliderZoom.setMin(7);
        allSliderZoom.setMax(14);
        allSliderZoom.setValue(10);

        transitionGUI.show();
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

            buildingChannelsGUIs();
            buildingCheckBoxesOfChannelGUIs();

            checkBoxesOfChannel.forEach(o -> o.setOnAction(event -> {
                channelGUIs.stream()
                        .filter(c -> c.getController().getId() == o.getIndex())
                        .findFirst()
                        .orElseThrow(NoSuchElementException::new).getNode().setVisible(o.isSelected());

                drawChannelsGUI();
            }));

            checkBoxOfChannelVBox.getChildren().addAll(checkBoxesOfChannel);

            if (connection.isConnected()) {
                controlInterface(
                        true,
                        true,
                        true,
                        true
                );
                channelGUIs.forEach(o -> o.getController().setReady(true));
            }
        }
    }

    public void buildingChannelsGUIs() {
        channelGUIs.clear();

        for (char i = 0; i < connection.getCountOfChannels(); i++) {
            CompositeNode<AnchorPane, Channel> node =
                    new CompositeNode<>(new FXMLLoader(resourceSource.getResource("Channel.fxml")));

            node.getController().setId(i);
            channelGUIs.add(node);
        }
        connection.setSamplesOfChannels(
                channelGUIs.stream()
                        .map(CompositeNode::getController)
                        .collect(Collectors.toSet())
        );
        allSliderZoom.setOnMouseReleased(e -> connection.setCapacity((int) allSliderZoom.getValue()));

        drawChannelsGUI();
    }

    public void buildingCheckBoxesOfChannelGUIs() {
        checkBoxesOfChannel.clear();

        for (char i = 0; i < connection.getCountOfChannels(); i++) {
            checkBoxesOfChannel.add(new CheckBoxOfChannel(i));
        }
    }

    public void drawChannelsGUI() {
        channelVBox.getChildren().clear();
        connection.setCapacity((int) allSliderZoom.getValue());
        channelGUIs.forEach(n -> {
            if (n.getNode().isVisible())
                channelVBox.getChildren().add(n.getNode());
        });
        resizeWindow(anchorPaneRoot.getHeight(), anchorPaneRoot.getWidth());
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

    @Override
    public void resizeWindow(double height, double width) {
        long count = channelGUIs.stream().
                map(CompositeNode::getNode).
                filter(Node::isVisible).count();

        double heightChannelGUIs = height / count;

        channelVBox.setPrefHeight(heightChannelGUIs);
        channelVBox.setPrefWidth(width - anchorPaneControl.getWidth());

        for (CompositeNode<AnchorPane, Channel> o : channelGUIs) {
            o.getController().resizeWindow(heightChannelGUIs, width - anchorPaneControl.getWidth() + 13);
        }

    }

    @Override
    public String getTitleWindow() {
        return ": biointerface data";
    }


    public void onBack() {
        try {
            ((WindowControllerWithProperty<PatientRecord>) generateNewWindow("PatientRecordOpen.fxml"))
                    .setProperty(examination.getPatientRecord())
                    .showWindow();
        } catch (UIException e) {
            e.printStackTrace();
        }
    }
}


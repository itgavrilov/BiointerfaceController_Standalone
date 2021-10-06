package ru.gsa.biointerface.ui.window.metering;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.StringConverter;
import ru.gsa.biointerface.domain.*;
import ru.gsa.biointerface.ui.UIException;
import ru.gsa.biointerface.ui.window.AbstractWindow;
import ru.gsa.biointerface.ui.window.WindowWithProperty;
import ru.gsa.biointerface.ui.window.graph.CheckBoxOfGraph;
import ru.gsa.biointerface.ui.window.graph.CompositeNode;

import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 07.11.2019.
 */
public class MeteringController extends AbstractWindow implements WindowWithProperty<PatientRecord> {
    static private MeteringController instants;
    private final List<CompositeNode<AnchorPane, GraphForMeteringController>> channelGUIs = new LinkedList<>();
    private final List<CheckBoxOfGraph> checkBoxesOfChannel = new LinkedList<>();
    private final StringConverter<Device> converter = new StringConverter<>() {
        @Override
        public String toString(Device device) {
            String str = "";
            if (device != null)
                str = String.valueOf(device.getId());
            return str;
        }

        @Override
        public Device fromString(String string) {
            return null;
        }
    };
    private Connection connection;
    private PatientRecord patientRecord;
    @FXML
    private AnchorPane anchorPaneControl;
    @FXML
    private Text patientRecordIdText;
    @FXML
    private Text secondNameText;
    @FXML
    private Text firstNameText;
    @FXML
    private Text middleNameText;
    @FXML
    private Text birthdayText;
    @FXML
    private Text icdText;
    @FXML
    private TextArea commentField;
    @FXML
    private Button scanningSerialPortsButton;
    @FXML
    private ComboBox<Device> availableDevicesComboBox;
    @FXML
    private Button startButton;
    @FXML
    private Button rebootButton;
    @FXML
    private Slider allSliderZoom;
    @FXML
    private VBox channelVBox;
    @FXML
    private VBox checkBoxOfChannelVBox;
    @FXML
    private Button recordingButton;

    static public void disconnect() {
        if (instants != null && instants.connection != null) {
            ConnectionFactory.disconnectScanningSerialPort();
            instants.connection.disconnect();

            if (instants.connection.isControllerTransmission()) {
                try {
                    instants.connection.controllerTransmissionStop();
                } catch (DomainException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public WindowWithProperty<PatientRecord> setProperty(PatientRecord patientRecord) {
        if (patientRecord == null)
            throw new NullPointerException("examination is null");

        this.patientRecord = patientRecord;

        return this;
    }

    @Override
    public void showWindow() throws UIException {
        if (resourceSource == null || transitionGUI == null)
            throw new UIException("resourceSource or transitionGUI is null. First call setResourceAndTransition()");
        if (patientRecord == null)
            throw new UIException("patientRecord is null. First call setParameter()");

        patientRecordIdText.setText(String.valueOf(patientRecord.getId()));
        secondNameText.setText(patientRecord.getSecondName());
        firstNameText.setText(patientRecord.getFirstName());
        middleNameText.setText(patientRecord.getMiddleName());
        if (patientRecord.getIcd() != null)
            icdText.setText(patientRecord.getIcd().toString());

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        birthdayText.setText(patientRecord.getBirthday().format(dateFormatter));

        availableDevicesComboBox.setConverter(converter);

        transitionGUI.show();
    }

    public void buttonScanningSerialPortsPush() {
        clearInterface();
        ConnectionFactory.scanningSerialPort(patientRecord);
        controlInterface(
                true,
                true,
                false,
                false,
                false
        );
    }

    public void availableDevicesComboBoxShowing() {
        clearInterface();
        controlInterface(
                true,
                true,
                false,
                false,
                false
        );
        availableDevicesComboBox.getItems().addAll(ConnectionFactory.getListDevices());
    }

    public void availableDevicesComboBoxSelect() {
        if (availableDevicesComboBox.getValue() != null) {
            connection = ConnectionFactory.getInstance(availableDevicesComboBox.getValue());

            buildingChannelsGUIs();

            if (connection.isConnected()) {
                controlInterface(
                        true,
                        true,
                        true,
                        true,
                        false
                );
            }
        }
    }

    public void buildingChannelsGUIs() {
        int capacity = (int) allSliderZoom.getValue();

        channelGUIs.clear();
        checkBoxesOfChannel.clear();

        for (int i = 0; i < connection.getDevice().getAmountChannels(); i++) {
            CompositeNode<AnchorPane, GraphForMeteringController> node =
                    new CompositeNode<>(new FXMLLoader(resourceSource.getResource("GraphForMetering.fxml")));
            node.getController().setGraph(connection.getGraphs().get(i));
            try {
                node.getController().setCapacity(capacity);
            } catch (DomainException e) {
                e.printStackTrace();
            }
            channelGUIs.add(node);

            CheckBoxOfGraph checkBox = new CheckBoxOfGraph(i);
            checkBox.setOnAction(event -> {
                node.getNode().setVisible(checkBox.isSelected());
                drawChannelsGUI();
            });
            try {
                node.getController().setCheckBox(checkBox);
            } catch (DomainException e) {
                e.printStackTrace();
            }
            checkBoxesOfChannel.add(checkBox);
        }

        allSliderZoom.valueProperty().addListener((ov, old_val, new_val) -> channelGUIs.forEach(o -> {
            try {
                o.getController().setCapacity(new_val.intValue());
            } catch (DomainException ex) {
                ex.printStackTrace();
            }
        }));

        drawChannelsGUI();
    }

    public void drawChannelsGUI() {
        channelGUIs.forEach(n -> {
            if (n.getNode().isVisible())
                channelVBox.getChildren().add(n.getNode());
        });
        checkBoxOfChannelVBox.getChildren().addAll(checkBoxesOfChannel);
        resizeWindow(anchorPaneRoot.getHeight(), anchorPaneRoot.getWidth());
    }

    private void clearInterface() {
        channelVBox.getChildren().clear();
        checkBoxOfChannelVBox.getChildren().clear();
        availableDevicesComboBox.getItems().clear();
        availableDevicesComboBox.setValue(null);
        startButton.setText("Start");
        recordingButton.setText("recording");
    }

    public void onStartButtonPush() {
        if (connection.isConnected()) {
            if (connection.isControllerTransmission()) {
                try {
                    connection.controllerTransmissionStop();
                } catch (DomainException e) {
                    e.printStackTrace();
                }
                controlInterface(
                        true,
                        true,
                        true,
                        true,
                        false
                );
                startButton.setText("Start");
            } else {
                try {
                    connection.controllerTransmissionStart();
                } catch (DomainException e) {
                    e.printStackTrace();
                }
                controlInterface(
                        false,
                        false,
                        true,
                        false,
                        true
                );
                startButton.setText("Stop");
            }
        }
        recordingButton.setText("recording");
    }

    public void onRebootButtonPush() {
        if (connection.isConnected())
            connection.controllerReboot();
        clearInterface();
        controlInterface(
                true,
                false,
                false,
                false,
                false);
    }

    public void onRecordingButtonPush() {
        if (connection.isRecording()) {
            connection.recordingStop();
            recordingButton.setText("recording");
        } else {
            connection.recordingStart(commentField.getText());
            recordingButton.setText("stop\nrecording");
        }
    }

    public void onBackButtonPush() {
        if (connection.isControllerTransmission()) {
            try {
                connection.controllerTransmissionStop();
            } catch (DomainException e) {
                e.printStackTrace();
            }
        }

        try {
            ((WindowWithProperty<PatientRecord>) generateNewWindow("PatientRecordOpen.fxml"))
                    .setProperty(patientRecord)
                    .showWindow();
        } catch (UIException e) {
            e.printStackTrace();
        }
    }

    public void commentFieldChange() {
        connection.changeCommentOnExamination(commentField.getText());
    }

    private void controlInterface(boolean enableButtonScanning,
                                  boolean enableNumberOfCOM,
                                  boolean enableComStart,
                                  boolean enableSliderZoom,
                                  boolean enableButtonRecording) {

        scanningSerialPortsButton.setDisable(!enableButtonScanning);
        availableDevicesComboBox.setDisable(!enableNumberOfCOM);
        startButton.setDisable(!enableComStart);
        rebootButton.setDisable(!enableComStart);
        allSliderZoom.setDisable(!enableSliderZoom);
        channelGUIs.forEach(o -> o.getController().setEnable(enableSliderZoom));
        checkBoxesOfChannel.forEach(o -> o.setDisable(!enableSliderZoom));
        recordingButton.setDisable(!enableButtonRecording);
        if (!enableButtonRecording) {
            if (connection != null)
                connection.recordingStop();
        }
    }

    @Override
    public void resizeWindow(double height, double width) {
        long count = channelGUIs.stream().
                map(CompositeNode::getNode).
                filter(Node::isVisible).count();

        double heightChannelGUIs = height / count;

        channelVBox.setPrefHeight(heightChannelGUIs);
        channelVBox.setPrefWidth(width - anchorPaneControl.getWidth());

        for (CompositeNode<AnchorPane, GraphForMeteringController> o : channelGUIs) {
            o.getController().resizeWindow(heightChannelGUIs, width - anchorPaneControl.getWidth() + 13);
        }

    }

    @Override
    public String getTitleWindow() {
        return ": new examination";
    }
}


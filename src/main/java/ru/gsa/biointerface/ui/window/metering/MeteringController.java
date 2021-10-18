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
    private final ConnectionFactory connectionFactory = ConnectionFactory.getInstance();
    private Connection connection;
    private PatientRecord patientRecord;
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
            try {
                ConnectionFactory.disconnectScanningSerialPort();
                instants.connection.disconnect();

                if (instants.connection.isTransmission()) {
                        instants.connection.transmissionStop();
                }
            } catch (DomainException e) {
                e.printStackTrace();
            }
        }
    }

    public WindowWithProperty<PatientRecord> setProperty(PatientRecord patientRecord) {
        if (patientRecord == null)
            throw new NullPointerException("patientRecord is null");

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
        if (patientRecord.getIcd() != null) {
            Icd icd = patientRecord.getIcd();
            icdText.setText(icd.getICD() + " (ICD-" + icd.getVersion() + ")");
        } else {
            icdText.setText("-");
        }

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        birthdayText.setText(patientRecord.getBirthday().format(dateFormatter));

        availableDevicesComboBox.setConverter(converter);

        transitionGUI.show();
    }

    public void buttonScanningSerialPortsPush() {
        clearInterface();
        try {
            connectionFactory.scanningSerialPort();
            controlInterface(
                    true,
                    true,
                    false,
                    false,
                    false
            );
        } catch (DomainException e) {
            e.printStackTrace();
        }
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
        availableDevicesComboBox.getItems().addAll(connectionFactory.getListDevices());
    }

    public void availableDevicesComboBoxSelect() {
        if (availableDevicesComboBox.getValue() != null) {
            try {
                connection = connectionFactory.getConnection(availableDevicesComboBox.getValue());
                connection.setPatientRecord(patientRecord);
                buildingChannelsGUIs();
                if (connection.isConnected()) {
                    controlInterface(
                            true,
                            false,
                            true,
                            true,
                            false
                    );
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void buildingChannelsGUIs() throws UIException {
        int capacity = (int) allSliderZoom.getValue();

        channelGUIs.clear();
        checkBoxesOfChannel.clear();

        for (int i = 0; i < connection.getDevice().getAmountChannels(); i++) {
            CompositeNode<AnchorPane, GraphForMeteringController> node =
                    new CompositeNode<>(new FXMLLoader(resourceSource.getResource("fxml/GraphForMetering.fxml")));
            try {
                node.getController().setGraph(connection.getGraph(i));
                node.getController().setCapacity(capacity);
                connection.getCash(i).addListener(node.getController());
                channelGUIs.add(node);

                CheckBoxOfGraph checkBox = new CheckBoxOfGraph(i);
                checkBox.setOnAction(event -> {
                    node.getNode().setVisible(checkBox.isSelected());
                    drawChannelsGUI();
                });
                try {
                    node.getController().setCheckBox(checkBox);
                    checkBoxesOfChannel.add(checkBox);
                } catch (DomainException e) {
                    throw new UIException("Graph checkbox setting error");
                }
            } catch (DomainException e) {
                throw new UIException("Graph setting error");
            }
        }

        allSliderZoom.valueProperty().addListener((ov, old_val, new_val) -> channelGUIs.forEach(o -> {
            try {
                o.getController().setCapacity(new_val.intValue());
            } catch (DomainException e) {
                e.printStackTrace();
            }
        }));

        drawChannelsGUI();
    }

    public void drawChannelsGUI() {
        channelVBox.getChildren().clear();
        channelGUIs.forEach(n -> {
            if (n.getNode().isVisible())
                channelVBox.getChildren().add(n.getNode());
        });

        checkBoxOfChannelVBox.getChildren().clear();
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
            if (connection.isTransmission()) {
                try {
                    connection.transmissionStop();
                    controlInterface(
                            true,
                            false,
                            true,
                            true,
                            false
                    );
                    startButton.setText("Start");
                } catch (DomainException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    connection.transmissionStart();
                    controlInterface(
                            false,
                            false,
                            true,
                            false,
                            true
                    );
                    startButton.setText("Stop");
                } catch (DomainException e) {
                    e.printStackTrace();
                }
            }
        }
        recordingButton.setText("recording");
    }

    public void onRebootButtonPush() {
        clearInterface();
        controlInterface(
                true,
                false,
                false,
                false,
                false);
        if (connection.isConnected()) {
            try {
                connection.controllerReboot();
            } catch (DomainException e) {
                e.printStackTrace();
            }
        }
    }

    public void onRecordingButtonPush() {
        if (connection.isRecording()) {
            try {
                connection.recordingStop();
                recordingButton.setText("recording");
            } catch (DomainException e) {
                e.printStackTrace();
            }
        } else {
            try {
                connection.recordingStart();
                recordingButton.setText("stop\nrecording");
            } catch (DomainException e) {
                e.printStackTrace();
            }
        }
    }

    public void onBackButtonPush() {
        try {
            if (connection != null && connection.isTransmission()) {
                connection.transmissionStop();
                connection.disconnect();
            }
            ((WindowWithProperty<PatientRecord>) generateNewWindow("fxml/PatientRecordOpen.fxml"))
                    .setProperty(patientRecord)
                    .showWindow();
        } catch (DomainException | UIException e) {
            e.printStackTrace();
        }
    }

    public void commentFieldChange() throws UIException {
        try {
            connection.changeCommentOnExamination(commentField.getText());
        } catch (DomainException e) {
            throw new UIException("Error updating comment to examination");
        }
    }

    private void controlInterface(boolean enableButtonScanning,
                                  boolean enableDevicesComboBox,
                                  boolean enableComStart,
                                  boolean enableSliderZoom,
                                  boolean enableButtonRecording) {

        scanningSerialPortsButton.setDisable(!enableButtonScanning);
        availableDevicesComboBox.setDisable(!enableDevicesComboBox);
        startButton.setDisable(!enableComStart);
        rebootButton.setDisable(!enableComStart);
        allSliderZoom.setDisable(!enableSliderZoom);
        channelGUIs.forEach(o -> o.getController().setEnable(enableSliderZoom));
        checkBoxesOfChannel.forEach(o -> o.setDisable(!enableSliderZoom));
        recordingButton.setDisable(!enableButtonRecording);
        if (!enableButtonRecording) {
            if (connection != null && connection.isRecording()) {
                try {
                    connection.recordingStop();
                } catch (DomainException e) {
                    e.printStackTrace();
                }
            }
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


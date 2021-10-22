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
import ru.gsa.biointerface.domain.entity.Device;
import ru.gsa.biointerface.domain.entity.Icd;
import ru.gsa.biointerface.domain.entity.PatientRecord;
import ru.gsa.biointerface.host.ConnectionFactory;
import ru.gsa.biointerface.host.HostException;
import ru.gsa.biointerface.services.*;
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
    private Connection connection;
    private PatientRecord patientRecord;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private final ConnectionFactory connectionFactory = ConnectionFactory.getInstance();
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
    private ComboBox<Device> deviceComboBox;
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
            } catch (HostException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
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
            throw new UIException("servicePatientRecord is null. First call setParameter()");

        patientRecordIdText.setText(String.valueOf(patientRecord.getId()));
        secondNameText.setText(patientRecord.getSecondName());
        firstNameText.setText(patientRecord.getFirstName());
        middleNameText.setText(patientRecord.getMiddleName());
        birthdayText.setText(patientRecord.getBirthdayInLocalDate().format(dateFormatter));
        deviceComboBox.setConverter(converter);

        if (patientRecord.getIcd() != null) {
            Icd icd = patientRecord.getIcd();
            icdText.setText(icd.getName() + " (ICD-" + icd.getVersion() + ")");
        } else {
            icdText.setText("-");
        }

        instants = this;
        transitionGUI.show();
    }

    public void buttonScanningSerialPortsPush() {
        try {
            connection = null;
            clearInterface();
            controlInterface(false);
            connectionFactory.scanningSerialPort();
        } catch (HostException e) {
            e.printStackTrace();
        }
    }

    public void devicesComboBoxShowing() {
        controlInterface(true);
        deviceComboBox.getItems().clear();
        deviceComboBox.getItems().addAll(connectionFactory.getDevices());
    }

    public void devicesComboBoxSelect() {
        if (deviceComboBox.getValue() != null) {
            try {
                connection = connectionFactory.getConnection(deviceComboBox.getValue());
                connection.setPatientRecord(patientRecord);
                buildingChannelsGUIs();
                if (connection.isConnected()) {
                    controlInterface(true);
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

        for (int i = 0; i < connection.getAmountChannels(); i++) {
            CompositeNode<AnchorPane, GraphForMeteringController> node =
                    new CompositeNode<>(
                            new FXMLLoader(
                                    resourceSource.getResource("fxml/GraphForMetering.fxml"
                                    )
                            )
                    );
            GraphForMeteringController graphController = node.getController();

            try {
                graphController.setNumberOfChannel(i);
                graphController.setConnection(connection);
                graphController.setCapacity(capacity);
                connection.addListenerInCash(i, graphController);
                channelGUIs.add(node);

                CheckBoxOfGraph checkBox = new CheckBoxOfGraph(i);
                checkBox.setOnAction(event -> {
                    node.getNode().setVisible(checkBox.isSelected());
                    drawChannelsGUI();
                });
                try {
                    graphController.setCheckBox(checkBox);
                    checkBoxesOfChannel.add(checkBox);
                } catch (ServiceException e) {
                    throw new UIException("Graph checkbox setting error");
                }
            } catch (ServiceException | HostException e) {
                throw new UIException("Graph setting error");
            }
        }

        allSliderZoom.valueProperty().addListener((ov, old_val, new_val) -> channelGUIs.forEach(o -> {
            try {
                o.getController().setCapacity(new_val.intValue());
            } catch (ServiceException e) {
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

    public void onStartButtonPush() {
        if (connection.isConnected()) {
            if (connection.isTransmission()) {
                try {
                    connection.transmissionStop();
                    controlInterface(true);
                } catch (HostException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    connection.transmissionStart();
                    controlInterface(false);
                } catch (HostException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void onRebootButtonPush() {
        clearInterface();
        controlInterface(true);
        if (connection.isConnected()) {
            try {
                connection.controllerReboot();
            } catch (HostException e) {
                e.printStackTrace();
            }
        }
    }

    public void onRecordingButtonPush() {
        if (connection.isRecording()) {
            try {
                connection.recordingStop();
            } catch (HostException e) {
                e.printStackTrace();
            }
        } else {
            try {
                connection.recordingStart();
            } catch (HostException e) {
                e.printStackTrace();
            }
        }
        controlInterface(true);
    }

    public void onBackButtonPush() {
        try {
            if (connection != null && connection.isConnected()) {
                try {
                    connection.disconnect();
                } catch (HostException e) {
                    e.printStackTrace();
                }
            }
            //noinspection unchecked
            ((WindowWithProperty<PatientRecord>) generateNewWindow("fxml/PatientRecordOpen.fxml"))
                    .setProperty(patientRecord)
                    .showWindow();
        } catch (UIException e) {
            e.printStackTrace();
        }
    }

    public void commentFieldChange() throws UIException {
        try {
            String comment = connection.setCommentForExamination(commentField.getText());
            commentField.setText(comment);
        } catch (HostException e) {
            throw new UIException("Error updating comment to examination");
        }
    }

    private void clearInterface() {
        deviceComboBox.setValue(null);
        channelVBox.getChildren().clear();
        checkBoxOfChannelVBox.getChildren().clear();
    }

    private void controlInterface(boolean enableButtonScanning) {
        scanningSerialPortsButton.setDisable(!enableButtonScanning);
        deviceComboBox.setDisable(connection != null);

        if (connection != null && connection.isConnected()) {
            startButton.setDisable(false);
            rebootButton.setDisable(false);
            if (connection.isTransmission()) {
                startButton.setText("Stop");
            } else {
                startButton.setText("Start");
            }
        } else {
            startButton.setDisable(true);
            rebootButton.setDisable(true);
            startButton.setText("Start");
        }

        rebootButton.setDisable(connection == null || !connection.isConnected());

        allSliderZoom.setDisable(connection == null || !connection.isConnected() || connection.isTransmission());
        channelGUIs.forEach(o -> o.getController().setEnable(!allSliderZoom.isDisable()));
        checkBoxesOfChannel.forEach(o -> o.setDisable(allSliderZoom.isDisable()));

        recordingButton.setDisable(connection == null || !connection.isTransmission());

        if (connection != null && connection.isRecording()) {
            recordingButton.setText("stop\nrecording");
        } else {
            recordingButton.setText("recording");
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


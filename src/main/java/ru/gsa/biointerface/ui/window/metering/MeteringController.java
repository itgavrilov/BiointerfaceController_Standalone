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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gsa.biointerface.domain.entity.Device;
import ru.gsa.biointerface.domain.entity.Icd;
import ru.gsa.biointerface.domain.entity.Patient;
import ru.gsa.biointerface.host.ConnectionToDeviceHandlerFactory;
import ru.gsa.biointerface.host.HostHandler;
import ru.gsa.biointerface.ui.window.AbstractWindow;
import ru.gsa.biointerface.ui.window.AlertError;
import ru.gsa.biointerface.ui.window.WindowWithProperty;
import ru.gsa.biointerface.ui.window.channel.ChannelCheckBox;
import ru.gsa.biointerface.ui.window.channel.CompositeNode;

import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 07.11.2019.
 */
public class MeteringController extends AbstractWindow implements WindowWithProperty<Patient> {
    private static final Logger LOGGER = LoggerFactory.getLogger(MeteringController.class);
    private static MeteringController instants;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private final ConnectionToDeviceHandlerFactory connectionToDeviceHandlerFactory = ConnectionToDeviceHandlerFactory.getInstance();
    private final List<CompositeNode<AnchorPane, ChannelForMeteringController>> channelGUIs = new LinkedList<>();
    private final List<ChannelCheckBox> checkBoxesOfChannel = new LinkedList<>();
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
    private HostHandler hostHandler;
    private Patient patient;
    @FXML
    private AnchorPane anchorPaneControl;
    @FXML
    private Text patientRecordIdText;
    @FXML
    private Text secondNameText;
    @FXML
    private Text firstNameText;
    @FXML
    private Text patronymicText;
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

    public MeteringController() {
    }

    static public void disconnect() {
        if (instants != null && instants.hostHandler != null) {
            ConnectionToDeviceHandlerFactory.disconnectScanningSerialPort();
            try {
                instants.hostHandler.disconnect();
                if (instants.hostHandler.isTransmission()) {
                    instants.hostHandler.transmissionStop();
                }
            } catch (Exception e) {
                LOGGER.error("Error disconnect from host", e);
                new AlertError("Error disconnect from host: " + e.getMessage());
            }
        }
    }

    @Override
    public WindowWithProperty<Patient> setProperty(Patient patient) {
        if (patient == null)
            throw new NullPointerException("patient is null");

        this.patient = patient;

        return this;
    }

    @Override
    public void showWindow() {
        if (resourceSource == null || transitionGUI == null) {
            throw new NullPointerException("" +
                    "resourceSource or transitionGUI is null. " +
                    "First call setResourceAndTransition()" +
                    "");
        }
        if (patient == null) {
            throw new NullPointerException("" +
                    "servicePatientRecord is null. " +
                    "First call setParameter()" +
                    "");
        }

        patientRecordIdText.setText(String.valueOf(patient.getId()));
        secondNameText.setText(patient.getSecondName());
        firstNameText.setText(patient.getFirstName());
        patronymicText.setText(patient.getPatronymic());
        birthdayText.setText(patient.getBirthdayInLocalDate().format(dateFormatter));
        deviceComboBox.setConverter(converter);

        if (patient.getIcd() != null) {
            Icd icd = patient.getIcd();
            icdText.setText(icd.getName() + " (ICD-" + icd.getVersion() + ")");
        } else {
            icdText.setText("-");
        }

        transitionGUI.show();
        instants = this;
    }

    public void buttonScanningSerialPortsPush() {
        hostHandler = null;
        clearInterface();
        controlInterface(false);
        connectionToDeviceHandlerFactory.scanningSerialPort();
    }

    public void devicesComboBoxShowing() {
        controlInterface(true);
        deviceComboBox.getItems().clear();
        deviceComboBox.getItems().addAll(connectionToDeviceHandlerFactory.getDevices());
    }

    public void devicesComboBoxSelect() {
        if (deviceComboBox.getValue() != null) {
            try {
                hostHandler = connectionToDeviceHandlerFactory.getConnection(deviceComboBox.getValue());
                hostHandler.connect();
                hostHandler.setPatient(patient);
                buildingChannelsGUIs();
                if (hostHandler.isConnected()) {
                    controlInterface(true);
                }
            } catch (Exception e) {
                new AlertError("Error host running: " + e.getMessage());
            }
        }
    }

    public void buildingChannelsGUIs() {
        int capacity = (int) allSliderZoom.getValue();
        channelGUIs.clear();
        checkBoxesOfChannel.clear();

        for (int i = 0; i < hostHandler.getAmountChannels(); i++) {
            CompositeNode<AnchorPane, ChannelForMeteringController> node =
                    new CompositeNode<>(
                            new FXMLLoader(
                                    resourceSource.getResource("fxml/ChannelForMetering.fxml"
                                    )
                            )
                    );
            ChannelForMeteringController graphController = node.getController();

            graphController.setNumber(i);
            graphController.setConnection(hostHandler);
            graphController.setCapacity(capacity);
            hostHandler.setListenerInChannel(i, graphController);
            channelGUIs.add(node);

            ChannelCheckBox checkBox = new ChannelCheckBox(i);
            checkBox.setOnAction(event -> {
                node.getNode().setVisible(checkBox.isSelected());
                drawChannelsGUI();
            });

            graphController.setCheckBox(checkBox);
            checkBoxesOfChannel.add(checkBox);
        }

        allSliderZoom.valueProperty().addListener((ov, old_val, new_val) ->
                channelGUIs.forEach(o ->
                        o.getController().setCapacity(new_val.intValue())
                )
        );
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
        LOGGER.info("Start button push");

        if (hostHandler.isConnected()) {
            if (hostHandler.isTransmission()) {
                try {
                    hostHandler.transmissionStop();
                    controlInterface(true);
                } catch (Exception e) {
                    LOGGER.error("Host is not running", e);
                    new AlertError("Host is not running: " + e.getMessage());
                }
            } else {
                try {
                    hostHandler.transmissionStart();
                    controlInterface(false);
                } catch (Exception e) {
                    LOGGER.error("Host is not running", e);
                    new AlertError("Host is not running: " + e.getMessage());
                }
            }
        }
    }

    public void onRebootButtonPush() {
        LOGGER.info("Reboot button push");
        clearInterface();
        controlInterface(true);

        if (hostHandler.isConnected()) {
            try {
                hostHandler.controllerReboot();
            } catch (Exception e) {
                LOGGER.error("Host is not running", e);
                new AlertError("Host is not running: " + e.getMessage());
            }
        }
    }

    public void onRecordingButtonPush() {
        LOGGER.info("Recording button push");

        if (hostHandler.isRecording()) {
            try {
                hostHandler.recordingStop();
            } catch (Exception e) {
                LOGGER.error("Host is not running", e);
                new AlertError("Host is not running: " + e.getMessage());
            }
        } else {
            try {
                hostHandler.recordingStart();
            } catch (Exception e) {
                LOGGER.error("Host is not transmission", e);
                new AlertError("Host is not transmission: " + e.getMessage());
            }
        }

        controlInterface(true);
    }

    public void onBackButtonPush() {
        LOGGER.info("Back button push");
        if (hostHandler != null && hostHandler.isConnected()) {
            try {
                hostHandler.disconnect();
            } catch (Exception e) {
                LOGGER.error("Error disconnected host", e);
                new AlertError("Error disconnect wish device: " + e.getMessage());
            }
        }
        try {
            //noinspection unchecked
            ((WindowWithProperty<Patient>) generateNewWindow("fxml/PatientOpen.fxml"))
                    .setProperty(patient)
                    .showWindow();
        } catch (Exception e) {
            new AlertError("Error load patient: " + e.getMessage());
        }
    }

    public void commentFieldChange() {
        LOGGER.info("Change commentField for Examination");
        try {
            hostHandler.setCommentForExamination(commentField.getText());
        } catch (Exception e) {
            LOGGER.error("Error comment change", e);
            new AlertError("Error comment change: " + e.getMessage());
        }
    }

    private void clearInterface() {
        deviceComboBox.setValue(null);
        channelVBox.getChildren().clear();
        checkBoxOfChannelVBox.getChildren().clear();
    }

    private void controlInterface(boolean enableButtonScanning) {
        scanningSerialPortsButton.setDisable(!enableButtonScanning);
        deviceComboBox.setDisable(hostHandler != null);

        if (hostHandler != null && hostHandler.isConnected()) {
            startButton.setDisable(false);
            rebootButton.setDisable(false);
            if (hostHandler.isTransmission()) {
                startButton.setText("Stop");
            } else {
                startButton.setText("Start");
            }
        } else {
            startButton.setDisable(true);
            rebootButton.setDisable(true);
            startButton.setText("Start");
        }

        rebootButton.setDisable(hostHandler == null || !hostHandler.isConnected());
        allSliderZoom.setDisable(hostHandler == null || !hostHandler.isConnected() || hostHandler.isTransmission());
        channelGUIs.forEach(o -> o.getController().setEnable(!allSliderZoom.isDisable()));
        checkBoxesOfChannel.forEach(o -> o.setDisable(allSliderZoom.isDisable()));
        recordingButton.setDisable(hostHandler == null || !hostHandler.isTransmission());

        if (hostHandler != null && hostHandler.isRecording()) {
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

        for (CompositeNode<AnchorPane, ChannelForMeteringController> o : channelGUIs) {
            o.getController().resizeWindow(heightChannelGUIs, width - anchorPaneControl.getWidth() + 13);
        }
    }

    @Override
    public String getTitleWindow() {
        return ": new examination";
    }
}


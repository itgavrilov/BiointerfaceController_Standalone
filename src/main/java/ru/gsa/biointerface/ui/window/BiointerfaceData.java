package ru.gsa.biointerface.ui.window;

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
import ru.gsa.biointerface.domain.Connection;
import ru.gsa.biointerface.domain.ConnectionFactory;
import ru.gsa.biointerface.domain.DomainException;
import ru.gsa.biointerface.domain.PatientRecord;
import ru.gsa.biointerface.ui.UIException;
import ru.gsa.biointerface.ui.window.channel.Channel;
import ru.gsa.biointerface.ui.window.channel.CheckBoxOfChannel;
import ru.gsa.biointerface.ui.window.channel.CompositeNode;

import java.time.format.DateTimeFormatter;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 07.11.2019.
 */
public class BiointerfaceData extends AbstractWindow implements WindowWithProperty<PatientRecord> {
    static private BiointerfaceData instants;
    private final Set<CompositeNode<AnchorPane, Channel>> channelGUIs = new TreeSet<>();
    private final Set<CheckBoxOfChannel> checkBoxesOfChannel = new TreeSet<>();
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
    private ComboBox<String> availableDevices;
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

        allSliderZoom.valueProperty().addListener((obs, oldval, newVal) ->
                allSliderZoom.setValue(newVal.intValue()));

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

    public void comboBoxComShowing() {
        clearInterface();
        controlInterface(
                true,
                true,
                false,
                false,
                false
        );
        availableDevices.getItems().addAll(ConnectionFactory.getSerialNumbers());
    }

    public void comboBoxComSelect() {
        if (availableDevices.getValue() != null && !"".equals(availableDevices.getValue())) {
            connection = ConnectionFactory.getInstance(availableDevices.getValue());

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
                        true,
                        false
                );
                channelGUIs.forEach(o -> o.getController().setReady(true));
            }
        }
    }

    public void buildingChannelsGUIs() {
        channelGUIs.clear();

        for (char i = 0; i < connection.getDevice().getAmountChannels(); i++) {
            CompositeNode<AnchorPane, Channel> node =
                    new CompositeNode<>(new FXMLLoader(resourceSource.getResource("Channel.fxml")));

            node.getController().setId(i);
            channelGUIs.add(node);
        }
        try {
            connection.registerChannelGUIs(
                    channelGUIs.stream()
                            .map(CompositeNode::getController)
                            .collect(Collectors.toSet())
            );
        } catch (DomainException e) {
            e.printStackTrace();
        }
        allSliderZoom.setOnMouseReleased(e -> {
            int capacity = (int) allSliderZoom.getValue();
            try {
                connection.setCapacity(capacity);
            } catch (DomainException ex) {
                ex.printStackTrace();
            }
        });

        drawChannelsGUI();
    }

    public void buildingCheckBoxesOfChannelGUIs() {
        checkBoxesOfChannel.clear();

        for (char i = 0; i < connection.getDevice().getAmountChannels(); i++) {
            checkBoxesOfChannel.add(new CheckBoxOfChannel(i));
        }
    }

    public void drawChannelsGUI() {
        channelVBox.getChildren().clear();
        try {
            connection.setCapacity((int) allSliderZoom.getValue());
        } catch (DomainException e) {
            e.printStackTrace();
        }
        channelGUIs.forEach(n -> {
            if (n.getNode().isVisible())
                channelVBox.getChildren().add(n.getNode());
        });
        resizeWindow(anchorPaneRoot.getHeight(), anchorPaneRoot.getWidth());
    }

    private void clearInterface() {
        channelVBox.getChildren().clear();
        checkBoxOfChannelVBox.getChildren().clear();
        availableDevices.getItems().clear();
        availableDevices.setValue(null);
        startButton.setText("Start");
        recordingButton.setText("recording");
    }

    public void buttonComStartPush() {
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

    public void buttonPushComReboot() {
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

    public void onRecording() {
        if (connection.isRecording()) {
            connection.recordingStop();
            recordingButton.setText("recording");
        } else {
            connection.recordingStart(commentField.getText());
            recordingButton.setText("stop\nrecording");
        }
    }

    public void onBack() {
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
        availableDevices.setDisable(!enableNumberOfCOM);
        startButton.setDisable(!enableComStart);
        rebootButton.setDisable(!enableComStart);
        allSliderZoom.setDisable(!enableSliderZoom);
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

        for (CompositeNode<AnchorPane, Channel> o : channelGUIs) {
            o.getController().resizeWindow(heightChannelGUIs, width - anchorPaneControl.getWidth() + 13);
        }

    }

    @Override
    public String getTitleWindow() {
        return ": biointerface data";
    }
}


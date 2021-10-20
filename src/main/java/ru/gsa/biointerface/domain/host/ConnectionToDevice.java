package ru.gsa.biointerface.domain.host;

import com.fazecast.jSerialComm.SerialPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gsa.biointerface.domain.*;
import ru.gsa.biointerface.domain.entity.GraphEntity;
import ru.gsa.biointerface.domain.entity.SampleEntity;
import ru.gsa.biointerface.domain.host.cash.DataListener;
import ru.gsa.biointerface.domain.host.serialport.ControlMessages;
import ru.gsa.biointerface.domain.host.serialport.DataCollector;
import ru.gsa.biointerface.domain.host.serialport.Handler;
import ru.gsa.biointerface.domain.host.serialport.SerialPortHost;
import ru.gsa.biointerface.domain.host.cash.Cash;
import ru.gsa.biointerface.domain.host.cash.SampleCash;
import ru.gsa.biointerface.ui.window.metering.Connection;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class ConnectionToDevice implements DataCollector, Connection {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionToDevice.class);
    private SerialPortHost serialPortHost;
    private Device device;
    private List<Cash> cashList;
    private PatientRecord patientRecord;

    private Examination examination;
    private List<Channel> channelList;
    private String comment;
    private boolean flagTransmission = false;

    public ConnectionToDevice(SerialPort serialPort) throws DomainException {
        if (serialPort == null)
            throw new NullPointerException("SerialPort is null");

        serialPortHost = new SerialPortHost(serialPort);
        serialPortHost.handler(new Handler(this));

        try {
            serialPortHost.start();
        } catch (Exception e) {
            throw new DomainException("SerialPortHost start error", e);
        }
        serialPortHost.sendPackage(ControlMessages.GET_CONFIG);
    }

    public PatientRecord getPatientRecord() {
        return patientRecord;
    }

    @Override
    public void setPatientRecord(PatientRecord patientRecord) throws DomainException {
        if (patientRecord == null)
            throw new NullPointerException("PatientRecord is null");
        if (device == null)
            throw new DomainException("device null");

        this.patientRecord = patientRecord;
    }

    @Override
    public int getAmountChannels() {
        return device.getAmountChannels();
    }

    public Device getDevice() {
        return device;
    }

    @Override
    public void setDevice(int serialNumber, int amountChannels) {
        if (serialNumber == 0)
            throw new IllegalArgumentException("Serial number is '0'");
        if (amountChannels == 0)
            throw new IllegalArgumentException("Amount channels is '0'");

        if (device != null && device.getId() == serialNumber) {
            if(device.getAmountChannels() != amountChannels)
                throw new IllegalArgumentException("Amount of channels is different from saved");

            return;
        }

        try {
            device = new Device(serialNumber, amountChannels);
            cashList = new ArrayList<>();
            channelList = new ArrayList<>();

            for (int i = 0; i < device.getAmountChannels(); i++) {
                cashList.add(new SampleCash());
                channelList.add(null);
            }

            examination = null;
            patientRecord = null;
        } catch (DomainException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isAvailableDevice() {
        return device != null;
    }

    @Override
    public void addListenerInCash(int numberOfChannel, DataListener listener) throws DomainException {
        if (listener == null)
            throw new NullPointerException("Listener null");
        if (device == null)
            throw new DomainException("Device null");
        if (numberOfChannel >= cashList.size() || numberOfChannel < 0)
            throw new DomainException("I > amount cashList");

        cashList.get(numberOfChannel).addListener(listener);
    }

    @Override
    public void addInCash(int numberOfChannel, int value) throws DomainException {
        if (device == null)
            throw new DomainException("Device null");
        if (numberOfChannel >= cashList.size() || numberOfChannel < 0)
            throw new DomainException("NumberOfChannel > amount cashList");

        cashList.get(numberOfChannel).add(value);

        if(isRecording())
            examination.setNewSamplesInGraph(numberOfChannel, value);
    }

    @Override
    public void setChannelInGraph(int numberOfChannel, Channel channel) throws DomainException {
        if (numberOfChannel < 0)
            throw new IllegalArgumentException("numberOfChannel < 0");
        if (patientRecord == null)
            throw new DomainException("PatientRecord is not init. First call setPatientRecord()");
        if (numberOfChannel >= channelList.size())
            throw new DomainException("I > amount channelList");
        if (isRecording())
            throw new NullPointerException("Recording is started");

        channelList.set(numberOfChannel, channel);

        if(examination != null)
            examination.setChannelInGraph(numberOfChannel, channel);
    }

    @Override
    public boolean isConnected() {
        boolean result = false;

        if (serialPortHost != null && serialPortHost.isRunning())
            result = serialPortHost.portIsOpen();

        return result;
    }

    @Override
    public void disconnect() throws DomainException {
        if (isConnected()) {
            try {
                if (isTransmission()) {
                    transmissionStop();
                }
                serialPortHost.stop();
                LOGGER.info("Disconnecting from device");
            } catch (Exception e) {
                throw new DomainException("SerialPortHost stop error", e);
            }
        }
    }

    public void connect() throws DomainException {
        if (!isConnected()) {
            try {
                serialPortHost.start();
            } catch (Exception e) {
                throw new DomainException("SerialPortHost start error", e);
            }
            serialPortHost.sendPackage(ControlMessages.GET_CONFIG);
        }
    }

    @Override
    public void transmissionStart() throws DomainException {
        if (patientRecord == null)
            throw new NullPointerException("PatientRecord is not init. First call setPatientRecord()");
        if (serialPortHost == null)
            throw new DomainException("Server is not initialized (null)");
        if (!serialPortHost.isRunning())
            throw new DomainException("Server is not running");

        serialPortHost.sendPackage(ControlMessages.START_TRANSMISSION);
        flagTransmission = true;
        LOGGER.info("Start transmission");
    }

    @Override
    public void transmissionStop() throws DomainException {
        if (!flagTransmission)
            throw new DomainException("Transmission is not started");
        if (serialPortHost == null)
            throw new DomainException("Server is not initialized (null)");

        serialPortHost.sendPackage(ControlMessages.STOP_TRANSMISSION);
        flagTransmission = false;
        LOGGER.info("Stop transmission");
    }

    @Override
    public void setFlagTransmission() {
        flagTransmission = true;
    }

    @Override
    public boolean isTransmission() {
        return flagTransmission;
    }

    @Override
    public void recordingStart() throws DomainException {
        if (device == null)
            throw new DomainException("device null");
        if (patientRecord == null)
            throw new NullPointerException("PatientRecord is not init. First call setPatientRecord()");
        if (!flagTransmission)
            throw new DomainException("Transmission is stop");
        if (isRecording())
            throw new DomainException("Recording is already in progress");


        examination = new Examination(patientRecord, device, channelList, comment);

        examination.recordingStart();
        LOGGER.info("Start recording");
    }

    @Override
    public void recordingStop() throws DomainException {
        if (!isRecording())
            throw new DomainException("Recording is stop");

        examination.recordingStop();
        examination = null;
        LOGGER.info("Stop recording");
    }

    @Override
    public boolean isRecording() {
        boolean result = false;

        if (examination != null)
            result = examination.isRecording();

        return result;
    }

    @Override
    public void controllerReboot() throws DomainException {
        if (serialPortHost == null)
            throw new NullPointerException("Server is not initialized (null)");

        if (isRecording())
            recordingStop();
        if (isTransmission())
            transmissionStop();
        serialPortHost.sendPackage(ControlMessages.REBOOT);
        LOGGER.info("Reboot controller");
    }

    @Override
    public void setCommentForExamination(String comment) throws DomainException {
        this.comment = comment;
        if (examination != null)
            examination.setComment(comment);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConnectionToDevice that = (ConnectionToDevice) o;
        return Objects.equals(serialPortHost, that.serialPortHost);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serialPortHost);
    }
}

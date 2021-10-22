package ru.gsa.biointerface.host;

import com.fazecast.jSerialComm.SerialPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gsa.biointerface.domain.entity.Channel;
import ru.gsa.biointerface.domain.entity.Device;
import ru.gsa.biointerface.domain.entity.Examination;
import ru.gsa.biointerface.domain.entity.PatientRecord;
import ru.gsa.biointerface.host.serialport.SerialPortHandler;
import ru.gsa.biointerface.host.cash.Cash;
import ru.gsa.biointerface.host.cash.DataListener;
import ru.gsa.biointerface.host.cash.SampleCash;
import ru.gsa.biointerface.host.serialport.ControlMessages;
import ru.gsa.biointerface.host.serialport.DataCollector;
import ru.gsa.biointerface.host.serialport.SerialPortHost;
import ru.gsa.biointerface.services.*;
import ru.gsa.biointerface.ui.window.metering.Connection;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class ConnectionHandler implements DataCollector, Connection {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionHandler.class);
    private final SerialPortHost serialPortHost;
    private final ServiceExamination serviceExamination;
    private final ServiceDevice serviceDevice;
    private final List<Cash> cashList = new ArrayList<>();
    private final List<Channel> channels = new ArrayList<>();

    private Device device;
    private PatientRecord patientRecord;
    private Examination examination;
    private String comment;
    private boolean flagTransmission = false;

    public ConnectionHandler(SerialPort serialPort) throws HostException {
        if (serialPort == null)
            throw new NullPointerException("SerialPort is null");

        try {
            serviceExamination = ServiceExamination.getInstance();
            serviceDevice = ServiceDevice.getInstance();
            serialPortHost = new SerialPortHost(serialPort);
            serialPortHost.handler(new SerialPortHandler(this));

            try {
                serialPortHost.start();
                serialPortHost.sendPackage(ControlMessages.GET_CONFIG);
            } catch (Exception e) {
                throw new HostException("SerialPortHost start error", e);
            }
        } catch (ServiceException e) {
            throw new HostException("Error connection to database", e);
        }
    }

    public PatientRecord getPatientRecord() {
        return patientRecord;
    }

    @Override
    public void setPatientRecord(PatientRecord patientRecord) throws HostException {
        if (patientRecord == null)
            throw new NullPointerException("PatientRecord is null");
        if (device == null)
            throw new HostException("Device null");

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
        if(serialNumber <= 0)
            throw new IllegalArgumentException("SerialNumber <= 0");
        if(amountChannels <= 0 || amountChannels > 8)
            throw new IllegalArgumentException("amountChannels <= 0 or > 8");

        if(device == null || device.getId() != serialNumber) {
            device = serviceDevice.create(serialNumber, amountChannels);
            examination = null;
            patientRecord = null;

            for (int i = 0; i < device.getAmountChannels(); i++) {
                cashList.add(new SampleCash());
                channels.add(null);
            }
        }
    }

    @Override
    public boolean isAvailableDevice() {
        return device != null;
    }

    @Override
    public void addListenerInCash(int numberOfChannel, DataListener listener) {
        if (listener == null)
            throw new NullPointerException("Listener null");
        if (numberOfChannel >= cashList.size() || numberOfChannel < 0)
            throw new IllegalArgumentException("I > amount cashList");
        if (device == null)
            throw new NullPointerException("Device is null");

        cashList.get(numberOfChannel).addListener(listener);
    }

    @Override
    public void addInCash(int numberOfChannel, int value) throws HostException {
        if (numberOfChannel >= cashList.size() || numberOfChannel < 0)
            throw new IllegalArgumentException("NumberOfChannel > amount cashList");
        if (device == null)
            throw new NullPointerException("Device is null");

        cashList.get(numberOfChannel).add(value);

        if (isRecording()) {
            try {
                examination.setSampleInGraph(numberOfChannel, value);
            } catch (ServiceException e) {
                throw new HostException("Error set sample", e);
            }
        }
    }

    @Override
    public void setChannelInGraph(int numberOfChannel, Channel channel) throws HostException {
        if (numberOfChannel < 0)
            throw new IllegalArgumentException("NumberOfChannel < 0");
        if (device == null)
            throw new NullPointerException("Device is null");
        if (numberOfChannel >= channels.size())
            throw new HostException("I > amount serviceChannelList");

        channels.set(numberOfChannel, channel);

        if (examination != null) {
            try {
                examination.setChannelInGraph(numberOfChannel, channel);
            } catch (ServiceException e) {
                throw new HostException("Error set channel", e);
            }
        }

        if(channel != null) {
            LOGGER.info("{} for graph {} is set", channel, numberOfChannel);
        } else {
            LOGGER.info("Channel for graph {} is reset", numberOfChannel);
        }
    }

    @Override
    public String setCommentForExamination(String newComment) {
        this.comment = newComment;

        if (examination != null) {
            String comment = examination.getComment();
            if (Objects.equals(comment, newComment)) {
                try {
                    examination.setComment(newComment);
                    serviceExamination.update(examination);
                } catch (ServiceException e) {
                    examination.setComment(comment);
                    this.comment = examination.getComment();
                    e.printStackTrace();
                }
            }
        }

        return this.comment;
    }

    public void connect() throws HostException {
        if (!isConnected()) {
            try {
                serialPortHost.start();
            } catch (Exception e) {
                throw new HostException("SerialPortHost start error", e);
            }
            serialPortHost.sendPackage(ControlMessages.GET_CONFIG);
        }
    }

    @Override
    public void disconnect() throws HostException {
        if (isConnected()) {
            try {
                if (isTransmission()) {
                    transmissionStop();
                }
                serialPortHost.stop();
                LOGGER.info("Disconnecting from serviceDevice");
            } catch (Exception e) {
                throw new HostException("SerialPortHost stop error", e);
            }
        }
    }

    @Override
    public boolean isConnected() {
        boolean result = false;

        if (serialPortHost != null && serialPortHost.isRunning())
            result = serialPortHost.portIsOpen();

        return result;
    }

    @Override
    public void transmissionStart() throws HostException {
        if (serialPortHost == null)
            throw new NullPointerException("Server is not initialized (null)");
        if (!serialPortHost.isRunning())
            throw new HostException("Server is not running");
        if (device == null)
            throw new NullPointerException("Device null");
        if (patientRecord == null)
            throw new HostException("ServicePatientRecord is not init. First call setPatientRecord()");

        serialPortHost.sendPackage(ControlMessages.START_TRANSMISSION);
        flagTransmission = true;
        LOGGER.info("Start transmission");
    }

    @Override
    public void transmissionStop() throws HostException {
        if (serialPortHost == null)
            throw new HostException("Server is not initialized (null)");
        if (!serialPortHost.isRunning())
            throw new HostException("Server is not running");

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
    public void recordingStart() throws HostException {
        if (device == null)
            throw new NullPointerException("Device null");
        if (patientRecord == null)
            throw new HostException("ServicePatientRecord is not init. First call setPatientRecord()");
        if (!flagTransmission)
            throw new HostException("Transmission is stop");
        if (isRecording())
            throw new HostException("Recording is already in progress");

        examination = serviceExamination.create(patientRecord, device, channels, comment);
        try {
            serviceDevice.save(device);
            serviceExamination.recordingStart(examination);
            LOGGER.info("Start recording");
        } catch (ServiceException e) {
            examination = null;
            throw new HostException("Error start recording", e);
        }
    }

    @Override
    public void recordingStop() throws HostException {
        if (!isRecording())
            throw new HostException("Recording is stop");
        serviceExamination.recordingStop(examination);
        examination = null;
        LOGGER.info("Stop recording");
    }

    @Override
    public boolean isRecording() {
        boolean result = false;

        if (examination != null) {
            result = examination.isRecording();
        }

        return result;
    }

    @Override
    public void controllerReboot() throws HostException {
        if (serialPortHost == null)
            throw new HostException("Server is not initialized (null)");

        if (isTransmission()) {
            try {
                if (isRecording()) {
                    recordingStop();
                }
                transmissionStop();
            } catch (HostException e) {
                throw new HostException("Error stop transmission/recording", e);
            }
        }

        serialPortHost.sendPackage(ControlMessages.REBOOT);
        LOGGER.info("Reboot controller");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConnectionHandler that = (ConnectionHandler) o;
        return Objects.equals(serialPortHost, that.serialPortHost);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serialPortHost);
    }

    @Override
    public String toString() {
        return "ConnectionHandler{" +
                "serialPortHost=" + serialPortHost.toString()+
                '}';
    }
}

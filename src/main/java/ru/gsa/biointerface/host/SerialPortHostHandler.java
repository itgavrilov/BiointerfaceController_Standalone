package ru.gsa.biointerface.host;

import com.fazecast.jSerialComm.SerialPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gsa.biointerface.domain.entity.*;
import ru.gsa.biointerface.host.cash.Cash;
import ru.gsa.biointerface.host.cash.DataListener;
import ru.gsa.biointerface.host.cash.SampleCash;
import ru.gsa.biointerface.host.exception.HostException;
import ru.gsa.biointerface.host.exception.HostNotRunningException;
import ru.gsa.biointerface.host.exception.HostNotTransmissionException;
import ru.gsa.biointerface.host.serialport.ControlMessages;
import ru.gsa.biointerface.host.serialport.DataCollector;
import ru.gsa.biointerface.host.serialport.SerialPortHandler;
import ru.gsa.biointerface.host.serialport.SerialPortHost;
import ru.gsa.biointerface.services.DeviceService;
import ru.gsa.biointerface.services.ExaminationService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class SerialPortHostHandler implements DataCollector, HostHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(SerialPortHostHandler.class);
    private final SerialPortHost serialPortHost;
    private final ExaminationService examinationService;
    private final DeviceService deviceService;
    private final List<Cash> cashList = new ArrayList<>();
    private final List<ChannelName> channelNames = new ArrayList<>();

    private Device device;
    private PatientRecord patientRecord;
    private Examination examination;
    private String comment;
    private boolean flagTransmission = false;

    public SerialPortHostHandler(SerialPort serialPort) throws Exception {
        if (serialPort == null)
            throw new NullPointerException("SerialPort is null");

        examinationService = ExaminationService.getInstance();
        deviceService = DeviceService.getInstance();
        serialPortHost = new SerialPortHost(serialPort);
        serialPortHost.handler(new SerialPortHandler(this));

        serialPortHost.start();
        serialPortHost.sendPackage(ControlMessages.GET_CONFIG);
    }

    public PatientRecord getPatientRecord() {
        return patientRecord;
    }

    @Override
    public void setPatientRecord(PatientRecord patientRecord) {
        if (patientRecord == null)
            throw new NullPointerException("PatientRecord is null");
        if (device == null)
            throw new NullPointerException("Device null");

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
    public void setDevice(int id, int amountChannels) {
        if (id <= 0)
            throw new IllegalArgumentException("SerialNumber <= 0");
        if (amountChannels <= 0 || amountChannels > 8)
            throw new IllegalArgumentException("amountChannels <= 0 or > 8");

        if (device == null || device.getId() != id) {
            device = new Device(id, amountChannels);
            examination = null;
            patientRecord = null;

            for (int i = 0; i < device.getAmountChannels(); i++) {
                cashList.add(new SampleCash());
                channelNames.add(null);
            }
        }
    }

    @Override
    public boolean isAvailableDevice() {
        return device != null;
    }

    @Override
    public void setListenerInChannel(int number, DataListener listener) {
        if (listener == null)
            throw new NullPointerException("Listener null");
        if (number >= cashList.size() || number < 0)
            throw new IllegalArgumentException("Number > amount cashList");
        if (device == null)
            throw new NullPointerException("Device is null");

        cashList.get(number).setListener(listener);
    }

    @Override
    public void setSampleInChannel(int number, int value) {
        if (number >= cashList.size() || number < 0)
            throw new IllegalArgumentException("Number > amount cashList");
        if (device == null)
            throw new NullPointerException("Device is null");

        cashList.get(number).add(value);

        if (isRecording()) {
            examination.setSampleInChannel(number, value);
        }
    }

    @Override
    public void setNameInChannel(int number, ChannelName channelName) {
        if (number < 0)
            throw new IllegalArgumentException("Number < 0");
        if (device == null)
            throw new NullPointerException("Device is null");
        if (number >= channelNames.size())
            throw new IllegalArgumentException("Number > amount serviceChannelList");

        channelNames.set(number, channelName);

        if (examination != null) {
            examination.setNameInChannel(number, channelName);
        }

        if (channelName != null) {
            LOGGER.info("ChannelName(id={}) is set in channel(number={})", channelName.getId(), number);
        } else {
            LOGGER.info("ChannelName is reset in channel(number={})", number);
        }
    }

    @Override
    public void setCommentForExamination(String newComment) throws Exception {
        this.comment = newComment;
        LOGGER.info("Set new comment for examination");

        if (examination != null) {
            String comment = examination.getComment();
            if (Objects.equals(comment, newComment)) {
                try {
                    examination.setComment(newComment);
                    examinationService.update(examination);
                    LOGGER.info("New comment is set in examination(number={})", examination.getId());
                } catch (Exception e) {
                    examination.setComment(comment);
                    LOGGER.error("Error update examination(number={})", examination.getId(), e);
                    throw e;
                }
            }
        }
    }

    public void connect() throws Exception {
        if (!isConnected()) {
            try {
                serialPortHost.start();
                serialPortHost.sendPackage(ControlMessages.GET_CONFIG);
                LOGGER.info("Connecting to device");
            } catch (Exception e) {
                LOGGER.error("Host is not running", e);
                throw e;
            }
        } else {
            LOGGER.warn("Device is already connected");
        }
    }

    @Override
    public void disconnect() throws Exception {
        if (isConnected()) {
            if (isTransmission()) {
                if (isRecording()) {
                    recordingStop();
                }
                transmissionStop();
            }
            serialPortHost.stop();
            LOGGER.info("Disconnecting from device");
        } else {
            LOGGER.warn("Device is already disconnected");
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
    public void transmissionStart() throws Exception {
        if (serialPortHost == null)
            throw new NullPointerException("Host is null");
        if (!serialPortHost.isRunning())
            throw new HostNotRunningException();
        if (device == null)
            throw new NullPointerException("Device null");
        if (patientRecord == null)
            throw new NullPointerException("PatientRecordService is not init. First call setPatientRecord()");

        serialPortHost.sendPackage(ControlMessages.START_TRANSMISSION);
        flagTransmission = true;
        LOGGER.info("Start transmission");
    }

    @Override
    public void transmissionStop() throws Exception {
        if (serialPortHost == null)
            throw new NullPointerException("Host is null");
        if (!serialPortHost.isRunning())
            throw new HostNotRunningException();

        if (isRecording()) {
            recordingStop();
        }

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
    public void recordingStart() throws Exception {
        if (device == null)
            throw new NullPointerException("Device null");
        if (patientRecord == null)
            throw new NullPointerException("PatientRecordService is not init. First call setPatientRecord()");
        if (!flagTransmission)
            throw new HostNotTransmissionException();

        if (!isRecording()) {
            examination = new Examination(
                    patientRecord,
                    device,
                    comment);

            for (int i = 0; i < device.getAmountChannels(); i++) {
                ChannelName channelName = channelNames.get(i);
                Channel channel = new Channel(i, examination, channelName);
                examination.getChannels().add(channel);
            }

            try {
                deviceService.save(device);
                examinationService.recordingStart(examination);
                LOGGER.info("Start recording");
            } catch (Exception e) {
                examination = null;
                LOGGER.error("Error recording started", e);
                throw new HostException("Error recording started", e);
            }
        } else {
            LOGGER.warn("Recording is already in progress");
        }
    }

    @Override
    public void recordingStop() throws Exception {
        if (!isRecording())
            throw new HostNotRunningException();

        examinationService.recordingStop(examination);
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
    public void controllerReboot() throws Exception {
        if (serialPortHost == null)
            throw new NullPointerException("Host is null");
        if (!serialPortHost.isRunning())
            throw new HostNotRunningException();

        serialPortHost.sendPackage(ControlMessages.REBOOT);
        disconnect();
        LOGGER.info("Reboot controller");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SerialPortHostHandler that = (SerialPortHostHandler) o;
        return Objects.equals(serialPortHost, that.serialPortHost);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serialPortHost);
    }

    @Override
    public String toString() {
        return "ConnectionHandler{" +
                "serialPortHost=" + serialPortHost.toString() +
                '}';
    }
}

package ru.gsa.biointerface.domain;

import com.fazecast.jSerialComm.SerialPort;
import ru.gsa.biointerface.domain.entity.Device;
import ru.gsa.biointerface.domain.entity.Examination;
import ru.gsa.biointerface.domain.entity.PatientRecord;
import ru.gsa.biointerface.domain.entity.Samples;
import ru.gsa.biointerface.domain.serialPortHost.*;
import ru.gsa.biointerface.ui.window.channel.Channel;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class ConnectionToDevice implements DataCollector, Connection {
    private final PatientRecord patientRecord;
    private final SerialPortHost serialPortHost;
    private final List<Samples<Integer>> samplesOfChannels = new LinkedList<>();
    private Device device;
    private Examination examination;
    private boolean flagTransmission = false;
    private boolean available = false;
    private boolean recording = false;

    public ConnectionToDevice(PatientRecord patientRecord, SerialPort serialPort) throws DomainException {
        if (serialPort == null)
            throw new NullPointerException("serialPort is null");
        if (patientRecord == null)
            throw new NullPointerException("examination is null");

        this.patientRecord = patientRecord;
        serialPortHost = new SerialPortHost(serialPort);
        serialPortHost.handler(new Handler(this));

        try {
            serialPortHost.start();
        } catch (Exception e) {
            e.printStackTrace();
            throw new DomainException("serialPortHost start error", e);
        }

        serialPortHost.sendPackage(ControlMessages.GET_CONFIG);
    }

    public PatientRecord getPatientRecord() {
        return patientRecord;
    }

    public SerialPort getSerialPort() {
        return serialPortHost.getSerialPort();
    }

    @Override
    public Device getDevice() {
        return device;
    }

    @Override
    public void setDevice(Device device) {
        if(device == null)
            throw new NullPointerException("device is null");

        this.device = device;
    }

    @Override
    public boolean isAvailable() {
        return available;
    }

    @Override
    public void setAvailableDevice(boolean available) {
        this.available = available;
    }

    @Override
    public List<Samples<Integer>> getSamplesOfChannels() {
        return samplesOfChannels;
    }

    @Override
    public void setSamplesOfChannels(Set<Channel> channelGUIs) {
        if (channelGUIs == null)
            throw new NullPointerException("channelGUIs is null");
        if (channelGUIs.size() < device.getAmountChannels())
            throw new RuntimeException("count of channelGUIs less than count of channels");

        samplesOfChannels.clear();
        channelGUIs.forEach(o -> samplesOfChannels.add(new Samples<>(o)));
        setCapacity(10);
    }

    @Override
    public void setCapacity(int capacity) {
        if (device == null)
            throw new NullPointerException("Device configuration empty");
        if (capacity == 0)
            throw new IllegalArgumentException("capacity is '0'");

        samplesOfChannels.forEach(o -> o.setCapacity(capacity));
    }

    @Override
    public boolean isConnected() {
        if (serialPortHost == null)
            throw new NullPointerException("server is not initialized (null)");

        return serialPortHost.isRunning();
    }

    @Override
    public void disconnect() {
        if (device != null)
            device = null;

        if (serialPortHost != null) {
            controllerTransmissionStop();

            try {
                serialPortHost.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void controllerTransmissionStart() {
        if (serialPortHost == null)
            throw new NullPointerException("server is not initialized (null)");
        if (!serialPortHost.isRunning())
            throw new RuntimeException("Server is not running");

        flagTransmission = true;
        serialPortHost.sendPackage(ControlMessages.START_TRANSMISSION);
    }

    @Override
    public void controllerTransmissionStop() {
        if (serialPortHost == null)
            throw new NullPointerException("server is not initialized (null)");

        flagTransmission = false;
        serialPortHost.sendPackage(ControlMessages.STOP_TRANSMISSION);
    }

    @Override
    public boolean isControllerTransmission() {
        return flagTransmission;
    }

    @Override
    public void recordingStart(String comment) {
        try {
            Devices.insert(device);
            examination = Examinations.insert(new Examination(
                    -1,
                    LocalDateTime.now(),
                    patientRecord,
                    device,
                    comment
            ));
        } catch (DomainException e) {
            e.printStackTrace();
        }

        recording = true;
    }

    @Override
    public void recordingStop() {
        recording = false;
    }

    @Override
    public boolean isRecording() {
        return recording;
    }

    @Override
    public void controllerReboot() {
        if (serialPortHost == null)
            throw new NullPointerException("server is not initialized (null)");

        flagTransmission = false;
        serialPortHost.sendPackage(ControlMessages.REBOOT);
    }

    @Override
    public void changeCommentOnExamination(String comment) {
        if (comment == null)
            throw new NullPointerException("comment is null");

        if (examination != null && !comment.equals(examination.getComment())) {
            examination.setComment(comment);
            try {
                Examinations.update(examination);
            } catch (DomainException e) {
                e.printStackTrace();
            }
        }
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

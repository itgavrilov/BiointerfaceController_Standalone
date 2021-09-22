package ru.gsa.biointerface.domain;

import com.fazecast.jSerialComm.SerialPort;
import ru.gsa.biointerface.domain.serialPortHost.ControlMessages;
import ru.gsa.biointerface.domain.serialPortHost.DataCollector;
import ru.gsa.biointerface.domain.serialPortHost.Handler;
import ru.gsa.biointerface.domain.serialPortHost.SerialPortHost;
import ru.gsa.biointerface.persistence.DAOException;
import ru.gsa.biointerface.persistence.dao.SampleDAO;
import ru.gsa.biointerface.ui.window.channel.Channel;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class ConnectionToDevice implements DataCollector, Connection {
    private final PatientRecord patientRecord;
    private final SerialPortHost serialPortHost;
    private final List<Samples> samplesOfChannels = new LinkedList<>();
    private Device device = null;
    private Examination examination = null;
    private boolean flagTransmission = false;

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

    @Override
    public Device getDevice() {
        return device;
    }

    @Override
    public void setDevice(Device device) {
        if (device == null)
            throw new NullPointerException("device is null");

        this.device = device;
    }

    @Override
    public boolean isAvailableDevice() {
        return device != null;
    }

    @Override
    public List<Samples> getSamplesOfChannels() {
        return samplesOfChannels;
    }

    @Override
    public void registerChannelGUIs(Set<Channel> channelGUIs) throws DomainException {
        if (channelGUIs == null)
            throw new NullPointerException("channelGUIs is null");
        if (channelGUIs.size() < device.getAmountChannels())
            throw new DomainException("count of channelGUIs less than count of channels");

        samplesOfChannels.clear();
        channelGUIs.forEach(o -> samplesOfChannels.add(new Samples(o)));
        setCapacity(10);
    }

    @Override
    public void setCapacity(int capacity) throws DomainException {
        if (device == null)
            throw new DomainException("Device configuration empty");
        if (capacity == 0)
            throw new DomainException("capacity is '0'");

        samplesOfChannels.forEach(o -> o.setCapacity(capacity));
    }

    @Override
    public boolean isConnected() {
        boolean result = false;

        if (serialPortHost != null)
            result = serialPortHost.isRunning();

        return result;
    }

    @Override
    public void disconnect() {
        if (device != null)
            device = null;

        if (serialPortHost != null) {
            try {
                controllerTransmissionStop();
                serialPortHost.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void controllerTransmissionStart() throws DomainException {
        if (serialPortHost == null)
            throw new DomainException("server is not initialized (null)");
        if (!serialPortHost.isRunning())
            throw new DomainException("Server is not running");

        flagTransmission = true;
        serialPortHost.sendPackage(ControlMessages.START_TRANSMISSION);
    }

    @Override
    public void controllerTransmissionStop() throws DomainException {
        if (serialPortHost == null)
            throw new DomainException("server is not initialized (null)");

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
            device.insert();

            examination = new Examination(
                    -1,
                    patientRecord.getEntity(),
                    device.getEntity(),
                    comment);
            examination.insert();

            samplesOfChannels.forEach(o -> o.setExamination(examination));
            SampleDAO.getInstance().beginTransaction();
        } catch (DomainException | DAOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void recordingStop() {
        examination = null;
        samplesOfChannels.forEach(o -> o.setExamination(examination));
        try {
            SampleDAO.getInstance().endTransaction();
        } catch (DAOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isRecording() {
        return flagTransmission && examination != null;
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
                examination.update();
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

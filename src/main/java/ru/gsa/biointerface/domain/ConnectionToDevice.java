package ru.gsa.biointerface.domain;

import com.fazecast.jSerialComm.SerialPort;
import ru.gsa.biointerface.domain.entity.ChannelEntity;
import ru.gsa.biointerface.domain.entity.ExaminationEntity;
import ru.gsa.biointerface.domain.serialPortHost.ControlMessages;
import ru.gsa.biointerface.domain.serialPortHost.DataCollector;
import ru.gsa.biointerface.domain.serialPortHost.Handler;
import ru.gsa.biointerface.domain.serialPortHost.SerialPortHost;
import ru.gsa.biointerface.persistence.DAOException;
import ru.gsa.biointerface.persistence.dao.SampleDAO;
import ru.gsa.biointerface.ui.window.ExaminationNew.ChannelController;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class ConnectionToDevice implements DataCollector, Connection {
    private final SerialPortHost serialPortHost;
    private final List<Channel> channels = new LinkedList<>();
    private Examination examination = new Examination(new ExaminationEntity());
    private boolean flagTransmission = false;

    public ConnectionToDevice(PatientRecord patientRecord, SerialPort serialPort) throws DomainException {
        if (serialPort == null)
            throw new NullPointerException("serialPort is null");
        if (patientRecord == null)
            throw new NullPointerException("examination is null");

        examination.setPatientRecord(patientRecord);
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
        return examination.getPatientRecord();
    }

    @Override
    public Device getDevice() {
        return examination.getDevice();
    }

    @Override
    public void setDevice(Device device) {
        if (device == null)
            throw new NullPointerException("device is null");

        examination.setDevice(device);

    }

    @Override
    public boolean isAvailableDevice() {
        return examination.getDevice() != null;
    }

    @Override
    public List<Channel> getSamplesOfChannels() {
        return channels;
    }

    @Override
    public void registerChannelGUIs(List<ChannelController> channelControllerGUIS) throws DomainException {
        if (channelControllerGUIS == null)
            throw new NullPointerException("channelGUIs is null");
        if (channelControllerGUIS.size() < examination.getAmountChannels())
            throw new DomainException("count of channelGUIs less than count of channels");

        channels.clear();

        for (int i = 0; i < examination.getAmountChannels(); i++) {
            String channelName = "Channel ".concat(String.valueOf(channels.size() + 1));
            Channel channel = new Channel(new ChannelEntity(channels.size(), examination.getEntity(), channelName));
            channels.add(channel);
        }

        for (int i = 0; i < examination.getAmountChannels(); i++) {
            channelControllerGUIS.get(i).setChannel(channels.get(i));
            channels.get(i).setListener(channelControllerGUIS.get(i));
        }
        setCapacity(10);
    }

    @Override
    public void setCapacity(int capacity) throws DomainException {
        if (examination.getDevice() == null)
            throw new DomainException("Device configuration empty");
        if (capacity == 0)
            throw new DomainException("capacity is '0'");

        channels.forEach(o -> o.setCapacity(capacity));
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
            examination.setComment(comment);
            examination.insert();

            channels.forEach(o -> {
                try {
                    o.setExaminationEntity(examination.getEntity());
                } catch (DomainException e) {
                    e.printStackTrace();
                }
            });
            SampleDAO.getInstance().beginTransaction();
        } catch (DomainException | DAOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void recordingStop() {
        examination.reset();

        channels.forEach(o -> {
            try {
                o.setExaminationEntity(examination.getEntity());
            } catch (DomainException e) {
                e.printStackTrace();
            }
        });
        try {
            SampleDAO.getInstance().endTransaction();
        } catch (DAOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isRecording() {
        return examination.getId() > 0;
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

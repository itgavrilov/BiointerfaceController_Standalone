package ru.gsa.biointerface.domain;

import com.fazecast.jSerialComm.SerialPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gsa.biointerface.domain.host.ControlMessages;
import ru.gsa.biointerface.domain.host.DataCollector;
import ru.gsa.biointerface.domain.host.Handler;
import ru.gsa.biointerface.domain.host.SerialPortHost;
import ru.gsa.biointerface.domain.host.dataCash.Cash;
import ru.gsa.biointerface.domain.host.dataCash.SampleCash;
import ru.gsa.biointerface.ui.window.metering.Connection;

import java.util.*;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class ConnectionToDevice implements DataCollector, Connection {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionToDevice.class);
    private final SerialPortHost serialPortHost;
    private Device device;
    private List<Cash> cashList;
    private List<Graph> graphList;
    private PatientRecord patientRecord;
    private Examination examination;
    private String comment;
    private boolean flagTransmission = false;
    private boolean flagRecording = false;

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

        cashList = new ArrayList<>();
        graphList = new ArrayList<>();

        for (int i = 0; i < device.getAmountChannels(); i++) {
            Graph graph = new Graph(i);
            Cash cash = new SampleCash();

            cash.addListener(graph);
            cashList.add(cash);
            graphList.add(graph);
        }
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
    public Cash getCash(int i) throws DomainException {
        if (device == null)
            throw new DomainException("Device null");
        if (patientRecord == null)
            throw new NullPointerException("PatientRecord is not init. First call setPatientRecord()");
        if(i >= cashList.size() || i < 0)
            throw new DomainException("I > amount cashList");

        return cashList.get(i);
    }

    @Override
    public Graph getGraph(int i) throws DomainException {
        if (device == null)
            throw new DomainException("Device null");
        if (patientRecord == null)
            throw new NullPointerException("PatientRecord is not init. First call setPatientRecord()");
        if(i >= graphList.size() || i < 0)
            throw new DomainException("I > amount graphList");

        return graphList.get(i);
    }

    @Override
    public boolean isConnected() {
        boolean result = false;

        if (serialPortHost != null)
            result = serialPortHost.isRunning();

        return result;
    }

    @Override
    public void disconnect() throws DomainException {
        if (serialPortHost != null) {
            try {
                transmissionStop();
                serialPortHost.stop();
                LOGGER.info("Disconnecting from device");
            } catch (Exception e) {
                throw new DomainException("SerialPortHost stop error", e);
            }
        }
    }

    @Override
    public void transmissionStart() throws DomainException {
        if (device == null)
            throw new DomainException("Device null");
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
        if(flagTransmission) {
            if (serialPortHost == null)
                throw new DomainException("Server is not initialized (null)");

            flagTransmission = false;
            serialPortHost.sendPackage(ControlMessages.STOP_TRANSMISSION);
            LOGGER.info("Stop transmission");
        }
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
        if (flagRecording)
            throw new DomainException("Recording is already in progress");

        device.insert();

        examination = new Examination(patientRecord, device, graphList, comment);
        examination.recordingStart();

        flagRecording = true;
        LOGGER.info("Start recording");
    }

    @Override
    public void recordingStop() throws DomainException {
        if(flagRecording) {
            try {
                examination.recordingStop();
                examination = null;
                flagRecording = false;
                LOGGER.info("Stop recording");
            } catch (DomainException e) {
                throw new DomainException("RecordingStop error");
            }
        }
    }

    @Override
    public boolean isRecording() {
        return flagRecording;
    }

    @Override
    public void controllerReboot() throws DomainException {
        if (serialPortHost == null)
            throw new NullPointerException("Server is not initialized (null)");

        serialPortHost.sendPackage(ControlMessages.REBOOT);
        recordingStop();
        transmissionStop();
        LOGGER.info("Reboot controller");
    }

    @Override
    public void changeCommentOnExamination(String comment) {
        this.comment = comment;
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

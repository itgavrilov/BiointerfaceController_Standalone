package ru.gsa.biointerface.domain;

import com.fazecast.jSerialComm.SerialPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gsa.biointerface.domain.entity.ExaminationEntity;
import ru.gsa.biointerface.domain.host.ControlMessages;
import ru.gsa.biointerface.domain.host.DataCollector;
import ru.gsa.biointerface.domain.host.Handler;
import ru.gsa.biointerface.domain.host.SerialPortHost;
import ru.gsa.biointerface.persistence.PersistenceException;
import ru.gsa.biointerface.persistence.dao.SampleDAO;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class ConnectionToDevice implements DataCollector, Connection {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionToDevice.class);
    private final SerialPortHost serialPortHost;
    private final List<Graph> graphs = new LinkedList<>();
    private final Examination examination = new Examination(new ExaminationEntity());
    private boolean flagTransmission = false;

    public ConnectionToDevice(PatientRecord patientRecord, SerialPort serialPort) throws DomainException {
        if (serialPort == null)
            throw new NullPointerException("SerialPort is null");
        if (patientRecord == null)
            throw new NullPointerException("Examination is null");

        examination.setPatientRecord(patientRecord);
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
        graphs.clear();
        for (int i = 0; i < device.getAmountChannels(); i++) {
            Graph graph = new Graph(i, examination.getEntity(), null);
            graphs.add(graph);
        }
    }

    @Override
    public boolean isAvailableDevice() {
        return examination.getDevice() != null;
    }

    @Override
    public List<Graph> getGraphs() {
        return graphs;
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
                controllerTransmissionStop();
                serialPortHost.stop();
            } catch (Exception e) {
                throw new DomainException("SerialPortHost stop error", e);
            }
        }
    }

    @Override
    public void controllerTransmissionStart() throws DomainException {
        if (serialPortHost == null)
            throw new DomainException("Server is not initialized (null)");
        if (!serialPortHost.isRunning())
            throw new DomainException("Server is not running");

        flagTransmission = true;
        serialPortHost.sendPackage(ControlMessages.START_TRANSMISSION);
        LOGGER.info("Start transmission");
    }

    @Override
    public void controllerTransmissionStop() throws DomainException {
        if (serialPortHost == null)
            throw new DomainException("Server is not initialized (null)");

        flagTransmission = false;
        serialPortHost.sendPackage(ControlMessages.STOP_TRANSMISSION);
        LOGGER.info("Stop transmission");
    }

    @Override
    public boolean isControllerTransmission() {
        return flagTransmission;
    }

    @Override
    public void recordingStart(String comment) throws DomainException {
        examination.setComment(comment);
        examination.insert();

        for(Graph graph: graphs){
            graph.setExamination(examination);
            graph.insert();
        }

        try {
            SampleDAO.getInstance().beginTransaction();
            LOGGER.info("Start recording");
        } catch (PersistenceException e) {
            throw new DomainException("BeginTransaction error", e);
        }
    }

    @Override
    public void recordingStop() throws DomainException {
        examination.reset();

        graphs.forEach(o -> o.setExamination(examination));
        LOGGER.info("Stop recording");
        try {
            SampleDAO.getInstance().endTransaction();
        } catch (PersistenceException e) {
            throw new DomainException("EndTransaction error", e);
        }
    }

    @Override
    public boolean isRecording() {
        return examination.getId() > 0;
    }

    @Override
    public void controllerReboot() {
        if (serialPortHost == null)
            throw new NullPointerException("Server is not initialized (null)");

        flagTransmission = false;
        serialPortHost.sendPackage(ControlMessages.REBOOT);
        LOGGER.info("Reboot controller");
    }

    @Override
    public void changeCommentOnExamination(String comment) {
        if (comment == null)
            throw new NullPointerException("Comment is null");

        if (!comment.equals(examination.getComment())) {
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

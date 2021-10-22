package ru.gsa.biointerface.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gsa.biointerface.domain.entity.*;
import ru.gsa.biointerface.persistence.PersistenceException;
import ru.gsa.biointerface.persistence.dao.ExaminationDAO;
import ru.gsa.biointerface.persistence.dao.GraphDAO;
import ru.gsa.biointerface.persistence.dao.SampleDAO;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class ServiceExamination {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceExamination.class);
    private static ServiceExamination instance = null;
    private final ExaminationDAO dao;
    private final GraphDAO daoGraph;
    private final SampleDAO daoSample;

    public static ServiceExamination getInstance() throws ServiceException {
        if (instance == null) {
            instance = new ServiceExamination();
        }

        return instance;
    }

    private ServiceExamination() throws ServiceException {
        try {
            dao = ExaminationDAO.getInstance();
            daoGraph = GraphDAO.getInstance();
            daoSample = SampleDAO.getInstance();
        } catch (PersistenceException e) {
            throw new ServiceException("Error connection to database", e);
        }
    }

    public Examination create(PatientRecord patientRecord, Device device, List<Channel> channels, String comment) {
        if (patientRecord == null)
            throw new NullPointerException("PatientRecord is null");
        if (device == null)
            throw new NullPointerException("Device is null");
        if (channels == null)
            throw new NullPointerException("Channels is null");
        if (channels.size() != device.getAmountChannels())
            throw new IllegalArgumentException("Amount channels differs from amount in device");

        Examination entity = new Examination(
                0,
                Timestamp.valueOf(LocalDateTime.now()),
                patientRecord,
                device,
                comment,
                new ArrayList<>()
        );

        for (int i = 0; i < device.getAmountChannels(); i++) {
            entity.getGraphs().add(new Graph(i, entity, channels.get(i), new LinkedList<>()));
        }

        return entity;
    }

    public List<Examination> getByPatientRecord(PatientRecord patientRecord) throws ServiceException {
        if (patientRecord == null)
            throw new NullPointerException("PatientRecord is null");

        try {
            return dao.getByPatientRecord(patientRecord);
        } catch (PersistenceException e) {
            throw new ServiceException("DAO getByPatientRecordId error");
        }
    }

    public List<Examination> getAll() throws ServiceException {
        try {
            return dao.getAll();
        } catch (PersistenceException e) {
            throw new ServiceException("Get all error", e);
        }
    }

    public Examination getById(long id) throws ServiceException {
        try {
            return dao.read(id);
        } catch (PersistenceException e) {
            throw new ServiceException("Get by id error", e);
        }
    }

    public void delete(Examination entity) throws ServiceException {
        try {
            dao.delete(entity);
            LOGGER.info("{} is deleted in the database", entity);
        } catch (PersistenceException e) {
            throw new ServiceException("Delete error");
        }
    }

    public void update(Examination entity) throws ServiceException {
        if (entity.getPatientRecord() == null)
            throw new NullPointerException("PatientRecord is null");
        if (entity.getDevice() == null)
            throw new NullPointerException("Device is null");

        try {
            dao.update(entity);
            LOGGER.info("{} updated in database", entity);
        } catch (PersistenceException e) {
            throw new ServiceException("Update error", e);
        }
    }

    public void recordingStart(Examination entity) throws ServiceException {
        if (entity.getPatientRecord() == null)
            throw new ServiceException("PatientRecord is null");
        if (entity.getDevice() == null)
            throw new ServiceException("Device is null");

        try {
            dao.transactionStart();
        } catch (PersistenceException e) {
            throw new ServiceException("TransactionStart error");
        }

        try {
            dao.insert(entity);
            entity.recordingStart();
            LOGGER.info("{} is recorded in database", entity);
        } catch (PersistenceException e) {
            throw new ServiceException("DAO insert " + entity + " error");
        }

    }

    public void recordingStop(Examination entity) {
        entity.recordingStop();
        try {
            dao.transactionStop();
        } catch (PersistenceException e) {
            e.printStackTrace();
        }
    }

    public Examination loadFromDatabaseWithGraphsById(Long id) throws ServiceException {
        Examination examination = getById(id);
        try {
            examination.setGraphs(daoGraph.getAllByExamination(examination));
            for (Graph graph: examination.getGraphs()){
                graph.setSamples(daoSample.getAllByGraph(graph));
            }

        } catch (PersistenceException e) {
            e.printStackTrace();
        }

        return examination;
    }
}

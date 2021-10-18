package ru.gsa.biointerface.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gsa.biointerface.domain.entity.ExaminationEntity;
import ru.gsa.biointerface.domain.entity.GraphEntity;
import ru.gsa.biointerface.domain.entity.PatientRecordEntity;
import ru.gsa.biointerface.persistence.PersistenceException;
import ru.gsa.biointerface.persistence.dao.ExaminationDAO;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class Examination implements Comparable<Examination> {
    private static final Logger LOGGER = LoggerFactory.getLogger(Examination.class);
    private final ExaminationEntity entity;


    static public Set<Examination> getAll() throws DomainException {
        try {
            List<ExaminationEntity> entities = ExaminationDAO.getInstance().getAll();
            Set<Examination> result = new TreeSet<>();
            entities.forEach(o -> result.add(new Examination(o)));
            return result;
        } catch (PersistenceException e) {
            throw new DomainException("DAO getAll examinations error");
        }
    }

    static public Set<Examination> getByPatientRecordId(PatientRecordEntity patientRecordEntity) throws DomainException {
        try {
            List<ExaminationEntity> entities = ExaminationDAO.getInstance().getByPatientRecord(patientRecordEntity);
            Set<Examination> result = new TreeSet<>();
            entities.forEach(o -> result.add(new Examination(o)));
            return result;
        } catch (PersistenceException e) {
            throw new DomainException("DAO getByPatientRecordId examinations error");
        }
    }

    public Examination(ExaminationEntity examinationEntity) {
        entity = examinationEntity;
    }

    public Examination(PatientRecord patientRecord, Device device, List<Graph> graphList, String comment) {
        if(patientRecord == null)
            throw new NullPointerException("PatientRecord is null");
        if(device == null)
            throw new NullPointerException("Device is null");
        if(graphList == null)
            throw new NullPointerException("graphList is null");

        entity = new ExaminationEntity(-1, Timestamp.valueOf(LocalDateTime.now()), patientRecord.getEntity(), device.getEntity(), comment, new ArrayList<>());

        for(Graph graph: graphList){
            graph.setExamination(this);
            entity.getGraphEntities().add(graph.getEntity());
        }
    }

    public void update() throws DomainException {
        if (entity.getDeviceEntity() != null) {
            if (entity.getPatientRecordEntity() != null) {
                try {
                    ExaminationDAO.getInstance().update(entity);
                    LOGGER.info("{} is updated in database", entity);
                } catch (PersistenceException e) {
                    throw new DomainException("DAO update examination error");
                }
            } else {
                throw new DomainException("PatientRecordEntity is null");
            }
        } else {
            throw new DomainException("DeviceEntity is null");
        }
    }

    public void delete() throws DomainException {
        try {
            ExaminationDAO.getInstance().delete(entity);
            LOGGER.info("{} is deleted in the database", entity);
        } catch (PersistenceException e) {
            throw new DomainException("DAO delete examination error");
        }
    }

    public ExaminationEntity getEntity() {
        return entity;
    }

    public long getId() {
        return entity.getId();
    }

    public LocalDateTime getDateTime() {
        return entity.getDateTime().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    public PatientRecord getPatientRecord() {
        PatientRecord patientRecord = null;

        if (entity.getPatientRecordEntity() != null)
            patientRecord = new PatientRecord(entity.getPatientRecordEntity());

        return patientRecord;
    }

    public Device getDevice() {
        Device device = null;
        if (entity.getDeviceEntity() != null)
            device = new Device(entity.getDeviceEntity());

        return device;
    }

    public String getComment() {
        return entity.getComment();
    }

    public void setComment(String comment) {
        LOGGER.info("{} is update in {}", comment, entity);
        entity.setComment(comment);
    }

    public List<Graph> getGraphs() throws DomainException {
        if (entity.getDeviceEntity() == null)
            throw new DomainException("Device is null");
        if (entity.getGraphEntities().size() == 0)
            throw new DomainException("Graphs is null");

        List<Graph> graphs = new ArrayList<>();
        List<GraphEntity> graphEntities = entity.getGraphEntities();
        for (GraphEntity graphEntity: graphEntities){
            graphs.add(new Graph(graphEntity));
        }

        return graphs;
    }

    public void recordingStart() throws DomainException {
        if (entity.getPatientRecordEntity() == null)
            throw new DomainException("PatientRecordEntity in " + entity + " is null");
        if (entity.getDeviceEntity() == null)
            throw new DomainException("DeviceEntity in " + entity +" is null");

        try {
            ExaminationDAO.getInstance().beginTransaction();
            ExaminationDAO.getInstance().insert(entity);
            LOGGER.info("{} is recorded in database", entity);
        } catch (PersistenceException e) {
            throw new DomainException("DAO insert " + entity + " error");
        }
    }

    public void recordingStop() throws DomainException{
        try {
            ExaminationDAO.getInstance().endTransaction();
        } catch (PersistenceException e) {
            throw new DomainException("EndTransaction error");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Examination that = (Examination) o;
        return entity.equals(that.entity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entity);
    }

    @Override
    public int compareTo(Examination o) {
        return entity.compareTo(o.entity);
    }

    @Override
    public String toString() {
        return entity.toString();
    }
}

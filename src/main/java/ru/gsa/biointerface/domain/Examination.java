package ru.gsa.biointerface.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gsa.biointerface.domain.entity.*;
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
    private final ExaminationDAO dao;
    private boolean recordingStart = false;

    public Examination(ExaminationEntity entity) {
        this.entity = entity;

        try {
            dao = ExaminationDAO.getInstance();
        } catch (PersistenceException e) {
            e.printStackTrace();
            throw new NullPointerException("DAO is null");
        }
    }

    public Examination(PatientRecord patientRecord, Device device, List<Channel> channelList, String comment) {
        if (patientRecord == null)
            throw new NullPointerException("PatientRecord is null");
        if (device == null)
            throw new NullPointerException("Device is null");
        if (channelList == null)
            throw new NullPointerException("ChannelList is null");
        if (channelList.size() != device.getAmountChannels())
            throw new IllegalArgumentException("ChannelList size differs from amount in device");

        entity = new ExaminationEntity(
                -1,
                Timestamp.valueOf(LocalDateTime.now()),
                patientRecord.getEntity(),
                device.getEntity(),
                comment,
                new ArrayList<>());

        for (int i = 0; i < device.getAmountChannels(); i++) {
            ChannelEntity channelEntity = null;

            if (channelList.get(i) != null)
                channelEntity = channelList.get(i).getEntity();

            entity.getGraphEntities().add(new GraphEntity(i, entity, channelEntity, new LinkedList<>()));
        }

        try {
            dao = ExaminationDAO.getInstance();
        } catch (PersistenceException e) {
            e.printStackTrace();
            throw new NullPointerException("DAO is null");
        }
    }

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

    static public Set<Examination> getByPatientRecord(PatientRecord patientRecord) throws DomainException {
        if(patientRecord == null || patientRecord.getEntity() == null)
            throw new NullPointerException("patientRecord or patientRecord.getEntity() is null");

        try {
            List<ExaminationEntity> entities = ExaminationDAO.getInstance().getByPatientRecordEntity(patientRecord.getEntity());
            Set<Examination> result = new TreeSet<>();
            entities.forEach(o -> result.add(new Examination(o)));
            return result;
        } catch (PersistenceException e) {
            throw new DomainException("DAO getByPatientRecordId examinations error");
        }
    }

    public void delete() throws DomainException {
        try {
            dao.delete(entity);
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
        if (entity.getPatientRecordEntity() == null)
            throw new NullPointerException("PatientRecord is null");

        return new PatientRecord(entity.getPatientRecordEntity());
    }

    public Device getDevice() {
        if (entity.getDeviceEntity() == null)
            throw new NullPointerException("Device is null");

        return new Device(entity.getDeviceEntity());
    }

    public String getComment() {
        return entity.getComment();
    }

    public void setComment(String comment) throws DomainException {
        entity.setComment(comment);

        if (entity.getDeviceEntity() != null) {
            if (entity.getPatientRecordEntity() != null) {
                try {
                    ExaminationDAO.getInstance().update(entity);
                    LOGGER.info("{} is update in {}", comment, entity);
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

    public void recordingStart() throws DomainException {
        if (entity.getPatientRecordEntity() == null)
            throw new DomainException("PatientRecordEntity is null");
        if (entity.getDeviceEntity() == null)
            throw new DomainException("DeviceEntity is null");

        try {
            dao.transactionStart();
        } catch (PersistenceException e) {
            throw new DomainException("DAO beginTransaction error");
        }

        try {
            dao.insert(entity);
            LOGGER.info("{} is recorded in database", entity);
            recordingStart = true;
        } catch (PersistenceException e) {
            throw new DomainException("DAO insert " + entity + " error");
        }
    }

    public void recordingStop() {
        try {
            recordingStart = false;
            dao.transactionStop();
        } catch (PersistenceException e) {
            e.printStackTrace();
        }
    }

    public boolean isRecording() {
        return recordingStart;
    }

    public void setChannelInGraph(int numberOfChannel, Channel channel) {
        entity.getGraphEntities().get(numberOfChannel).setChannelEntity(channel.getEntity());
    }

    public void setNewSamplesInGraph(int numberOfChannel, int value) throws DomainException {
        if (numberOfChannel >= entity.getGraphEntities().size() || numberOfChannel < 0)
            throw new DomainException("NumberOfChannel > amount graphList size");

        GraphEntity graphEntity = entity.getGraphEntities().get(numberOfChannel);
        List<SampleEntity> entities = graphEntity.getSampleEntities();
        SampleEntity sampleEntity =
                new SampleEntity(
                        entities.size(),
                        graphEntity,
                        value
                );
        entities.add(entities.size(), sampleEntity);
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
        return entity.hashCode();
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

package ru.gsa.biointerface.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gsa.biointerface.domain.entity.DeviceEntity;
import ru.gsa.biointerface.domain.entity.ExaminationEntity;
import ru.gsa.biointerface.domain.entity.PatientRecordEntity;
import ru.gsa.biointerface.persistence.PersistenceException;
import ru.gsa.biointerface.persistence.dao.ExaminationDAO;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class Examination implements Comparable<Examination> {
    private static final Logger LOGGER = LoggerFactory.getLogger(Examination.class);
    private ExaminationEntity entity;

    public Examination(int id, PatientRecordEntity patientRecordEntity, DeviceEntity deviceEntity, String comment) {
        this(new ExaminationEntity(id, LocalDateTime.now(), patientRecordEntity, deviceEntity, comment));
    }

    public Examination(ExaminationEntity examinationEntity) {
        if (examinationEntity.getDateTime() == null)
            throw new NullPointerException("DateTime is null");

        entity = examinationEntity;
    }

    static public Set<Examination> getAll() throws DomainException {
        try {
            Set<ExaminationEntity> entities = ExaminationDAO.getInstance().getAll();
            Set<Examination> result = new TreeSet<>();
            entities.forEach(o -> result.add(new Examination(o)));
            return result;
        } catch (PersistenceException e) {
            throw new DomainException("DAO getAll examinations error");
        }
    }

    static public Set<Examination> getByPatientRecordId(PatientRecord patientRecord) throws DomainException {
        try {
            Set<ExaminationEntity> entities = ExaminationDAO.getInstance().getByPatientRecord(patientRecord.getEntity());
            Set<Examination> result = new TreeSet<>();
            entities.forEach(o -> result.add(new Examination(o)));
            return result;
        } catch (PersistenceException e) {
            throw new DomainException("DAO getByPatientRecordId examinations error");
        }
    }

    public void insert() throws DomainException {
        if (entity.getDeviceEntity() != null) {
            Device device = new Device(entity.getDeviceEntity());
            device.insert();

            if (entity.getPatientRecord() != null) {
                try {
                    entity.setDateTime(LocalDateTime.now());
                    entity = ExaminationDAO.getInstance().insert(entity);
                    LOGGER.info("Examination is recorded in database wish id '{}'", entity);
                } catch (PersistenceException e) {
                    throw new DomainException("DAO insert examination error");
                }
            } else {
                throw new DomainException("PatientRecordEntity is null");
            }
        } else {
            throw new DomainException("DeviceEntity is null");
        }
    }

    public void update() throws DomainException {
        if (entity.getDeviceEntity() != null) {
            if (entity.getPatientRecord() != null) {
                try {
                    ExaminationDAO.getInstance().update(entity);
                    LOGGER.info("Examination '{}' is updated in database", entity);
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
            LOGGER.info("Examination '{}' is deleted in the database", entity);
        } catch (PersistenceException e) {
            throw new DomainException("DAO delete examination error");
        }
    }

    public ExaminationEntity getEntity() {
        return entity;
    }

    public void reset() {
        entity.setId(-1);
    }

    public int getId() {
        return entity.getId();
    }

    public LocalDateTime getDateTime() {
        return entity.getDateTime();
    }

    public PatientRecord getPatientRecord() {
        PatientRecord patientRecord = null;

        if (entity.getPatientRecord() != null)
            patientRecord = new PatientRecord(entity.getPatientRecord());

        return patientRecord;
    }

    public void setPatientRecord(PatientRecord patientRecord) {
        if (patientRecord == null)
            throw new NullPointerException("PatientRecord is null");

        LOGGER.info("Set PatientRecord '{}' in Examination '{}'", patientRecord, entity);
        entity.setPatientRecordEntity(patientRecord.getEntity());
    }

    public Device getDevice() {
        Device device = null;
        if (entity.getDeviceEntity() != null)
            device = new Device(entity.getDeviceEntity());

        return device;
    }

    public void setDevice(Device device) {
        if (device == null)
            throw new NullPointerException("Device is null");

        LOGGER.info("Set Device '{}' in Examination '{}'", device, entity);
        entity.setDeviceEntity(device.getEntity());
    }

    public int getAmountChannels() {
        return entity.getDeviceEntity().getAmountChannels();
    }

    public String getComment() {
        return entity.getComment();
    }

    public void setComment(String comment) {
        LOGGER.info("Comment '{}' is update in the Examination '{}'", comment, entity);
        entity.setComment(comment);
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

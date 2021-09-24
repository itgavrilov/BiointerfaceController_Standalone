package ru.gsa.biointerface.domain;

import ru.gsa.biointerface.domain.entity.DeviceEntity;
import ru.gsa.biointerface.domain.entity.ExaminationEntity;
import ru.gsa.biointerface.domain.entity.PatientRecordEntity;
import ru.gsa.biointerface.persistence.DAOException;
import ru.gsa.biointerface.persistence.dao.ExaminationDAO;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class Examination implements Comparable<Examination> {
    private ExaminationEntity entity;

    public Examination(int id, PatientRecordEntity patientRecordEntity, DeviceEntity deviceEntity, String comment) {
        this(new ExaminationEntity(id, LocalDateTime.now(), patientRecordEntity, deviceEntity, comment));
    }

    public Examination(ExaminationEntity examinationEntity) {
        if (examinationEntity.getDateTime() == null)
            throw new NullPointerException("dateTime is null");

        entity = examinationEntity;
    }

    static public Set<Examination> getSetAll() throws DomainException {
        try {
            Set<ExaminationEntity> entities = ExaminationDAO.getInstance().getAll();
            Set<Examination> result = new TreeSet<>();
            entities.forEach(o -> result.add(new Examination(o)));
            return result;
        } catch (DAOException e) {
            e.printStackTrace();
            throw new DomainException("dao getAll examinations error");
        }
    }

    static public Set<Examination> getSetByPatientRecordId(PatientRecord patientRecord) throws DomainException {
        try {
            Set<ExaminationEntity> entities = ExaminationDAO.getInstance().getByPatientRecord(patientRecord.getEntity());
            Set<Examination> result = new TreeSet<>();
            entities.forEach(o -> result.add(new Examination(o)));
            return result;
        } catch (DAOException e) {
            e.printStackTrace();
            throw new DomainException("dao getByPatientRecordId examinations error");
        }
    }

    public void insert() throws DomainException {
        if(entity.getDeviceEntity() != null) {
            Device device = new Device(entity.getDeviceEntity());
            device.insert();

            if(entity.getPatientRecord() != null) {
                PatientRecord patientRecord = new PatientRecord(entity.getPatientRecord());

                try {
                    entity.setPatientRecordEntity(patientRecord.getEntity());
                    entity.setDeviceEntity(device.getEntity());
                    entity.setDateTime(LocalDateTime.now());
                    entity = ExaminationDAO.getInstance().insert(entity);
                } catch (DAOException e) {
                    e.printStackTrace();
                    throw new DomainException("dao insert examination error");
                }
            } else {
                throw new DomainException("patientRecordEntity is null");
            }
        } else {
            throw new DomainException("deviceEntity is null");
        }
    }

    public void update() throws DomainException {
        if(entity.getDeviceEntity() != null) {
            Device device = new Device(entity.getDeviceEntity());

            if(entity.getPatientRecord() != null) {
                PatientRecord patientRecord = new PatientRecord(entity.getPatientRecord());

                try {
                    entity.setPatientRecordEntity(patientRecord.getEntity());
                    entity.setDeviceEntity(device.getEntity());
                    ExaminationDAO.getInstance().update(entity);
                } catch (DAOException e) {
                    e.printStackTrace();
                    throw new DomainException("dao update examination error");
                }
            } else {
                throw new DomainException("patientRecordEntity is null");
            }
        } else {
            throw new DomainException("deviceEntity is null");
        }
    }

    public void delete() throws DomainException {
        try {
            ExaminationDAO.getInstance().delete(entity);
        } catch (DAOException e) {
            e.printStackTrace();
            throw new DomainException("dao delete examination error");
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

    public void setPatientRecord(PatientRecord patientRecord) {
        if (patientRecord == null)
            throw new NullPointerException("patientRecord is null");

        entity.setPatientRecordEntity(patientRecord.getEntity());
    }

    public PatientRecord getPatientRecord() {
        PatientRecord patientRecord = null;

        if(entity.getPatientRecord() != null)
            patientRecord = new PatientRecord(entity.getPatientRecord());

        return patientRecord;
    }

    public void setDevice(Device device) {
        if (device == null)
            throw new NullPointerException("device is null");

        entity.setDeviceEntity(device.getEntity());
    }

    public Device getDevice() {
        Device device = null;
        if(entity.getDeviceEntity() != null)
            device = new Device(entity.getDeviceEntity());

        return device;
    }

    public int getAmountChannels() {
        return entity.getDeviceEntity().getAmountChannels();
    }

    public String getComment() {
        return entity.getComment();
    }

    public void setComment(String comment) {
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
}

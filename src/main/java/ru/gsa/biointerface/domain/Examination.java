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

public class Examination implements Comparable<Examination> {
    private ExaminationEntity entity;
    private PatientRecord patientRecord;
    private Device device;

    public Examination(ExaminationEntity examinationEntity) {
        if (examinationEntity.getDateTime() == null)
            throw new NullPointerException("dateTime is null");

        entity = examinationEntity;
        this.patientRecord = new PatientRecord(entity.getPatientRecord());
        this.device = new Device(entity.getDevice());
    }

    public Examination(int id, PatientRecordEntity patientRecordEntity, DeviceEntity deviceEntity, String comment) {
        entity = new ExaminationEntity(id, LocalDateTime.now(), patientRecordEntity, deviceEntity, comment);
        this.patientRecord = new PatientRecord(entity.getPatientRecord());
        this.device = new Device(entity.getDevice());
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
        try {
            entity = ExaminationDAO.getInstance().insert(entity);
        } catch (DAOException e) {
            e.printStackTrace();
            throw new DomainException("dao insert examination error");
        }
    }

    public void update() throws DomainException {
        try {
            ExaminationDAO.getInstance().update(entity);
        } catch (DAOException e) {
            e.printStackTrace();
            throw new DomainException("dao update examination error");
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

    public int getId() {
        return entity.getId();
    }

    public LocalDateTime getDateTime() {
        return entity.getDateTime();
    }

    public PatientRecord getPatientRecord() {
        return patientRecord;
    }

    public void setPatientRecord(PatientRecord patientRecord) {
        if (patientRecord == null)
            throw new NullPointerException("patientRecord is null");

        this.patientRecord = patientRecord;
        entity.setPatientRecord(patientRecord.getEntity());
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        if (device == null)
            throw new NullPointerException("device is null");

        this.device = device;
        entity.setDevice(device.getEntity());
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

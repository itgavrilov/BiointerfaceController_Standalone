package ru.gsa.biointerface.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gsa.biointerface.domain.entity.IcdEntity;
import ru.gsa.biointerface.domain.entity.PatientRecordEntity;
import ru.gsa.biointerface.persistence.PersistenceException;
import ru.gsa.biointerface.persistence.dao.PatientRecordDAO;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class PatientRecord implements Comparable<PatientRecord> {
    private static final Logger LOGGER = LoggerFactory.getLogger(PatientRecord.class);
    private final PatientRecordEntity entity;

    public PatientRecord(int id, String secondName, String firstName, String middleName, LocalDate birthday, IcdEntity icdEntity, String comment) {
        this(new PatientRecordEntity(id, secondName, firstName, middleName, birthday, icdEntity, comment));
    }

    public PatientRecord(PatientRecordEntity patientRecordEntity) {
        if (patientRecordEntity.getId() == 0)
            throw new NullPointerException("Id is null");
        if (patientRecordEntity.getSecondName() == null)
            throw new NullPointerException("SecondName is null");
        if (patientRecordEntity.getFirstName() == null)
            throw new NullPointerException("FirstName is null");
        if (patientRecordEntity.getMiddleName() == null)
            throw new NullPointerException("MiddleName is null");
        if (patientRecordEntity.getBirthday() == null)
            throw new NullPointerException("Birthday is null");

        entity = patientRecordEntity;
    }

    static public Set<PatientRecord> getSetAll() throws DomainException {
        try {
            Set<PatientRecordEntity> entitys = PatientRecordDAO.getInstance().getAll();
            Set<PatientRecord> result = new TreeSet<>();
            entitys.forEach(o -> result.add(new PatientRecord(o)));
            return result;
        } catch (PersistenceException e) {
            throw new DomainException("DAO getAll patientRecords error");
        }
    }

    public void insert() throws DomainException {
        try {
            PatientRecordDAO.getInstance().insert(entity);
            LOGGER.info("PatientRecord '{}' is recorded in database", entity);
        } catch (PersistenceException e) {
            e.printStackTrace();
            throw new DomainException("DAO insert patientRecord error");
        }
    }

    public void update() throws DomainException {
        try {
            PatientRecordDAO.getInstance().update(entity);
            LOGGER.info("PatientRecord '{}' is update in database", entity);
        } catch (PersistenceException e) {
            e.printStackTrace();
            throw new DomainException("DAO update patientRecord error");
        }
    }

    public void delete() throws DomainException {
        try {
            PatientRecordDAO.getInstance().delete(entity);
            LOGGER.info("PatientRecord '{}' is deleted in database", entity);
        } catch (PersistenceException e) {
            e.printStackTrace();
            throw new DomainException("dao delete patientRecord error");
        }
    }

    public PatientRecordEntity getEntity() {
        return entity;
    }

    public int getId() {
        return entity.getId();
    }

    public String getSecondName() {
        return entity.getSecondName();
    }

    public String getFirstName() {
        return entity.getFirstName();
    }

    public String getMiddleName() {
        return entity.getMiddleName();
    }

    public LocalDate getBirthday() {
        return entity.getBirthday();
    }

    public Icd getIcd() {
        Icd icd = null;
        if (entity.getIcdEntity() != null)
            icd = new Icd(entity.getIcdEntity());

        return icd;
    }

    public void setIcd(Icd icd) {
        if (icd != null) {
            LOGGER.info("ICD '{}' is set in patientRecord '{}'", icd, entity);
            entity.setIcd(icd.getEntity());
        } else {
            LOGGER.info("ICD is deleted in patientRecord '{}'", entity);
            entity.setIcd(null);
        }
    }

    public String getComment() {
        return entity.getComment();
    }

    public void setComment(String comment) {
        LOGGER.info("Comment '{}' is update in patientRecord '{}'", comment, entity);
        entity.setComment(comment);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PatientRecord that = (PatientRecord) o;
        return entity.equals(that.entity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entity);
    }

    @Override
    public int compareTo(PatientRecord o) {
        return entity.compareTo(o.entity);
    }

    @Override
    public String toString() {
        return entity.toString();
    }
}

package ru.gsa.biointerface.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gsa.biointerface.domain.entity.PatientRecordEntity;
import ru.gsa.biointerface.persistence.PersistenceException;
import ru.gsa.biointerface.persistence.dao.PatientRecordDAO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class PatientRecord implements Comparable<PatientRecord> {
    private static final Logger LOGGER = LoggerFactory.getLogger(PatientRecord.class);
    private final PatientRecordEntity entity;

    public PatientRecord(int id, String secondName, String firstName, String middleName, LocalDate birthday, Icd icd, String comment) throws DomainException {
        if (id <= 0)
            throw new NullPointerException("Id <= 0");
        if (secondName == null)
            throw new NullPointerException("SecondName is null");
        if ("".equals(secondName))
            throw new NullPointerException("SecondName is empty");
        if (firstName == null)
            throw new NullPointerException("FirstName is null");
        if ("".equals(firstName))
            throw new NullPointerException("FirstName is empty");
        if (middleName == null)
            throw new NullPointerException("MiddleName is null");
        if ("".equals(middleName))
            throw new NullPointerException("MiddleName is empty");
        if (birthday == null)
            throw new NullPointerException("Birthday is null");
        if (icd == null)
            throw new NullPointerException("icd is null");

        try {
            PatientRecordEntity readEntity = PatientRecordDAO.getInstance().read(id);
            if (readEntity == null) {
                entity = new PatientRecordEntity(
                        id,
                        secondName,
                        firstName,
                        middleName,
                        localDateToDate(birthday),
                        icd.getEntity(),
                        comment
                );
                PatientRecordDAO.getInstance().insert(entity);
                LOGGER.info("{} is recorded in database", entity);
            } else {
                entity = readEntity;
            }

        } catch (PersistenceException e) {
            throw new DomainException("DAO insert patientRecord error");
        }
    }

    public PatientRecord(PatientRecordEntity entity) {
        this.entity = entity;
    }

    private static Calendar localDateToDate(LocalDate localDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        //noinspection MagicConstant
        calendar.set(
                localDate.getYear(),
                localDate.getMonthValue() - 1,
                localDate.getDayOfMonth()
        );
        return calendar;
    }

    private static LocalDate dateToLocalDate(Calendar calendar) {
        return LocalDateTime.ofInstant(calendar.toInstant(), ZoneId.systemDefault()).toLocalDate();
    }

    static public Set<PatientRecord> getAll() throws DomainException {
        try {
            List<PatientRecordEntity> entitys = PatientRecordDAO.getInstance().getAll();
            Set<PatientRecord> result = new TreeSet<>();
            entitys.forEach(o -> result.add(new PatientRecord(o)));
            return result;
        } catch (PersistenceException e) {
            throw new DomainException("DAO getAll patientRecords error");
        }
    }

    public void delete() throws DomainException {
        try {
            PatientRecordDAO.getInstance().delete(entity);
            LOGGER.info("{} is deleted in database", entity);
        } catch (PersistenceException e) {
            throw new DomainException("DAO delete patientRecord error");
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
        return dateToLocalDate(entity.getBirthday());
    }

    public Icd getIcd() {
        Icd icd = null;
        if (entity.getIcdEntity() != null)
            icd = new Icd(entity.getIcdEntity());

        return icd;
    }

    public void setIcd(Icd icd) throws DomainException {

        try {
            if (icd != null) {
                entity.setIcdEntity(icd.getEntity());
                PatientRecordDAO.getInstance().update(entity);
                LOGGER.info("{} is set in {}", icd, entity);
            } else {
                entity.setIcdEntity(null);
                PatientRecordDAO.getInstance().update(entity);
                LOGGER.info("ICD is deleted in {}", entity);
            }
        } catch (PersistenceException e) {
            throw new DomainException("DAO update patientRecord error");
        }
    }

    public String getComment() {
        return entity.getComment();
    }

    public void setComment(String comment) throws DomainException {
        entity.setComment(comment);
        try {
            PatientRecordDAO.getInstance().update(entity);
            LOGGER.info("{} is update in {}", comment, entity);
        } catch (PersistenceException e) {
            throw new DomainException("DAO update patientRecord error");
        }
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
        return entity.hashCode();
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

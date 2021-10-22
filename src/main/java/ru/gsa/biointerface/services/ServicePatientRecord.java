package ru.gsa.biointerface.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gsa.biointerface.domain.entity.Icd;
import ru.gsa.biointerface.domain.entity.PatientRecord;
import ru.gsa.biointerface.persistence.PersistenceException;
import ru.gsa.biointerface.persistence.dao.IcdDAO;
import ru.gsa.biointerface.persistence.dao.PatientRecordDAO;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class ServicePatientRecord {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServicePatientRecord.class);
    private static ServicePatientRecord instance = null;
    private final PatientRecordDAO dao;

    public static ServicePatientRecord getInstance() throws ServiceException {
        if (instance == null) {
            instance = new ServicePatientRecord();
        }

        return instance;
    }

    private ServicePatientRecord() throws ServiceException {
        try {
            dao = PatientRecordDAO.getInstance();
        } catch (PersistenceException e) {
            throw new ServiceException("Error connection to database", e);
        }
    }

    public PatientRecord create(int id, String secondName, String firstName, String middleName, LocalDate birthday, Icd icd, String comment) {
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

        return new PatientRecord(
                id,
                secondName,
                firstName,
                middleName,
                localDateToDate(birthday),
                icd,
                comment
        );
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

    public List<PatientRecord> getAll() throws ServiceException {
        try {
            return dao.getAll();
        } catch (PersistenceException e) {
            throw new ServiceException("Get all error", e);
        }
    }

    public PatientRecord getById(long id) throws ServiceException {
        try {
            return dao.read(id);
        } catch (PersistenceException e) {
            throw new ServiceException("Get all error", e);
        }
    }

    public void save(PatientRecord entity) throws ServiceException {
        if (entity.getId() <= 0)
            throw new NullPointerException("Id <= 0");
        if (entity.getSecondName() == null)
            throw new NullPointerException("SecondName is null");
        if ("".equals(entity.getSecondName()))
            throw new NullPointerException("SecondName is empty");
        if (entity.getFirstName() == null)
            throw new NullPointerException("FirstName is null");
        if ("".equals(entity.getFirstName()))
            throw new NullPointerException("FirstName is empty");
        if (entity.getMiddleName() == null)
            throw new NullPointerException("MiddleName is null");
        if ("".equals(entity.getMiddleName()))
            throw new NullPointerException("MiddleName is empty");
        if (entity.getBirthday() == null)
            throw new NullPointerException("Birthday is null");

        try {
            PatientRecord readEntity = dao.read(entity.getId());

            if (readEntity == null) {
                dao.insert(entity);
                LOGGER.info("{} is recorded in database", entity);
            }
        } catch (PersistenceException e) {
            throw new ServiceException("Save error");
        }
    }

    public void delete(PatientRecord entity) throws ServiceException {
        try {
            PatientRecord readEntity = dao.read(entity.getId());

            if(readEntity != null) {
                dao.delete(entity);
                LOGGER.info("{} is deleted in database", entity);
            }
        } catch (PersistenceException e) {
            throw new ServiceException("Delete error", e);
        }
    }

    public void update(PatientRecord entity) throws ServiceException {
        if (entity.getId() <= 0)
            throw new NullPointerException("Id <= 0");
        if (entity.getSecondName() == null)
            throw new NullPointerException("SecondName is null");
        if ("".equals(entity.getSecondName()))
            throw new NullPointerException("SecondName is empty");
        if (entity.getFirstName() == null)
            throw new NullPointerException("FirstName is null");
        if ("".equals(entity.getFirstName()))
            throw new NullPointerException("FirstName is empty");
        if (entity.getMiddleName() == null)
            throw new NullPointerException("MiddleName is null");
        if ("".equals(entity.getMiddleName()))
            throw new NullPointerException("MiddleName is empty");
        if (entity.getBirthday() == null)
            throw new NullPointerException("Birthday is null");

        try {
            dao.update(entity);
            LOGGER.info("{} updated in database", entity);
        } catch (PersistenceException e) {
            throw new ServiceException("Update error", e);
        }
    }
}

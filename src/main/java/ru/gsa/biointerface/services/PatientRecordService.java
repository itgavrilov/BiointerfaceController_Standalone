package ru.gsa.biointerface.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gsa.biointerface.domain.entity.Icd;
import ru.gsa.biointerface.domain.entity.PatientRecord;
import ru.gsa.biointerface.repository.PatientRecordRepository;
import ru.gsa.biointerface.repository.exception.*;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class PatientRecordService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PatientRecordService.class);
    private static PatientRecordService instance = null;
    private final PatientRecordRepository dao;

    public static PatientRecordService getInstance() throws NoConnectionException {
        if (instance == null) {
            instance = new PatientRecordService();
        }

        return instance;
    }

    private PatientRecordService() throws NoConnectionException {
        dao = PatientRecordRepository.getInstance();
    }

    public PatientRecord create(
            int id,
            String secondName,
            String firstName,
            String middleName,
            LocalDate birthday,
            Icd icd,
            String comment
    )  throws Exception {
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

        PatientRecord entity = new PatientRecord(
                id,
                secondName,
                firstName,
                middleName,
                localDateToDate(birthday),
                icd,
                comment
        );
        LOGGER.info("New patient record created");

        return entity;
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

    public List<PatientRecord> getAll() throws Exception {
        List<PatientRecord> entities = dao.getAll();

        if(entities.size() > 0) {
            LOGGER.info("Get all patientRecords from database");
        } else {
            LOGGER.info("PatientRecords is not found in database");
        }

        return entities;
    }

    public PatientRecord getById(long id) throws Exception {
        PatientRecord entity = dao.read(id);

        if(entity != null) {
            LOGGER.info("Get patientRecord(id={}) from database", entity.getId());
        } else {
            LOGGER.error("PatientRecord(id={}) is not found in database", id);
            throw new NoSuchElementException(
                    "PatientRecord(id=" + id + ") is not found in database"
            );
        }

        return entity;
    }

    public void save(PatientRecord entity) throws Exception {
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

        PatientRecord readEntity = dao.read(entity.getId());

        if (readEntity == null) {
            dao.insert(entity);
            LOGGER.info("PatientRecord(id={}) is recorded in database", entity.getId());
        } else {
            LOGGER.error("PatientRecord(id={}) already exists in database", entity.getId());
            throw new IllegalArgumentException(
                    "PatientRecord(id=" + entity.getId() + ") already exists in database"
            );
        }
    }

    public void delete(PatientRecord entity)  throws Exception {
        PatientRecord readEntity = dao.read(entity.getId());

        if(readEntity != null) {
            dao.delete(entity);
            LOGGER.info("PatientRecord(id={}) is deleted in database", entity.getId());
        } else {
            LOGGER.error("PatientRecord(id={}) not found in database", entity.getId());
            throw new NoSuchElementException(
                    "PatientRecord(id=" + entity.getId() + ") not found in database"
            );
        }
    }

    public void update(PatientRecord entity) throws Exception {
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

        PatientRecord readEntity = dao.read(entity.getId());

        if(readEntity != null) {
            dao.update(entity);
            LOGGER.info("PatientRecord(id={}) updated in database", entity.getId());
        } else {
            LOGGER.error("PatientRecord(id={}) not found in database", entity.getId());
            throw new NoSuchElementException("PatientRecord(id=" + entity.getId() + ") not found in database");
        }
    }
}

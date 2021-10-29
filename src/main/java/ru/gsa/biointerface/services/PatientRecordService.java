package ru.gsa.biointerface.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gsa.biointerface.domain.entity.PatientRecord;
import ru.gsa.biointerface.repository.PatientRecordRepository;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class PatientRecordService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PatientRecordService.class);
    private static PatientRecordService instance = null;
    private final PatientRecordRepository dao;

    private PatientRecordService() throws Exception {
        dao = PatientRecordRepository.getInstance();
    }

    public static PatientRecordService getInstance() throws Exception {
        if (instance == null) {
            instance = new PatientRecordService();
        }

        return instance;
    }

    public List<PatientRecord> getAll() throws Exception {
        List<PatientRecord> entities = dao.getAll();

        if (entities.size() > 0) {
            LOGGER.info("Get all patientRecords from database");
        } else {
            LOGGER.info("PatientRecords is not found in database");
        }

        return entities;
    }

    public PatientRecord getById(long id) throws Exception {
        if (id <= 0)
            throw new IllegalArgumentException("Id <= 0");

        PatientRecord entity = dao.read(id);

        if (entity != null) {
            LOGGER.info("Get patientRecord(id={}) from database", entity.getId());
        } else {
            LOGGER.error("PatientRecord(id={}) is not found in database", id);
            throw new EntityNotFoundException(
                    "PatientRecord(id=" + id + ") is not found in database"
            );
        }

        return entity;
    }

    public void save(PatientRecord entity) throws Exception {
        if (entity == null)
            throw new NullPointerException("Entity is null");
        if (entity.getId() <= 0)
            throw new IllegalArgumentException("Id <= 0");
        if (entity.getSecondName() == null)
            throw new NullPointerException("SecondName is null");
        if (entity.getSecondName().isBlank())
            throw new IllegalArgumentException("SecondName is blank");
        if (entity.getFirstName() == null)
            throw new NullPointerException("FirstName is null");
        if (entity.getFirstName().isBlank())
            throw new IllegalArgumentException("FirstName is blank");
        if (entity.getMiddleName() == null)
            throw new NullPointerException("MiddleName is null");
        if (entity.getMiddleName().isBlank())
            throw new IllegalArgumentException("MiddleName is blank");
        if (entity.getBirthday() == null)
            throw new NullPointerException("Birthday is null");
        if (entity.getExaminations() == null)
            throw new NullPointerException("Examinations is null");

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

    public void delete(PatientRecord entity) throws Exception {
        if (entity == null)
            throw new NullPointerException("Entity is null");
        if (entity.getId() <= 0)
            throw new IllegalArgumentException("Id <= 0");

        PatientRecord readEntity = dao.read(entity.getId());

        if (readEntity != null) {
            dao.delete(entity);
            LOGGER.info("PatientRecord(id={}) is deleted in database", entity.getId());
        } else {
            LOGGER.error("PatientRecord(id={}) not found in database", entity.getId());
            throw new EntityNotFoundException(
                    "PatientRecord(id=" + entity.getId() + ") not found in database"
            );
        }
    }

    public void update(PatientRecord entity) throws Exception {
        if (entity == null)
            throw new NullPointerException("Entity is null");
        if (entity.getId() <= 0)
            throw new IllegalArgumentException("Id <= 0");
        if (entity.getSecondName() == null)
            throw new NullPointerException("SecondName is null");
        if (entity.getSecondName().isBlank())
            throw new IllegalArgumentException("SecondName is blank");
        if (entity.getFirstName() == null)
            throw new NullPointerException("FirstName is null");
        if (entity.getFirstName().isBlank())
            throw new IllegalArgumentException("FirstName is blank");
        if (entity.getMiddleName() == null)
            throw new NullPointerException("MiddleName is null");
        if (entity.getMiddleName().isBlank())
            throw new IllegalArgumentException("MiddleName is blank");
        if (entity.getBirthday() == null)
            throw new NullPointerException("Birthday is null");
        if (entity.getExaminations() == null)
            throw new NullPointerException("Examinations is null");

        PatientRecord readEntity = dao.read(entity.getId());

        if (readEntity != null) {
            dao.update(entity);
            LOGGER.info("PatientRecord(id={}) updated in database", entity.getId());
        } else {
            LOGGER.error("PatientRecord(id={}) not found in database", entity.getId());
            throw new NoSuchElementException("PatientRecord(id=" + entity.getId() + ") not found in database");
        }
    }
}

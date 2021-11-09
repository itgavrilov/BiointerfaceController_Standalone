package ru.gsa.biointerface.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gsa.biointerface.domain.entity.Patient;
import ru.gsa.biointerface.repository.PatientRepository;
import ru.gsa.biointerface.repository.impl.PatientRepositoryImpl;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class PatientService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PatientService.class);
    private static PatientService instance = null;
    private final PatientRepository repository;

    private PatientService() throws Exception {
        repository = PatientRepositoryImpl.getInstance();
    }

    public static PatientService getInstance() throws Exception {
        if (instance == null) {
            instance = new PatientService();
        }

        return instance;
    }

    public List<Patient> findAll() throws Exception {
        List<Patient> entities = repository.findAll();

        if (entities.size() > 0) {
            LOGGER.info("Get all patientRecords from database");
        } else {
            LOGGER.info("PatientRecords is not found in database");
        }

        return entities;
    }

    public Patient findById(Integer id) throws Exception {
        if (id == null)
            throw new NullPointerException("Id is null");
        if (id <= 0)
            throw new IllegalArgumentException("Id <= 0");

        Optional<Patient> optional = repository.findById(id);

        if (optional.isPresent()) {
            LOGGER.info("Get patientRecord(id={}) from database", optional.get().getId());
            return optional.get();
        } else {
            LOGGER.error("PatientRecord(id={}) is not found in database", id);
            throw new EntityNotFoundException(
                    "PatientRecord(id=" + id + ") is not found in database"
            );
        }
    }

    public void save(Patient entity) throws Exception {
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
        if (entity.getPatronymic() == null)
            throw new NullPointerException("MiddleName is null");
        if (entity.getPatronymic().isBlank())
            throw new IllegalArgumentException("MiddleName is blank");
        if (entity.getBirthday() == null)
            throw new NullPointerException("Birthday is null");
        if (entity.getExaminations() == null)
            throw new NullPointerException("Examinations is null");

        repository.save(entity);
        LOGGER.info("PatientRecord(id={}) is recorded in database", entity.getId());
    }

    public void delete(Patient entity) throws Exception {
        if (entity == null)
            throw new NullPointerException("Entity is null");
        if (entity.getId() <= 0)
            throw new IllegalArgumentException("Id <= 0");

        Optional<Patient> optional = repository.findById(entity.getId());

        if (optional.isPresent()) {
            repository.delete(optional.get());
            LOGGER.info("PatientRecord(id={}) is deleted in database", optional.get().getId());
        } else {
            LOGGER.error("PatientRecord(id={}) not found in database", entity.getId());
            throw new EntityNotFoundException(
                    "PatientRecord(id=" + entity.getId() + ") not found in database"
            );
        }
    }
}

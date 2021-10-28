package ru.gsa.biointerface.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gsa.biointerface.domain.entity.Icd;
import ru.gsa.biointerface.repository.IcdRepository;

import javax.persistence.EntityNotFoundException;
import javax.validation.constraints.*;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class IcdService {
    private static final Logger LOGGER = LoggerFactory.getLogger(IcdService.class);
    private static IcdService instance = null;
    private final IcdRepository dao;

    private IcdService() throws Exception {
        dao = IcdRepository.getInstance();
    }

    public static IcdService getInstance() throws Exception {
        if (instance == null) {
            instance = new IcdService();
        }

        return instance;
    }

    public Icd create(
            @NotNull(message = "Name can't be null")
            @NotBlank(message = "Name can't be blank")
            @Size(min = 3, max = 35, message = "Name should be have chars between 3-35")
                    String name,
            @NotNull(message = "Version can't be null")
            @Min(value = 10, message = "Version can't be lass then 10")
            @Max(value = 99, message = "Version can't be more than 99")
                    int version,
            @Size(max = 400, message = "Comment can't be more than 400 chars")
                    String comment) throws Exception {
        if (name == null)
            throw new NullPointerException("name is null");
        if ("".equals(name))
            throw new IllegalArgumentException("name is empty");
        if (version <= 0)
            throw new IllegalArgumentException("version <= 0");

        Icd entity = new Icd(name, version, comment);
        LOGGER.info("New icd created");

        return entity;
    }

    public List<Icd> getAll() throws Exception {
        List<Icd> entities = dao.getAll();

        if (entities.size() > 0) {
            LOGGER.info("Get all icds from database");
        } else {
            LOGGER.info("Icds is not found in database");
        }

        return entities;
    }

    public Icd getById(long id) throws Exception {
        if (id <= 0)
            throw new IllegalArgumentException("id <= 0");

        Icd entity = dao.read(id);

        if (entity != null) {
            LOGGER.info("Get icd(id={}) from database", entity.getId());
        } else {
            LOGGER.error("Icd(id={}) is not found in database", id);
            throw new NoSuchElementException("Icd(id=" + id + ") is not found in database");
        }

        return entity;
    }

    public void save(Icd entity) throws Exception {
        if (entity == null)
            throw new NullPointerException("Entity is null");
        if (entity.getName() == null)
            throw new NullPointerException("name is null");
        if ("".equals(entity.getName()))
            throw new IllegalArgumentException("name is empty");
        if (entity.getVersion() <= 0)
            throw new IllegalArgumentException("version <= 0");
        if (entity.getPatientRecords() == null)
            throw new NullPointerException("PatientRecords is null");

        Icd readEntity = dao.read(entity.getId());

        if (readEntity == null) {
            dao.insert(entity);
            LOGGER.info("Icd(id={}) is recorded in database", entity.getId());
        } else {
            LOGGER.error("Icd(id={}) already exists in database", entity.getId());
            throw new IllegalArgumentException("Icd(id=" + entity.getId() + ") already exists in database");
        }
    }

    public void delete(Icd entity) throws Exception {
        if (entity == null)
            throw new NullPointerException("Entity is null");
        if (entity.getId() <= 0)
            throw new IllegalArgumentException("Id <= 0");

        Icd readEntity = dao.read(entity.getId());

        if (readEntity != null) {
            dao.delete(entity);
            LOGGER.info("Icd(id={}) is deleted in database", entity.getId());
        } else {
            LOGGER.error("Icd(id={}) not found in database", entity.getId());
            throw new EntityNotFoundException("Icd(id=" + entity.getId() + ") not found in database");
        }
    }

    public void update(Icd entity) throws Exception {
        if (entity == null)
            throw new NullPointerException("Entity is null");
        if (entity.getId() <= 0)
            throw new IllegalArgumentException("id <= 0");
        if (entity.getName() == null)
            throw new NullPointerException("Name is null");
        if ("".equals(entity.getName()))
            throw new IllegalArgumentException("Name is empty");
        if (entity.getVersion() <= 0)
            throw new IllegalArgumentException("version <= 0");
        if (entity.getPatientRecords() == null)
            throw new NullPointerException("PatientRecords is null");

        Icd readEntity = dao.read(entity.getId());

        if (readEntity != null) {
            dao.update(entity);
            LOGGER.info("Icd(id={}) updated in database", entity.getId());
        } else {
            LOGGER.error("Icd(id={}) not found in database", entity.getId());
            throw new EntityNotFoundException("Icd(id=" + entity.getId() + ") not found in database");
        }
    }
}

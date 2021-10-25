package ru.gsa.biointerface.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gsa.biointerface.domain.entity.Icd;
import ru.gsa.biointerface.repository.IcdRepository;
import ru.gsa.biointerface.repository.exception.NoConnectionException;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class IcdService {
    private static final Logger LOGGER = LoggerFactory.getLogger(IcdService.class);
    private static IcdService instance = null;
    private final IcdRepository dao;

    public static IcdService getInstance() throws NoConnectionException {
        if (instance == null) {
            instance = new IcdService();
        }

        return instance;
    }

    private IcdService() throws NoConnectionException {
        dao = IcdRepository.getInstance();
    }

    public Icd create(String name, int version, String comment) throws Exception {
        if (name == null)
            throw new NullPointerException("name is null");
        if ("".equals(name))
            throw new NullPointerException("name is empty");
        if (version <= 0)
            throw new IllegalArgumentException("version <= 0");

        Icd entity = new Icd(-1, name, version, comment);
        LOGGER.info("New icd created");

        return entity;
    }

    public List<Icd> getAll()  throws Exception {
        List<Icd> entities = dao.getAll();

        if(entities.size() > 0) {
            LOGGER.info("Get all icds from database");
        } else {
            LOGGER.info("Icds is not found in database");
        }

        return entities;
    }

    public Icd getById(long id)  throws Exception {
        Icd entity = dao.read(id);

        if(entity != null) {
            LOGGER.info("Get icd(id={}) from database", entity.getId());
        } else {
            LOGGER.error("Icd(id={}) is not found in database", id);
            throw new NoSuchElementException("Icd(id=" + id + ") is not found in database");
        }

        return entity;
    }

    public void save(Icd entity)  throws Exception {
        if (entity.getName() == null)
            throw new NullPointerException("name is null");
        if ("".equals(entity.getName()))
            throw new NullPointerException("name is empty");
        if (entity.getVersion() <= 0)
            throw new IllegalArgumentException("version <= 0");

        Icd readEntity = dao.read(entity.getId());

        if (readEntity == null) {
            dao.insert(entity);
            LOGGER.info("Icd(id={}) is recorded in database", entity.getId());
        } else {
            LOGGER.error("Icd(id={}) already exists in database", entity.getId());
            throw new IllegalArgumentException("Icd(id=" + entity.getId() + ") already exists in database");
        }
    }

    public void delete(Icd entity)  throws Exception {
        Icd readEntity = dao.read(entity.getId());

        if(readEntity != null) {
            dao.delete(entity);
            LOGGER.info("Icd(id={}) is deleted in database", entity.getId());
        } else {
            LOGGER.error("Icd(id={}) not found in database", entity.getId());
            throw new NoSuchElementException("Icd(id=" + entity.getId() + ") not found in database");
        }
    }

    public void update(Icd entity)  throws Exception {
        if (entity.getName() == null)
            throw new NullPointerException("name is null");
        if ("".equals(entity.getName()))
            throw new NullPointerException("name is empty");
        if (entity.getVersion() <= 0)
            throw new IllegalArgumentException("version <= 0");

        Icd readEntity = dao.read(entity.getId());

        if(readEntity != null) {
            dao.update(entity);
            LOGGER.info("Icd(id={}) updated in database", entity.getId());
        } else {
            LOGGER.error("Icd(id={}) not found in database", entity.getId());
            throw new NoSuchElementException("Icd(id=" + entity.getId() + ") not found in database");
        }
    }
}

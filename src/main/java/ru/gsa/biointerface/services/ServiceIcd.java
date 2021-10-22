package ru.gsa.biointerface.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gsa.biointerface.domain.entity.Icd;
import ru.gsa.biointerface.persistence.PersistenceException;
import ru.gsa.biointerface.persistence.dao.IcdDAO;

import java.util.List;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class ServiceIcd {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceIcd.class);
    private static ServiceIcd instance = null;
    private final IcdDAO dao;

    public static ServiceIcd getInstance() throws ServiceException {
        if (instance == null) {
            instance = new ServiceIcd();
        }

        return instance;
    }

    private ServiceIcd() throws ServiceException {
        try {
            dao = IcdDAO.getInstance();
        } catch (PersistenceException e) {
            throw new ServiceException("Error connection to database", e);
        }
    }

    public Icd create(String name, int version, String comment) {
        if (name == null)
            throw new NullPointerException("ICD is null");
        if ("".equals(name))
            throw new NullPointerException("ICD is empty");
        if (version <= 0)
            throw new IllegalArgumentException("version <= 0");

        return new Icd(-1, name, version, comment);
    }

    public List<Icd> getAll() throws ServiceException {
        try {
            return dao.getAll();
        } catch (PersistenceException e) {
            throw new ServiceException("Get all error", e);
        }
    }

    public Icd getById(long id) throws ServiceException {
        try {
            return dao.read(id);
        } catch (PersistenceException e) {
            throw new ServiceException("Get all error", e);
        }
    }

    public void save(Icd entity) throws ServiceException {
        if (entity.getName() == null)
            throw new NullPointerException("ICD is null");
        if ("".equals(entity.getName()))
            throw new NullPointerException("ICD is empty");
        if (entity.getVersion() <= 0)
            throw new IllegalArgumentException("version <= 0");

        try {
            Icd readEntity = dao.read(entity.getId());

            if (readEntity == null) {
                dao.insert(entity);
                LOGGER.info("{} is recorded in database", entity);
            }
        } catch (PersistenceException e) {
            throw new ServiceException("Save error", e);
        }
    }

    public void delete(Icd entity) throws ServiceException {
        try {
            dao.delete(entity);
            LOGGER.info("{} is deleted in database", entity);
        } catch (PersistenceException e) {
            throw new ServiceException("Delete error", e);
        }
    }

    public void update(Icd entity) throws ServiceException {
        if (entity.getName() == null)
            throw new NullPointerException("ICD is null");
        if ("".equals(entity.getName()))
            throw new NullPointerException("ICD is empty");
        if (entity.getVersion() <= 0)
            throw new IllegalArgumentException("version <= 0");

        try {
            dao.update(entity);
            LOGGER.info("{} updated in database", entity);
        } catch (PersistenceException e) {
            throw new ServiceException("Update error", e);
        }
    }
}

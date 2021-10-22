package ru.gsa.biointerface.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gsa.biointerface.domain.entity.Channel;
import ru.gsa.biointerface.persistence.PersistenceException;
import ru.gsa.biointerface.persistence.dao.ChannelDAO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class ServiceChannel {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceChannel.class);
    private static ServiceChannel instance = null;
    private final ChannelDAO dao;

    public static ServiceChannel getInstance() throws ServiceException {
        if (instance == null) {
            instance = new ServiceChannel();
        }

        return instance;
    }

    private ServiceChannel() throws ServiceException {
        try {
            dao = ChannelDAO.getInstance();
        } catch (PersistenceException e) {
            throw new ServiceException("Error connection to database", e);
        }
    }

    public Channel create(String name, String comment) {
        if (name == null)
            throw new NullPointerException("Name is null");
        if ("".equals(name))
            throw new IllegalArgumentException("Name is empty");

        return new Channel(0, name, comment, new ArrayList<>());
    }

    public List<Channel> getAll() throws ServiceException {
        try {
            return dao.getAll();
        } catch (PersistenceException e) {
            throw new ServiceException("Get all error", e);
        }
    }

    public Channel getById(long id) throws ServiceException {
        try {
            return dao.read(id);
        } catch (PersistenceException e) {
            throw new ServiceException("Get all error", e);
        }
    }

    public void save(Channel entity) throws ServiceException {
        if (entity.getName() == null)
            throw new NullPointerException("Name is null");
        if ("".equals(entity.getName()))
            throw new IllegalArgumentException("Name is empty");

        try {
            Channel readEntity = dao.read(entity.getId());

            if (readEntity == null) {
                dao.insert(entity);
                LOGGER.info("{} is recorded in database", entity);
            }
        } catch (PersistenceException e) {
            throw new ServiceException("Save error");
        }
    }

    public void delete(Channel entity) throws ServiceException {
        try {
            dao.delete(entity);
            LOGGER.info("{} is deleted in database", entity);
        } catch (PersistenceException e) {
            throw new ServiceException("Delete error", e);
        }
    }

    public void update(Channel entity) throws ServiceException {
        if (entity.getName() == null)
            throw new NullPointerException("Name is null");
        if ("".equals(entity.getName()))
            throw new IllegalArgumentException("Name is empty");

        try {
            dao.update(entity);
            LOGGER.info("{} updated in database", entity);
        } catch (PersistenceException e) {
            throw new ServiceException("Update error", e);
        }
    }
}

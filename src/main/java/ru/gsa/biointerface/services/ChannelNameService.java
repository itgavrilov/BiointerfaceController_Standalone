package ru.gsa.biointerface.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gsa.biointerface.domain.entity.ChannelName;
import ru.gsa.biointerface.repository.ChannelNameRepository;
import ru.gsa.biointerface.repository.exception.NoConnectionException;

import javax.persistence.EntityNotFoundException;
import javax.persistence.NonUniqueResultException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class ChannelNameService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChannelNameService.class);
    private static ChannelNameService instance = null;
    private final ChannelNameRepository dao;

    public static ChannelNameService getInstance() throws NoConnectionException {
        if (instance == null) {
            instance = new ChannelNameService();
        }

        return instance;
    }

    private ChannelNameService() throws NoConnectionException {
        dao = ChannelNameRepository.getInstance();
    }

    public ChannelName create(String name, String comment) throws Exception {
        if (name == null)
            throw new NullPointerException("Name is null");
        if ("".equals(name))
            throw new IllegalArgumentException("Name is empty");

        ChannelName entity = new ChannelName(-1, name, comment, new ArrayList<>());
        LOGGER.info("New channelName created");

        return entity;
    }

    public List<ChannelName> getAll() throws Exception{
        List<ChannelName> entities = dao.getAll();

        if(entities.size() > 0) {
            LOGGER.info("Get all channelNames from database");
        } else {
            LOGGER.info("ChannelNames is not found in database");
        }

        return entities;
    }

    public ChannelName getById(long id) throws Exception {
        ChannelName entity = dao.read(id);

        if(entity != null) {
            LOGGER.info("Get channelName(id={}) from database", entity.getId());
        } else {
            LOGGER.error("ChannelName(id={}) is not found in database", id);
            throw new EntityNotFoundException("ChannelName(id=" + id + ") is not found in database");
        }

        return entity;
    }

    public void save(ChannelName entity) throws Exception {
        if (entity.getName() == null)
            throw new NullPointerException("Name is null");
        if ("".equals(entity.getName()))
            throw new IllegalArgumentException("Name is empty");

        ChannelName readEntity = dao.read(entity.getId());

        if (readEntity == null) {
            dao.insert(entity);
            LOGGER.info("ChannelName(id={})  is recorded in database", entity.getId());
        } else {
            LOGGER.error("ChannelName(id={}) already exists in database", entity.getId());
            throw new NonUniqueResultException("ChannelName(id=" + entity.getId() + ") already exists in database");
        }
    }

    public void delete(ChannelName entity) throws Exception {
        ChannelName readEntity = dao.read(entity.getId());

        if(readEntity != null) {
            dao.delete(entity);
            LOGGER.info("ChannelName(id={}) is deleted in database", entity.getId());
        } else {
            LOGGER.info("ChannelName(id={}) not found in database", entity.getId());
            throw new EntityNotFoundException("ChannelName(id=" + entity.getId() + ") not found in database");
        }
    }

    public void update(ChannelName entity) throws Exception {
        if (entity.getName() == null)
            throw new NullPointerException("Name is null");
        if ("".equals(entity.getName()))
            throw new IllegalArgumentException("Name is empty");

        ChannelName readEntity = dao.read(entity.getId());

        if(readEntity != null) {
            dao.update(entity);
            LOGGER.info("ChannelName(id={}) updated in database", entity.getId());
        } else {
            LOGGER.error("ChannelName(id={}) not found in database", entity.getId());
            throw new EntityNotFoundException("ChannelName(id=" + entity.getId() + ") not found in database");
        }
    }
}

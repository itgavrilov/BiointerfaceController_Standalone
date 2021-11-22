package ru.gsa.biointerface.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gsa.biointerface.domain.entity.ChannelName;
import ru.gsa.biointerface.repository.ChannelNameRepository;
import ru.gsa.biointerface.repository.impl.ChannelNameRepositoryImpl;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class ChannelNameService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChannelNameService.class);
    private static ChannelNameService instance = null;
    private final ChannelNameRepository repository;

    private ChannelNameService() throws Exception {
        repository = ChannelNameRepositoryImpl.getInstance();
    }

    public static ChannelNameService getInstance() throws Exception {
        if (instance == null) {
            instance = new ChannelNameService();
        }

        return instance;
    }

    public List<ChannelName> findAll() throws Exception {
        List<ChannelName> entities = repository.findAll();

        if (entities.size() > 0) {
            LOGGER.info("Get all channelNames from database");
        } else {
            LOGGER.info("ChannelNames is not found in database");
        }

        return entities;
    }

    public ChannelName findById(Integer id) throws Exception {
        if (id == null)
            throw new NullPointerException("Id is null");
        if (id <= 0)
            throw new IllegalArgumentException("Id <= 0");

        Optional<ChannelName> optional = repository.findById(id);

        if (optional.isPresent()) {
            LOGGER.info("Get channelName(id={}) from database", optional.get().getId());

            return optional.get();
        } else {
            LOGGER.error("ChannelName(id={}) is not found in database", id);
            throw new EntityNotFoundException("ChannelName(id=" + id + ") is not found in database");
        }
    }

    public ChannelName save(ChannelName entity) throws Exception {
        if (entity == null)
            throw new NullPointerException("Entity is null");
        if (entity.getName() == null)
            throw new NullPointerException("Name is null");
        if (entity.getName().isBlank())
            throw new IllegalArgumentException("Name is blank");
        if (entity.getChannels() == null)
            throw new NullPointerException("Channels is null");

        entity = repository.save(entity);
        LOGGER.info("ChannelName(id={})  is recorded in database", entity.getId());

        return entity;
    }

    public void delete(ChannelName entity) throws Exception {
        if (entity == null)
            throw new NullPointerException("Entity is null");
        if (entity.getId() <= 0)
            throw new IllegalArgumentException("Id <= 0");

        Optional<ChannelName> optional = repository.findById(entity.getId());

        if (optional.isPresent()) {
            repository.delete(optional.get());
            LOGGER.info("ChannelName(id={}) is deleted in database", optional.get().getId());
        } else {
            LOGGER.info("ChannelName(id={}) not found in database", entity.getId());
            throw new EntityNotFoundException("ChannelName(id=" + entity.getId() + ") not found in database");
        }
    }
}

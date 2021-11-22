package ru.gsa.biointerface.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gsa.biointerface.domain.entity.Device;
import ru.gsa.biointerface.repository.DeviceRepository;
import ru.gsa.biointerface.repository.impl.DeviceRepositoryImpl;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class DeviceService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceService.class);
    private static DeviceService instance = null;
    private final DeviceRepository repository;

    private DeviceService() throws Exception {
        repository = DeviceRepositoryImpl.getInstance();
    }

    public static DeviceService getInstance() throws Exception {
        if (instance == null) {
            instance = new DeviceService();
        }

        return instance;
    }

    public List<Device> findAll() throws Exception {
        List<Device> entities = repository.findAll();

        if (entities.size() > 0) {
            LOGGER.info("Get all devices from database");
        } else {
            LOGGER.info("Devices is not found in database");
        }

        return entities;
    }

    public Device findById(Integer id) throws Exception {
        if (id == null)
            throw new NullPointerException("Id is null");
        if (id <= 0)
            throw new IllegalArgumentException("Id <= 0");

        Optional<Device> optional = repository.findById(id);

        if (optional.isPresent()) {
            LOGGER.info("Get device(id={}) from database", optional.get().getId());

            return optional.get();
        } else {
            LOGGER.error("Device(id={}) is not found in database", id);
            throw new EntityNotFoundException("Device(id=" + id + ") is not found in database");
        }
    }

    public Device save(Device entity) throws Exception {
        if (entity == null)
            throw new NullPointerException("Entity is null");
        if (entity.getId() <= 0)
            throw new IllegalArgumentException("Id <= 0");
        if (entity.getAmountChannels() <= 0)
            throw new IllegalArgumentException("Amount channels <= 0");
        if (entity.getExaminations() == null)
            throw new NullPointerException("Examinations is null");

        entity = repository.save(entity);
        LOGGER.info("Device(id={}) is recorded in database", entity.getId());

        return entity;
    }

    public void delete(Device entity) throws Exception {
        if (entity == null)
            throw new NullPointerException("Entity is null");
        if (entity.getId() <= 0)
            throw new IllegalArgumentException("Id <= 0");

        Optional<Device> optional = repository.findById(entity.getId());

        if (optional.isPresent()) {
            repository.delete(optional.get());
            LOGGER.info("Device(id={}) is deleted in database", optional.get().getId());
        } else {
            LOGGER.info("Device(id={}) not found in database", entity.getId());
            throw new EntityNotFoundException("Device(id=" + entity.getId() + ") not found in database");
        }
    }
}

package ru.gsa.biointerface.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gsa.biointerface.domain.entity.Device;
import ru.gsa.biointerface.persistence.PersistenceException;
import ru.gsa.biointerface.persistence.dao.ChannelDAO;
import ru.gsa.biointerface.persistence.dao.DeviceDAO;

import java.util.List;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class ServiceDevice {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceDevice.class);
    private static ServiceDevice instance = null;
    private final DeviceDAO dao;

    public static ServiceDevice getInstance() throws ServiceException {
        if (instance == null) {
            instance = new ServiceDevice();
        }

        return instance;
    }

    private ServiceDevice() throws ServiceException {
        try {
            dao = DeviceDAO.getInstance();
        } catch (PersistenceException e) {
            throw new ServiceException("Error connection to database", e);
        }
    }

    public Device create(int id, int amountChannels) {
        if (id < 0)
            throw new IllegalArgumentException("Serial number is '0'");
        if (amountChannels == 0)
            throw new IllegalArgumentException("Amount channels is '0'");

        return new Device(id, amountChannels, "");
    }

    public List<Device> getAll() throws ServiceException {
        try {
            return dao.getAll();
        } catch (PersistenceException e) {
            throw new ServiceException("Get all error", e);
        }
    }

    public Device getById(long id) throws ServiceException {
        try {
            return dao.read(id);
        } catch (PersistenceException e) {
            throw new ServiceException("Get all error", e);
        }
    }

    public void save(Device entity) throws ServiceException {
        if (entity.getId() < 0)
            throw new IllegalArgumentException("Serial number is '0'");
        if (entity.getAmountChannels() == 0)
            throw new IllegalArgumentException("Amount channels is '0'");

        try {
            Device readEntity = dao.read(entity.getId());

            if (readEntity == null) {
                dao.insert(entity);
                LOGGER.info("{} is recorded in database", entity);
            }
        } catch (PersistenceException e) {
            throw new ServiceException("Save error");
        }
    }

    public void delete(Device entity) throws ServiceException {
        try {
            Device readEntity = dao.read(entity.getId());

            if(readEntity != null) {
                dao.delete(entity);
                LOGGER.info("{} is deleted in database", entity);
            }
        } catch (PersistenceException e) {
            throw new ServiceException("Delete error", e);
        }
    }

    public void update(Device entity) throws ServiceException {
        if (entity.getId() < 0)
            throw new IllegalArgumentException("Serial number is '0'");
        if (entity.getAmountChannels() == 0)
            throw new IllegalArgumentException("Amount channels is '0'");

        try {
            dao.update(entity);
            LOGGER.info("{} updated in database", entity);
        } catch (PersistenceException e) {
            throw new ServiceException("Update error", e);
        }
    }
}

package ru.gsa.biointerface.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gsa.biointerface.domain.entity.DeviceEntity;
import ru.gsa.biointerface.domain.host.serialport.DeviceConfig;
import ru.gsa.biointerface.persistence.PersistenceException;
import ru.gsa.biointerface.persistence.dao.DeviceDAO;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class Device implements Comparable<Device> {
    private static final Logger LOGGER = LoggerFactory.getLogger(Device.class);
    private DeviceEntity entity;

    static public Set<Device> getAll() throws DomainException {
        try {
            List<DeviceEntity> entitys = DeviceDAO.getInstance().getAll();
            Set<Device> result = new TreeSet<>();
            entitys.forEach(o -> result.add(new Device(o)));
            return result;
        } catch (PersistenceException e) {
            throw new DomainException("DAO getAll devices error");
        }
    }

    public Device(DeviceEntity entity) {
        if(entity == null)
            throw new NullPointerException("Entity is null");

        this.entity = entity;
    }

    public Device(int id, int amountChannels) throws DomainException {
        if (id < 0)
            throw new IllegalArgumentException("Serial number is '0'");
        if (amountChannels == 0)
            throw new IllegalArgumentException("Amount channels is '0'");

        try {
            DeviceEntity readEntity = DeviceDAO.getInstance().read(id);
            if (readEntity == null) {
                entity = new DeviceEntity(id, amountChannels, "");
                DeviceDAO.getInstance().insert(entity);
                LOGGER.info("{} is recorded in database", entity);
            } else {
                entity = readEntity;
            }
        } catch (PersistenceException e) {
            throw new DomainException("DAO insert devices error");
        }
    }

    public void delete() throws DomainException {
        try {
            DeviceDAO.getInstance().delete(entity);
            LOGGER.info("{} is deleted in database", entity);
        } catch (PersistenceException e) {
            throw new DomainException("DAO delete device error");
        }
    }

    public DeviceEntity getEntity() {
        return entity;
    }

    public int getId() {
        return entity.getId();
    }

    public int getAmountChannels() {
        return entity.getAmountChannels();
    }

    public String getComment() {
        return entity.getComment();
    }

    public void setComment(String comment) throws DomainException {
        entity.setComment(comment);
        try {
            DeviceDAO.getInstance().update(entity);
            LOGGER.info("{} is update in {}", comment, entity);
        } catch (PersistenceException e) {
            throw new DomainException("DAO update device error");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Device device = (Device) o;
        return entity.equals(device.entity);
    }

    @Override
    public int hashCode() {
        return entity.hashCode();
    }

    @Override
    public int compareTo(Device o) {
        return entity.compareTo(o.entity);
    }

    @Override
    public String toString() {
        return entity.toString();
    }
}

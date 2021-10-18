package ru.gsa.biointerface.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gsa.biointerface.domain.entity.DeviceEntity;
import ru.gsa.biointerface.domain.host.DeviceConfig;
import ru.gsa.biointerface.persistence.PersistenceException;
import ru.gsa.biointerface.persistence.dao.DeviceDAO;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class Device implements DeviceConfig, Comparable<Device> {
    private static final Logger LOGGER = LoggerFactory.getLogger(Device.class);
    private final DeviceEntity entity;

    public Device(int id, int amountChannels, String comment) {
        this(new DeviceEntity(id, amountChannels, comment));
    }

    public Device(DeviceEntity entity) {
        if (entity.getId() == 0)
            throw new IllegalArgumentException("Serial number is '0'");
        if (entity.getAmountChannels() == 0)
            throw new IllegalArgumentException("Amount channels is '0'");

        this.entity = entity;
    }

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

    public void insert() throws DomainException {
        try {
            if (DeviceDAO.getInstance().read(entity.getId()) == null) {
                DeviceDAO.getInstance().insert(entity);
                LOGGER.info("{} is recorded in database", entity);
            }
        } catch (PersistenceException e) {
            throw new DomainException("DAO insert devices error");
        }
    }

    public void update() throws DomainException {
        try {
            DeviceDAO.getInstance().update(entity);
            LOGGER.info("{} is updated in database", entity);
        } catch (PersistenceException e) {
            throw new DomainException("DAO update device error");
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

    @Override
    public int getId() {
        return entity.getId();
    }

    @Override
    public int getAmountChannels() {
        return entity.getAmountChannels();
    }

    public String getComment() {
        return entity.getComment();
    }

    public void setComment(String comment) {
        LOGGER.info("{} is update in {}", comment, entity);
        entity.setComment(comment);
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
        return Objects.hash(entity);
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

package ru.gsa.biointerface.domain;

import ru.gsa.biointerface.domain.entity.DeviceEntity;
import ru.gsa.biointerface.domain.serialPortHost.DeviceConfig;
import ru.gsa.biointerface.persistence.DAOException;
import ru.gsa.biointerface.persistence.dao.DeviceDAO;

import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

public class Device implements DeviceConfig, Comparable<Device> {
    private final DeviceEntity entity;

    public Device(int id, int amountChannels, String comment) {
        this(new DeviceEntity(id, amountChannels, comment));
    }

    public Device(DeviceEntity deviceEntity) {
        if (deviceEntity.getId() == 0)
            throw new IllegalArgumentException("Serial number is '0'");
        if (deviceEntity.getAmountChannels() == 0)
            throw new IllegalArgumentException("Amount channels is '0'");

        this.entity = deviceEntity;
    }

    static public Set<Device> getSetAll() throws DomainException {
        try {
            Set<DeviceEntity> entitys = DeviceDAO.getInstance().getAll();
            Set<Device> result = new TreeSet<>();
            entitys.forEach(o -> result.add(new Device(o)));
            return result;
        } catch (DAOException e) {
            e.printStackTrace();
            throw new DomainException("dao getAll devices error");
        }
    }

    public void update() throws DomainException {
        try {
            DeviceDAO.getInstance().update(entity);
        } catch (DAOException e) {
            e.printStackTrace();
            throw new DomainException("dao update device error");
        }
    }

    public void delete() throws DomainException {
        try {
            DeviceDAO.getInstance().delete(entity);
        } catch (DAOException e) {
            e.printStackTrace();
            throw new DomainException("dao delete device error");
        }
    }

    public void insert() throws DomainException {
        try {
            if (DeviceDAO.getInstance().getById(entity.getId()) == null)
                DeviceDAO.getInstance().insert(entity);
        } catch (DAOException e) {
            e.printStackTrace();
            throw new DomainException("dao insert devices error");
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
}

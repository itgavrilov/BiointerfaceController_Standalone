package ru.gsa.biointerface.domain;

import ru.gsa.biointerface.domain.entity.Device;
import ru.gsa.biointerface.persistence.DAOException;
import ru.gsa.biointerface.persistence.dao.DeviceDAO;

import java.util.Set;

public class Devices {

    static public void update(Device device) throws DomainException {
        try {
            DeviceDAO.getInstance().update(device);
        } catch (DAOException e) {
            e.printStackTrace();
            throw new DomainException("dao update device error");
        }
    }

    static public void delete(Device device) throws DomainException {
        try {
            DeviceDAO.getInstance().delete(device);
        } catch (DAOException e) {
            e.printStackTrace();
            throw new DomainException("dao delete device error");
        }
    }

    static public void insert(Device device) throws DomainException {
        try {
            if (DeviceDAO.getInstance().getById(device.getId()) == null)
                DeviceDAO.getInstance().insert(device);
        } catch (DAOException e) {
            e.printStackTrace();
            throw new DomainException("dao insert devices error");
        }
    }

    static public Set<Device> getSetAll() throws DomainException {
        try {
            return DeviceDAO.getInstance().getAll();
        } catch (DAOException e) {
            e.printStackTrace();
            throw new DomainException("dao getAll devices error");
        }
    }
}

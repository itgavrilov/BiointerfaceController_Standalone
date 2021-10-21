package ru.gsa.biointerface.persistence.dao;

import ru.gsa.biointerface.domain.entity.DeviceEntity;
import ru.gsa.biointerface.persistence.PersistenceException;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class DeviceDAO extends AbstractDAO<DeviceEntity, Integer> {
    protected static DeviceDAO dao;

    private DeviceDAO() throws PersistenceException {
        super();
    }

    public static DeviceDAO getInstance() throws PersistenceException {
        if (dao == null)
            dao = new DeviceDAO();

        return dao;
    }
}

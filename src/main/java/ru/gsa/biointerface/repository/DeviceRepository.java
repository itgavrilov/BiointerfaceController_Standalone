package ru.gsa.biointerface.repository;

import ru.gsa.biointerface.domain.entity.Device;
import ru.gsa.biointerface.repository.database.AbstractRepository;
import ru.gsa.biointerface.repository.exception.NoConnectionException;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class DeviceRepository extends AbstractRepository<Device, Long> {
    protected static DeviceRepository dao;

    private DeviceRepository() throws NoConnectionException {
        super();
    }

    public static DeviceRepository getInstance() throws NoConnectionException {
        if (dao == null)
            dao = new DeviceRepository();

        return dao;
    }
}

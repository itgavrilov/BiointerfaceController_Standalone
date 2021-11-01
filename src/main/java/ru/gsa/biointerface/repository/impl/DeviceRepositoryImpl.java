package ru.gsa.biointerface.repository.impl;

import ru.gsa.biointerface.domain.entity.Device;
import ru.gsa.biointerface.repository.DeviceRepository;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class DeviceRepositoryImpl extends AbstractRepository<Device, Long> implements DeviceRepository {
    private static DeviceRepository dao;

    private DeviceRepositoryImpl() throws Exception {
        super();
    }

    public static DeviceRepository getInstance() throws Exception {
        if (dao == null) {
            dao = new DeviceRepositoryImpl();
        }

        return dao;
    }
}

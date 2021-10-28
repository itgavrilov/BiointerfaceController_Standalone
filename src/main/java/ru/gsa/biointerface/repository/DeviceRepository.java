package ru.gsa.biointerface.repository;

import ru.gsa.biointerface.domain.entity.Device;
import ru.gsa.biointerface.repository.database.AbstractRepository;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class DeviceRepository extends AbstractRepository<Device, Long> {
    private static DeviceRepository dao;

    private DeviceRepository() throws Exception {
        super();
    }

    public static DeviceRepository getInstance() throws Exception {
        if (dao == null) {
            dao = new DeviceRepository();
        }

        return dao;
    }
}

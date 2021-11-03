package ru.gsa.biointerface.repository.impl;

import ru.gsa.biointerface.domain.entity.Device;
import ru.gsa.biointerface.repository.DeviceRepository;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class DeviceRepositoryImpl extends AbstractRepository<Device, Integer> implements DeviceRepository {
    private static DeviceRepository repository;

    private DeviceRepositoryImpl() throws Exception {
        super();
    }

    public static DeviceRepository getInstance() throws Exception {
        if (repository == null) {
            repository = new DeviceRepositoryImpl();
        }

        return repository;
    }

    @Override
    public Device save(Device entity) throws Exception {
        if (entity == null)
            throw new NullPointerException("Entity is null");

        if (entity.getId() > 0 && existsById(entity.getId())) {
            update(entity);
        } else {
            insert(entity);
        }

        return entity;
    }
}

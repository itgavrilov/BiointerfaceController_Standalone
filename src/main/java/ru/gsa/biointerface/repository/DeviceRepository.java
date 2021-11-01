package ru.gsa.biointerface.repository;

import ru.gsa.biointerface.domain.entity.Device;

import java.util.List;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 01/11/2021
 */
public interface DeviceRepository {
    void insert(Device device) throws Exception;

    Device getById(Long id) throws Exception;

    void update(Device device) throws Exception;

    void delete(Device device) throws Exception;

    List<Device> getAll() throws Exception;
}

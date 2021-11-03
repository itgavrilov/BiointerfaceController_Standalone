package ru.gsa.biointerface.repository;

import ru.gsa.biointerface.domain.entity.Device;

import java.util.List;
import java.util.Optional;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 01/11/2021
 */
public interface DeviceRepository {
    List<Device> findAll() throws Exception;

    Optional<Device> findById(Integer id) throws Exception;

    Device save(Device device) throws Exception;

    void delete(Device device) throws Exception;

    boolean existsById(Integer id) throws Exception;
}

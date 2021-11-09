package ru.gsa.biointerface.repository;

import ru.gsa.biointerface.domain.entity.Patient;

import java.util.List;
import java.util.Optional;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 01/11/2021
 */
public interface PatientRepository {
    List<Patient> findAll() throws Exception;

    Optional<Patient> findById(Integer id) throws Exception;

    Patient save(Patient patient) throws Exception;

    void delete(Patient patient) throws Exception;

    boolean existsById(Integer id) throws Exception;
}

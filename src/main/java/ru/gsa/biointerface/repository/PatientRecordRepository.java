package ru.gsa.biointerface.repository;

import ru.gsa.biointerface.domain.entity.PatientRecord;

import java.util.List;
import java.util.Optional;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 01/11/2021
 */
public interface PatientRecordRepository {
    List<PatientRecord> findAll() throws Exception;

    Optional<PatientRecord> findById(Integer id) throws Exception;

    PatientRecord save(PatientRecord patientRecord) throws Exception;

    void delete(PatientRecord patientRecord) throws Exception;

    boolean existsById(Integer id) throws Exception;
}

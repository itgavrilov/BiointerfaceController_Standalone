package ru.gsa.biointerface.repository;

import ru.gsa.biointerface.domain.entity.PatientRecord;

import java.util.List;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 01/11/2021
 */
public interface PatientRecordRepository {
    void insert(PatientRecord patientRecord) throws Exception;

    PatientRecord getById(Long id) throws Exception;

    void update(PatientRecord patientRecord) throws Exception;

    void delete(PatientRecord patientRecord) throws Exception;

    List<PatientRecord> getAll() throws Exception;
}

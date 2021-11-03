package ru.gsa.biointerface.repository;

import ru.gsa.biointerface.domain.entity.Examination;
import ru.gsa.biointerface.domain.entity.PatientRecord;

import java.util.List;
import java.util.Optional;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 01/11/2021
 */
public interface ExaminationRepository {
    List<Examination> findAll() throws Exception;

    List<Examination> findAllByPatientRecord(PatientRecord patientRecord) throws Exception;

    Optional<Examination> findById(Integer id) throws Exception;

    Examination save(Examination examination) throws Exception;

    void delete(Examination examination) throws Exception;

    boolean existsById(Integer id) throws Exception;
}

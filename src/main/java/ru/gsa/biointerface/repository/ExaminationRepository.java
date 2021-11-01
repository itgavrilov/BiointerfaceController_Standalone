package ru.gsa.biointerface.repository;

import ru.gsa.biointerface.domain.entity.Examination;
import ru.gsa.biointerface.domain.entity.PatientRecord;

import java.util.List;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 01/11/2021
 */
public interface ExaminationRepository {
    void insert(Examination examination) throws Exception;

    Examination getById(Long id) throws Exception;

    void update(Examination examination) throws Exception;

    void delete(Examination examination) throws Exception;

    List<Examination> getAll() throws Exception;

    List<Examination> getByPatientRecord(PatientRecord patientRecord) throws Exception;

    void transactionOpen() throws Exception;

    void transactionClose() throws Exception;

    boolean sessionIsOpen();

    boolean transactionIsOpen();
}

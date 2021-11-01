package ru.gsa.biointerface.repository.impl;

import ru.gsa.biointerface.domain.entity.PatientRecord;
import ru.gsa.biointerface.repository.PatientRecordRepository;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class PatientRecordRepositoryImpl extends AbstractRepository<PatientRecord, Long> implements PatientRecordRepository {
    private static PatientRecordRepository dao;

    private PatientRecordRepositoryImpl() throws Exception {
        super();
    }

    public static PatientRecordRepository getInstance() throws Exception {
        if (dao == null) {
            dao = new PatientRecordRepositoryImpl();
        }

        return dao;
    }
}

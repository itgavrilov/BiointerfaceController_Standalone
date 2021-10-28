package ru.gsa.biointerface.repository;

import ru.gsa.biointerface.domain.entity.PatientRecord;
import ru.gsa.biointerface.repository.database.AbstractRepository;


/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class PatientRecordRepository extends AbstractRepository<PatientRecord, Long> {
    private static PatientRecordRepository dao;

    private PatientRecordRepository() throws Exception {
        super();
    }

    public static PatientRecordRepository getInstance() throws Exception {
        if (dao == null) {
            dao = new PatientRecordRepository();
        }

        return dao;
    }
}

package ru.gsa.biointerface.repository;

import ru.gsa.biointerface.domain.entity.PatientRecord;
import ru.gsa.biointerface.repository.database.AbstractRepository;
import ru.gsa.biointerface.repository.exception.NoConnectionException;


/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class PatientRecordRepository extends AbstractRepository<PatientRecord, Long> {
    protected static PatientRecordRepository dao;

    private PatientRecordRepository() throws NoConnectionException {
        super();
    }

    public static PatientRecordRepository getInstance() throws NoConnectionException {
        if (dao == null)
            dao = new PatientRecordRepository();

        return dao;
    }
}

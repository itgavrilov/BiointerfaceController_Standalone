package ru.gsa.biointerface.persistence.dao;

import ru.gsa.biointerface.domain.entity.PatientRecord;
import ru.gsa.biointerface.persistence.PersistenceException;


/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class PatientRecordDAO extends AbstractDAO<PatientRecord, Long> {
    protected static PatientRecordDAO dao;

    private PatientRecordDAO() throws PersistenceException {
        super();
    }

    public static PatientRecordDAO getInstance() throws PersistenceException {
        if (dao == null)
            dao = new PatientRecordDAO();

        return dao;
    }
}

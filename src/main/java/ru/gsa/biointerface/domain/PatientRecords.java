package ru.gsa.biointerface.domain;

import ru.gsa.biointerface.domain.entity.PatientRecord;
import ru.gsa.biointerface.persistence.DAOException;
import ru.gsa.biointerface.persistence.dao.PatientRecordDAO;

import java.util.Set;

public class PatientRecords {

    static public void insert(PatientRecord patientRecord) throws DomainException {
        try {
            PatientRecordDAO.getInstance().insert(patientRecord);
        } catch (DAOException e) {
            e.printStackTrace();
            throw new DomainException("dao insert patientRecord error");
        }
    }

    static public void update(PatientRecord patientRecord) throws DomainException {
        try {
            PatientRecordDAO.getInstance().update(patientRecord);
        } catch (DAOException e) {
            e.printStackTrace();
            throw new DomainException("dao update patientRecord error");
        }
    }

    static public void delete(PatientRecord patientRecord) throws DomainException {
        try {
            PatientRecordDAO.getInstance().delete(patientRecord);
        } catch (DAOException e) {
            e.printStackTrace();
            throw new DomainException("dao delete patientRecord error");
        }
    }

    static public Set<PatientRecord> getSetAll() throws DomainException {
        try {
            return PatientRecordDAO.getInstance().getAll();
        } catch (DAOException e) {
            e.printStackTrace();
            throw new DomainException("dao getAll patientRecords error");
        }
    }
}

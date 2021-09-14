package ru.gsa.biointerface.domain;

import ru.gsa.biointerface.domain.entity.Examination;
import ru.gsa.biointerface.domain.entity.PatientRecord;
import ru.gsa.biointerface.persistence.DAOException;
import ru.gsa.biointerface.persistence.dao.ExaminationDAO;

import java.util.Set;

public class Examinations {

    static public Examination insert(Examination examination) throws DomainException {
        try {
            return ExaminationDAO.getInstance().insert(examination);
        } catch (DAOException e) {
            e.printStackTrace();
            throw new DomainException("dao insert examination error");
        }
    }

    static public void update(Examination examination) throws DomainException {
        try {
            ExaminationDAO.getInstance().update(examination);
        } catch (DAOException e) {
            e.printStackTrace();
            throw new DomainException("dao update examination error");
        }
    }

    static public void delete(Examination examination) throws DomainException {
        try {
            ExaminationDAO.getInstance().delete(examination);
        } catch (DAOException e) {
            e.printStackTrace();
            throw new DomainException("dao delete examination error");
        }
    }

    static public Set<Examination> getSetAll() throws DomainException {
        try {
            return ExaminationDAO.getInstance().getAll();
        } catch (DAOException e) {
            e.printStackTrace();
            throw new DomainException("dao getAll examinations error");
        }
    }


    static public Set<Examination> getSetByPatientRecordId(PatientRecord patientRecord) throws DomainException {
        try {
            return ExaminationDAO.getInstance().getByPatientRecord(patientRecord);
        } catch (DAOException e) {
            e.printStackTrace();
            throw new DomainException("dao getByPatientRecordId examinations error");
        }
    }
}

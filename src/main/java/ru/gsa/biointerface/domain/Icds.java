package ru.gsa.biointerface.domain;

import ru.gsa.biointerface.domain.entity.Icd;
import ru.gsa.biointerface.persistence.DAOException;
import ru.gsa.biointerface.persistence.dao.IcdDAO;

import java.util.Set;

public class Icds {

    static public void insert(Icd icd) throws DomainException {
        try {
            IcdDAO.getInstance().insert(icd);
        } catch (DAOException e) {
            e.printStackTrace();
            throw new DomainException("dao insert icd error");
        }
    }

    static public void update(Icd icd) throws DomainException {
        try {
            IcdDAO.getInstance().update(icd);
        } catch (DAOException e) {
            e.printStackTrace();
            throw new DomainException("dao update icd error");
        }
    }

    static public void delete(Icd icd) throws DomainException {
        try {
            IcdDAO.getInstance().delete(icd);
        } catch (DAOException e) {
            e.printStackTrace();
            throw new DomainException("dao delete icd error");
        }
    }

    static public Set<Icd> getSetAll() throws DomainException {
        try {
            return IcdDAO.getInstance().getAll();
        } catch (DAOException e) {
            e.printStackTrace();
            throw new DomainException("dao getAll icds error");
        }
    }
}

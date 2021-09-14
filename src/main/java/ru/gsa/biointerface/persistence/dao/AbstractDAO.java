package ru.gsa.biointerface.persistence.dao;

import ru.gsa.biointerface.persistence.DAOException;
import ru.gsa.biointerface.persistence.DB;
import ru.gsa.biointerface.persistence.DBHandler;

public abstract class AbstractDAO<Entity> implements DAO<Entity> {
    protected final DB db;

    protected AbstractDAO() throws DAOException {
        db = DBHandler.getInstance();
    }
}

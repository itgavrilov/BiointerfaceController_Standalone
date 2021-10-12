package ru.gsa.biointerface.persistence.dao;

import ru.gsa.biointerface.persistence.PersistenceException;
import ru.gsa.biointerface.persistence.DB;
import ru.gsa.biointerface.persistence.DBHandler;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public abstract class AbstractDAO<Entity> implements DAO<Entity> {
    protected final DB db;

    protected AbstractDAO() throws PersistenceException {
        db = DBHandler.getInstance();
    }
}

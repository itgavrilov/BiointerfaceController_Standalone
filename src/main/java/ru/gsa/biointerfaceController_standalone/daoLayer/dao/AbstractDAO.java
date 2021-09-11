package ru.gsa.biointerfaceController_standalone.daoLayer.dao;

import ru.gsa.biointerfaceController_standalone.daoLayer.DAOException;
import ru.gsa.biointerfaceController_standalone.daoLayer.DB;
import ru.gsa.biointerfaceController_standalone.daoLayer.DBHandler;

public abstract class AbstractDAO<Entity> implements DAO<Entity> {
    protected final DB db;

    protected AbstractDAO() throws DAOException {
        db = DBHandler.getInstance();
    }
}

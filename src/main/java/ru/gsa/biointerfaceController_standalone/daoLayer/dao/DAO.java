package ru.gsa.biointerfaceController_standalone.daoLayer.dao;

import ru.gsa.biointerfaceController_standalone.daoLayer.DAOException;

import java.util.Set;

public interface DAO<Entity> {

    boolean insert(Entity entity) throws DAOException;

    Entity getById(int id) throws DAOException;

    boolean update(Entity entity) throws DAOException;

    boolean delete(Entity entity) throws DAOException;

    Set<Entity> getAll() throws DAOException;
}

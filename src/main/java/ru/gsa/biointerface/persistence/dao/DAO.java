package ru.gsa.biointerface.persistence.dao;

import ru.gsa.biointerface.persistence.DAOException;

import java.util.Set;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public interface DAO<Entity> {

    Entity insert(Entity entity) throws DAOException;

    Entity getById(int id) throws DAOException;

    boolean update(Entity entity) throws DAOException;

    boolean delete(Entity entity) throws DAOException;

    Set<Entity> getAll() throws DAOException;
}

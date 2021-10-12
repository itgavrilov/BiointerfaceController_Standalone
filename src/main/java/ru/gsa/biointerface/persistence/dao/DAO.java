package ru.gsa.biointerface.persistence.dao;

import ru.gsa.biointerface.persistence.PersistenceException;

import java.util.Set;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public interface DAO<Entity> {

    Entity insert(Entity entity) throws PersistenceException;

    Entity getById(int id) throws PersistenceException;

    boolean update(Entity entity) throws PersistenceException;

    boolean delete(Entity entity) throws PersistenceException;

    Set<Entity> getAll() throws PersistenceException;
}

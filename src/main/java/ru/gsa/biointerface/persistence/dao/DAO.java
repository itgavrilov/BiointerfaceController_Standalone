package ru.gsa.biointerface.persistence.dao;

import ru.gsa.biointerface.persistence.PersistenceException;


/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public interface DAO<Entity, Key> {

    Entity insert(Entity entity) throws PersistenceException;

    Entity read(Key key) throws PersistenceException;

    boolean update(Entity entity) throws PersistenceException;

    boolean delete(Entity entity) throws PersistenceException;
}

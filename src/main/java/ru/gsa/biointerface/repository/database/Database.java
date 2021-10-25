package ru.gsa.biointerface.repository.database;

import org.hibernate.SessionFactory;

import javax.persistence.PersistenceException;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public interface Database {
    SessionFactory getSessionFactory() throws PersistenceException;
}

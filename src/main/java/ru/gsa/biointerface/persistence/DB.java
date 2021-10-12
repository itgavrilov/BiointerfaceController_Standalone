package ru.gsa.biointerface.persistence;

import java.sql.Connection;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public interface DB {
    Connection getConnection() throws PersistenceException;
    void disconnect();
}
